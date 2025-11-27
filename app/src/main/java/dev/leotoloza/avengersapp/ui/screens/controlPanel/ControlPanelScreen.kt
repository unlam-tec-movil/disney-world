package dev.leotoloza.avengersapp.ui.screens.controlPanel

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.leotoloza.avengersapp.ui.viewmodels.PanelControlViewModel

@Composable
fun PanelControlScreen(
    viewModel: PanelControlViewModel,
    onNavigateToFavorites: () -> Unit,
    onLogout: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isButtonEnabled by viewModel.isRemoteConfigButtonEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0)) // Light gray background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Buttons
        ControlPanelButton(
            text = "Favoritos", backgroundColor = Color(0xFFFFF9C4), // Light Yellow
            textColor = Color.Black, onClick = onNavigateToFavorites
        )

        Spacer(modifier = Modifier.height(32.dp))

        ControlPanelButton(
            text = "Remote config",
            backgroundColor = Color(0xFFB3E5FC), // Light Blue
            textColor = Color.Black, onClick = {
                Toast.makeText(
                    context, "¡Este botón es controlado por remote config!", Toast.LENGTH_SHORT
                ).show()
            }, enabled = isButtonEnabled
        )

        Spacer(modifier = Modifier.height(32.dp))

        ControlPanelButton(
            text = "Forzar Crash", backgroundColor = Color(0xFFFFCCBC), // Light Pink/Orange
            textColor = Color.Black, onClick = { viewModel.onForceCrash() })

        Spacer(modifier = Modifier.height(32.dp))

        ControlPanelButton(
            text = "Acción Analytics",
            backgroundColor = Color.White,
            textColor = Color.Black,
            onClick = { /* TODO */ })

        Spacer(modifier = Modifier.weight(1f))

        ControlPanelButton(
            text = "Cerrar sesión", backgroundColor = Color(0xFFFFCDD2), // Light Red
            textColor = Color.Black, onClick = {
                viewModel.onLogout()
                onLogout()
            })
    }
}

@Composable
fun ControlPanelButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, disabledContainerColor = Color.Gray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp)
            .then(
                if (borderColor != null) Modifier.border(
                    2.dp, borderColor, RoundedCornerShape(16.dp)
                )
                else Modifier
            )
    ) {
        Text(
            text = text, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Medium
        )
    }
}
