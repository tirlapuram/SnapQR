package uk.ac.tees.mad.snapqr.ui.scanqr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.snapqr.data.ScanHistory
import uk.ac.tees.mad.snapqr.data.ScanHistoryDao
import java.util.Date
import javax.inject.Inject

data class ScanState(
    val qrType: String = "[TYPE]",
    val qrContent: String = "[CONTENT]"
)

@HiltViewModel
class ScanQRViewModel @Inject constructor(
    private val dao: ScanHistoryDao
) : ViewModel() {
    private val _state = MutableStateFlow(ScanState())
    val state = _state.asStateFlow()

    fun updateState(qrType: String, qrContent: String) {
        _state.update {
            it.copy(qrType = qrType, qrContent = qrContent)
        }
        viewModelScope.launch {
            val scanHistory = ScanHistory(
                qrContent = qrContent,
                qrType = qrType,
                scanDate = Date()
            )
            dao.insertScan(scanHistory)
        }
    }
}