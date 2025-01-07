package uk.ac.tees.mad.snapqr.ui.favoritescan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.snapqr.data.ScanHistory
import java.util.Date

class ScanQRViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favorites = MutableStateFlow<List<ScanHistory>>(emptyList())
    val favorites= _favorites.asStateFlow()

    fun loadFavoriteScans() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("favorites")
                    .get()
                    .addOnSuccessListener { documents ->
                        val favoriteScans = documents.map { document ->
                            ScanHistory(
                                id = document.id.hashCode(),
                                qrType = document.getString("qrType") ?: "",
                                qrContent = document.getString("qrContent") ?: "",
                                scanDate = document.getDate("timestamp") ?: Date()
                            )
                        }
                        _favorites.value = favoriteScans
                    }
            }
        }
    }

    fun removeFavoriteScan(scanId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("favorites")
                    .document(scanId)
                    .delete()
                    .addOnSuccessListener {
                        loadFavoriteScans()
                    }
            }
        }
    }
}
