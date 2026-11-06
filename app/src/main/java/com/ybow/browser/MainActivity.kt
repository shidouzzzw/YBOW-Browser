package com.ybow.browser

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.ui.YbowApp
import com.ybow.browser.ui.theme.YbowTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            YbowTheme(themeMode = settings.themeMode) {
                YbowApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onForeground()
    }

    override fun onStop() {
        viewModel.onBackground()
        super.onStop()
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data?.toString() ?: return
        if (data.startsWith("http://") || data.startsWith("https://")) {
            viewModel.handleIncomingUrl(data)
        }
    }
}
