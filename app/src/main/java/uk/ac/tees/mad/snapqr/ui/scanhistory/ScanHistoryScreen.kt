package uk.ac.tees.mad.snapqr.ui.scanhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.data.ScanHistory
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScanHistoryNav : SnapNav {
    override val route: String = "scan_history"

}

@Composable
fun ScanHistoryItem(
    scan: ScanHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(scan.scanDate.time))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = "QR Type: ${scan.qrType}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Content: ${scan.qrContent}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Scanned on: $formattedDate",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Scan")
        }
    }
    Divider(modifier = Modifier.padding(top = 8.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    navController: NavHostController,
    scanQRViewModel: ScanQRViewModel
) {
    LaunchedEffect(Unit) {
        scanQRViewModel.loadScans()
    }

    val scans = scanQRViewModel.scans.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (scans.isEmpty()) {
                Text(text = "No scans found.", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(scans) { scan ->
                        ScanHistoryItem(
                            scan,
                            onClick = {
                                scanQRViewModel.updateState(scan.qrType, scan.qrContent, false)
                                navController.navigate(ScanDetailNav.route)
                            },
                            onDelete = {
                                scanQRViewModel.deleteScan(scan.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
