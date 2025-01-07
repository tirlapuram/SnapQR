package uk.ac.tees.mad.snapqr.ui.favoritescan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.snapqr.data.ScanHistory
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor() : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favorites = MutableStateFlow<List<ScanHistory>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadFavoriteScans() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
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
                        _isLoading.value = false
                    }.addOnFailureListener {
                        _isLoading.value = false
                        it.printStackTrace()
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
                        _isLoading.value = false
                    }.addOnFailureListener {
                        _isLoading.value = false
                        it.printStackTrace()
                    }
            }
        }
    }
}
