package uk.ac.tees.mad.snapqr.ui.scanqr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private val dao: ScanHistoryDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _state = MutableStateFlow(ScanState())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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

    fun addScanToFavorites(
        qrType: String,
        qrContent: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val favoriteData = hashMapOf(
                    "qrType" to qrType,
                    "qrContent" to qrContent,
                    "timestamp" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("favorites")
                    .add(favoriteData)
                    .addOnSuccessListener {
                        _isLoading.value = false
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        _isLoading.value = false
                        onFailure(exception)
                    }
            } else {
                onFailure(Exception("User not authenticated"))
            }
        }
    }
}