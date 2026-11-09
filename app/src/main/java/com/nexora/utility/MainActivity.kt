package com.nexora.utility

import android.app.Activity
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Background = Color(0xFF080D0B)
private val SurfaceDark = Color(0xFF121B17)
private val Accent = Color(0xFF39D98A)
private val SecondaryText = Color(0xFFA2B2A9)
private enum class Page { HOME, CLEANER, STORAGE, VAULT, VPN, TOOLS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NexoraApp(this) }
    }
}

@Composable
private fun NexoraApp(activity: Activity) {
    var page by remember { mutableStateOf(Page.HOME) }
    var cacheSize by remember { mutableLongStateOf(readCacheSize(activity)) }
    var notice by remember { mutableStateOf("Your device, made simpler.") }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Background, surface = SurfaceDark, primary = Accent,
        onPrimary = Background, onBackground = Color.White, onSurface = Color.White,
        secondary = SecondaryText
    )) {
        Scaffold(containerColor = Background, bottomBar = {
            NavigationBar(containerColor = SurfaceDark) {
                listOf(Page.HOME to "Home", Page.CLEANER to "Clean", Page.STORAGE to "Storage", Page.VAULT to "Vault", Page.VPN to "VPN", Page.TOOLS to "Tools").forEach { (target, label) ->
                    NavigationBarItem(
                        selected = page == target, onClick = { page = target },
                        icon = { Text(when (target) { Page.HOME -> "⌂"; Page.CLEANER -> "✦"; Page.STORAGE -> "▤"; Page.VAULT -> "▣"; Page.VPN -> "⌁"; Page.TOOLS -> "⚙" }, fontSize = 20.sp) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Accent, selectedTextColor = Accent, indicatorColor = Accent.copy(alpha = .12f), unselectedTextColor = SecondaryText)
                    )
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                Row(Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("NEXORA", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text(when (page) { Page.HOME -> "Utility"; Page.CLEANER -> "Smart cleaner"; Page.STORAGE -> "Storage"; Page.VAULT -> "Private vault"; Page.VPN -> "VPN setup"; Page.TOOLS -> "Device tools" }, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("●", color = Accent, fontSize = 20.sp)
                }
                when (page) {
                    Page.HOME -> HomePage(cacheSize, notice) { page = it }
                    Page.CLEANER -> CleanerPage(cacheSize, notice,
                        refresh = { cacheSize = readCacheSize(activity); notice = "Cache estimate refreshed." },
                        clean = {
                            val before = readCacheSize(activity)
                            activity.cacheDir.listFiles().orEmpty().forEach { it.deleteRecursively() }
                            cacheSize = readCacheSize(activity)
                            val freed = (before - cacheSize).coerceAtLeast(0)
                            notice = "Removed " + formatBytes(freed) + " from this app's cache."
                        })
                    Page.STORAGE -> StoragePage(activity)
                    Page.VAULT -> VaultPage(activity)
                    Page.VPN -> VpnPage(activity)
                    Page.TOOLS -> ToolsPage(activity) { notice = it }
                }
            }
        }
    }
}

@Composable
private fun HomePage(cacheSize: Long, notice: String, open: (Page) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(24.dp)).padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("DEVICE CARE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("A cleaner, calmer phone.", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Simple tools that respect your files and privacy.", color = SecondaryText)
                Spacer(Modifier.height(4.dp))
                Text(notice, color = Accent, fontSize = 12.sp)
            }
        }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("App cache", formatBytes(cacheSize), Modifier.weight(1f))
            MetricCard("Cleaner", "Ready to scan", Modifier.weight(1f))
        } }
        item { FeatureRow("Smart cleaner", "Review and clear Nexora's temporary cache", "✦") { open(Page.CLEANER) } }
        item { FeatureRow("Storage analyzer", "See device storage capacity and free space", "▤") { open(Page.STORAGE) } }
        item { FeatureRow("Device tools", "Open Android's built-in device settings", "⚙") { open(Page.TOOLS) } }
        item {
            Text("Privacy first", color = Accent, fontWeight = FontWeight.SemiBold)
            Text("Nexora never silently deletes personal photos, videos, downloads or other apps' data.", color = SecondaryText, fontSize = 13.sp)
        }
    }
}

@Composable
private fun CleanerPage(cacheSize: Long, notice: String, refresh: () -> Unit, clean: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Text("SAFE CLEANUP", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text(formatBytes(cacheSize), fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("Estimated temporary cache owned by Nexora Utility", color = SecondaryText, fontSize = 13.sp)
        }
        Button(onClick = refresh, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = Accent)) { Text("Refresh scan") }
        Button(onClick = clean, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background)) { Text("Clear Nexora cache") }
        Text(notice, color = SecondaryText, fontSize = 13.sp)
        Text("Android does not let ordinary apps silently clear other apps' caches. This cleaner only removes temporary files belonging to Nexora.", color = SecondaryText, fontSize = 13.sp)
    }
}

private data class ScannedFile(val uri: Uri, val name: String, val size: Long)

@Composable
private fun StoragePage(activity: Activity) {
    var folderLabel by remember { mutableStateOf("No folder selected") }
    var files by remember { mutableStateOf<List<ScannedFile>>(emptyList()) }
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    var duplicateGroups by remember { mutableStateOf<List<DuplicateScanner.DuplicateGroup>>(emptyList()) }
    var duplicateMessage by remember { mutableStateOf("Duplicate scan checks exact file contents, not similar photos.") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var scanMessage by remember { mutableStateOf("Choose a folder to review large files. Nothing is deleted automatically.") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching {
                activity.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val root = DocumentFile.fromTreeUri(activity, uri)
                val found = mutableListOf<ScannedFile>()
                var visited = 0
                fun walk(dir: DocumentFile) {
                    if (visited >= 5000) return
                    dir.listFiles().forEach { item ->
                        visited++
                        if (item.isDirectory) walk(item)
                        else if (item.isFile) {
                            val size = item.length()
                            if (size >= 20L * 1024L * 1024L) found.add(ScannedFile(item.uri, item.name ?: "Unnamed file", size))
                        }
                    }
                }
                if (root != null) walk(root)
                files = found.sortedByDescending { it.size }.take(100)
                selected = emptySet()
                duplicateGroups = emptyList()
                folderLabel = root?.name ?: "Selected folder"
                scanMessage = "Reviewed $visited folder entries. Large-file matches are shown below."
            }.onFailure { scanMessage = "Could not read this folder. Please choose it again." }
        }
    }
    val stats = remember { runCatching { StatFs(Environment.getDataDirectory().path) }.getOrNull() }
    val total = stats?.totalBytes ?: 0L
    val free = stats?.availableBytes ?: 0L
    val used = (total - free).coerceAtLeast(0)
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete selected files?") },
            text = { Text("This permanently deletes ${selected.size} selected file(s) from the folder you chose. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    val targets = files.filter { selected.contains(it.uri.toString()) }
                    var removed = 0
                    targets.forEach { item -> if (runCatching { DocumentFile.fromSingleUri(activity, item.uri)?.delete() ?: false }.getOrDefault(false)) removed++ }
                    files = files.filterNot { selected.contains(it.uri.toString()) }
                    selected = emptySet()
                    scanMessage = "Deleted $removed of ${targets.size} selected file(s)."
                    showDeleteConfirm = false
                }) { Text("Delete", color = Color(0xFFFF7777)) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(22.dp)).padding(20.dp)) {
                Text("INTERNAL STORAGE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(12.dp))
                Text(if (total > 0) formatBytes(used) else "Unavailable", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("Used of " + formatBytes(total) + " total", color = SecondaryText)
                Spacer(Modifier.height(12.dp))
                if (total > 0) LinearProgressIndicator(progress = { used.toFloat() / total.toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Accent, trackColor = Color(0xFF29352F))
                Spacer(Modifier.height(10.dp))
                Text("Available: " + if (total > 0) formatBytes(free) else "Unavailable", color = SecondaryText, fontSize = 13.sp)
            }
        }
        item {
            Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(18.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("LARGE FILE REVIEW", color = Accent, fontWeight = FontWeight.Bold)
                Text("Choose a folder. Nexora will list files 20 MB or larger; it will never delete anything until you select files and confirm.", color = SecondaryText, fontSize = 12.sp)
                Button(onClick = { picker.launch(null) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background)) { Text("Choose folder to scan") }
                Text(folderLabel, color = SecondaryText, fontSize = 12.sp)
                Text(scanMessage, color = SecondaryText, fontSize = 12.sp)
            }
        }
        if (files.isNotEmpty()) {
            item {
                Text("Large files (${files.size})", fontWeight = FontWeight.Bold)
                Button(onClick = {
                    duplicateGroups = DuplicateScanner.findDuplicates(
                        activity.contentResolver,
                        files.map { DuplicateScanner.Candidate(it.uri, it.name, it.size) }
                    )
                    duplicateMessage = "Checked ${files.size} listed large files. Exact matches are grouped below."
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = Accent)) { Text("Find exact duplicates in listed files") }
                Text(duplicateMessage, color = SecondaryText, fontSize = 11.sp)
            }
            items(files, key = { it.uri.toString() }) { file ->
                Row(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(14.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = selected.contains(file.uri.toString()), onCheckedChange = { checked ->
                        selected = if (checked) selected + file.uri.toString() else selected - file.uri.toString()
                    })
                    Column(Modifier.weight(1f)) {
                        Text(file.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                        Text(formatBytes(file.size), color = SecondaryText, fontSize = 12.sp)
                    }
                }
            }
            if (duplicateGroups.isNotEmpty()) {
                item { Text("Exact duplicate groups (${duplicateGroups.size})", fontWeight = FontWeight.Bold) }
                items(duplicateGroups, key = { it.sha256 }) { group ->
                    Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(14.dp)).padding(12.dp)) {
                        Text("${group.items.size} identical files", color = Accent, fontWeight = FontWeight.SemiBold)
                        group.items.forEach { Text(it.displayName, color = SecondaryText, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }
            }
            item {
                Button(onClick = { showDeleteConfirm = true }, enabled = selected.isNotEmpty(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9D3434))) {
                    Text("Delete selected (${selected.size})")
                }
            }
        }
        item { Text("The scan only accesses the folder you explicitly select through Android's system file picker. It is limited to 5,000 entries per scan and displays up to 100 large files.", color = SecondaryText, fontSize = 12.sp) }
    }
}


@Composable
private fun VaultPage(activity: Activity) {
    val context = LocalContext.current
    val store = remember { VaultStore(context) }
    var entries by remember { mutableStateOf(store.entries()) }
    var message by remember { mutableStateOf("Files are encrypted with an Android Keystore AES-256-GCM key.") }
    var exportingId by remember { mutableStateOf<String?>(null) }
    val importPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { store.encrypt(uri) }
                .onSuccess {
                    entries = store.entries()
                    message = "Encrypted file added. The original file was left unchanged."
                }
                .onFailure { message = "Could not encrypt this file. Check file access and free space." }
        }
    }
    val exportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        val id = exportingId
        if (uri != null && id != null) {
            val result = runCatching {
                val temp = store.decryptToCache(id)
                try {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        temp.inputStream().use { input -> input.copyTo(output) }
                    } ?: error("Could not create destination file")
                } finally { temp.delete() }
            }
            message = if (result.isSuccess) "Decrypted copy exported. Vault copy remains encrypted." else "Export failed. Please try again."
        }
        exportingId = null
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(20.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ENCRYPTED STORAGE", color = Accent, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
                Text("Your files, encrypted.", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text("AES-256-GCM encryption with a non-exportable Android Keystore key. Original files are not removed.", color = SecondaryText, fontSize = 12.sp)
                Button(onClick = { importPicker.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background)) { Text("Add file to vault") }
                Text(message, color = SecondaryText, fontSize = 12.sp)
            }
        }
        if (entries.isEmpty()) item { Text("Your vault is empty.", color = SecondaryText) }
        items(entries, key = { it.id }) { entry ->
            Row(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(14.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Encrypted file", fontWeight = FontWeight.SemiBold)
                    Text("${formatBytes(entry.encryptedBytes)} • ${entry.id.take(8)}", color = SecondaryText, fontSize = 11.sp)
                }
                TextButton(onClick = {
                    exportingId = entry.id
                    exportPicker.launch("nexora-export-${entry.id.take(8)}.bin")
                }) { Text("Export") }
                TextButton(onClick = {
                    if (store.delete(entry.id)) {
                        entries = store.entries()
                        message = "Encrypted vault item deleted."
                    } else message = "Could not delete this vault item."
                }) { Text("Delete", color = Color(0xFFFF7777)) }
            }
        }
        item {
            Text("Security note: this version encrypts files at rest but does not yet require a separate PIN or biometric prompt to open the vault. Protect your device with Android screen lock.", color = SecondaryText, fontSize = 12.sp)
        }
    }
}

@Composable
private fun VpnPage(activity: Activity) {
    var active by remember { mutableStateOf(isVpnActive(activity)) }
    var message by remember { mutableStateOf("No provider has been selected in Nexora. Configure one in Android VPN settings.") }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(20.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ANDROID VPN", color = Accent, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
            Text(if (active) "VPN transport detected" else "No active VPN detected", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(if (active) "Android currently reports an active VPN network. Nexora does not know which provider operates it or verify its privacy/security." else "Choose and install a trusted VPN provider first. Android's built-in VPN settings can configure supported provider profiles.", color = SecondaryText, fontSize = 13.sp)
        }
        Button(onClick = {
            runCatching { activity.startActivity(Intent(Settings.ACTION_VPN_SETTINGS)) }
                .onFailure { message = "Android VPN settings are unavailable on this device." }
        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background)) {
            Text("Open Android VPN settings")
        }
        OutlinedButton(onClick = {
            active = isVpnActive(activity)
            message = if (active) "Android reports an active VPN transport." else "No active VPN transport is currently reported."
        }, modifier = Modifier.fillMaxWidth()) { Text("Refresh VPN status", color = Accent) }
        Text(message, color = SecondaryText, fontSize = 12.sp)
        Text("Provider setup: install a VPN service you trust, then follow its instructions in Android VPN settings. Enter provider credentials only in the provider's official app or Android's system settings—not in Nexora or this chat.", color = SecondaryText, fontSize = 12.sp)
        Text("Important: detecting Android's VPN transport does not verify the provider, encryption protocol, server location, DNS routing, kill switch or absence of leaks. Nexora will never claim those checks passed without dedicated verification.", color = SecondaryText, fontSize = 12.sp)
    }
}

private fun isVpnActive(activity: Activity): Boolean = runCatching {
    val manager = activity.getSystemService(ConnectivityManager::class.java) ?: return@runCatching false
    val network = manager.activeNetwork ?: return@runCatching false
    manager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
}.getOrDefault(false)

@Composable
private fun ToolsPage(activity: Activity, report: (String) -> Unit) {
    val memoryInfo = android.app.ActivityManager.MemoryInfo()
    activity.getSystemService(android.app.ActivityManager::class.java)?.getMemoryInfo(memoryInfo)
    val batteryPercent = activity.getSystemService(android.os.BatteryManager::class.java)
        ?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("DEVICE DIAGNOSTICS", color = Accent, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
        Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(18.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text("${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}", fontWeight = FontWeight.SemiBold)
            Text("Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})", color = SecondaryText, fontSize = 12.sp)
            Text("Available RAM: ${if (memoryInfo.availMem > 0) formatBytes(memoryInfo.availMem) else "Unavailable"}", color = SecondaryText, fontSize = 12.sp)
            Text("Battery: ${if (batteryPercent in 0..100) "$batteryPercent%" else "Unavailable"}", color = SecondaryText, fontSize = 12.sp)
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                val thermal = activity.getSystemService(android.os.PowerManager::class.java)?.currentThermalStatus
                Text("Thermal status: ${when (thermal) { 0 -> "None"; 1 -> "Light"; 2 -> "Moderate"; 3 -> "Severe"; 4 -> "Critical"; 5 -> "Emergency"; 6 -> "Shutdown"; else -> "Unavailable" }}", color = SecondaryText, fontSize = 12.sp)
            }
        }
        Text("Use Android's secure system screens for device-level controls.", color = SecondaryText, fontSize = 13.sp)
        FeatureRow("App settings", "Manage permissions, storage and uninstall apps", "▣") {
            launchSetting(activity, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:" + activity.packageName)), report)
        }
        FeatureRow("Network settings", "Manage Wi-Fi, mobile network and VPN", "⌁") { launchSetting(activity, Intent(Settings.ACTION_WIRELESS_SETTINGS), report) }
        FeatureRow("Display settings", "Brightness, screen timeout and display", "☼") { launchSetting(activity, Intent(Settings.ACTION_DISPLAY_SETTINGS), report) }
        FeatureRow("Battery settings", "Battery usage and power controls", "ϟ") { launchSetting(activity, Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS), report) }
        FeatureRow("Sound settings", "Volume and sound preferences", "♫") { launchSetting(activity, Intent(Settings.ACTION_SOUND_SETTINGS), report) }
        Text("Game booster, app hiding, app locking and VPN require additional Android permissions or a real VPN provider. They are not represented as active until implemented and tested.", color = SecondaryText, fontSize = 12.sp)
    }
}

private fun launchSetting(activity: Activity, intent: Intent, report: (String) -> Unit) {
    runCatching { activity.startActivity(intent) }
        .onSuccess { report("Opened Android settings.") }
        .onFailure { report("This settings screen is unavailable on this device.") }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(SurfaceDark, RoundedCornerShape(18.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = SecondaryText, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FeatureRow(title: String, description: String, glyph: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).background(Accent.copy(alpha = .12f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Text(glyph, color = Accent, fontSize = 20.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(description, color = SecondaryText, fontSize = 12.sp)
        }
        Text("›", color = Accent, fontSize = 24.sp)
    }
}

private fun readCacheSize(activity: Activity): Long =
    activity.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = listOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var index = -1
    do { value /= 1024.0; index++ } while (value >= 1024 && index < units.lastIndex)
    return String.format(Locale.US, "%.1f %s", value, units[index])
}
