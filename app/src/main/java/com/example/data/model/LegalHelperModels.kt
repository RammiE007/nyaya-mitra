package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

// --- Room Database Entities ---

@Entity(tableName = "legal_queries")
data class LegalQuery(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val queryText: String,
    val sections: String,
    val isBailable: Boolean,
    val isCognizable: Boolean,
    val explanationHindi: String,
    val explanationEnglish: String,
    val punishment: String,
    val timelineJson: String, // Stored as a serialized JSON array
    val checklistJson: String, // Stored as a serialized JSON array
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "document_analyses")
data class DocumentAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val docType: String,
    val summaryJson: String, // Stored as a serialized JSON array
    val risksJson: String, // Stored as a serialized JSON array
    val lawReferencesJson: String, // Stored as a serialized JSON array
    val suggestedActionsJson: String, // Stored as a serialized JSON array
    val originalText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "lawyer_bookings")
data class LawyerBooking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lawyerId: String,
    val lawyerName: String,
    val lawyerSpecialty: String,
    val lawyerRating: Float,
    val lawyerImageRes: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val consultingMode: String, // Chat, Call, Video, In-Person
    val status: String, // "Upcoming", "Completed"
    val fee: String,
    val caseSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Gemini Structured AI Responses (Moshi Adapters) ---

@JsonClass(generateAdapter = true)
data class GeminiQueryResponse(
    val sections: String,
    val isBailable: Boolean,
    val isCognizable: Boolean,
    val explanationHindi: String,
    val explanationEnglish: String,
    val punishment: String,
    val timeline: List<String>,
    val checklist: List<String>
)

@JsonClass(generateAdapter = true)
data class GeminiDocResponse(
    val docType: String,
    val summary: List<String>,
    val risks: List<String>,
    val lawReferences: List<String>,
    val suggestedActions: List<String>
)

// --- Static Models ---

data class Lawyer(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Float,
    val reviewsCount: Int,
    val experienceYears: Int,
    val startingFee: String,
    val city: String,
    val languages: List<String>,
    val barId: String,
    val responseTime: String,
    val bio: String,
    val imageRes: Int // local mock illustration / avatar resource
)
