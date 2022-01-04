package com.telnyx.webrtc.composevoicesample.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telnyx.webrtc.composevoicesample.BuildConfig
import com.telnyx.webrtc.composevoicesample.ui.home.HomeScreen
import com.telnyx.webrtc.composevoicesample.ui.login.LoginScreen
import com.telnyx.webrtc.composevoicesample.ui.shared.BaseViewModel
import com.telnyx.webrtc.composevoicesample.ui.theme.ComposeVoiceSampleTheme
import timber.log.Timber

const val TO_HOME_NAVIGATION = "destination_home"
const val TO_LOGIN_NAVIGATION = "destination_login"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        setContent {
            ComposeVoiceSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    VoiceApp()
                }
            }
        }
    }
}

@Composable
private fun VoiceApp() {
    val navController = rememberNavController()
    val baseViewModel: BaseViewModel = viewModel()

    NavHost(navController = navController, startDestination = TO_LOGIN_NAVIGATION) {
        composable(route = TO_HOME_NAVIGATION) {
            HomeScreen(baseViewModel) { navController.navigate(TO_LOGIN_NAVIGATION) }

        }
        composable(route = TO_LOGIN_NAVIGATION) {
            LoginScreen(baseViewModel) {navController.navigate(TO_HOME_NAVIGATION)}
        }
    }
}

