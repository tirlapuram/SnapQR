package uk.ac.tees.mad.snapqr.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import uk.ac.tees.mad.snapqr.SnapNav
import uk.ac.tees.mad.snapqr.ui.favoritescan.FavoriteNav
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeNav
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailNav
import uk.ac.tees.mad.snapqr.ui.theme.AppBlackColor
import uk.ac.tees.mad.snapqr.ui.theme.AppBlueColor
import uk.ac.tees.mad.snapqr.ui.theme.AppFocusColor
import uk.ac.tees.mad.snapqr.ui.theme.AppUnFocusedColor
import uk.ac.tees.mad.snapqr.ui.theme.BackgroundColor


object RegisterNav : SnapNav {
    override val route: String = "register?fromFavoriteScreen={fromFavoriteScreen}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    fromFavoriteScreen: Boolean = false
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColor)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Text(
                    text = "Welcome to SnapQR!",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 28.sp,
                    color = AppBlackColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter your details to continue",
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    color = AppBlackColor
                )

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Full name") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppFocusColor,
                        unfocusedBorderColor = AppUnFocusedColor
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AppFocusColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppFocusColor,
                        unfocusedBorderColor = AppUnFocusedColor
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = AppFocusColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))
                val visibilityIcon = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppFocusColor,
                        unfocusedBorderColor = AppUnFocusedColor
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Email Icon",
                            tint = AppFocusColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = visibilityIcon, description)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            register(
                                email,
                                password,
                                name,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT)
                                        .show()
                                    if (fromFavoriteScreen) {
                                        navController.navigate(ScanDetailNav.route) {
                                            popUpTo(FavoriteNav.route) {
                                                inclusive = true
                                            }
                                        }
                                    } else {
                                        navController.navigate(HomeNav.route) {
                                            popUpTo(HomeNav.route) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        "Login failed: ${it}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLoading = false
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(AppBlueColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        text = "Login",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already have an account?",
                        color = AppBlackColor,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Login",
                        color = AppBlueColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }


                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

            }
        }
    }
}

private fun register(
    email: String,
    password: String,
    fullName: String,
    onSuccess: () -> Unit,
    onFailure: (String?) -> Unit
) {
    val userMap = mapOf(
        "email" to email,
        "password" to password,
        "name" to fullName
    )
    FirebaseAuth.getInstance()
        .createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { task ->
            if (task.user?.uid != null) {
                Firebase.firestore.collection("users")
                    .document(task.user?.uid!!).set(userMap)
                    .addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener {
                        onFailure(it.message)
                    }
            }
        }.addOnFailureListener { ex ->
            onFailure(ex.message)
        }
}
