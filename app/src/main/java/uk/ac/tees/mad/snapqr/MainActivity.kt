package uk.ac.tees.mad.snapqr

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.snapqr.ui.theme.SnapQRTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scanType = intent.getStringExtra("scanType")
            val message = intent.getStringExtra("scanContent")?.substringAfterLast(":")
            Log.d("TAG", "$scanType, $message")
            SnapQRTheme {
                SnapQRNav(scanType, message)
            }
        }
    }
}

