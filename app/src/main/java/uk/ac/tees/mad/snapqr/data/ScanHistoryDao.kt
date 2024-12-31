package uk.ac.tees.mad.snapqr.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scanHistory: ScanHistory)

    @Query("SELECT * FROM scan_history ORDER BY scanDate DESC")
    suspend fun getAllScans(): List<ScanHistory>

    @Query("DELETE FROM scan_history WHERE id = :scanId")
    suspend fun deleteScanById(scanId: Int)
}
