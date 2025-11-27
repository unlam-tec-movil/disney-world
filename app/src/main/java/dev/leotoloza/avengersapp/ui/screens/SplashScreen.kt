package dev.leotoloza.avengersapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import dev.leotoloza.avengersapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToNextScreen: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500)
        if (FirebaseAuth.getInstance().currentUser != null) {
            onNavigateToNextScreen()
        } else {
            onNavigateToAuth()
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(
                    id = R.drawable.ic_superhero_disabled
                ),
                contentDescription = "character icon",
                tint = Color.Unspecified,
            )
            Text(
                text = stringResource(id = R.string.marvel_challenge),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(6.dp),
            )
        }
    }
}