import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val HOME = "home"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

}