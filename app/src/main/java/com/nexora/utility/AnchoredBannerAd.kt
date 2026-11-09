package com.nexora.utility

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * One bottom-anchored adaptive banner. Uses Google's official test unit until
 * the app owner supplies a production AdMob banner unit ID.
 */
@Composable
fun AnchoredBannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthDp = maxWidth.value.toInt().coerceAtLeast(1)
        val adView = remember(context, widthDp) {
            AdView(context).apply {
                adUnitId = TEST_BANNER_UNIT_ID
                setAdSize(AdSize.getLargeAnchoredAdaptiveBannerAdSize(context, widthDp))
            }
        }
        LaunchedEffect(adView) {
            adView.loadAd(AdRequest.Builder().build())
        }
        DisposableEffect(adView) {
            onDispose { adView.destroy() }
        }
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        )
    }
}

private const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
