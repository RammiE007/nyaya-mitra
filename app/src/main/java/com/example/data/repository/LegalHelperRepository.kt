package com.example.data.repository

import com.example.data.db.LegalQueryDao
import com.example.data.db.DocumentAnalysisDao
import com.example.data.db.LawyerBookingDao
import com.example.data.model.LegalQuery
import com.example.data.model.DocumentAnalysis
import com.example.data.model.LawyerBooking
import kotlinx.coroutines.flow.Flow

class LegalHelperRepository(
    private val legalQueryDao: LegalQueryDao,
    private val documentAnalysisDao: DocumentAnalysisDao,
    private val lawyerBookingDao: LawyerBookingDao
) {
    val allQueries: Flow<List<LegalQuery>> = legalQueryDao.getAllQueries()
    val allAnalyses: Flow<List<DocumentAnalysis>> = documentAnalysisDao.getAllAnalyses()
    val allBookings: Flow<List<LawyerBooking>> = lawyerBookingDao.getAllBookings()

    suspend fun insertQuery(query: LegalQuery): Long {
        return legalQueryDao.insertQuery(query)
    }

    suspend fun updateQuery(query: LegalQuery) {
        legalQueryDao.updateQuery(query)
    }

    suspend fun deleteQueryById(id: Int) {
        legalQueryDao.deleteQueryById(id)
    }

    suspend fun insertAnalysis(analysis: DocumentAnalysis): Long {
        return documentAnalysisDao.insertAnalysis(analysis)
    }

    suspend fun deleteAnalysisById(id: Int) {
        documentAnalysisDao.deleteAnalysisById(id)
    }

    suspend fun insertBooking(booking: LawyerBooking): Long {
        return lawyerBookingDao.insertBooking(booking)
    }

    suspend fun deleteBookingById(id: Int) {
        lawyerBookingDao.deleteBookingById(id)
    }
}
