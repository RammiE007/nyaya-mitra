package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GeminiDocResponse
import com.example.data.model.GeminiQueryResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val queryAdapter = moshi.adapter(GeminiQueryResponse::class.java)
    private val docAdapter = moshi.adapter(GeminiDocResponse::class.java)

    /**
     * Converts a Bitmap to a Base64 string for multimodal sending.
     */
    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Asks a general legal question. Maps to IPC/BNS sections and explains bilingually.
     */
    suspend fun explainLegalQuery(userQuery: String): GeminiQueryResponse? = withContext(Dispatchers.IO) {
        val systemPrompt = """
            You are an expert AI Legal Assistant specializing in Indian Law (specifically including the old Indian Penal Code (IPC), the new Bharatiya Nyaya Sanhita (BNS) 2023, Bharatiya Nagarik Suraksha Sanhita (BNSS), and Bharatiya Sakshya Adhiniyam (BSA)).
            Your goal is to analyze the user's legal situation or query, map any relevant sections from IPC to BNS (or vice versa), state if it is bailable/non-bailable and cognizable/non-cognizable under Indian law, explain the situation and punishment range in simple Hindi and simple English, give a step-by-step visual court process timeline (list of 4-5 stages in English), and outline a 3-5 item checklist of what they should do next (in English).
            
            You MUST return your response in a strict valid JSON format. Do NOT wrap the JSON in markdown code blocks like ```json ... ```. Just return the raw JSON text directly.
            
            The JSON MUST follow this exact structure and use these exact key names:
            {
              "sections": "e.g., IPC Section 354 / BNS Section 74 equivalent (Assault/criminal force to woman with intent to outrage her modesty)",
              "isBailable": false,
              "isCognizable": true,
              "explanationHindi": "हिंदी में सरल कानूनी स्पष्टीकरण...",
              "explanationEnglish": "Simple English legal explanation...",
              "punishment": "Imprisonment up to 3 years, or fine, or both.",
              "timeline": ["FIR Registration", "Investigation by Police", "Filing of Chargesheet", "Trial in Court", "Final Judgment"],
              "checklist": ["Do not delete any evidence or messages", "Prepare bail application if applicable", "Consult a criminal defense lawyer immediately"]
            }
        """.trimIndent()

        val fullPrompt = "$systemPrompt\n\nUser Situation/Query: $userQuery"
        
        try {
            val rawResponse = makeGeminiPostRequest(fullPrompt, null) ?: return@withContext null
            val parsedJson = cleanJsonString(rawResponse)
            Log.d(TAG, "Cleaned JSON Response: $parsedJson")
            return@withContext queryAdapter.fromJson(parsedJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error in explainLegalQuery", e)
            return@withContext null
        }
    }

    /**
     * Analyzes a legal document (text or visual).
     */
    suspend fun analyzeDocument(userNotes: String, bitmap: Bitmap?): GeminiDocResponse? = withContext(Dispatchers.IO) {
        val systemPrompt = """
            You are an expert AI Legal Document Analyzer specializing in Indian law contracts, legal notices, FIRs, rent agreements, and court summons.
            Analyze the provided document image and any notes provided by the user. Return a structured legal analysis in strict valid JSON format. Do NOT wrap the JSON in markdown code blocks.
            
            The JSON MUST follow this exact structure and use these exact key names:
            {
              "docType": "e.g., Rent Agreement / Police Notice / Consumer Court Summons / FIR Copy",
              "summary": ["Key Point 1", "Key Point 2", "Key Point 3"],
              "risks": ["Risk or clause to watch out for", "Action deadline mentioned in the document"],
              "lawReferences": ["Applicable Section or Act (e.g., Section 138 of NI Act)", "Relevant rule of local state Rent Control"],
              "suggestedActions": ["Reply to notice within 15 days", "Consult a property lawyer", "Prepare original identity proofs"]
            }
        """.trimIndent()

        val fullPrompt = if (userNotes.isNotEmpty()) {
            "$systemPrompt\n\nUser Notes: $userNotes\nAnalyze the document image or notes provided."
        } else {
            "$systemPrompt\nAnalyze this document."
        }

        try {
            val rawResponse = makeGeminiPostRequest(fullPrompt, bitmap) ?: return@withContext null
            val parsedJson = cleanJsonString(rawResponse)
            Log.d(TAG, "Cleaned Doc JSON Response: $parsedJson")
            return@withContext docAdapter.fromJson(parsedJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error in analyzeDocument", e)
            return@withContext null
        }
    }

    /**
     * Internal network call helper.
     */
    private fun makeGeminiPostRequest(promptText: String, bitmap: Bitmap?): String? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or default placeholder!")
            return null
        }

        val url = "$BASE_URL?key=$apiKey"

        val jsonRequest = JSONObject()
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()

        // Part 1: Prompt text
        val textPart = JSONObject().put("text", promptText)
        partsArray.put(textPart)

        // Part 2: Multimodal image (if present)
        if (bitmap != null) {
            val imageBase64 = bitmap.toBase64()
            val inlineDataObj = JSONObject()
                .put("mimeType", "image/jpeg")
                .put("data", imageBase64)
            
            val imagePart = JSONObject().put("inlineData", inlineDataObj)
            partsArray.put(imagePart)
        }

        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        jsonRequest.put("contents", contentsArray)

        // Optional: Adding generationConfig to ensure JSON output
        val generationConfig = JSONObject()
            .put("responseMimeType", "application/json")
        jsonRequest.put("generationConfig", generationConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonRequest.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Unsuccessful response: ${response.code} ${response.message}")
                    null
                } else {
                    val bodyString = response.body?.string()
                    Log.d(TAG, "Raw Response: $bodyString")
                    extractTextFromGeminiResponse(bodyString)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "API call threw exception", e)
            null
        }
    }

    /**
     * Extracts the text content from the Gemini nested JSON structure.
     */
    private fun extractTextFromGeminiResponse(bodyString: String?): String? {
        if (bodyString.isNullOrEmpty()) return null
        return try {
            val root = JSONObject(bodyString)
            val candidates = root.getJSONArray("candidates")
            val candidate = candidates.getJSONObject(0)
            val content = candidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            parts.getJSONObject(0).getString("text")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse candidate text from response: $bodyString", e)
            null
        }
    }

    /**
     * Cleans up markdown JSON indicators like ```json and ``` if they are returned by mistake.
     */
    private fun cleanJsonString(rawText: String): String {
        var cleaned = rawText.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7)
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3)
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length - 3)
        }
        return cleaned.trim()
    }
}
