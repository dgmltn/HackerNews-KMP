package presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import presentation.screens.about.AboutScreen
import presentation.screens.details.DetailsScreen
import presentation.screens.details.DetailsScreenTab
import presentation.screens.main.MainScreen
import presentation.screens.user.UserScreen

sealed class AppRoute {
    @Serializable
    object MainRoute: AppRoute()

    @Serializable
    data class DetailsRoute(
        @SerialName("id")
        val id: Long,
        @SerialName("tab")
        val tab: String // on iOS, NavHost 2.9.1 doesn't like when this is an enum (like DetailsScreenTab)
    ): AppRoute()

    @Serializable
    object AboutRoute: AppRoute()

    @Serializable
    object UserRoute: AppRoute()
}



@Composable
fun RootScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.MainRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<AppRoute.MainRoute> {
            MainScreen(
                onClickItem = { navController.navigate(
                    AppRoute.DetailsRoute(
                        id = it.getItemId(),
                        tab = DetailsScreenTab.Webview.name
                    )
                ) },
                onClickComment = { navController.navigate(
                    AppRoute.DetailsRoute(
                        id = it.getItemId(),
                        tab = DetailsScreenTab.Comments.name
                    )
                ) },
                onClickAbout = { navController.navigate(AppRoute.AboutRoute) },
                onClickUser = { navController.navigate(AppRoute.UserRoute) },
            )
        }

        composable<AppRoute.DetailsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.DetailsRoute>()
            DetailsScreen(
                itemId = route.id,
                tab = DetailsScreenTab.from(route.tab),
                onBack = { navController.popBackStack() },
            )
        }

        composable<AppRoute.AboutRoute> {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable<AppRoute.UserRoute> {
            UserScreen(onBack = { navController.popBackStack() })
        }
    }
}
