package com.pokiepaws.mobile.ui.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun SecureScreen() {
    val activity = LocalContext.current.findActivity()

    DisposableEffect(activity) {
        activity?.window?.let { SecureWindowFlag.acquire(it) }

        onDispose {
            activity?.window?.let { SecureWindowFlag.release(it) }
        }
    }
}

private object SecureWindowFlag {
    private var activeScreens = 0
    private var wasSecureBeforeFirstScreen = false

    fun acquire(window: android.view.Window) {
        if (activeScreens == 0) {
            wasSecureBeforeFirstScreen = window.hasSecureFlag()
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE,
            )
        }
        activeScreens++
    }

    fun release(window: android.view.Window) {
        activeScreens = (activeScreens - 1).coerceAtLeast(0)
        if (activeScreens == 0 && !wasSecureBeforeFirstScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

private fun android.view.Window.hasSecureFlag(): Boolean =
    attributes.flags.and(WindowManager.LayoutParams.FLAG_SECURE) != 0

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
