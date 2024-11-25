package uk.ac.tees.mad.snapqr.ui.scanqr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import uk.ac.tees.mad.snapqr.CAMERA_PERMISSION
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.cameraPermissionRequest
import uk.ac.tees.mad.snapqr.openPermissionSetting
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailNav
import java.util.concurrent.Executors

object ScanNav : SnapNav {
    override val route: String = "scanqr"

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRScreen(
    navController: NavHostController,
    scanQRViewModel: ScanQRViewModel,
    cameraXViewModel: CameraXViewModel = hiltViewModel()
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val state = scanQRViewModel.state.collectAsState().value
    val qrType = state.qrType
    var qrContent = state.qrContent
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

            }
        }

    LaunchedEffect(Unit) {
        when {
            (context as Activity).shouldShowRequestPermissionRationale(CAMERA_PERMISSION) -> {
                context.cameraPermissionRequest {
                    context.openPermissionSetting()
                }
            }

            else -> {
                requestPermissionLauncher.launch(CAMERA_PERMISSION)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan QR") },
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Showing camera preview using AndroidView
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    previewView.post {
                        cameraXViewModel.processCameraProvider.observe(lifecycleOwner) { cameraProvider ->
                            val preview = Preview.Builder().build().apply {
                                setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            cameraProvider?.unbindAll()
                            cameraProvider?.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview
                            )

                            val barcodeScanner = BarcodeScanning.getClient()
                            val imageAnalysis = ImageAnalysis.Builder().build()
                            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                processImage(scanQRViewModel, barcodeScanner, imageProxy, context) {
                                    navController.navigate(ScanDetailNav.route)
                                }
                            }

                            cameraProvider?.bindToLifecycle(
                                lifecycleOwner, cameraSelector, imageAnalysis
                            )
                        }
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(
                visible = qrType.isNotEmpty(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "QR Type: $qrType", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (qrType == "URL") {
                            TextButton(onClick = {
                                if (!qrContent.startsWith("http://") && !qrContent.startsWith("https://")) {
                                    qrContent = "http://$qrContent"
                                }
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(qrContent))
                                context.startActivity(browserIntent)
                            }) {
                                Text(text = qrContent, fontSize = 18.sp)
                            }
                        } else {
                            Text(text = qrContent, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImage(
    viewModel: ScanQRViewModel,
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    context: Context,
    onScanSuccess: () -> Unit
) {
    val inputImage =
        InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                val barcode = barcodes.first()
                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {
                        Log.d("QR", barcode.url?.url.toString())
                        viewModel.updateState(
                            qrType = "URL",
                            qrContent = barcode.url?.url.toString()
                        )
                    }

                    else -> {
                        viewModel.updateState(qrType = "Text", qrContent = barcode.rawValue ?: "")
                    }
                }

                // Trigger vibration
                val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        100,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

                onScanSuccess()
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}