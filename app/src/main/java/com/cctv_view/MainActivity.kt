package com.cctv_view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cctv_view.ui.screens.PlayerScreen
import com.cctv_view.ui.screens.SettingsActivity
import com.cctv_view.ui.theme.CCTVViewTheme
import com.cctv_view.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCTVViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerScreen(
                        onOpenSettings = { openSettings() }
                    )
                }
            }
        }
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    // Activity 层面的按键分发 - 确保按键能到达 Compose
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            Log.d("MainActivity", "dispatchKeyEvent: ${it.keyCode}, action: ${it.action}")
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("MainActivity", "onKeyDown: $keyCode")
        return super.onKeyDown(keyCode, event)
    }

    class ViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(applicationContext.applicationContext as android.app.Application) as T
        }
    }
}
