import android.net.Uri
import dev.gitlive.firebase.storage.File

actual fun toFile(path: String): File = File(Uri.parse(path))