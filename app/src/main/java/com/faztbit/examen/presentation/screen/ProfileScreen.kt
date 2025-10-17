package com.faztbit.examen.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faztbit.examen.presentation.state.MedalsUiState
import com.faztbit.examen.presentation.viewmodel.MedalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToMedals: () -> Unit,
    onNavigateToMissions: () -> Unit,
    onNavigateToStreaks: () -> Unit,
    onNavigateToAlbum: () -> Unit,
    viewModel: MedalsViewModel = hiltViewModel()
) {
    val uiState: MedalsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.resumeEngine()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Mi Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        if (uiState.isEngineRunning) {
                            viewModel.stopEngine()
                        } else {
                            viewModel.startEngine()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.isEngineRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isEngineRunning) "Pausar motor" else "Iniciar motor",
                        tint = if (uiState.isEngineRunning) Color.Red else Color.Green
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Avatar Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable { viewModel.onAvatarTap() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Usuario Gamificado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Nivel de Progreso General",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Engine Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isEngineRunning) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (uiState.isEngineRunning) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = if (uiState.isEngineRunning) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isEngineRunning) "Motor de Puntos Activo" else "Motor de Puntos Pausado",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (uiState.isEngineRunning) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation Modules
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ModuleCard(
                    title = "Medallas",
                    icon = Icons.Default.EmojiEvents,
                    description = "${uiState.medals.size} medallas disponibles",
                    onClick = onNavigateToMedals,
                    isActive = true
                )
            }
            item {
                ModuleCard(
                    title = "Misiones",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    description = "Próximamente",
                    onClick = onNavigateToMissions,
                    isActive = false
                )
            }
            item {
                ModuleCard(
                    title = "Rachas",
                    icon = Icons.Default.Whatshot,
                    description = "Próximamente",
                    onClick = onNavigateToStreaks,
                    isActive = false
                )
            }
            item {
                ModuleCard(
                    title = "Álbum",
                    icon = Icons.Default.PhotoLibrary,
                    description = "Próximamente",
                    onClick = onNavigateToAlbum,
                    isActive = false
                )
            }
        }
    }

    // Reset Confirmation Dialog
    if (uiState.showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissResetConfirmation() },
            title = { Text("Reiniciar Progreso") },
            text = { Text("¿Estás seguro de que quieres reiniciar todo tu progreso? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmReset() }
                ) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissResetConfirmation() }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Error Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            viewModel.clearError()
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    isActive: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = isActive) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = if (isActive) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isActive) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) 
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}