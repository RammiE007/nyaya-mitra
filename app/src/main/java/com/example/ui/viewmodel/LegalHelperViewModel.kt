package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.db.LegalHelperDatabase
import com.example.data.model.DocumentAnalysis
import com.example.data.model.GeminiDocResponse
import com.example.data.model.GeminiQueryResponse
import com.example.data.model.Lawyer
import com.example.data.model.LawyerBooking
import com.example.data.model.LegalQuery
import com.example.data.repository.LegalHelperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray

sealed interface QueryUiState {
    object Idle : QueryUiState
    object Loading : QueryUiState
    data class Success(val data: GeminiQueryResponse) : QueryUiState
    data class Error(val message: String) : QueryUiState
}

sealed interface DocUiState {
    object Idle : DocUiState
    object Loading : DocUiState
    data class Success(val data: GeminiDocResponse) : DocUiState
    data class Error(val message: String) : DocUiState
}

class LegalHelperViewModel(
    application: Application,
    private val repository: LegalHelperRepository
) : AndroidViewModel(application) {

    private val TAG = "LegalHelperViewModel"

    // --- State Streams from Room ---
    val savedQueries: StateFlow<List<LegalQuery>> = repository.allQueries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedAnalyses: StateFlow<List<DocumentAnalysis>> = repository.allAnalyses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedBookings: StateFlow<List<LawyerBooking>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Active Analysis UI States ---
    private val _queryUiState = MutableStateFlow<QueryUiState>(QueryUiState.Idle)
    val queryUiState: StateFlow<QueryUiState> = _queryUiState.asStateFlow()

    private val _docUiState = MutableStateFlow<DocUiState>(DocUiState.Idle)
    val docUiState: StateFlow<DocUiState> = _docUiState.asStateFlow()

    // --- Active Case Detail UI (for viewing from history) ---
    private val _viewingQuery = MutableStateFlow<LegalQuery?>(null)
    val viewingQuery: StateFlow<LegalQuery?> = _viewingQuery.asStateFlow()

    private val _viewingDoc = MutableStateFlow<DocumentAnalysis?>(null)
    val viewingDoc: StateFlow<DocumentAnalysis?> = _viewingDoc.asStateFlow()

    // --- User Profile / Free-tier Limits ---
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _remainingFreeQueries = MutableStateFlow(5)
    val remainingFreeQueries: StateFlow<Int> = _remainingFreeQueries.asStateFlow()

    private val _remainingFreeDocs = MutableStateFlow(1)
    val remainingFreeDocs: StateFlow<Int> = _remainingFreeDocs.asStateFlow()

    // --- Actions ---

    fun upgradeToPro() {
        _isPro.value = true
        _remainingFreeQueries.value = 999
        _remainingFreeDocs.value = 999
    }

    fun setViewingQuery(query: LegalQuery?) {
        _viewingQuery.value = query
    }

    fun setViewingDoc(doc: DocumentAnalysis?) {
        _viewingDoc.value = doc
    }

    fun resetQueryState() {
        _queryUiState.value = QueryUiState.Idle
    }

    fun resetDocState() {
        _docUiState.value = DocUiState.Idle
    }

    /**
     * Executes AI explanation for a legal query
     */
    fun askLegalQuestion(userQueryText: String) {
        if (userQueryText.trim().isEmpty()) return

        if (!_isPro.value && _remainingFreeQueries.value <= 0) {
            _queryUiState.value = QueryUiState.Error("Free limit reached! Upgrade to Pro for unlimited AI queries.")
            return
        }

        viewModelScope.launch {
            _queryUiState.value = QueryUiState.Loading
            try {
                val response = GeminiClient.explainLegalQuery(userQueryText)
                if (response != null) {
                    _queryUiState.value = QueryUiState.Success(response)
                    
                    // Decrease free count
                    if (!_isPro.value) {
                        _remainingFreeQueries.value = (_remainingFreeQueries.value - 1).coerceAtLeast(0)
                    }

                    // Save to local DB
                    val legalQuery = LegalQuery(
                        queryText = userQueryText,
                        sections = response.sections,
                        isBailable = response.isBailable,
                        isCognizable = response.isCognizable,
                        explanationHindi = response.explanationHindi,
                        explanationEnglish = response.explanationEnglish,
                        punishment = response.punishment,
                        timelineJson = response.timeline.toJsonString(),
                        checklistJson = response.checklist.toJsonString()
                    )
                    repository.insertQuery(legalQuery)
                } else {
                    _queryUiState.value = QueryUiState.Error("Failed to get response from AI. Please check your internet connection or Gemini API key configuration in settings.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in askLegalQuestion VM", e)
                _queryUiState.value = QueryUiState.Error("An unexpected error occurred: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Executes AI Document Analysis
     */
    fun analyzeDocument(userNotes: String, bitmap: Bitmap?, fileName: String = "Scanned_Doc.jpg") {
        if (bitmap == null && userNotes.isEmpty()) return

        if (!_isPro.value && _remainingFreeDocs.value <= 0) {
            _docUiState.value = DocUiState.Error("Free limit reached! Upgrade to Pro for unlimited document analyses.")
            return
        }

        viewModelScope.launch {
            _docUiState.value = DocUiState.Loading
            try {
                val response = GeminiClient.analyzeDocument(userNotes, bitmap)
                if (response != null) {
                    _docUiState.value = DocUiState.Success(response)

                    // Decrease free count
                    if (!_isPro.value) {
                        _remainingFreeDocs.value = (_remainingFreeDocs.value - 1).coerceAtLeast(0)
                    }

                    // Save to local DB
                    val documentAnalysis = DocumentAnalysis(
                        fileName = fileName,
                        docType = response.docType,
                        summaryJson = response.summary.toJsonString(),
                        risksJson = response.risks.toJsonString(),
                        lawReferencesJson = response.lawReferences.toJsonString(),
                        suggestedActionsJson = response.suggestedActions.toJsonString(),
                        originalText = userNotes
                    )
                    repository.insertAnalysis(documentAnalysis)
                } else {
                    _docUiState.value = DocUiState.Error("Failed to analyze document. Verify your API key is correctly setup in the Secrets panel.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in analyzeDocument VM", e)
                _docUiState.value = DocUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Books a Lawyer consultation and saves to offline bookings database
     */
    fun bookLawyerConsultation(
        lawyer: Lawyer,
        date: String,
        time: String,
        mode: String,
        caseSummaryText: String
    ) {
        viewModelScope.launch {
            val booking = LawyerBooking(
                lawyerId = lawyer.id,
                lawyerName = lawyer.name,
                lawyerSpecialty = lawyer.specialty,
                lawyerRating = lawyer.rating,
                lawyerImageRes = lawyer.imageRes,
                appointmentDate = date,
                appointmentTime = time,
                consultingMode = mode,
                status = "Upcoming",
                fee = lawyer.startingFee,
                caseSummary = caseSummaryText
            )
            repository.insertBooking(booking)
        }
    }

    fun deleteQuery(query: LegalQuery) {
        viewModelScope.launch {
            repository.deleteQueryById(query.id)
            if (_viewingQuery.value?.id == query.id) {
                _viewingQuery.value = null
            }
        }
    }

    fun deleteAnalysis(analysis: DocumentAnalysis) {
        viewModelScope.launch {
            repository.deleteAnalysisById(analysis.id)
            if (_viewingDoc.value?.id == analysis.id) {
                _viewingDoc.value = null
            }
        }
    }

    fun cancelBooking(booking: LawyerBooking) {
        viewModelScope.launch {
            repository.deleteBookingById(booking.id)
        }
    }

    // --- JSON Serialization Helpers ---
    private fun List<String>.toJsonString(): String {
        val jsonArray = JSONArray()
        for (item in this) {
            jsonArray.put(item)
        }
        return jsonArray.toString()
    }
}

class LegalHelperViewModelFactory(
    private val application: Application,
    private val repository: LegalHelperRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LegalHelperViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LegalHelperViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Utility conversion functions for views
fun String.fromJsonArray(): List<String> {
    val list = mutableListOf<String>()
    if (this.isEmpty()) return list
    try {
        val jsonArray = JSONArray(this)
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
    } catch (e: Exception) {
        Log.e("JsonConversion", "Error parsing json array", e)
    }
    return list
}
