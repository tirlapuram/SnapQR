package uk.ac.tees.mad.snapqr.ui.scanqr

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.ac.tees.mad.snapqr.SnapNav

object ScanNav : SnapNav {
    override val route: String = "scanqr"

}

@Composable
fun ScanQRScreen() {
    Text(text = "ScanQR")
}