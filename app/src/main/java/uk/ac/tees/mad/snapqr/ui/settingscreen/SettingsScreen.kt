package uk.ac.tees.mad.snapqr.ui.settingscreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.snapqr.CAMERA_PERMISSION
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.cameraPermissionRequest
import uk.ac.tees.mad.snapqr.openPermissionSetting
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeNav
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRViewModel
import java.util.concurrent.Executors

object SettingsNav : SnapNav {
    override val route: String = "settings"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    scanQRViewModel: ScanQRViewModel
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Not Logged In"
    var name by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (user?.uid != null) {
            isLoading = true
            Firebase.firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener {
                    val data = it.data
                    name = data?.get("name") as String? ?: "No username"
                    isLoading = false
                }.addOnFailureListener {
                    it.printStackTrace()
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (user != null) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Icon",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = email, fontSize = 16.sp)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Please login to get more features.")
                        TextButton(
                            onClick = {
                                navController.navigate("login?fromFavoriteScreen=false")
                            }
                        ) {
                            Text(text = "Login")
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Clear Scan History")
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (user != null) {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Logged out!", Toast.LENGTH_SHORT).show()
                        navController.navigate(HomeNav.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Logout")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Clear Scan History") },
                text = {
                    Text("Are you sure you want to delete all scan history? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isLoading = true
                            scanQRViewModel.clearScanHistory(
                                onSuccess = {
                                    scope.launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Scan history cleared!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isLoading = false
                                        showDialog = false
                                    }
                                },
                                onFailure = { exception ->
                                    scope.launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Failed to clear history: $exception",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isLoading = false
                                        showDialog = false
                                    }
                                }
                            )
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
