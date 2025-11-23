package presentation.screens.user

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.style.TextAlign
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.back_button_content_description
import hackernewskmp.composeapp.generated.resources.hello_world
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.user
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.appTopAppBarColors

@Serializable
object UserRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.user)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left_linear),
                            contentDescription = stringResource(Res.string.back_button_content_description),
                        )
                    }
                },
                colors = appTopAppBarColors(),
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.hello_world),
                textAlign = TextAlign.Center,
            )
        }
    }
}
