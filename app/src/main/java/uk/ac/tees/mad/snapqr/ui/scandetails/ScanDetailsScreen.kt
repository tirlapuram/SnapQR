package uk.ac.tees.mad.snapqr.ui.scandetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.tees.mad.snapqr.SnapNav
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
    var qrContent = state.qrContent
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan Details") },
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
                .padding(16.dp)
        ) {
            Text(text = "QR Type: ${state.qrType}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                Text(text = "QR Content: ${state.qrContent}", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Favorite")
            }
        }
    }
}
