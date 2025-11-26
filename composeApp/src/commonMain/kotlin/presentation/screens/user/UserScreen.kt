package presentation.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.back_button_content_description
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.ic_check_circle_bold
import hackernewskmp.composeapp.generated.resources.seen_stories
import hackernewskmp.composeapp.generated.resources.stats_section_title
import hackernewskmp.composeapp.generated.resources.sync_enabled
import hackernewskmp.composeapp.generated.resources.sync_info_section_title
import hackernewskmp.composeapp.generated.resources.unconfirmed
import hackernewskmp.composeapp.generated.resources.user
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel
import presentation.viewmodels.UserViewModel
import ui.AppPreview
import ui.appTopAppBarColors

@Composable
fun UserScreen(
    onBack: () -> Unit,
) {
    val mainViewModel = koinInject<MainViewModel>()
    val userViewModel = koinInject<UserViewModel>()

    val mainState by mainViewModel.state
    val userState by userViewModel.state

    UserScreen(
        seenItemCount = mainState.seenItemsIds.size,
        isSyncEnabled = userState.isSyncEnabled,
        syncEmail = userState.syncEmailAddress,
        isSyncEmailConfirmed = userState.isSyncConfirmed,
        onBack = onBack,
        onSetSyncEnabled = userViewModel::setIsSyncEnabled,
        onSetSyncEmailAddress = userViewModel::setSyncEmailAddress,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserScreen(
    seenItemCount: Int,
    isSyncEnabled: Boolean,
    syncEmail: String? = null,
    isSyncEmailConfirmed: Boolean = false,
    onBack: () -> Unit,
    onSetSyncEnabled: (Boolean) -> Unit,
    onSetSyncEmailAddress: (String) -> Unit,
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
            item("title_stats") {
                SectionTitle(
                    title = stringResource(Res.string.stats_section_title)
                )
            }
            item(key = "stats_seen_stories") {
                InfoRow(
                    title = stringResource(Res.string.seen_stories),
                    value = seenItemCount.toString(),
                )
            }
            item(key = "title_sync") {
                SectionTitle(
                    title = stringResource(Res.string.sync_info_section_title)
                )
            }
            item(key = "is_sync_enabled") {
                InfoRow(
                    title = {
                        Text(
                            text = stringResource(Res.string.sync_enabled),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    value = {
                        Switch(
                            checked = isSyncEnabled,
                            onCheckedChange = { onSetSyncEnabled(it) },
                        )
                    }
                )
            }
            item(key = "sync_user_info") {
                val enteredEmailAddress = remember { TextFieldState() }
                when {
                    !isSyncEnabled -> Unit
                    syncEmail.isNullOrEmpty() -> {
                        InfoRow {
                            TextField(
                                state = enteredEmailAddress,
                                placeholder = { Text("Email address") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = { onSetSyncEnabled(false) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = { onSetSyncEmailAddress(enteredEmailAddress.text.toString()) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Verify")
                            }
                        }

                    }
                    !isSyncEmailConfirmed -> {
                        InfoRow(
                            title = syncEmail,
                            value = stringResource(Res.string.unconfirmed),
                        )
                    }
                    else -> {
                        InfoRow(
                            title = {
                                Text(
                                    text = syncEmail,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            },
                            value = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check_circle_bold),
                                    contentDescription = null
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun InfoRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    InfoRow(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        value = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End,
            )
        }
    )
}

@Composable
private fun InfoRow(
    title: @Composable RowScope.() -> Unit,
    value: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    InfoRow(
        modifier = modifier,
        content = {
            title()
            Spacer(modifier = Modifier.weight(1f))
            value()
        }
    )
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large,
            )
            .minimumInteractiveComponentSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        content()
    }
}

@Preview
@Composable
private fun Preview_UserScreen() {
    var isSyncEnabled by remember { mutableStateOf(false) }
    var syncEmail by remember { mutableStateOf("") }
    var isSyncEmailConfirmed by remember { mutableStateOf(false) }


    AppPreview {
        UserScreen(
            seenItemCount = 25,
            isSyncEnabled = isSyncEnabled,
            syncEmail = syncEmail,
            isSyncEmailConfirmed = isSyncEmailConfirmed,
            onBack = {
                isSyncEnabled = false
                syncEmail = ""
                isSyncEmailConfirmed = false
            },
            onSetSyncEnabled = { isSyncEnabled = it },
            onSetSyncEmailAddress = { syncEmail = it },
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (syncEmail.isNotEmpty() && !isSyncEmailConfirmed) {
                Button(
                    onClick = { isSyncEmailConfirmed = true},
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        "Simulate email confirmation clicked",
                    )
                }
            }
        }
    }
}