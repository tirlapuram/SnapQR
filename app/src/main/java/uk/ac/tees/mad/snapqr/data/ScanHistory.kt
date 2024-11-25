package uk.ac.tees.mad.snapqr.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scan_history")
data class ScanHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val qrContent: String,
    val qrType: String,
    val scanDate: Date
)
