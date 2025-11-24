package presentation.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.back_button_content_description
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.seen_stories
import hackernewskmp.composeapp.generated.resources.user
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel
import ui.AppPreview
import ui.appTopAppBarColors

@Serializable
object UserRoute

@Composable
fun UserScreen(
    onBack: () -> Unit,
) {
    val mainViewModel = koinInject<MainViewModel>()
    val state by mainViewModel.state
    val seenItemCount = state.seenItemsIds.size

    UserScreen(
        seenItemCount = seenItemCount,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserScreen(
    seenItemCount: Int,
    onBack: () -> Unit,
) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                InfoRow(
                    title = stringResource(Res.string.seen_stories),
                    value = seenItemCount.toString(),
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large,
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.End,
        )
    }
}

@Preview
@Composable
private fun Preview_UserScreen() {
    AppPreview {
        UserScreen(
            seenItemCount = 25,
            onBack = {},
        )
    }
}