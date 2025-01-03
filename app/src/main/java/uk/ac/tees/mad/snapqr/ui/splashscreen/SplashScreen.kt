package uk.ac.tees.mad.snapqr.ui.splashscreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.snapqr.R
import uk.ac.tees.mad.snapqr.SnapNav

object SplashNav : SnapNav {
    override val route: String = "splash"
}

@Composable
fun SplashScreen(navigateBack: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(3000L) // 3 seconds delay
        withContext(Dispatchers.Main) {
            navigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.qr_code_scan),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
        )


        Spacer(modifier = Modifier.weight(1f))
        Row {

            Text(
                text = "Welcome to ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "SnapQR",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Scan QR codes effortlessly!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen({})
}
