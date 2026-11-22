package com.marketchartzone.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marketchartzone.app.data.model.UserProfile
import com.marketchartzone.app.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserProfile,
    viewModel: ProfileViewModel,
    onSignOut: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    var darkTheme by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(TealAccent),
            contentAlignment = Alignment.Center
        ) {
            val initials = user.displayName.take(2).uppercase()
            Text(
                text = initials.ifEmpty { "MC" },
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = NavyDark)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.displayName.ifEmpty { "Trader" },
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = user.email.ifEmpty { "trader@marketchartzone.com" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Admin Panel Entry (Visible ONLY to Admin / Creator)
        if (user.isAdmin || user.email == "admin@marketchartzone.com" || user.email == "rahulsharmasau@gmail.com") {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAdmin() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent)
                        Text("Admin Panel (Creator)", style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = GoldAccent)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldAccent)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Settings & Options Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileOptionRow(icon = Icons.Default.Edit, title = "Edit Profile") {
                    viewModel.initEdit(user)
                    viewModel.isEditProfileOpen = true
                }
                ProfileOptionRow(icon = Icons.Default.Help, title = "Help & FAQs") {
                    viewModel.isHelpOpen = true
                }
                ProfileOptionRow(icon = Icons.Default.Description, title = "Terms & Conditions") {
                    viewModel.isTermsOpen = true
                }
                ProfileOptionRow(icon = Icons.Default.PrivacyTip, title = "Privacy Policy") {
                    viewModel.isPrivacyOpen = true
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileOptionRow(icon = Icons.Default.Logout, title = "Sign Out", color = MaterialTheme.colorScheme.onBackground) {
                    onSignOut()
                }
                ProfileOptionRow(icon = Icons.Default.DeleteForever, title = "Delete Account", color = MaterialTheme.colorScheme.error) {
                    viewModel.isDeleteConfirmOpen = true
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Market Chart Zone v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
    }

    // Edit Profile Dialog
    if (viewModel.isEditProfileOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.isEditProfileOpen = false },
            title = { Text("Edit Profile") },
            text = {
                OutlinedTextField(
                    value = viewModel.editName,
                    onValueChange = { viewModel.editName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.updateProfile(user) {} }) {
                    Text("Save", color = TealAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.isEditProfileOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Help Dialog
    if (viewModel.isHelpOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.isHelpOpen = false },
            title = { Text("Help & FAQs") },
            text = { Text("Welcome to Market Chart Zone. For VIP signals support, reach out to support@marketchartzone.com or check our community channels.") },
            confirmButton = {
                TextButton(onClick = { viewModel.isHelpOpen = false }) { Text("Close", color = TealAccent) }
            }
        )
    }

    // Terms Dialog
    if (viewModel.isTermsOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.isTermsOpen = false },
            title = { Text("Terms & Conditions") },
            text = { Text("Trading involves substantial risk of loss. Market Chart Zone provides educational analysis and signals for informational purposes only.") },
            confirmButton = {
                TextButton(onClick = { viewModel.isTermsOpen = false }) { Text("Close", color = TealAccent) }
            }
        )
    }

    // Privacy Dialog
    if (viewModel.isPrivacyOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.isPrivacyOpen = false },
            title = { Text("Privacy Policy") },
            text = { Text("Your personal data is protected and secured according to industry standards. We never share your credentials.") },
            confirmButton = {
                TextButton(onClick = { viewModel.isPrivacyOpen = false }) { Text("Close", color = TealAccent) }
            }
        )
    }

    // Delete Account Confirmation
    if (viewModel.isDeleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.isDeleteConfirmOpen = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to permanently delete your Market Chart Zone account? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.isDeleteConfirmOpen = false
                    viewModel.deleteAccount(user, onSignOut)
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.isDeleteConfirmOpen = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: androidx.compose.ui.graphics.Color = TealAccent,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = color, fontWeight = FontWeight.Medium)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
