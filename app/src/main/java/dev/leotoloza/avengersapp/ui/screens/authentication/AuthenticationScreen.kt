package dev.leotoloza.avengersapp.ui.screens.authentication

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dev.leotoloza.avengersapp.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthenticationScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSignUp by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                scope.launch {
                    try {
                        auth.signInWithCredential(credential).await()
                        onLoginSuccess()
                    } catch (e: Exception) {
                        errorMessage = "Error al iniciar sesión con Google: ${e.message}"
                        isLoading = false
                    }
                }
            } catch (e: ApiException) {
                errorMessage = "Error al iniciar sesión con Google: ${e.message}"
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // Check if already logged in
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSignUp) "Crear Cuenta" else "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Black) },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            if (isSignUp) {
                                auth.createUserWithEmailAndPassword(email, password).await()
                            } else {
                                auth.signInWithEmailAndPassword(email, password).await()
                            }
                            onLoginSuccess()
                        } catch (e: Exception) {
                            errorMessage = e.message
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.5f),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isSignUp) "Crear Cuenta" else "Iniciar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { isSignUp = !isSignUp },
                enabled = !isLoading
            ) {
                Text(
                    text = if (isSignUp) "¿Ya tienes cuenta? Inicia sesión"
                    else "¿No tienes cuenta? Regístrate",
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Iniciar sesión con Google", color = Color.Black)
            }
        }
    }
}
