import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.gitlive.firebase.storage.File
import di.commonModules
import di.platformModules
import moe.tlaster.precompose.PreComposeApp
import navigation.Nav
import org.koin.compose.KoinApplication
import ui.theme.AppTheme

expect fun toFile(path: String): File

@Composable
fun App(useDarkTheme: Boolean = false, dynamicColor: Boolean = false) {
    KoinApplication(application = {
        platformModules.forEach { modules(it) }
        commonModules(isProduction = true).forEach { modules(it) }
    }) {
        PreComposeApp {
            AppTheme(dynamicColor = dynamicColor, useDarkTheme = useDarkTheme) {
                Nav(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
