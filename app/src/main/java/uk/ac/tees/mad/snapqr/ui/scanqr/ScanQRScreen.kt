package uk.ac.tees.mad.snapqr.ui.scanqr

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import uk.ac.tees.mad.snapqr.CAMERA_PERMISSION
import uk.ac.tees.mad.snapqr.MainActivity
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
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->

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
                        showNotification(
                            context,
                            "QR Scan Successful",
                            "URL",
                            "Scan content: ${barcode.url?.url}"
                        )
                    }

                    else -> {
                        viewModel.updateState(qrType = "Text", qrContent = barcode.rawValue ?: "")
                        showNotification(
                            context,
                            "QR Scan Successful",
                            "Text",
                            "Scan content: ${barcode.rawValue}"
                        )
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
        .addOnFailureListener {
            showNotification(
                context,
                title = "QR Scan Failed",
                scanType = null,
                message = "Failed to read the QR code."
            )
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

fun showNotification(context: Context, title: String, scanType: String?, message: String) {
    val channelId = "QR_SCAN_CHANNEL"

    val channel = NotificationChannel(
        channelId, "QR Scan Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Notifications for QR code scans"
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)


    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("scanType", scanType)
        putExtra("scanContent", message)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)


    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        return
    }
    NotificationManagerCompat.from(context)
        .notify(
            1001,
            if (scanType == null) notificationBuilder.build()
            else notificationBuilder.setContentIntent(
                pendingIntent
            ).build()
        )
}
