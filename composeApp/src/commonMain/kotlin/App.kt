import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import modules.dataModule
import modules.repositoryModule
import modules.useCaseModule
import modules.viewModelModule
import org.koin.compose.KoinApplication
import presentation.RootScreen
import ui.AppTheme

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            dataModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }) {
        AppTheme {
            RootScreen()
        }
    }
}
