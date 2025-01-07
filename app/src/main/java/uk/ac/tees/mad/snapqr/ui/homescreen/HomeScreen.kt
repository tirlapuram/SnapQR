package uk.ac.tees.mad.snapqr.ui.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.snapqr.R
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.ui.scanhistory.ScanHistoryNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanNav

object HomeNav : SnapNav {
    override val route: String = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 30.sp)
                )
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            ElevatedCard(
                onClick = { navController.navigate(ScanNav.route) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.qr_code_scan),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(100.dp)
                    )
                    Column {
                        Text(
                            text = "Scan",
                            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                        )
                        Text(
                            text = "Scan any QR code",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.circle),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(18.dp)
                            .size(80.dp)
                    )
                    Column {
                        Text(
                            text = "Favorite scans",
                            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                        )
                        Text(
                            text = "Manage your favorite scans",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                onClick = { navController.navigate(ScanHistoryNav.route) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.history),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(18.dp)
                            .size(80.dp)
                    )
                    Column {
                        Text(
                            text = "History",
                            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                        )
                        Text(
                            text = "Check your previous scans",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(18.dp)
                            .size(80.dp)
                    )
                    Column {
                        Text(
                            text = "Settings",
                            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                        )
                        Text(
                            text = "Manage your app settings",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
            }
        }
    }
}