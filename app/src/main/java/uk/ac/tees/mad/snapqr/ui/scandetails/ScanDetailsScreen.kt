package uk.ac.tees.mad.snapqr.ui.scandetails

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRViewModel

object ScanDetailNav : SnapNav {
    override val route: String = "scan_details"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailsScreen(
    navController: NavHostController,
    scanQRViewModel: ScanQRViewModel
) {
    val state = scanQRViewModel.state.collectAsState().value
    val qrType = state.qrType
    val qrContent = state.qrContent
    val context = LocalContext.current
    val isLoading = scanQRViewModel.isLoading.collectAsState().value
    val scope = rememberCoroutineScope()
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null

    var showLoginDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(HomeNav.route) {
                            popUpTo(HomeNav.route) {
                                inclusive = true
                            }
                        }
                    }) {
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
                .padding(16.dp)
        ) {
            Text(text = "QR Type: $qrType", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (qrType == "URL") {
                TextButton(onClick = {
                    if (!qrContent.startsWith("http://") && !qrContent.startsWith("https://")) {
                        scanQRViewModel.updateState(qrType, qrContent = "http://$qrContent")
                    }
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(qrContent))
                    context.startActivity(browserIntent)
                }) {
                    Text(text = qrContent, fontSize = 18.sp)
                }
            } else {
                Text(text = "QR Content: $qrContent", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isUserLoggedIn) {
                        // If user is logged in, add to favorites
                        scanQRViewModel.addScanToFavorites(
                            qrType = qrType,
                            qrContent = qrContent,
                            onSuccess = {
                                scope.launch(Dispatchers.Main) {
                                    navController.navigate(HomeNav.route) {
                                        popUpTo(HomeNav.route) {
                                            inclusive = true
                                        }
                                    }
                                    Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            },
                            onFailure = { exception ->
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Failed to add to favorites: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    } else {
                        // Show login dialog if the user is not logged in
                        showLoginDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Favorite")
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                )
            }

            if (showLoginDialog) {
                LoginDialog(
                    onDismiss = { showLoginDialog = false },
                    onLoginClick = {
//                        navController.navigate("${AuthenticationNav.route}?fromFavoriteScreen=true")
                    }
                )
            }
        }
    }
}

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Login Required") },
        text = { Text("You need to log in to add scans to favorites.") },
        confirmButton = {
            TextButton(onClick = {
                onLoginClick()
                onDismiss()
            }) {
                Text("Log in")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
