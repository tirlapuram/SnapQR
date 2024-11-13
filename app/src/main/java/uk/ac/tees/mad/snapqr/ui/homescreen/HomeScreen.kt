package uk.ac.tees.mad.snapqr.ui.homescreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.ac.tees.mad.snapqr.SnapNav

object HomeNav : SnapNav {
    override val route: String = "home"
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Text(text = "Home")
}