package uk.ac.tees.mad.snapqr.ui.favoritescan

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.data.ScanHistory
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FavoriteNav : SnapNav {
    override val route: String = "favorite_scans"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScansScreen(
    navController: NavHostController,
    viewmodel: FavoriteViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewmodel.loadFavoriteScans()
    }

    val favorites = viewmodel.favorites.collectAsState().value
    val loading = viewmodel.isLoading.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Favorite Scans") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                LinearProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (favorites.isEmpty()) {
                    Text(
                        text = "No favorite scans found.",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(favorites) { scan ->
                            FavoriteScanItem(scan, viewmodel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteScanItem(scan: ScanHistory, viewmodel: FavoriteViewModel) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(scan.scanDate.time))
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "QR Type: ${scan.qrType}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Content: ", fontSize = 16.sp)
            TextButton(
                onClick = {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            if (!scan.qrContent.startsWith("http://") && !scan.qrContent.startsWith(
                                    "https://"
                                )
                            ) {
                                "http://${scan.qrContent}"
                            } else {
                                scan.qrContent
                            }
                        )
                    )
                    context.startActivity(browserIntent)
                }
            ) {
                Text(text = scan.qrContent, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Saved on: $formattedDate",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        IconButton(onClick = {
            viewmodel.removeFavoriteScan(scan.id.toString())
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Remove from Favorites")
        }
    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
}
