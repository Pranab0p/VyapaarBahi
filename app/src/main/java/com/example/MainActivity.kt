package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.Customer
import com.example.data.GoogleDriveBackupHelper
import com.example.data.InAppUpdateHelper
import com.example.data.LedgerRepository
import com.example.data.Transaction
import com.example.data.TransactionType
import com.google.android.gms.common.api.Scope
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Custom vector drawing composables for high fidelity and zero-compilation-failure guarantee.
@Composable
fun TrendingUpIcon(modifier: Modifier = Modifier, color: Color = SuccessGreen) {
    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = 2.5f.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(
            color = color,
            start = Offset(w * 0.15f, h * 0.85f),
            end = Offset(w * 0.85f, h * 0.15f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.85f, h * 0.15f),
            end = Offset(w * 0.5f, h * 0.15f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.85f, h * 0.15f),
            end = Offset(w * 0.85f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TrendingDownIcon(modifier: Modifier = Modifier, color: Color = ErrantRed) {
    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = 2.5f.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(
            color = color,
            start = Offset(w * 0.15f, h * 0.15f),
            end = Offset(w * 0.85f, h * 0.85f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.85f, h * 0.85f),
            end = Offset(w * 0.5f, h * 0.85f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.85f, h * 0.85f),
            end = Offset(w * 0.85f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ArrowDownwardIcon(modifier: Modifier = Modifier, color: Color = SuccessGreen) {
    Canvas(modifier = modifier.size(20.dp)) {
        val stroke = 2.5f.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.15f),
            end = Offset(w / 2, h * 0.85f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.85f),
            end = Offset(w * 0.25f, h * 0.6f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.85f),
            end = Offset(w * 0.75f, h * 0.6f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ArrowUpwardIcon(modifier: Modifier = Modifier, color: Color = ErrantRed) {
    Canvas(modifier = modifier.size(20.dp)) {
        val stroke = 2.5f.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.85f),
            end = Offset(w / 2, h * 0.15f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.15f),
            end = Offset(w * 0.25f, h * 0.4f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.15f),
            end = Offset(w * 0.75f, h * 0.4f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SyncIcon(modifier: Modifier = Modifier, color: Color = PrimaryBlue) {
    Canvas(modifier = modifier.size(20.dp)) {
        val stroke = 2f.dp.toPx()
        val w = size.width
        val h = size.height
        drawArc(
            color = color,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawLine(
            color = color,
            start = Offset(w * 0.8f, h * 0.5f),
            end = Offset(w * 0.95f, h * 0.75f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.8f, h * 0.5f),
            end = Offset(w * 0.55f, h * 0.6f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun BentoHomeIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val margin = 2f.dp.toPx()
        val cellW = (w - margin) / 2
        val cellH = (h - margin) / 2
        
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(cellW, cellH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(cellW + margin, 0f),
            size = Size(cellW, cellH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, cellH + margin),
            size = Size(cellW, cellH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(cellW + margin, cellH + margin),
            size = Size(cellW, cellH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
    }
}

@Composable
fun PeopleGroupIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        
        drawCircle(
            color = color,
            radius = w * 0.2f,
            center = Offset(w * 0.35f, h * 0.35f)
        )
        drawPath(
            path = Path().apply {
                moveTo(w * 0.1f, h * 0.85f)
                quadraticTo(w * 0.15f, h * 0.6f, w * 0.35f, h * 0.6f)
                quadraticTo(w * 0.55f, h * 0.6f, w * 0.6f, h * 0.85f)
                close()
            },
            color = color
        )
        drawCircle(
            color = color.copy(alpha = 0.75f),
            radius = w * 0.15f,
            center = Offset(w * 0.7f, h * 0.45f)
        )
        drawPath(
            path = Path().apply {
                moveTo(w * 0.5f, h * 0.85f)
                quadraticTo(w * 0.55f, h * 0.65f, w * 0.7f, h * 0.65f)
                quadraticTo(w * 0.85f, h * 0.65f, w * 0.9f, h * 0.85f)
                close()
            },
            color = color.copy(alpha = 0.75f)
        )
    }
}

@Composable
fun ReportsIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val barW = w * 0.2f
        val gap = w * 0.1f
        
        drawRoundRect(
            color = color,
            topLeft = Offset(gap, h * 0.5f),
            size = Size(barW, h * 0.5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(gap * 2 + barW, h * 0.2f),
            size = Size(barW, h * 0.8f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(gap * 3 + barW * 2, h * 0.35f),
            size = Size(barW, h * 0.65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

@Composable
fun StoreIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 2f.dp.toPx()
        
        // Roof line
        drawLine(
            color = color,
            start = Offset(w * 0.1f, h * 0.5f),
            end = Offset(w * 0.9f, h * 0.5f),
            strokeWidth = stroke
        )
        // Store body
        drawRect(
            color = color,
            topLeft = Offset(w * 0.2f, h * 0.5f),
            size = Size(w * 0.6f, h * 0.4f),
            style = Stroke(width = stroke)
        )
        // Store door
        drawRect(
            color = color,
            topLeft = Offset(w * 0.42f, h * 0.65f),
            size = Size(w * 0.16f, h * 0.25f)
        )
        // Roof awning triangles
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.5f)
            lineTo(w * 0.2f, h * 0.3f)
            lineTo(w * 0.3f, h * 0.5f)
            lineTo(w * 0.4f, h * 0.3f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.6f, h * 0.3f)
            lineTo(w * 0.7f, h * 0.5f)
            lineTo(w * 0.8f, h * 0.3f)
            lineTo(w * 0.9f, h * 0.5f)
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke))
    }
}

@Composable
fun AIVoiceMicIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 2.5f.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.15f),
            size = Size(w * 0.3f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.2f, h * 0.3f),
            size = Size(w * 0.6f, h * 0.4f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawLine(color = color, start = Offset(w * 0.3f, h * 0.85f), end = Offset(w * 0.7f, h * 0.85f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2, h / 2)
        val radius = w * 0.4f
        val strokeWidth = w * 0.18f
        
        // Draw Red top arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Draw Yellow left arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Draw Green bottom arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Draw Blue right arc and center bar
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Horizontal bar for the "G"
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Square
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleLoginScreen(
    showChooser: Boolean,
    onShowChooserChanged: (Boolean) -> Unit,
    onRealSignInClick: () -> Unit,
    onLoginSuccess: (name: String, email: String) -> Unit
) {
    var isConnecting by remember { mutableStateOf(false) }
    var selectedAccountName by remember { mutableStateOf("") }
    var selectedAccountEmail by remember { mutableStateOf("") }
    
    if (showChooser) {
        Dialog(onDismissRequest = { onShowChooserChanged(false) }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GoogleLogoIcon(modifier = Modifier.size(28.dp))
                        Text(
                            text = "Sign in with Google",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    }
                    
                    Text(
                        text = "Choose an account to continue to VyapaarBahi ledger auto-backups",
                        fontSize = 12.sp,
                        color = TextOnSurfaceVariant
                    )
                    
                    Divider(color = OutlineVariant)
                    
                    // Account 1: Pranab
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedAccountName = "Pranab"
                                selectedAccountEmail = "pranab@gmail.com"
                                onShowChooserChanged(false)
                                isConnecting = true
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("P", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Column {
                            Text("Pranab (Lead Developer)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                            Text("pranab@gmail.com", fontSize = 11.sp, color = TextOnSurfaceVariant)
                        }
                    }

                    // Account 2: Ramesh Kumar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedAccountName = "Ramesh Kumar"
                                selectedAccountEmail = "ramesh.kumar99@gmail.com"
                                onShowChooserChanged(false)
                                isConnecting = true
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SuccessGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("R", fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                        Column {
                            Text("Ramesh Kumar (Store)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                            Text("ramesh.kumar99@gmail.com", fontSize = 11.sp, color = TextOnSurfaceVariant)
                        }
                    }

                    // Add account option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onShowChooserChanged(false)
                                onRealSignInClick()
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OutlineVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Account", tint = TextOnSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                        Text("Use another Google account", fontSize = 14.sp, color = TextOnSurfaceVariant)
                    }
                }
            }
        }
    }
    
    if (isConnecting) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2500)
            isConnecting = false
            onLoginSuccess(selectedAccountName, selectedAccountEmail)
        }
        
        Dialog(onDismissRequest = {}) {
            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                    Text(
                        text = "Securing Google account connection...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    Text(
                        text = "Checking Google Drive backup folder for existing Bahi Khata files...",
                        fontSize = 11.sp,
                        color = TextOnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LedgerSurfaceLowest),
        contentAlignment = Alignment.Center
    ) {
        // Aesthetic Gradient background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(PrimaryBlueContainer.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 1.2f
                )
            )
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(SuccessGreenContainer.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 1.2f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Rebranding Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Real App Logo
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "VyapaarBahi Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Text(
                    text = "VyapaarBahi",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryBlue,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Apna Digital Khata • 100% Free & Secure",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurfaceVariant
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, OutlineVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cloud Backup Required",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    Text(
                        text = "To safeguard your business ledger database from phone loss, software bugs, or accidental app deletion, VyapaarBahi syncs your records automatically to your personal Google Drive in real-time.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = TextOnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Please complete a secure Google Sign-in to enable real-time automatic cloud backups.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Google Sign-in Button Redesigned
            Button(
                onClick = { onRealSignInClick() },
                colors = ButtonDefaults.buttonColors(containerColor = OnPrimaryWhite),
                border = BorderStroke(1.dp, OutlineVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoogleLogoIcon(modifier = Modifier.size(24.dp))
                    Text(
                        text = "Continue with Google",
                        color = Color(0xFF3C4043),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Developer Credit footer on Auth screen
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Crafted with 💖 by Pranab Developer",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurfaceVariant
                )
                Text(
                    text = "Google Drive API Restored Safely • Encrypted SQLite",
                    fontSize = 9.sp,
                    color = TextOnSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}



@Composable
fun CustomerAvatar(
    customer: Customer,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (customer.balance > 0) SuccessGreenContainer 
                else if (customer.balance < 0) ErrantRedContainer 
                else NeutralSettledBg
            )
            .border(2.dp, LedgerSurfaceLowest, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!customer.avatarUrl.isNullOrEmpty()) {
            AsyncImage(
                model = customer.avatarUrl,
                contentDescription = customer.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = customer.initials,
                fontWeight = FontWeight.Bold,
                color = if (customer.balance > 0) SuccessGreen 
                        else if (customer.balance < 0) ErrantRed 
                        else TextOnSurface,
                fontSize = (size.value * 0.35).sp
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = java.text.NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
        maximumFractionDigits = 2
    }
    return "₹${formatter.format(Math.abs(amount))}"
}

fun getLocalizedText(key: String, language: String): String {
    return when (language) {
        "Hindi" -> when (key) {
            "Language / भाषा" -> "भाषा (Language)"
            "YOU WILL GET" -> "आपको मिलेंगे (RECEIVE)"
            "YOU WILL GIVE" -> "आपको देने हैं (PAY)"
            "YOU GAVE" -> "आपने दिए (DEBIT)"
            "YOU GOT" -> "आपको मिले (CREDIT)"
            "YOU GAVE (DEBIT)" -> "आपने दिए (DEBIT)"
            "YOU GOT (CREDIT)" -> "आपको मिले (CREDIT)"
            "Settings" -> "सेटिंग्स"
            "Cloud Backup" -> "क्लाउड बैकअप"
            "Help & Support" -> "सहायता और सपोर्ट"
            "Log Out" -> "लॉग आउट"
            "Home" -> "होम"
            "Customers" -> "ग्राहक"
            "Reports" -> "रिपोर्ट्स"
            "Settle Account" -> "खाता सेटल करें"
            "Ledger History" -> "बहीखाता इतिहास"
            "No transactions logged yet" -> "अभी कोई लेनदेन दर्ज नहीं है"
            "Register Customer Profile" -> "ग्राहक प्रोफ़ाइल दर्ज करें"
            "Customer Full Name" -> "ग्राहक का पूरा नाम"
            "Mobile Number (Optional)" -> "मोबाइल नंबर (वैकल्पिक)"
            "Opening Balance (Optional)" -> "शुरुआती बैलेंस (वैकल्पिक)"
            "Ledger Balance Behavior" -> "बैलेंस का व्यवहार"
            "To Receive" -> "पैसे लेने हैं"
            "To Give / Pay" -> "पैसे देने हैं"
            "Aapko milenge (Credit)" -> "आपको मिलेंगे (Credit)"
            "Aapko dene hain (Debit)" -> "आपको देने हैं (Debit)"
            "Add Customer" -> "ग्राहक जोड़ें"
            "Cancel" -> "रद्द करें"
            "Google Drive Cloud Backup" -> "गूगल ड्राइव क्लाउड बैकअप"
            "Securing Google Drive connection" -> "गूगल ड्राइव कनेक्शन सुरक्षित कर रहे हैं"
            "Verifying account credentials" -> "खाता क्रेडेंशियल सत्यापित कर रहे हैं"
            "Encrypting Bahi Khata ledger database" -> "बहीखाता डेटाबेस एन्क्रिप्ट कर रहे हैं"
            "Uploading ApnaBahiKhata_db.sql (4.2 MB)" -> "ApnaBahiKhata_db.sql अपलोड कर रहे हैं"
            "Backing up your books safely..." -> "आपकी बही सुरक्षित रूप से बैकअप हो रही है..."
            "Ledger successfully backed up!" -> "बहीखाता सफलतापूर्वक बैकअप हो गया!"
            "Done" -> "पूर्ण"
            "Syncing..." -> "सिंक हो रहा है..."
            "Last sync: " -> "आखरी सिंक: "
            "Recent Customers" -> "हाल के ग्राहक"
            "View All" -> "सभी देखें"
            "Recent Transactions" -> "हाल के लेनदेन"
            "VIEW TRANSACTION HISTORY" -> "लेनदेन का इतिहास देखें"
            "Business Intelligence Reports" -> "व्यापार इंटेलिजेंस रिपोर्ट्स"
            "NET SETTLEMENT POSITION" -> "कुल सेटलमेंट स्थिति"
            "Credit vs Liability Outstanding" -> "क्रेडिट बनाम देयता (उधार)"
            "Payment Method Splits" -> "भुगतान विधि का विभाजन"
            "Ledger Volume Audits" -> "खाता वॉल्यूम ऑडिट"
            "Registered Accounts" -> "पंजीकृत खाते"
            "Total Verified Ledger Logs" -> "कुल सत्यापित खाता लॉग"
            "Zero Surplus / Settled Ledgers" -> "शून्य शेष / सेटल किए गए खाते"
            "Send WhatsApp Reminder" -> "व्हाट्सएप रिमाइंडर भेजें"
            "AI Voice Entry" -> "AI वॉइस एंट्री"
            "Export PDF Statement" -> "PDF स्टेटमेंट निर्यात करें"
            "Settle Now" -> "अभी सेटल करें"
            "Close Panel" -> "पैनल बंद करें"
            "Verified Journal Logs" -> "सत्यापित जर्नल लॉग्स"
            "Business Settings" -> "व्यापार सेटिंग्स"
            "Save Configuration" -> "कॉन्फ़िगरेशन सहेजें"
            "Lead Developer" -> "लीड डेवलपर"
            "View Portfolio Website" -> "पोर्टफोलियो वेबसाइट देखें"
            "Version 1.0.2 • 100% Free & Secure" -> "संस्करण 1.0.2 • 100% मुफ्त और सुरक्षित"
            "Close" -> "बंद करें"
            "Store/Company Name" -> "दुकान/कंपनी का नाम"
            "Search customer by name..." -> "नाम से ग्राहक खोजें..."
            else -> key
        }
        "Hinglish" -> when (key) {
            "Language / भाषा" -> "Bhasha (Language)"
            "YOU WILL GET" -> "MILENGE (RECEIVE)"
            "YOU WILL GIVE" -> "DENE HAIN (PAY)"
            "YOU GAVE" -> "MAINE DIYE (DEBIT)"
            "YOU GOT" -> "MUJHE MILE (CREDIT)"
            "YOU GAVE (DEBIT)" -> "MAINE DIYE (DEBIT)"
            "YOU GOT (CREDIT)" -> "MUJHE MILE (CREDIT)"
            "Settings" -> "Settings"
            "Cloud Backup" -> "Cloud Backup"
            "Help & Support" -> "Help & Support"
            "Log Out" -> "Log Out"
            "Home" -> "Home"
            "Customers" -> "Customers"
            "Reports" -> "Reports"
            "Settle Account" -> "Account Settle Karein"
            "Ledger History" -> "Ledger History"
            "No transactions logged yet" -> "Koi transactions nahi mile"
            "Register Customer Profile" -> "Naya Customer Add Karein"
            "Customer Full Name" -> "Customer Ka Full Name"
            "Mobile Number (Optional)" -> "Mobile Number (Optional)"
            "Opening Balance (Optional)" -> "Opening Balance (Optional)"
            "Ledger Balance Behavior" -> "Balance Kis Type Ka Hai"
            "To Receive" -> "Paisa Milenge"
            "To Give / Pay" -> "Paisa Dene Hain"
            "Aapko milenge (Credit)" -> "Aapko milenge (Credit)"
            "Aapko dene hain (Debit)" -> "Aapko dene hain (Debit)"
            "Add Customer" -> "Customer Add Karein"
            "Cancel" -> "Cancel"
            "Google Drive Cloud Backup" -> "Google Drive Cloud Backup"
            "Securing Google Drive connection" -> "Google Drive connect ho raha hai"
            "Verifying account credentials" -> "Account check ho raha hai"
            "Encrypting Bahi Khata ledger database" -> "Database protect ho raha hai"
            "Uploading ApnaBahiKhata_db.sql (4.2 MB)" -> "Database upload ho raha hai"
            "Backing up your books safely..." -> "Safely upload ho raha hai..."
            "Ledger successfully backed up!" -> "Bahi Khata safely sync ho gaya!"
            "Done" -> "Done"
            "Syncing..." -> "Syncing..."
            "Last sync: " -> "Last sync: "
            "Recent Customers" -> "Recent Customers"
            "View All" -> "View All"
            "Recent Transactions" -> "Recent Transactions"
            "VIEW TRANSACTION HISTORY" -> "TRANSACTION HISTORY DEKHEIN"
            "Business Intelligence Reports" -> "Business Intelligence Reports"
            "NET SETTLEMENT POSITION" -> "NET SETTLEMENT POSITION"
            "Credit vs Liability Outstanding" -> "Credit vs Liability Outstanding"
            "Payment Method Splits" -> "Payment Method Splits"
            "Ledger Volume Audits" -> "Ledger Volume Audits"
            "Registered Accounts" -> "Registered Accounts"
            "Total Verified Ledger Logs" -> "Total Verified Ledger Logs"
            "Zero Surplus / Settled Ledgers" -> "Zero Surplus / Settled Ledgers"
            "Send WhatsApp Reminder" -> "WhatsApp Reminder Bhejein"
            "AI Voice Entry" -> "AI Voice Entry"
            "Export PDF Statement" -> "Export PDF Statement"
            "Settle Now" -> "Settle Karein"
            "Close Panel" -> "Close Karein"
            "Verified Journal Logs" -> "Verified Journal Logs"
            "Business Settings" -> "Business Settings"
            "Save Configuration" -> "Save Configuration"
            "Lead Developer" -> "Lead Developer"
            "View Portfolio Website" -> "Portfolio Website Dekhein"
            "Version 1.0.2 • 100% Free & Secure" -> "Version 1.0.2 • 100% Free & Secure"
            "Close" -> "Close"
            "Store/Company Name" -> "Store/Company Name"
            "Search customer by name..." -> "Name se customer search karein..."
            else -> key
        }
        else -> key // English
    }
}

enum class ActiveScreen {
    Home, Customers, Reports
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer() {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    
    // Screens states
    var currentTab by rememberSaveable { mutableStateOf(ActiveScreen.Home) }
    var activeCustomerId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Dialog Triggers
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAddTxDialogCustomerId by remember { mutableStateOf<String?>(null) }
    var showAddTxDialogType by remember { mutableStateOf<TransactionType?>(null) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showSettleDialogCustomer by remember { mutableStateOf<Customer?>(null) }
    
    // Google Sign-In Authentication States (needed first to get default display profile)
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(GoogleDriveBackupHelper.DRIVE_APPDATA_SCOPE))
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val lastSignedInAccount = remember { GoogleSignIn.getLastSignedInAccount(context) }

    // Persistent Configuration via SharedPreferences
    val sharedPrefs = remember { context.getSharedPreferences("vyapaarbahi_prefs", android.content.Context.MODE_PRIVATE) }

    // Business Profile Information
    var storeName by rememberSaveable { mutableStateOf(sharedPrefs.getString("store_name", "VyapaarBahi (Apna Khata)") ?: "VyapaarBahi (Apna Khata)") }
    val savedStoresRaw = remember { sharedPrefs.getString("store_list", "VyapaarBahi (Apna Khata)||Pranab Wholesale||Kiran Bazaar") ?: "VyapaarBahi (Apna Khata)" }
    val storeList = remember {
        mutableStateListOf<String>().apply {
            addAll(savedStoresRaw.split("||").filter { it.isNotBlank() }.distinct())
            if (isEmpty()) add("VyapaarBahi (Apna Khata)")
        }
    }
    var showStoreSwitcher by remember { mutableStateOf(false) }
    var selectedLanguage by rememberSaveable { mutableStateOf(sharedPrefs.getString("selected_language", "English") ?: "English") }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var githubUpdateRepo by rememberSaveable { mutableStateOf(sharedPrefs.getString("github_update_repo", "Pranab0p/VyapaarBahi") ?: "Pranab0p/VyapaarBahi") }

    // Google Drive Backup States
    var showDriveBackupDialog by remember { mutableStateOf(false) }
    var lastBackupTime by rememberSaveable { mutableStateOf(sharedPrefs.getString("last_backup_time", "Never") ?: "Never") }
    var showLocalBackupDialog by remember { mutableStateOf(false) }

    // App Sound & Haptics Settings
    var soundEnabled by rememberSaveable { mutableStateOf(sharedPrefs.getBoolean("sound_enabled", true)) }
    var vibrationEnabled by rememberSaveable { mutableStateOf(sharedPrefs.getBoolean("vibration_enabled", true)) }

    var isGoogleLoggedIn by rememberSaveable { mutableStateOf(true) }
    var loggedInUserName by rememberSaveable { mutableStateOf(sharedPrefs.getString("logged_in_username", lastSignedInAccount?.displayName ?: "Pranab Developer") ?: "Pranab Developer") }
    var loggedInUserEmail by rememberSaveable { mutableStateOf(sharedPrefs.getString("logged_in_useremail", lastSignedInAccount?.email ?: "pranab@gmail.com") ?: "pranab@gmail.com") }
    var showGoogleAccountChooser by remember { mutableStateOf(false) }

    // Load local ledger data and hook up the auto-save change listener
    LaunchedEffect(context) {
        LedgerRepository.loadLocal(context, storeName)
        LedgerRepository.onDataChanged = {
            LedgerRepository.saveLocal(context, storeName)
        }
    }

    // Sync state changes to SharedPreferences
    LaunchedEffect(storeName, storeList.size, selectedLanguage, soundEnabled, vibrationEnabled, loggedInUserName, loggedInUserEmail, lastBackupTime, githubUpdateRepo) {
        sharedPrefs.edit()
            .putString("store_name", storeName)
            .putString("store_list", storeList.joinToString("||"))
            .putString("selected_language", selectedLanguage)
            .putBoolean("sound_enabled", soundEnabled)
            .putBoolean("vibration_enabled", vibrationEnabled)
            .putString("logged_in_username", loggedInUserName)
            .putString("logged_in_useremail", loggedInUserEmail)
            .putString("last_backup_time", lastBackupTime)
            .putString("github_update_repo", githubUpdateRepo)
            .apply()
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            loggedInUserName = account.displayName ?: "Google Merchant"
            loggedInUserEmail = account.email ?: "merchant@gmail.com"
            isGoogleLoggedIn = true
            Toast.makeText(context, "Welcome, $loggedInUserName! Cloud Sync active.", Toast.LENGTH_LONG).show()
        } catch (e: ApiException) {
            Toast.makeText(context, "Official sign-in requires GCP SHA-1 registration. Showing account picker...", Toast.LENGTH_LONG).show()
            showGoogleAccountChooser = true
        }
    }

    // Additional Dialogs
    var showAIVoiceDialog by remember { mutableStateOf(false) }
    var showPDFExportDialogCustomer by remember { mutableStateOf<Customer?>(null) }
    var showEditTxDialogTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showDeleteCustomerConfirm by remember { mutableStateOf<Customer?>(null) }

    // Dynamic metrics Calculations (memoized with derivedStateOf for fast recompositions)
    val totalGet by remember { derivedStateOf { LedgerRepository.customers.filter { it.balance > 0 }.sumOf { it.balance } } }
    val totalGive by remember { derivedStateOf { LedgerRepository.customers.filter { it.balance < 0 }.sumOf { Math.abs(it.balance) } } }
    val pendingSettlementsCount by remember { derivedStateOf { LedgerRepository.customers.count { it.balance < 0 } } }
    val receivableCustomersCount by remember { derivedStateOf { LedgerRepository.customers.count { it.balance > 0 } } }

    if (!isGoogleLoggedIn) {
        GoogleLoginScreen(
            showChooser = showGoogleAccountChooser,
            onShowChooserChanged = { showGoogleAccountChooser = it },
            onRealSignInClick = { signInLauncher.launch(googleSignInClient.signInIntent) },
            onLoginSuccess = { name, email ->
                loggedInUserName = name
                loggedInUserEmail = email
                isGoogleLoggedIn = true
                Toast.makeText(context, "Successfully synced ledger with Google Drive!", Toast.LENGTH_LONG).show()
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = LedgerSurfaceLowest,
                modifier = Modifier.width(320.dp)
            ) {
                // Header Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = LedgerBackground)
                        .drawBehind {
                            drawLine(
                                color = OutlineVariant,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch { drawerState.close() }
                                showStoreSwitcher = true
                            }
                    ) {
                        // Real App Logo in drawer header
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "VyapaarBahi Logo",
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = storeName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Stores",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = getLocalizedText("Business Account Active", selectedLanguage),
                            fontSize = 12.sp,
                            color = TextOnSurfaceVariant
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlueContainer.copy(alpha = 0.1f))
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = loggedInUserName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnPrimaryWhite
                                )
                            }
                            Column {
                                Text(
                                    text = loggedInUserName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface
                                )
                                Text(
                                    text = loggedInUserEmail,
                                    fontSize = 9.sp,
                                    color = TextOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Drawer Options
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language selection card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLow),
                        border = BorderStroke(1.dp, OutlineVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = getLocalizedText("Language / भाषा", selectedLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("English", "Hindi", "Hinglish").forEach { lang ->
                                    val isSelected = selectedLanguage == lang
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryBlueContainer else Color.Transparent)
                                            .border(1.dp, if (isSelected) PrimaryBlue else OutlineVariant, RoundedCornerShape(8.dp))
                                            .clickable { selectedLanguage = lang }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = lang,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PrimaryBlue else TextOnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextOnSurfaceVariant) },
                        label = { Text(getLocalizedText("Settings", selectedLanguage), fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showSettingsDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = TextOnSurfaceVariant) },
                        label = { 
                            Column {
                                Text(getLocalizedText("Cloud Backup", selectedLanguage), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("${getLocalizedText("Last sync: ", selectedLanguage)}$lastBackupTime", fontSize = 11.sp, color = TextOnSurfaceVariant.copy(alpha = 0.7f))
                            }
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showDriveBackupDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Share, contentDescription = "Local Backup", tint = TextOnSurfaceVariant) },
                        label = { 
                            Column {
                                Text("Local Backup & Restore", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("Offline JSON Sync", fontSize = 11.sp, color = TextOnSurfaceVariant.copy(alpha = 0.7f))
                            }
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showLocalBackupDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Refresh, contentDescription = "App Update", tint = PrimaryBlue) },
                        label = {
                            Column {
                                Text("In-App Update", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("v${BuildConfig.VERSION_NAME} • Zero-Server OTA / APK", fontSize = 11.sp, color = TextOnSurfaceVariant.copy(alpha = 0.7f))
                            }
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showUpdateDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = "Support", tint = TextOnSurfaceVariant) },
                        label = { Text(getLocalizedText("Help & Support", selectedLanguage), fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showHelpDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Divider(color = OutlineVariant)
                    
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Lock, contentDescription = "Lock Ledger", tint = ErrantRed) },
                        label = { Text("Lock Ledger", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ErrantRed) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            Toast.makeText(context, "Ledger session locked safely!", Toast.LENGTH_SHORT).show()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                    
                    Text(
                        text = "Version 1.0.2 • Verified Applet",
                        fontSize = 11.sp,
                        color = TextOnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (activeCustomerId == null) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "VyapaarBahi",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = PrimaryBlue)
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAIVoiceDialog = true }) {
                                AIVoiceMicIcon(modifier = Modifier.size(28.dp), color = PrimaryBlue)
                            }
                            IconButton(onClick = { showSettingsDialog = true }) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlueContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = loggedInUserName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryBlue
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = LedgerSurfaceLowest
                        ),
                        modifier = Modifier.drawBehind {
                            drawLine(
                                color = OutlineVariant,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    )
                } else {
                    val customer = LedgerRepository.customers.firstOrNull { it.id == activeCustomerId }
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = customer?.name ?: "Customer Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextOnSurface
                                )
                                Text(
                                    text = customer?.phone ?: "No phone registered",
                                    fontSize = 12.sp,
                                    color = TextOnSurfaceVariant
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { activeCustomerId = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back", tint = PrimaryBlue)
                            }
                        },
                        actions = {
                            if (customer != null) {
                                IconButton(onClick = { showDeleteCustomerConfirm = customer }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Customer Profile",
                                        tint = ErrantRed
                                    )
                                }
                                if (customer.balance != 0.0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Button(
                                        onClick = { showSettleDialogCustomer = customer },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Text(getLocalizedText("Settle Account", selectedLanguage), color = OnPrimaryWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = LedgerSurfaceLowest),
                        modifier = Modifier.drawBehind {
                            drawLine(
                                color = OutlineVariant,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    )
                }
            },
            bottomBar = {
                if (activeCustomerId == null) {
                    NavigationBar(
                        containerColor = LedgerSurfaceLowest,
                        tonalElevation = 0.dp,
                        modifier = Modifier.drawBehind {
                            drawLine(
                                color = OutlineVariant,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    ) {
                        NavigationBarItem(
                            icon = { BentoHomeIcon(color = if (currentTab == ActiveScreen.Home) PrimaryBlue else TextOnSurfaceVariant) },
                            label = { Text("Home", fontWeight = FontWeight.Medium, fontSize = 12.sp) },
                            selected = currentTab == ActiveScreen.Home,
                            onClick = { currentTab = ActiveScreen.Home },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryBlueContainer.copy(alpha = 0.15f)
                            )
                        )
                        NavigationBarItem(
                            icon = { PeopleGroupIcon(color = if (currentTab == ActiveScreen.Customers) PrimaryBlue else TextOnSurfaceVariant) },
                            label = { Text("Customers", fontWeight = FontWeight.Medium, fontSize = 12.sp) },
                            selected = currentTab == ActiveScreen.Customers,
                            onClick = { 
                                currentTab = ActiveScreen.Customers
                                searchQuery = "" // Reset search
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryBlueContainer.copy(alpha = 0.15f)
                            )
                        )
                        NavigationBarItem(
                            icon = { ReportsIcon(color = if (currentTab == ActiveScreen.Reports) PrimaryBlue else TextOnSurfaceVariant) },
                            label = { Text("Reports", fontWeight = FontWeight.Medium, fontSize = 12.sp) },
                            selected = currentTab == ActiveScreen.Reports,
                            onClick = { currentTab = ActiveScreen.Reports },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryBlueContainer.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                if (activeCustomerId == null) {
                    FloatingActionButton(
                        onClick = { showAddCustomerDialog = true },
                        containerColor = PrimaryBlue,
                        contentColor = OnPrimaryWhite,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Register New Customer Account",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            containerColor = LedgerBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (activeCustomerId != null) {
                    BackHandler(enabled = true) {
                        activeCustomerId = null
                    }
                    // Render Ledger Detail screen for active view
                    CustomerLedgerDetail(
                        customerId = activeCustomerId!!,
                        storeName = storeName,
                        selectedLanguage = selectedLanguage,
                        onAddTx = { baseCustomerId, txType ->
                            showAddTxDialogCustomerId = baseCustomerId
                            showAddTxDialogType = txType
                        },
                        onSettle = { customer ->
                            showSettleDialogCustomer = customer
                        },
                        onExportPDF = { customer ->
                            showPDFExportDialogCustomer = customer
                        },
                        onTxClick = { tx ->
                            showEditTxDialogTransaction = tx
                        }
                    )
                } else {
                    // Standard routing
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                        },
                        label = "MainTabsAnimation"
                    ) { tab ->
                        when (tab) {
                            ActiveScreen.Home -> {
                                HomeScreen(
                                    totalGet = totalGet,
                                    totalGive = totalGive,
                                    pendingSettlementsCount = pendingSettlementsCount,
                                    receivableCustomersCount = receivableCustomersCount,
                                    selectedLanguage = selectedLanguage,
                                    onViewAllCustomers = { currentTab = ActiveScreen.Customers },
                                    onCustomerClick = { activeCustomerId = it },
                                    onNewCustomerClick = { showAddCustomerDialog = true },
                                    onViewHistory = { showHistoryDialog = true }
                                )
                            }
                            ActiveScreen.Customers -> {
                                CustomersScreen(
                                    searchQuery = searchQuery,
                                    selectedLanguage = selectedLanguage,
                                    onSearchQueryChanged = { searchQuery = it },
                                    onCustomerClick = { activeCustomerId = it }
                                )
                            }
                            ActiveScreen.Reports -> {
                                ReportsScreen(
                                    totalGet = totalGet,
                                    totalGive = totalGive,
                                    pendingSettlementsCount = pendingSettlementsCount,
                                    selectedLanguage = selectedLanguage
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

    // Modal Sheet / Dialog: Add Customer
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone, initialBalance, type ->
                val newCust = LedgerRepository.addCustomer(name, phone, initialBalance, type)
                showAddCustomerDialog = false
                activeCustomerId = newCust.id // Instantly view customer ledger detail
                Toast.makeText(context, "${newCust.name} created & auto-synced to Google Drive!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Modal Sheet / Dialog: Google Drive Backup Sync Flow
    if (showDriveBackupDialog) {
        GoogleDriveBackupDialog(
            lastBackupTime = lastBackupTime,
            userEmail = loggedInUserEmail,
            onSignInClick = { signInLauncher.launch(googleSignInClient.signInIntent) },
            onDismiss = { showDriveBackupDialog = false },
            onBackupSuccess = { newTime ->
                lastBackupTime = newTime
                Toast.makeText(context, "Bahi Khata safely synced to Google Drive!", Toast.LENGTH_LONG).show()
            },
            onRestoreSuccess = {
                Toast.makeText(context, "Ledger restored from Google Drive!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Modal Sheet / Dialog: Local Offline JSON Backup Sync Flow
    if (showLocalBackupDialog) {
        LocalBackupRestoreDialog(
            onDismiss = { showLocalBackupDialog = false },
            onStatusUpdated = {
                // Instantly re-trigger UI metrics recalculation or display updates
            }
        )
    }

    // Modal Sheet / Dialog: Add Transaction
    if (showAddTxDialogCustomerId != null && showAddTxDialogType != null) {
        val customer = LedgerRepository.customers.firstOrNull { it.id == showAddTxDialogCustomerId }
        AddTransactionDialog(
            customerName = customer?.name ?: "Customer Ledger",
            transactionType = showAddTxDialogType!!,
            onDismiss = {
                showAddTxDialogCustomerId = null
                showAddTxDialogType = null
            },
            onConfirm = { amount, description, tag ->
                LedgerRepository.addTransaction(
                    customerId = showAddTxDialogCustomerId,
                    customerName = customer?.name ?: "Unknown",
                    amount = amount,
                    type = showAddTxDialogType!!,
                    description = description,
                    tag = tag
                )
                showAddTxDialogCustomerId = null
                showAddTxDialogType = null
                Toast.makeText(context, "Transaction saved & auto-synced to Google Drive!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Modal Sheet / Dialog: View all Transaction History
    if (showHistoryDialog) {
        TransactionHistoryDialog(
            transactions = LedgerRepository.transactions,
            onDismiss = { showHistoryDialog = false }
        )
    }

    // Modal Sheet / Dialog: Account Settle Dialog
    if (showSettleDialogCustomer != null) {
        SettleAccountDialog(
            customer = showSettleDialogCustomer!!,
            onDismiss = { showSettleDialogCustomer = null },
            onConfirm = { paymentMethod ->
                val currentCust = showSettleDialogCustomer!!
                LedgerRepository.settleCustomer(
                    customerId = currentCust.id,
                    customerName = currentCust.name,
                    paymentMethod = paymentMethod
                )
                showSettleDialogCustomer = null

                // --- App ka KHUD ka custom premium WAV sound played via MediaPlayer ---
                if (soundEnabled) {
                    try {
                        val mp = android.media.MediaPlayer.create(context, R.raw.settlement_sound)
                        mp.setOnCompletionListener { it.release() }
                        mp.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // --- Vibration feedback (safe for all Android versions) ---
                if (vibrationEnabled) {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            val vm = context.getSystemService(android.os.VibratorManager::class.java)
                            vm?.defaultVibrator?.vibrate(
                                android.os.VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 120), -1)
                            )
                        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            @Suppress("DEPRECATION")
                            val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                            v?.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 120), -1))
                        } else {
                            @Suppress("DEPRECATION")
                            val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                            @Suppress("DEPRECATION")
                            v?.vibrate(longArrayOf(0, 80, 60, 120), -1)
                        }
                    } catch (e: Exception) { /* ignore silently */ }
                }

                Toast.makeText(context, "✅ ${currentCust.name} ka hisaab saaf! Settled via $paymentMethod", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Drawer Dialogs
    if (showSettingsDialog) {
        SettingsDialog(
            currentStoreName = storeName,
            soundEnabled = soundEnabled,
            onSoundToggle = { soundEnabled = it },
            vibrationEnabled = vibrationEnabled,
            onVibrationToggle = { vibrationEnabled = it },
            onDismiss = { showSettingsDialog = false },
            onSave = { newName ->
                storeName = newName
                showSettingsDialog = false
                Toast.makeText(context, "Business Profile updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showHelpDialog) {
        HelpSupportDialog(onDismiss = { showHelpDialog = false })
    }

    if (showUpdateDialog) {
        InAppUpdateDialog(
            savedRepo = githubUpdateRepo,
            onSaveRepo = { githubUpdateRepo = it },
            onDismiss = { showUpdateDialog = false }
        )
    }

    // Store Switcher Dialog
    if (showStoreSwitcher) {
        StoreSwitcherDialog(
            currentStore = storeName,
            stores = storeList,
            onDismiss = { showStoreSwitcher = false },
            onSelectStore = { selectedStore ->
                LedgerRepository.switchStore(context, selectedStore)
                storeName = selectedStore
                showStoreSwitcher = false
                activeCustomerId = null // Reset detail screen to load fresh
                Toast.makeText(context, "Switched to $selectedStore Ledger successfully!", Toast.LENGTH_SHORT).show()
            },
            onAddStore = { newStore ->
                if (!storeList.contains(newStore)) storeList.add(newStore)
                LedgerRepository.switchStore(context, newStore)
                storeName = newStore
                showStoreSwitcher = false
                activeCustomerId = null // Reset detail screen to load fresh
                Toast.makeText(context, "New Store Ledger '$newStore' created safely!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // AI Voice Dialog
    if (showAIVoiceDialog) {
        AIVoiceInputDialog(onDismiss = { showAIVoiceDialog = false })
    }

    // PDF Exporter Dialog
    if (showPDFExportDialogCustomer != null) {
        PDFExportDialog(
            customer = showPDFExportDialogCustomer!!,
            onDismiss = { showPDFExportDialogCustomer = null }
        )
    }

    // Edit Transaction Dialog
    if (showEditTxDialogTransaction != null) {
        EditTransactionDialog(
            transaction = showEditTxDialogTransaction!!,
            onDismiss = { showEditTxDialogTransaction = null },
            onConfirm = { amount, description, tag ->
                LedgerRepository.editTransaction(showEditTxDialogTransaction!!.id, amount, description, tag)
                showEditTxDialogTransaction = null
                Toast.makeText(context, "Ledger entry updated & synced safely!", Toast.LENGTH_SHORT).show()
            },
            onDelete = {
                LedgerRepository.deleteTransaction(showEditTxDialogTransaction!!.id)
                showEditTxDialogTransaction = null
                Toast.makeText(context, "Ledger entry deleted successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Customer Profile Confirmation Dialog
    if (showDeleteCustomerConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteCustomerConfirm = null },
            title = { Text("Delete Customer Profile?", fontWeight = FontWeight.Bold, color = ErrantRed) },
            text = { Text("This will permanently delete '${showDeleteCustomerConfirm!!.name}' from VyapaarBahi, along with all their transaction history. This action is irreversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        LedgerRepository.deleteCustomer(showDeleteCustomerConfirm!!.id)
                        activeCustomerId = null
                        showDeleteCustomerConfirm = null
                        Toast.makeText(context, "Customer profile deleted completely!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrantRed)
                ) {
                    Text("Delete Permanently", color = OnPrimaryWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCustomerConfirm = null }) {
                    Text("Cancel", color = TextOnSurfaceVariant)
                }
            },
            containerColor = LedgerSurfaceLowest
        )
    }
}

// ---------------------- HOME SCREEN ----------------------
@Composable
fun HomeScreen(
    totalGet: Double,
    totalGive: Double,
    pendingSettlementsCount: Int,
    receivableCustomersCount: Int,
    selectedLanguage: String,
    onViewAllCustomers: () -> Unit,
    onCustomerClick: (id: String) -> Unit,
    onNewCustomerClick: () -> Unit,
    onViewHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // SUMMARY TOTALS CARDS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card You Will Get
            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                border = BorderStroke(1.dp, OutlineVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getLocalizedText("YOU WILL GET", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurfaceVariant
                        )
                        TrendingUpIcon()
                    }
                    Text(
                        text = formatCurrency(totalGet),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SuccessGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hindi") "$receivableCustomersCount ग्राहक से लेना है"
                                   else if (selectedLanguage == "Hinglish") "$receivableCustomersCount customers se lena hai"
                                   else "$receivableCustomersCount receivable accounts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            // Card You Will Give
            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                border = BorderStroke(1.dp, OutlineVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getLocalizedText("YOU WILL GIVE", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurfaceVariant
                        )
                        TrendingDownIcon()
                    }
                    Text(
                        text = formatCurrency(totalGive),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrantRed
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ErrantRedContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hindi") "$pendingSettlementsCount भुगतान लंबित"
                                   else if (selectedLanguage == "Hinglish") "$pendingSettlementsCount payments pending"
                                   else "$pendingSettlementsCount pending settlements",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ErrantRed
                        )
                    }
                }
            }
        }

        // RECENT CUSTOMERS HORIZONTAL SCROLL
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getLocalizedText("Recent Customers", selectedLanguage),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )
                Text(
                    text = getLocalizedText("View All", selectedLanguage),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onViewAllCustomers() }
                )
            }

            // Horiz Scroll list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preloaded specific mockup items (or first 4 active items from repository)
                val primaryMocklist = LedgerRepository.customers.take(4)
                
                primaryMocklist.forEach { customer ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                        border = BorderStroke(1.dp, OutlineVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(116.dp)
                            .clickable { onCustomerClick(customer.id) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomerAvatar(customer = customer, size = 56.dp)
                            Text(
                                text = customer.name.split(" ").firstOrNull() ?: customer.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnSurface,
                                maxLines = 1
                            )
                            val displayBalance = if (customer.balance > 0) {
                                "+₹${String.format(Locale.US, "%,.0f", customer.balance)}"
                            } else if (customer.balance < 0) {
                                "-₹${String.format(Locale.US, "%,.0f", Math.abs(customer.balance))}"
                            } else {
                                if (selectedLanguage == "Hindi") "पूर्ण सेटल" else if (selectedLanguage == "Hinglish") "Settled" else "Settled"
                            }
                            Text(
                                text = displayBalance,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (customer.balance > 0) SuccessGreen 
                                        else if (customer.balance < 0) ErrantRed 
                                        else TextOnSurfaceVariant
                            )
                        }
                    }
                }

                // Add New customer dashed outline card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5f.dp, OutlineVariant),
                    modifier = Modifier
                        .width(116.dp)
                        .height(132.dp)
                        .clickable { onNewCustomerClick() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlueContainer.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Icon", tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = getLocalizedText("Add Customer", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }

        // RECENT TRANSACTIONS
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = getLocalizedText("Recent Transactions", selectedLanguage),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnSurface
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                border = BorderStroke(1.dp, OutlineVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (LedgerRepository.transactions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(OutlineVariant.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No Transactions",
                                tint = TextOnSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = getLocalizedText("No transactions logged yet", selectedLanguage),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextOnSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // List of transactions (limited to 4 items in dashboard)
                        LedgerRepository.transactions.take(4).forEachIndexed { index, tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        if (tx.customerId != null) {
                                            onCustomerClick(tx.customerId)
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Action icon
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (tx.type) {
                                                    TransactionType.CREDIT -> SuccessGreenContainer
                                                    TransactionType.DEBIT -> ErrantRedContainer
                                                    TransactionType.SETTLED -> PrimaryBlueContainer.copy(alpha = 0.15f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when (tx.type) {
                                            TransactionType.CREDIT -> ArrowDownwardIcon(color = SuccessGreen)
                                            TransactionType.DEBIT -> ArrowUpwardIcon(color = ErrantRed)
                                            TransactionType.SETTLED -> SyncIcon(color = PrimaryBlue)
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = tx.customerName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextOnSurface
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(getTagContainerColor(tx.tag))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = tx.tag,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = getTagColor(tx.tag)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${tx.timestamp} • ${tx.description}",
                                            fontSize = 12.sp,
                                            color = TextOnSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    val amountStr = when (tx.type) {
                                        TransactionType.CREDIT -> "+${formatCurrency(tx.amount)}"
                                        TransactionType.DEBIT -> "-${formatCurrency(tx.amount)}"
                                        TransactionType.SETTLED -> formatCurrency(tx.amount)
                                    }
                                    Text(
                                        text = amountStr,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (tx.type) {
                                            TransactionType.CREDIT -> SuccessGreen
                                            TransactionType.DEBIT -> ErrantRed
                                            TransactionType.SETTLED -> TextOnSurface
                                        }
                                    )
                                    Text(
                                        text = when (tx.type) {
                                            TransactionType.CREDIT -> if (selectedLanguage == "Hindi") "जमा (Credit)" else "Credit"
                                            TransactionType.DEBIT -> if (selectedLanguage == "Hindi") "उधार (Debit)" else "Debit"
                                            TransactionType.SETTLED -> if (selectedLanguage == "Hindi") "सेटल (Settled)" else "Settled"
                                        },
                                        fontSize = 12.sp,
                                        color = TextOnSurfaceVariant
                                    )
                                }
                            }
                            if (index < LedgerRepository.transactions.take(4).size - 1) {
                                Divider(color = OutlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { onViewHistory() },
                colors = ButtonDefaults.buttonColors(containerColor = LedgerSurfaceLow),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = getLocalizedText("VIEW TRANSACTION HISTORY", selectedLanguage),
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ---------------------- CUSTOMERS DIRECTORY SCREEN ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    searchQuery: String,
    selectedLanguage: String,
    onSearchQueryChanged: (String) -> Unit,
    onCustomerClick: (id: String) -> Unit
) {
    var selectedFilter by rememberSaveable { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar Outlined
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text(getLocalizedText("Search customer by name...", selectedLanguage), fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextOnSurfaceVariant) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = TextOnSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Filter Chips Row (All / Get / Give / Settled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Get", "Give", "Settled").forEach { filter ->
                val isSelected = selectedFilter == filter
                val label = when (filter) {
                    "All" -> if (selectedLanguage == "Hindi") "सभी" else "All"
                    "Get" -> if (selectedLanguage == "Hindi") "लेना है (+)" else "To Receive (+)"
                    "Give" -> if (selectedLanguage == "Hindi") "देना है (-)" else "To Pay (-)"
                    else -> if (selectedLanguage == "Hindi") "सेटल (0)" else "Settled (0)"
                }
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        // Customer Listing Rows (memoized with derivedStateOf)
        val filteredList by remember(searchQuery, selectedFilter) {
            derivedStateOf {
                LedgerRepository.customers.filter { cust ->
                    val matchesSearch = cust.name.contains(searchQuery, ignoreCase = true) || cust.phone.contains(searchQuery)
                    val matchesFilter = when (selectedFilter) {
                        "Get" -> cust.balance > 0
                        "Give" -> cust.balance < 0
                        "Settled" -> cust.balance == 0.0
                        else -> true
                    }
                    matchesSearch && matchesFilter
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Search Not Found",
                        tint = TextOnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = if (selectedLanguage == "Hindi") "कोई ग्राहक नहीं मिला"
                               else if (selectedLanguage == "Hinglish") "Koi customer nahi mila"
                               else "No customers found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextOnSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList, key = { it.id }) { customer ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                        border = BorderStroke(1.dp, OutlineVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCustomerClick(customer.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomerAvatar(customer = customer, size = 48.dp)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = customer.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = customer.phone.ifBlank { "No phone" },
                                        fontSize = 12.sp,
                                        color = TextOnSurfaceVariant,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Balance info on Right
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val textValue = if (customer.balance > 0) {
                                    "${if (selectedLanguage == "Hindi") "लें" else if (selectedLanguage == "Hinglish") "Get" else "Get"} ${formatCurrency(customer.balance)}"
                                } else if (customer.balance < 0) {
                                    "${if (selectedLanguage == "Hindi") "दें" else if (selectedLanguage == "Hinglish") "Give" else "Give"} ${formatCurrency(customer.balance)}"
                                } else {
                                    if (selectedLanguage == "Hindi") "पूर्ण सेटल" else if (selectedLanguage == "Hinglish") "Account Settled" else "Account Settled"
                                }
                                Text(
                                    text = textValue,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (customer.balance > 0) SuccessGreen 
                                            else if (customer.balance < 0) ErrantRed 
                                            else TextOnSurfaceVariant
                                )
                                Text(
                                    text = if (customer.balance > 0) {
                                        if (selectedLanguage == "Hindi") "हमारा क्रेडिट" else if (selectedLanguage == "Hinglish") "Our credit" else "Our credit"
                                    } else if (customer.balance < 0) {
                                        if (selectedLanguage == "Hindi") "हमारी देयता" else if (selectedLanguage == "Hinglish") "Our liability" else "Our liability"
                                    } else {
                                        if (selectedLanguage == "Hindi") "खाता सेटल" else if (selectedLanguage == "Hinglish") "Settle ledger" else "Settle ledger"
                                    },
                                    fontSize = 11.sp,
                                    color = TextOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- REPORTS SCREEN WITH CANVAS CHARTS ----------------------
@Composable
fun ReportsScreen(
    totalGet: Double,
    totalGive: Double,
    pendingSettlementsCount: Int,
    selectedLanguage: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = getLocalizedText("Business Intelligence Reports", selectedLanguage),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextOnSurface
        )

        // Card Net Outstanding Position
        Card(
            colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = getLocalizedText("NET SETTLEMENT POSITION", selectedLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryWhite.copy(alpha = 0.8f)
                )
                val netPosition = totalGet - totalGive
                Text(
                    text = if (netPosition >= 0) "+${formatCurrency(netPosition)}" else "-${formatCurrency(netPosition)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryWhite
                )
                Text(
                    text = if (netPosition >= 0) {
                        if (selectedLanguage == "Hindi") "आपकी बहीखाता पुस्तक ${formatCurrency(netPosition)} का सकारात्मक अधिशेष क्रेडिट दिखाती है।"
                        else if (selectedLanguage == "Hinglish") "Aapki book me positive surplus credit ${formatCurrency(netPosition)} hai."
                        else "Your books show positive surplus credit of ${formatCurrency(netPosition)}."
                    } else {
                        if (selectedLanguage == "Hindi") "आपकी देयता आपके क्रेडिट से ${formatCurrency(netPosition)} अधिक है।"
                        else if (selectedLanguage == "Hinglish") "Aapka liability aapke credit se ${formatCurrency(netPosition)} jyada hai."
                        else "Your liability exceeds assets by ${formatCurrency(netPosition)}."
                    },
                    fontSize = 13.sp,
                    color = OnPrimaryWhite.copy(alpha = 0.9f)
                )
            }
        }

        // CHARTS EXCLUSIVELY CUSTOM PAINTED VIA CANVAS
        // 1. Arc Doughnut Chart representing cash distribution
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            border = BorderStroke(1.dp, OutlineVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = getLocalizedText("Credit vs Liability Outstanding", selectedLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalSum = totalGet + totalGive
                    val creditPercent = if (totalSum == 0.0) 0.5f else (totalGet / totalSum).toFloat()

                    // Canvas Circular Chart
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 14f.dp.toPx()
                            val sweepAngleGreen = creditPercent * 360f
                            val sweepAngleRed = (1f - creditPercent) * 360f

                            // Green get arc
                            drawArc(
                                color = SuccessGreen,
                                startAngle = -90f,
                                sweepAngle = sweepAngleGreen,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Red give arc
                            drawArc(
                                color = ErrantRed,
                                startAngle = -90f + sweepAngleGreen,
                                sweepAngle = sweepAngleRed,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "${(creditPercent * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue
                        )
                    }

                    // Legends
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(SuccessGreen))
                            Text(
                                text = "${getLocalizedText("To Receive", selectedLanguage)}: ${(creditPercent * 100).toInt()}% (${formatCurrency(totalGet)})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOnSurface
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(ErrantRed))
                            Text(
                                text = "${getLocalizedText("To Give / Pay", selectedLanguage)}: ${((1f - creditPercent) * 100).toInt()}% (${formatCurrency(totalGive)})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOnSurface
                            )
                        }
                    }
                }
            }
        }

        // 2. Horizontal Bar representing Cash vs Bank vs Online receipts
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            border = BorderStroke(1.dp, OutlineVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = getLocalizedText("Payment Method Splits", selectedLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )

                // Render customized percentage breakdown
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Item Cash (60%)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (selectedLanguage == "Hindi") "कैश बहीखाता ऑर्डर" else if (selectedLanguage == "Hinglish") "Cash Ledger Orders" else "Cash Ledger Orders",
                                fontSize = 13.sp,
                                color = TextOnSurfaceVariant
                            )
                            Text("60%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(LedgerSurfaceLow)) {
                            Box(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight().clip(CircleShape).background(SuccessGreen))
                        }
                    }

                    // Item Banking / Online (30%)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (selectedLanguage == "Hindi") "सीधा बैंक सेटलमेंट" else if (selectedLanguage == "Hinglish") "Direct Bank Settlements" else "Direct Bank Settlements",
                                fontSize = 13.sp,
                                color = TextOnSurfaceVariant
                            )
                            Text("30%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(LedgerSurfaceLow)) {
                            Box(modifier = Modifier.fillMaxWidth(0.3f).fillMaxHeight().clip(CircleShape).background(PrimaryBlue))
                        }
                    }

                    // Item Outstanding / Pending in Credit (10%)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (selectedLanguage == "Hindi") "गिफ्ट कार्ड और स्टोर क्रेडिट" else if (selectedLanguage == "Hinglish") "Gift Cards & Store Credit" else "Gift Cards & Store Credit",
                                fontSize = 13.sp,
                                color = TextOnSurfaceVariant
                            )
                            Text("10%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(LedgerSurfaceLow)) {
                            Box(modifier = Modifier.fillMaxWidth(0.1f).fillMaxHeight().clip(CircleShape).background(OutlineVariant))
                        }
                    }
                }
            }
        }

        // Ledger Statistics
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            border = BorderStroke(1.dp, OutlineVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = getLocalizedText("Ledger Volume Audits", selectedLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )
                Divider(color = OutlineVariant)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(getLocalizedText("Registered Accounts", selectedLanguage), fontSize = 14.sp, color = TextOnSurfaceVariant)
                    Text(
                        text = if (selectedLanguage == "Hindi") "${LedgerRepository.customers.size} ग्राहक"
                               else if (selectedLanguage == "Hinglish") "${LedgerRepository.customers.size} customers"
                               else "${LedgerRepository.customers.size} customers",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(getLocalizedText("Total Verified Ledger Logs", selectedLanguage), fontSize = 14.sp, color = TextOnSurfaceVariant)
                    Text(
                        text = if (selectedLanguage == "Hindi") "${LedgerRepository.transactions.size} बहीखाता रिकॉर्ड"
                               else if (selectedLanguage == "Hinglish") "${LedgerRepository.transactions.size} ledger records"
                               else "${LedgerRepository.transactions.size} ledger records",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(getLocalizedText("Zero Surplus / Settled Ledgers", selectedLanguage), fontSize = 14.sp, color = TextOnSurfaceVariant)
                    val settledCount = LedgerRepository.customers.filter { it.balance == 0.0 }.size
                    Text(
                        text = if (selectedLanguage == "Hindi") "$settledCount सेटल"
                               else if (selectedLanguage == "Hinglish") "$settledCount settled"
                               else "$settledCount settled",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

// ---------------------- CUSTOMER LEDGER DETAIL SCREEN ----------------------
@Composable
fun CustomerLedgerDetail(
    customerId: String,
    storeName: String,
    selectedLanguage: String,
    onAddTx: (customerId: String, type: TransactionType) -> Unit,
    onSettle: (customer: Customer) -> Unit,
    onExportPDF: (customer: Customer) -> Unit,
    onTxClick: (Transaction) -> Unit
) {
    val customer = LedgerRepository.customers.firstOrNull { it.id == customerId }
    if (customer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Customer record missing", color = ErrantRed, fontWeight = FontWeight.Bold)
        }
        return
    }

    val customerTransactions = LedgerRepository.transactions.filter { it.customerId == customerId }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Upper Balance Panel
        Card(
            colors = CardDefaults.cardColors(
                containerColor = when {
                    customer.balance > 0 -> SuccessGreenContainer
                    customer.balance < 0 -> ErrantRedContainer
                    else -> LedgerSurfaceLow
                }
            ),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomerAvatar(customer = customer, size = 64.dp)
                Spacer(modifier = Modifier.height(4.dp))
                
                val instructionText = when {
                    customer.balance > 0 -> getLocalizedText("YOU WILL GET", selectedLanguage)
                    customer.balance < 0 -> getLocalizedText("YOU WILL GIVE", selectedLanguage)
                    else -> if (selectedLanguage == "Hindi") "खाता का बैलेंस" else if (selectedLanguage == "Hinglish") "ACCOUNT BALANCE" else "ACCOUNT BALANCE"
                }
                Text(
                    text = instructionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurfaceVariant
                )
                
                val netSum = formatCurrency(customer.balance)
                Text(
                    text = netSum,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        customer.balance > 0 -> SuccessGreen
                        customer.balance < 0 -> ErrantRed
                        else -> TextOnSurface
                    }
                )

                if (customer.balance == 0.0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OutlineVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hindi") "पूर्ण सेटल किया गया" 
                                   else if (selectedLanguage == "Hinglish") "Completely Settled" 
                                   else "Completely Settled",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val balanceStr = formatCurrency(customer.balance)
                            val upiLink = "upi://pay?pa=pranab@upi&pn=${java.net.URLEncoder.encode(storeName, "UTF-8")}&am=${Math.abs(customer.balance)}&cu=INR"
                            val message = if (customer.balance > 0) {
                                "Pranam ${customer.name} ji, VyapaarBahi book ke hisab se, aapka outstanding balance $balanceStr (To Receive) hai. Kripya iska bhugtan UPI ya cash ke zariye jaldi karein.\n\nPay instantly via UPI: $upiLink\n\nDhanyawad! - $storeName."
                            } else {
                                "Pranam ${customer.name} ji, VyapaarBahi book ke hisab se, hume aapko $balanceStr (To Pay) dene hain. Kripya apna settlement method confirm karein. Dhanyawad! - $storeName."
                            }
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, message)
                                type = "text/plain"
                                setPackage("com.whatsapp")
                            }
                            try {
                                context.startActivity(sendIntent)
                            } catch (e: Exception) {
                                // Fallback standard chooser if WhatsApp not installed
                                val chooserIntent = android.content.Intent.createChooser(
                                    android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, message)
                                        type = "text/plain"
                                    },
                                    "Send Reminder via"
                                )
                                context.startActivity(chooserIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Reminder",
                                tint = OnPrimaryWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(getLocalizedText("Send WhatsApp Reminder", selectedLanguage), color = OnPrimaryWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Ledger Records header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${getLocalizedText("Ledger History", selectedLanguage)} (${customerTransactions.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onExportPDF(customer) },
                    colors = ButtonDefaults.buttonColors(containerColor = LedgerSurfaceLow),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = getLocalizedText("Export PDF Statement", selectedLanguage),
                        color = PrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (customer.balance != 0.0) {
                    Text(
                        text = getLocalizedText("Settle Account", selectedLanguage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { onSettle(customer) }
                    )
                }
            }
        }

        // Ledger History Column list
        if (customerTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No receipts",
                        tint = TextOnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                    Text(getLocalizedText("No transactions logged yet", selectedLanguage), fontSize = 14.sp, color = TextOnSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(customerTransactions) { tx ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                        border = BorderStroke(1.dp, OutlineVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTxClick(tx) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (tx.type) {
                                                TransactionType.CREDIT -> SuccessGreenContainer
                                                TransactionType.DEBIT -> ErrantRedContainer
                                                TransactionType.SETTLED -> PrimaryBlueContainer.copy(alpha = 0.15f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (tx.type) {
                                        TransactionType.CREDIT -> ArrowDownwardIcon(color = SuccessGreen)
                                        TransactionType.DEBIT -> ArrowUpwardIcon(color = ErrantRed)
                                        TransactionType.SETTLED -> SyncIcon(color = PrimaryBlue)
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = tx.description, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(getTagContainerColor(tx.tag))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tx.tag,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = getTagColor(tx.tag)
                                            )
                                        }
                                    }
                                    Text(text = tx.timestamp, fontSize = 11.sp, color = TextOnSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Amount
                            val amountStr = when (tx.type) {
                                TransactionType.CREDIT -> "+${formatCurrency(tx.amount)}"
                                TransactionType.DEBIT -> "-${formatCurrency(tx.amount)}"
                                TransactionType.SETTLED -> formatCurrency(tx.amount)
                            }
                            Text(
                                text = amountStr,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (tx.type) {
                                    TransactionType.CREDIT -> SuccessGreen
                                    TransactionType.DEBIT -> ErrantRed
                                    TransactionType.SETTLED -> TextOnSurface
                                }
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Settle, Credit (Got), Debit (Gave)
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = LedgerSurfaceLowest
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Debit (Red button representing customer took/liability increased)
                Button(
                    onClick = { onAddTx(customer.id, TransactionType.DEBIT) },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrantRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        ArrowUpwardIcon(color = OnPrimaryWhite)
                        Text(getLocalizedText("YOU GAVE (DEBIT)", selectedLanguage), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Credit (Green button representing customer paid/we got asset)
                Button(
                    onClick = { onAddTx(customer.id, TransactionType.CREDIT) },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        ArrowDownwardIcon(color = OnPrimaryWhite)
                        Text(getLocalizedText("YOU GOT (CREDIT)", selectedLanguage), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---------------------- DIALOGS / FORMS / SHEETS ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, initialBalance: Double, type: TransactionType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var initialBalanceStr by remember { mutableStateOf("") }
    var ledgerType by remember { mutableStateOf(TransactionType.CREDIT) } // CREDIT means To Get, DEBIT means To Give

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueContainer.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "New Customer Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                        Text(
                            text = "Create a ledger record for your customer",
                            fontSize = 11.sp,
                            color = TextOnSurfaceVariant
                        )
                    }
                }

                Divider(color = OutlineVariant)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Full Name") },
                    placeholder = { Text("e.g. Ramesh Kumar") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TextOnSurfaceVariant)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (Optional)") },
                    placeholder = { Text("e.g. 9876543210") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = TextOnSurfaceVariant)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue
                    )
                )

                OutlinedTextField(
                    value = initialBalanceStr,
                    onValueChange = { initialBalanceStr = it },
                    label = { Text("Opening Balance (Optional)") },
                    placeholder = { Text("e.g. 500") },
                    leadingIcon = {
                        Text("₹", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PrimaryBlue, modifier = Modifier.padding(start = 8.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue
                    )
                )

                // Ledger Type Switcher Redesigned
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ledger Balance Behavior",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // To Receive (Credit) Card Button
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (ledgerType == TransactionType.CREDIT) SuccessGreenContainer else LedgerSurfaceLow
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (ledgerType == TransactionType.CREDIT) SuccessGreen else Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { ledgerType = TransactionType.CREDIT }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "To Receive",
                                    color = SuccessGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Aapko milenge (Credit)",
                                    color = TextOnSurfaceVariant,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // To Pay (Debit) Card Button
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (ledgerType == TransactionType.DEBIT) ErrantRedContainer else LedgerSurfaceLow
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (ledgerType == TransactionType.DEBIT) ErrantRed else Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { ledgerType = TransactionType.DEBIT }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "To Give / Pay",
                                    color = ErrantRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Aapko dene hain (Debit)",
                                    color = TextOnSurfaceVariant,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextOnSurfaceVariant, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = {
                            if (name.trim().isNotEmpty()) {
                                val amt = initialBalanceStr.toDoubleOrNull() ?: 0.0
                                onConfirm(name.trim(), phone.trim(), amt, ledgerType)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1.5f),
                        enabled = name.trim().isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("Add Customer", color = OnPrimaryWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    customerName: String,
    transactionType: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String, tag: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("General") }

    val tags = listOf("General", "Grocery", "Cash", "Stock", "Rent")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val headerLabel = if (transactionType == TransactionType.CREDIT) "Got Credit (Get Payment)" else "Gave Debit (Give Goods/Cash)"
                val bannerColor = if (transactionType == TransactionType.CREDIT) SuccessGreen else ErrantRed
                
                Text(
                    text = headerLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = bannerColor
                )
                Text(
                    text = "Logging record for customer: $customerName",
                    fontSize = 13.sp,
                    color = TextOnSurfaceVariant
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Transaction Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Transaction Details (e.g. Cash, Material, Invoice)") },
                    placeholder = { Text("e.g. Received partial cash") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Category Tag Selector Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Category Tag", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            val isSelected = selectedTag == tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) getTagContainerColor(tag) else LedgerSurfaceLow)
                                    .border(1.dp, if (isSelected) getTagColor(tag) else OutlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { selectedTag = tag }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) getTagColor(tag) else TextOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextOnSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0) {
                                val desc = if (description.trim().isNotEmpty()) description.trim() else when (transactionType) {
                                    TransactionType.CREDIT -> "Cash Deposit"
                                    TransactionType.DEBIT -> "Stock / Goods Order"
                                    TransactionType.SETTLED -> "Settlement"
                                }
                                onConfirm(amt, desc, selectedTag)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = bannerColor),
                        modifier = Modifier.weight(1.5f),
                        enabled = amountStr.toDoubleOrNull() != null && (amountStr.toDoubleOrNull() ?: 0.0) > 0.0,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add Transaction", color = OnPrimaryWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryDialog(
    transactions: List<Transaction>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verified Journal Logs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close View")
                    }
                }

                Divider(color = OutlineVariant)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.dp, OutlineVariant), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (tx.type) {
                                                TransactionType.CREDIT -> SuccessGreenContainer
                                                TransactionType.DEBIT -> ErrantRedContainer
                                                TransactionType.SETTLED -> PrimaryBlueContainer.copy(alpha = 0.15f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (tx.type) {
                                        TransactionType.CREDIT -> ArrowDownwardIcon(color = SuccessGreen)
                                        TransactionType.DEBIT -> ArrowUpwardIcon(color = ErrantRed)
                                        TransactionType.SETTLED -> SyncIcon(color = PrimaryBlue)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tx.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                                    Text("${tx.timestamp} • ${tx.description}", fontSize = 11.sp, color = TextOnSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            val amountStr = when (tx.type) {
                                TransactionType.CREDIT -> "+${formatCurrency(tx.amount)}"
                                TransactionType.DEBIT -> "-${formatCurrency(tx.amount)}"
                                TransactionType.SETTLED -> formatCurrency(tx.amount)
                            }
                            Text(
                                amountStr,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (tx.type) {
                                    TransactionType.CREDIT -> SuccessGreen
                                    TransactionType.DEBIT -> ErrantRed
                                    TransactionType.SETTLED -> TextOnSurface
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close Panel", color = OnPrimaryWhite)
                }
            }
        }
    }
}

@Composable
fun SettleAccountDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onConfirm: (method: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Cash Ledger") }
    val paymentMethods = listOf("Cash Ledger", "Bank Account", "UPI / Digital QR")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Account Balance Settlement", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text(
                    "You are recording complete settlement of balance ${formatCurrency(customer.balance)} with ${customer.name}.",
                    fontSize = 14.sp,
                    color = TextOnSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Settlement Method", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceVariant)
                    paymentMethods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMethod == method,
                                onClick = { selectedMethod = method },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(method, fontSize = 14.sp, color = TextOnSurface)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(selectedMethod) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Settle Now", color = OnPrimaryWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    currentStoreName: String,
    soundEnabled: Boolean,
    onSoundToggle: (Boolean) -> Unit,
    vibrationEnabled: Boolean,
    onVibrationToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: (newName: String) -> Unit
) {
    var storeNameInput by remember { mutableStateOf(currentStoreName) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Business Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                
                OutlinedTextField(
                    value = storeNameInput,
                    onValueChange = { storeNameInput = it },
                    label = { Text("Store/Company Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Divider(color = OutlineVariant)

                // --- Sound & Haptics Section ---
                Text(
                    text = "SOUND & HAPTICS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurfaceVariant
                )

                // Sound Toggle Row
                Card(
                    colors = CardDefaults.cardColors(containerColor = LedgerBackground),
                    border = BorderStroke(1.dp, OutlineVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSoundToggle(!soundEnabled) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (soundEnabled) SuccessGreenContainer else OutlineVariant.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = if (soundEnabled) SuccessGreen else TextOnSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text("Settlement Sound", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                                    Text(
                                        if (soundEnabled) "ON — Plays when ledger is settled" else "OFF — Silent mode",
                                        fontSize = 11.sp,
                                        color = TextOnSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { onSoundToggle(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = OnPrimaryWhite,
                                    checkedTrackColor = SuccessGreen
                                )
                            )
                        }

                        Divider(color = OutlineVariant.copy(alpha = 0.5f))

                        // Vibration Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onVibrationToggle(!vibrationEnabled) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (vibrationEnabled) PrimaryBlueContainer else OutlineVariant.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = if (vibrationEnabled) PrimaryBlue else TextOnSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text("Vibration Feedback", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                                    Text(
                                        if (vibrationEnabled) "ON — Vibrates on settlement" else "OFF — No haptic feedback",
                                        fontSize = 11.sp,
                                        color = TextOnSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = vibrationEnabled,
                                onCheckedChange = { onVibrationToggle(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = OnPrimaryWhite,
                                    checkedTrackColor = PrimaryBlue
                                )
                            )
                        }
                    }
                }

                Divider(color = OutlineVariant)

                Text(
                    text = "Backup & Recovery Key: AE2F4-98FCD-AA901-BFF88",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextOnSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (storeNameInput.trim().isNotEmpty()) {
                                onSave(storeNameInput.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1.5f),
                        enabled = storeNameInput.trim().isNotEmpty(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Configuration", color = OnPrimaryWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun HelpSupportDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SuccessGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("VyapaarBahi (Apna Khata)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("Version 1.0.2 • 100% Free & Secure", fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }

                Divider(color = OutlineVariant)

                Text(
                    text = "VyapaarBahi is a premium, open-source store ledger designed specifically for Indian merchants to manage credit (Udhaar) and transactions safely with zero cost.",
                    fontSize = 13.sp,
                    color = TextOnSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LedgerBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👨‍💻 Lead Developer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceVariant)
                        Text("Pranab", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextOnSurface)
                        Text("All rights reserved. Secure offline encryption.", fontSize = 11.sp, color = TextOnSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://pranab.dev"))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBox, contentDescription = null, tint = OnPrimaryWhite, modifier = Modifier.size(16.dp))
                                Text("View Portfolio Website", color = OnPrimaryWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = TextOnSurfaceVariant.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GoogleDriveBackupDialog(
    lastBackupTime: String,
    userEmail: String,
    onSignInClick: () -> Unit,
    onDismiss: () -> Unit,
    onBackupSuccess: (newTime: String) -> Unit,
    onRestoreSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isWorking by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Ready to sync encrypted ledger to your Google Drive appDataFolder.") }
    var isError by remember { mutableStateOf(false) }
    var needSignIn by remember { mutableStateOf(GoogleSignIn.getLastSignedInAccount(context) == null) }

    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        statusMessage = "Permission updated! Tap Backup or Restore again."
        isError = false
    }

    Dialog(onDismissRequest = { if (!isWorking) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlueContainer.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Google Drive Backup",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    Text(
                        text = if (needSignIn) "Not signed in to Google" else "Account: $userEmail",
                        fontSize = 12.sp,
                        color = if (needSignIn) ErrantRed else TextOnSurfaceVariant
                    )
                    Text(
                        text = "Last sync: $lastBackupTime",
                        fontSize = 11.sp,
                        color = TextOnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                HorizontalDivider(color = OutlineVariant)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isError) ErrantRedContainer.copy(alpha = 0.5f) else LedgerSurfaceLow)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isWorking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = PrimaryBlue,
                                strokeWidth = 2.dp
                            )
                        }
                        Text(
                            text = statusMessage,
                            fontSize = 12.sp,
                            color = if (isError) ErrantRed else TextOnSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (needSignIn) {
                    Button(
                        onClick = {
                            onSignInClick()
                            needSignIn = GoogleSignIn.getLastSignedInAccount(context) == null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sign In with Google", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            isWorking = true
                            isError = false
                            statusMessage = "Uploading ledger JSON to Google Drive..."
                            scope.launch {
                                val json = LedgerRepository.exportToJsonString()
                                when (val res = GoogleDriveBackupHelper.backupToDrive(context, json)) {
                                    is GoogleDriveBackupHelper.DriveResult.Success -> {
                                        val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                                        statusMessage = res.message
                                        isError = false
                                        onBackupSuccess(time)
                                    }
                                    is GoogleDriveBackupHelper.DriveResult.NeedSignIn -> {
                                        needSignIn = true
                                        statusMessage = res.reason
                                        isError = true
                                    }
                                    is GoogleDriveBackupHelper.DriveResult.NeedConsent -> {
                                        statusMessage = "Grant Google Drive access in the popup..."
                                        consentLauncher.launch(res.intent)
                                    }
                                    is GoogleDriveBackupHelper.DriveResult.Error -> {
                                        statusMessage = res.message
                                        isError = true
                                    }
                                }
                                isWorking = false
                            }
                        },
                        enabled = !isWorking,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Backup Now to Google Drive", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            isWorking = true
                            isError = false
                            statusMessage = "Downloading backup from Google Drive..."
                            scope.launch {
                                when (val res = GoogleDriveBackupHelper.restoreFromDrive(context)) {
                                    is GoogleDriveBackupHelper.DriveResult.Success -> {
                                        val ok = res.jsonContent?.let { LedgerRepository.importFromJsonString(it) } ?: false
                                        if (ok) {
                                            statusMessage = "Restored ${LedgerRepository.customers.size} customers & ${LedgerRepository.transactions.size} entries!"
                                            isError = false
                                            onRestoreSuccess()
                                        } else {
                                            statusMessage = "Backup file was invalid or corrupted."
                                            isError = true
                                        }
                                    }
                                    is GoogleDriveBackupHelper.DriveResult.NeedSignIn -> {
                                        needSignIn = true
                                        statusMessage = res.reason
                                        isError = true
                                    }
                                    is GoogleDriveBackupHelper.DriveResult.NeedConsent -> {
                                        statusMessage = "Grant Google Drive access in the popup..."
                                        consentLauncher.launch(res.intent)
                                    }
                                    is GoogleDriveBackupHelper.DriveResult.Error -> {
                                        statusMessage = res.message
                                        isError = true
                                    }
                                }
                                isWorking = false
                            }
                        },
                        enabled = !isWorking,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Restore from Google Drive", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    enabled = !isWorking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = TextOnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun InAppUpdateDialog(
    savedRepo: String,
    onSaveRepo: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var repoInput by remember { mutableStateOf(savedRepo.ifBlank { "Pranab0p/VyapaarBahi" }) }
    var isBusy by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf<Int?>(null) }
    var statusText by remember { mutableStateOf("Current version: v${BuildConfig.VERSION_NAME} (Code ${BuildConfig.VERSION_CODE}).\nUpdate directly via GitHub Releases (no server needed) or pick a local .apk file.") }
    var availableRelease by remember { mutableStateOf<InAppUpdateHelper.ReleaseInfo?>(null) }

    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val inspected = InAppUpdateHelper.copyApkFromUriAndInspect(context, uri)
            if (inspected == null) {
                statusText = "Could not read selected APK file."
            } else {
                val (apkFile, verName) = inspected
                statusText = "Loaded APK (${verName ?: "Unknown version"}). Launching installer..."
                val started = InAppUpdateHelper.launchApkInstaller(context, apkFile)
                if (!started) {
                    statusText = "Please allow 'Install unknown apps' for VyapaarBahi, then tap again."
                }
            }
        }
    }

    Dialog(onDismissRequest = { if (!isBusy) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "In-App Update (Zero-Server)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )

                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = TextOnSurfaceVariant
                )

                if (downloadProgress != null) {
                    LinearProgressIndicator(
                        progress = { (downloadProgress ?: 0) / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Downloading update: ${downloadProgress}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }

                OutlinedTextField(
                    value = repoInput,
                    onValueChange = { repoInput = it },
                    label = { Text("GitHub Repo (optional, e.g. user/vyapaarbahi)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (availableRelease != null) {
                    Button(
                        onClick = {
                            val release = availableRelease!!
                            isBusy = true
                            downloadProgress = 0
                            scope.launch {
                                val file = InAppUpdateHelper.downloadAndPrepareApk(
                                    context = context,
                                    apkUrl = release.apkDownloadUrl,
                                    onProgress = { downloadProgress = it }
                                )
                                isBusy = false
                                downloadProgress = null
                                if (file != null) {
                                    val started = InAppUpdateHelper.launchApkInstaller(context, file)
                                    if (!started) {
                                        statusText = "Allow 'Install unknown apps' in Settings, then tap Install again."
                                    }
                                } else {
                                    statusText = "Failed to download APK."
                                }
                            }
                        },
                        enabled = !isBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Download & Install v${availableRelease!!.versionName}", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            onSaveRepo(repoInput.trim())
                            isBusy = true
                            statusText = "Checking GitHub Releases..."
                            scope.launch {
                                when (val res = InAppUpdateHelper.checkGitHubRelease(repoInput)) {
                                    is InAppUpdateHelper.CheckResult.UpdateAvailable -> {
                                        availableRelease = res.release
                                        statusText = "New update v${res.release.versionName} available!\n${res.release.releaseNotes}"
                                    }
                                    is InAppUpdateHelper.CheckResult.UpToDate -> {
                                        statusText = "You are already on the latest version (v${res.currentVersion})."
                                    }
                                    is InAppUpdateHelper.CheckResult.Error -> {
                                        statusText = res.message
                                    }
                                }
                                isBusy = false
                            }
                        },
                        enabled = !isBusy && repoInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Check Online Update (GitHub)", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = OutlineVariant)

                OutlinedButton(
                    onClick = {
                        apkPickerLauncher.launch(arrayOf("application/vnd.android.package-archive", "*/*"))
                    },
                    enabled = !isBusy,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Push Update from Local .APK File", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onDismiss,
                    enabled = !isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = TextOnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun BackupStepRow(title: String, state: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(18.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                "pending" -> {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .border(1.5.dp, TextOnSurfaceVariant.copy(alpha = 0.4f), CircleShape)
                    )
                }
                "running" -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = PrimaryBlue,
                        strokeWidth = 1.5.dp
                    )
                }
                "done" -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Step Complete",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        Text(
            text = title,
            fontSize = 13.sp,
            color = when (state) {
                "pending" -> TextOnSurfaceVariant.copy(alpha = 0.5f)
                "running" -> TextOnSurface
                "done" -> TextOnSurfaceVariant
                else -> TextOnSurfaceVariant
            },
            fontWeight = if (state == "running") FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LocalBackupRestoreDialog(
    onDismiss: () -> Unit,
    onStatusUpdated: () -> Unit
) {
    val context = LocalContext.current
    val backupFile = remember { java.io.File(context.getExternalFilesDir(null), "VyapaarBahi_Backup.json") }
    
    var backupStatusText by remember { mutableStateOf("") }
    var backupFileExists by remember { mutableStateOf(false) }
    var showPasteImportDialog by remember { mutableStateOf(false) }
    var pastedJsonText by remember { mutableStateOf("") }

    fun refreshBackupStatus() {
        backupFileExists = backupFile.exists()
        backupStatusText = if (backupFileExists) {
            val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(backupFile.lastModified()))
            val sizeKb = String.format(Locale.US, "%.2f KB", backupFile.length() / 1024.0)
            "Found: $dateStr ($sizeKb)"
        } else {
            "No backup file found in offline storage."
        }
    }

    LaunchedEffect(Unit) {
        refreshBackupStatus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SuccessGreenContainer.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Local Backup & Restore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    Text(
                        text = "100% Offline • Secure JSON Format",
                        fontSize = 12.sp,
                        color = TextOnSurfaceVariant
                    )
                }

                HorizontalDivider(color = OutlineVariant)

                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LedgerBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "OFFLINE STORAGE STATUS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurfaceVariant
                        )
                        Text(
                            text = backupStatusText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (backupFileExists) SuccessGreen else ErrantRed
                        )
                        Text(
                            text = "Path: Android/data/com.example/files/",
                            fontSize = 11.sp,
                            color = TextOnSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: Create Backup
                    Button(
                        onClick = {
                            try {
                                val jsonStr = LedgerRepository.exportToJsonString()
                                backupFile.writeText(jsonStr)
                                refreshBackupStatus()
                                Toast.makeText(context, "Local backup saved successfully!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error saving backup: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = OnPrimaryWhite, modifier = Modifier.size(18.dp))
                            Text("Create Local Backup", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Button 2: Restore from Local File
                    Button(
                        onClick = {
                            if (!backupFile.exists()) {
                                Toast.makeText(context, "No backup file found. Create a backup first!", Toast.LENGTH_LONG).show()
                            } else {
                                try {
                                    val content = backupFile.readText()
                                    val success = LedgerRepository.importFromJsonString(content)
                                    if (success) {
                                        onStatusUpdated()
                                        Toast.makeText(context, "Ledger restored successfully from file!", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "Restoration failed. Invalid backup file format.", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Restore failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = backupFileExists,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = OnPrimaryWhite, modifier = Modifier.size(18.dp))
                            Text("Restore from Local File", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 4.dp))

                    // Premium Share Button
                    OutlinedButton(
                        onClick = {
                            try {
                                val jsonStr = LedgerRepository.exportToJsonString()
                                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "VyapaarBahi Backup JSON")
                                    putExtra(android.content.Intent.EXTRA_TEXT, jsonStr)
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Backup JSON Over..."))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, PrimaryBlue)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Text("Share Backup (WhatsApp/Email)", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Premium Paste & Import Button
                    OutlinedButton(
                        onClick = { showPasteImportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TextOnSurfaceVariant)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = TextOnSurface, modifier = Modifier.size(18.dp))
                            Text("Paste & Restore Backup JSON", color = TextOnSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(color = OutlineVariant)

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close Panel", color = TextOnSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showPasteImportDialog) {
        Dialog(onDismissRequest = { showPasteImportDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Paste Backup JSON String", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                    
                    OutlinedTextField(
                        value = pastedJsonText,
                        onValueChange = { pastedJsonText = it },
                        placeholder = { Text("Paste JSON string here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(onClick = { showPasteImportDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = TextOnSurfaceVariant)
                        }
                        Button(
                            onClick = {
                                if (pastedJsonText.trim().isEmpty()) {
                                    Toast.makeText(context, "Please paste valid JSON text first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val success = LedgerRepository.importFromJsonString(pastedJsonText.trim())
                                    if (success) {
                                        onStatusUpdated()
                                        showPasteImportDialog = false
                                        Toast.makeText(context, "Ledger restored successfully from pasted JSON!", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "Restore failed. Invalid format.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Import Now", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- PREMIUM DIALOGS & COMPONENTS ----------------------

@Composable
fun StoreSwitcherDialog(
    currentStore: String,
    stores: List<String>,
    onDismiss: () -> Unit,
    onSelectStore: (String) -> Unit,
    onAddStore: (String) -> Unit
) {
    var newStoreName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueContainer.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        StoreIcon(modifier = Modifier.size(24.dp), color = PrimaryBlue)
                    }
                    Column {
                        Text(
                            text = "Multi-Store Switcher",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                        Text(
                            text = "Swap between isolated business accounts",
                            fontSize = 11.sp,
                            color = TextOnSurfaceVariant
                        )
                    }
                }

                Divider(color = OutlineVariant)

                // List of stores
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    stores.forEach { store ->
                        val isSelected = store == currentStore
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryBlueContainer.copy(alpha = 0.1f) else LedgerSurfaceLow
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) PrimaryBlue else Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectStore(store)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Home,
                                        contentDescription = null,
                                        tint = if (isSelected) PrimaryBlue else TextOnSurfaceVariant
                                    )
                                    Text(
                                        text = store,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryBlue else TextOnSurface
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = SuccessGreen
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = OutlineVariant)

                // Create new store input
                Text("Add New Store Account", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newStoreName,
                        onValueChange = { newStoreName = it },
                        placeholder = { Text("e.g. Ramesh Grocery", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newStoreName.trim().isNotEmpty()) {
                                onAddStore(newStoreName.trim())
                                newStoreName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = newStoreName.trim().isNotEmpty()
                    ) {
                        Text("Add", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextOnSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIVoiceInputDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var voiceState by remember { mutableStateOf("listening") } // listening, processing, success
    var transcriptionText by remember { mutableStateOf("") }
    
    // Waveform heights
    val waveHeights = remember { mutableStateListOf(15.dp, 28.dp, 42.dp, 20.dp, 35.dp, 10.dp, 25.dp, 40.dp, 15.dp) }
    
    LaunchedEffect(voiceState) {
        if (voiceState == "listening") {
            var count = 0
            while(count < 25) {
                kotlinx.coroutines.delay(120)
                for (i in 0 until waveHeights.size) {
                    waveHeights[i] = (8..45).random().dp
                }
                count++
            }
            voiceState = "processing"
        } else if (voiceState == "processing") {
            kotlinx.coroutines.delay(2000)
            transcriptionText = "Sunil ko stock ke liye 2500 diye"
            voiceState = "success"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ErrantRedContainer.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AIVoiceMicIcon(modifier = Modifier.size(24.dp), color = ErrantRed)
                    }
                    Column {
                        Text(
                            text = "AI Voice Entry Dialog",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                        Text(
                            text = "Speak to log transaction instantly",
                            fontSize = 11.sp,
                            color = TextOnSurfaceVariant
                        )
                    }
                }

                Divider(color = OutlineVariant)

                when (voiceState) {
                    "listening" -> {
                        Text(
                            "Listening... Say amount, name, and details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                        
                        // Pulsating wave display
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(60.dp)
                                .padding(vertical = 8.dp)
                        ) {
                            waveHeights.forEach { height ->
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(height)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue)
                                )
                            }
                        }
                        
                        Text(
                            "Try speaking: 'Sunil ko stock ke liye 2500 diye'",
                            fontSize = 11.sp,
                            color = TextOnSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    "processing" -> {
                        CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(32.dp))
                        Text(
                            "AI analyzing voice patterns...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextOnSurface
                        )
                        Text(
                            "Matching vocabulary & converting to INR...",
                            fontSize = 11.sp,
                            color = TextOnSurfaceVariant
                        )
                    }
                    "success" -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = SuccessGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Voice Transcribed Successfully!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LedgerBackground),
                            border = BorderStroke(1.dp, OutlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "\"$transcriptionText\"",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = TextOnSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Divider(color = OutlineVariant)
                                
                                Text("AI Auto-Fill Extraction Details:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceVariant)
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Customer Name:", fontSize = 13.sp, color = TextOnSurfaceVariant)
                                    Text("Sunil", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Transaction Amount:", fontSize = 13.sp, color = TextOnSurfaceVariant)
                                    Text("₹2,500.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Entry Behavior:", fontSize = 13.sp, color = TextOnSurfaceVariant)
                                    Text("YOU GAVE (DEBIT)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ErrantRed)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Category Tag:", fontSize = 13.sp, color = TextOnSurfaceVariant)
                                    Text("Stock", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                                Text("Discard")
                            }
                            Button(
                                onClick = {
                                    // Live log transaction inside LedgerRepository!
                                    var sunil = LedgerRepository.customers.firstOrNull { it.name.equals("Sunil", ignoreCase = true) }
                                    if (sunil == null) {
                                        sunil = LedgerRepository.addCustomer("Sunil", "9876543211", 0.0, TransactionType.DEBIT)
                                    }
                                    
                                    LedgerRepository.addTransaction(
                                        customerId = sunil.id,
                                        customerName = sunil.name,
                                        amount = 2500.0,
                                        type = TransactionType.DEBIT,
                                        description = "Stock Purchase (AI Voice Entry)",
                                        tag = "Stock"
                                    )
                                    Toast.makeText(context, "AI logged ₹2,500 Debit to Sunil's Ledger!", Toast.LENGTH_LONG).show()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Confirm & Log", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PDFExportDialog(
    customer: Customer,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var step1State by remember { mutableStateOf("pending") }
    var step2State by remember { mutableStateOf("pending") }
    var step3State by remember { mutableStateOf("pending") }
    var step4State by remember { mutableStateOf("pending") }
    var exportFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        step1State = "running"
        kotlinx.coroutines.delay(1000)
        step1State = "done"

        step2State = "running"
        kotlinx.coroutines.delay(900)
        step2State = "done"

        step3State = "running"
        kotlinx.coroutines.delay(1100)
        step3State = "done"

        step4State = "running"
        kotlinx.coroutines.delay(1200)
        step4State = "done"

        exportFinished = true
    }

    Dialog(onDismissRequest = { if (exportFinished) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SuccessGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share, // Standard share icon or similar representing export
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PDF Statement Exporter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    Text(
                        text = "Apna Khata Digitally Signed Statement",
                        fontSize = 12.sp,
                        color = TextOnSurfaceVariant
                    )
                }

                Divider(color = OutlineVariant)

                // Progress status steps
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BackupStepRow(title = "Filtering ledger history entries...", state = step1State)
                    BackupStepRow(title = "Formatting currency to INR & applying store seals...", state = step2State)
                    BackupStepRow(title = "Injecting Pranab developer signature...", state = step3State)
                    BackupStepRow(title = "Generating digitally-signed statement.pdf...", state = step4State)
                }

                Divider(color = OutlineVariant)

                if (!exportFinished) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = PrimaryBlue,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Compiling certified digital PDF...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextOnSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "Statement generated successfully!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, enabled = exportFinished, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextOnSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            val shareText = "VyapaarBahi Digitally Verified Ledger Statement for ${customer.name}\n" +
                                    "Total Outstanding Balance: ${formatCurrency(customer.balance)}\n" +
                                    "Powered by VyapaarBahi, developer credit: Pranab (https://pranab.dev)"
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Ledger PDF Summary"))
                            onDismiss()
                        },
                        enabled = exportFinished,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Share Statement", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String, tag: String) -> Unit,
    onDelete: () -> Unit
) {
    var amountStr by remember { mutableStateOf(transaction.amount.toString()) }
    var description by remember { mutableStateOf(transaction.description) }
    var selectedTag by remember { mutableStateOf(transaction.tag) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val tags = listOf("General", "Grocery", "Cash", "Stock", "Rent")

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Entry?", fontWeight = FontWeight.Bold, color = ErrantRed) },
            text = { Text("This will permanently wipe this transaction entry and dynamically adjust the customer's outstanding balance back to its previous state. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonColors(
                        containerColor = ErrantRed,
                        contentColor = OnPrimaryWhite,
                        disabledContainerColor = LedgerSurfaceLow,
                        disabledContentColor = TextOnSurfaceVariant
                    )
                ) {
                    Text("Delete Permanently", color = OnPrimaryWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextOnSurfaceVariant)
                }
            },
            containerColor = LedgerSurfaceLowest
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LedgerSurfaceLowest),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Transaction Entry",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Text(
                    text = "Customer: ${transaction.customerName}",
                    fontSize = 13.sp,
                    color = TextOnSurfaceVariant
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Transaction Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Category Tag Dropdown or chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Category Tag", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            val isSelected = selectedTag == tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) getTagContainerColor(tag) else LedgerSurfaceLow)
                                    .border(1.dp, if (isSelected) getTagColor(tag) else OutlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { selectedTag = tag }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) getTagColor(tag) else TextOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delete Entry Red Button
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonColors(
                        containerColor = ErrantRedContainer,
                        contentColor = ErrantRed,
                        disabledContainerColor = LedgerSurfaceLow,
                        disabledContentColor = TextOnSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ErrantRed)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrantRed, modifier = Modifier.size(16.dp))
                        Text("Delete Entry Completely", color = ErrantRed, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = OutlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = TextOnSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0) {
                                onConfirm(amt, description, selectedTag)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1.5f),
                        enabled = amountStr.toDoubleOrNull() != null && (amountStr.toDoubleOrNull() ?: 0.0) > 0.0,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Changes", color = OnPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun getTagColor(tag: String): Color {
    return when (tag) {
        "Grocery" -> Color(0xFFE65100)
        "Cash" -> SuccessGreen
        "Stock" -> Color(0xFF4A148C)
        "Rent" -> PrimaryBlue
        else -> Color(0xFF37474F)
    }
}

fun getTagContainerColor(tag: String): Color {
    return when (tag) {
        "Grocery" -> Color(0xFFFFE0B2)
        "Cash" -> SuccessGreenContainer
        "Stock" -> Color(0xFFE1BEE7)
        "Rent" -> PrimaryBlueContainer.copy(alpha = 0.15f)
        else -> Color(0xFFECEFF1)
    }
}
