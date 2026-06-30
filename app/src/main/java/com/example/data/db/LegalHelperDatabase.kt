package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.LegalQuery
import com.example.data.model.DocumentAnalysis
import com.example.data.model.LawyerBooking
import kotlinx.coroutines.flow.Flow

@Dao
interface LegalQueryDao {
    @Query("SELECT * FROM legal_queries ORDER BY timestamp DESC")
    fun getAllQueries(): Flow<List<LegalQuery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(query: LegalQuery): Long

    @Update
    suspend fun updateQuery(query: LegalQuery)

    @Query("DELETE FROM legal_queries WHERE id = :id")
    suspend fun deleteQueryById(id: Int)
}

@Dao
interface DocumentAnalysisDao {
    @Query("SELECT * FROM document_analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<DocumentAnalysis>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: DocumentAnalysis): Long

    @Query("DELETE FROM document_analyses WHERE id = :id")
    suspend fun deleteAnalysisById(id: Int)
}

@Dao
interface LawyerBookingDao {
    @Query("SELECT * FROM lawyer_bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<LawyerBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: LawyerBooking): Long

    @Query("DELETE FROM lawyer_bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)
}

@Database(
    entities = [LegalQuery::class, DocumentAnalysis::class, LawyerBooking::class],
    version = 1,
    exportSchema = false
)
abstract class LegalHelperDatabase : RoomDatabase() {
    abstract fun legalQueryDao(): LegalQueryDao
    abstract fun documentAnalysisDao(): DocumentAnalysisDao
    abstract fun lawyerBookingDao(): LawyerBookingDao

    companion object {
        @Volatile
        private var INSTANCE: LegalHelperDatabase? = null

        fun getDatabase(context: Context): LegalHelperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LegalHelperDatabase::class.java,
                    "legal_helper_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
