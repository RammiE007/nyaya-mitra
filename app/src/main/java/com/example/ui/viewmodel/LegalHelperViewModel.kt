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

    // --- Onboarding / Tour State ---
    private val _showOnboarding = MutableStateFlow(true)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _currentOnboardingStep = MutableStateFlow(0)
    val currentOnboardingStep: StateFlow<Int> = _currentOnboardingStep.asStateFlow()

    fun startOnboarding() {
        _currentOnboardingStep.value = 0
        _showOnboarding.value = true
    }

    fun completeOnboarding() {
        _showOnboarding.value = false
    }

    fun nextOnboardingStep() {
        _currentOnboardingStep.value = _currentOnboardingStep.value + 1
    }

    fun prevOnboardingStep() {
        _currentOnboardingStep.value = (_currentOnboardingStep.value - 1).coerceAtLeast(0)
    }

    // --- Dual-Role Multi-Language & Auth State ---
    private val _userRole = MutableStateFlow("client") // "client", "lawyer", "admin"
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow("en") // "hi", "en"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _phoneInput = MutableStateFlow("")
    val phoneInput: StateFlow<String> = _phoneInput.asStateFlow()

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    // --- Lawyer Profile & Onboarding ---
    private val _lawyerVerificationStatus = MutableStateFlow("none") // "none", "pending", "verified", "rejected"
    val lawyerVerificationStatus: StateFlow<String> = _lawyerVerificationStatus.asStateFlow()

    val lawyerName = MutableStateFlow("")
    val lawyerEmail = MutableStateFlow("")
    val lawyerBarState = MutableStateFlow("Delhi Bar Council")
    val lawyerEnrollmentNo = MutableStateFlow("")
    val lawyerEnrollmentYear = MutableStateFlow("2018")
    val lawyerExperience = MutableStateFlow("5")
    val lawyerPracticeAreas = MutableStateFlow(setOf("Criminal", "Civil"))
    val lawyerFees = MutableStateFlow(mapOf("chat" to "299", "call" to "499", "video" to "599"))
    val lawyerBio = MutableStateFlow("")
    val lawyerBankName = MutableStateFlow("")
    val lawyerAccountNo = MutableStateFlow("")
    val lawyerIfsc = MutableStateFlow("")
    val lawyerListingPlan = MutableStateFlow("Free")

    private val _lawyerIsOnline = MutableStateFlow(true)
    val lawyerIsOnline: StateFlow<Boolean> = _lawyerIsOnline.asStateFlow()

    // Leads list
    private val _lawyerLeads = MutableStateFlow<List<LawyerLead>>(
        listOf(
            LawyerLead(
                id = "LD-4821",
                clientName = "Anonymous Client",
                caseType = "Criminal Case",
                aiSummary = "BNS §74 (old IPC §354) FIR darz hui hai Hazratganj PS Lucknow mein. Client ko bail aur agla process samajhna hai.",
                applicableSection = "BNS §74 (IPC §354)",
                bailableStatus = "Non-Bailable",
                consultType = "Chat Consult",
                fee = "₹299",
                timeReceived = "15 mins ago"
            ),
            LawyerLead(
                id = "LD-9204",
                clientName = "Suresh K. (Small Business)",
                caseType = "Contract Notice",
                aiSummary = "Vendor has sent legal notice for non-payment of ₹2,50,000. Under Section 138 of Negotiable Instruments Act. Client has check receipts.",
                applicableSection = "NI Act §138",
                bailableStatus = "Bailable",
                consultType = "Phone Call",
                fee = "₹499",
                timeReceived = "1 hr ago"
            )
        )
    )
    val lawyerLeads: StateFlow<List<LawyerLead>> = _lawyerLeads.asStateFlow()

    private val _lawyerEarnings = MutableStateFlow(0.0)
    val lawyerEarnings: StateFlow<Double> = _lawyerEarnings.asStateFlow()

    private val _lawyerRatingAvg = MutableStateFlow(4.8)
    val lawyerRatingAvg: StateFlow<Double> = _lawyerRatingAvg.asStateFlow()

    private val _lawyerReviewCount = MutableStateFlow(12)
    val lawyerReviewCount: StateFlow<Int> = _lawyerReviewCount.asStateFlow()

    private val _lawyerAcceptedConsultations = MutableStateFlow<List<LawyerLead>>(emptyList())
    val lawyerAcceptedConsultations: StateFlow<List<LawyerLead>> = _lawyerAcceptedConsultations.asStateFlow()

    val lawyerSlots = MutableStateFlow(
        mapOf(
            "Monday" to listOf("09:00 AM - 01:00 PM", "02:00 PM - 06:00 PM"),
            "Tuesday" to listOf("10:00 AM - 05:00 PM"),
            "Wednesday" to listOf("Court Day - Unavailable"),
            "Thursday" to listOf("11:00 AM - 07:00 PM"),
            "Friday" to listOf("09:00 AM - 03:00 PM"),
            "Saturday" to listOf("10:00 AM - 01:00 PM")
        )
    )

    // --- Actions ---

    fun selectLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun selectRole(role: String) {
        _userRole.value = role
    }

    fun updatePhoneInput(phone: String) {
        _phoneInput.value = phone
    }

    fun updateOtpInput(otp: String) {
        _otpInput.value = otp
    }

    fun verifyOTP() {
        if (_phoneInput.value.isNotEmpty() && _otpInput.value.length == 6) {
            _isRegistered.value = true
        }
    }

    fun logout() {
        _isRegistered.value = false
        _phoneInput.value = ""
        _otpInput.value = ""
        _lawyerVerificationStatus.value = "none"
        _userRole.value = "client"
    }

    fun registerClient(name: String, city: String, state: String) {
        _isRegistered.value = true
    }

    fun registerLawyerSubmit() {
        _lawyerVerificationStatus.value = "pending"
    }

    fun setLawyerOnline(isOnline: Boolean) {
        _lawyerIsOnline.value = isOnline
    }

    fun acceptLead(leadId: String) {
        val currentList = _lawyerLeads.value
        val lead = currentList.find { it.id == leadId }
        if (lead != null) {
            val updatedLead = lead.copy(status = "Accepted")
            _lawyerLeads.value = currentList.map { if (it.id == leadId) updatedLead else it }
            _lawyerAcceptedConsultations.value = _lawyerAcceptedConsultations.value + updatedLead
            val numericFee = lead.fee.replace("₹", "").trim().toDoubleOrNull() ?: 0.0
            _lawyerEarnings.value = _lawyerEarnings.value + numericFee
        }
    }

    fun declineLead(leadId: String) {
        val currentList = _lawyerLeads.value
        val lead = currentList.find { it.id == leadId }
        if (lead != null) {
            val updatedLead = lead.copy(status = "Declined")
            _lawyerLeads.value = currentList.map { if (it.id == leadId) updatedLead else it }
        }
    }

    fun adminApproveLawyer() {
        _lawyerVerificationStatus.value = "verified"
    }

    fun adminRejectLawyer() {
        _lawyerVerificationStatus.value = "rejected"
    }

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

data class LawyerLead(
    val id: String,
    val clientName: String,
    val caseType: String,
    val aiSummary: String,
    val applicableSection: String,
    val bailableStatus: String,
    val consultType: String,
    val fee: String,
    val timeReceived: String,
    val status: String = "Pending"
)

