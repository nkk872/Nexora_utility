package com.nexora.utility

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
private enum class Page { HOME, CLEANER, STORAGE, TOOLS }

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
                listOf(Page.HOME to "Home", Page.CLEANER to "Clean", Page.STORAGE to "Storage", Page.TOOLS to "Tools").forEach { (target, label) ->
                    NavigationBarItem(
                        selected = page == target, onClick = { page = target },
                        icon = { Text(when (target) { Page.HOME -> "⌂"; Page.CLEANER -> "✦"; Page.STORAGE -> "▤"; Page.TOOLS -> "⚙" }, fontSize = 20.sp) },
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
                        Text(when (page) { Page.HOME -> "Utility"; Page.CLEANER -> "Smart cleaner"; Page.STORAGE -> "Storage"; Page.TOOLS -> "Device tools" }, fontSize = 27.sp, fontWeight = FontWeight.Bold)
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
                    Page.STORAGE -> StoragePage()
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

@Composable
private fun StoragePage() {
    val stats = remember { runCatching { StatFs(Environment.getDataDirectory().path) }.getOrNull() }
    val total = stats?.totalBytes ?: 0L
    val free = stats?.availableBytes ?: 0L
    val used = (total - free).coerceAtLeast(0)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Text("INTERNAL STORAGE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text(if (total > 0) formatBytes(used) else "Unavailable", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Used of " + formatBytes(total) + " total", color = SecondaryText)
            Spacer(Modifier.height(16.dp))
            if (total > 0) LinearProgressIndicator(progress = { used.toFloat() / total.toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Accent, trackColor = Color(0xFF29352F))
        }
        MetricCard("Available space", if (total > 0) formatBytes(free) else "Unavailable", Modifier.fillMaxWidth())
        Text("Storage figures come from Android's filesystem statistics. They are estimates of the shared data partition, not a scan of every personal file.", color = SecondaryText, fontSize = 13.sp)
    }
}

@Composable
private fun ToolsPage(activity: Activity, report: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
