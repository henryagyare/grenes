package me.ayitinya.grenes

import MainView
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.dynamicLinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener(this) {
            var deepLink: Uri? = null
            if (it != null) {
                deepLink = it.link
            }

            Log.d("MainActivity", "mainactivity $deepLink")
        }
            .addOnFailureListener(this) {
                Log.d("MainActivity", "mainactivity $it")
            }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent, darkIcons = useDarkIcons
                )

                onDispose { }
            }

            MainView(
                useDarkTheme = isSystemInDarkTheme(),
                dynamicColor = true,
            )
        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
////    App()
//}