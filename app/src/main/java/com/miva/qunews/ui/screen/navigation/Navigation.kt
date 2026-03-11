package com.miva.qunews.ui.screen.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.miva.qunews.ui.screen.newsdetail.NewsDetailScreen
import com.miva.qunews.ui.screen.newslist.NewsListScreen
import com.miva.qunews.ui.screen.search.SearchScreen

sealed class Screen(val route: String) {
    object NewsList : Screen("news_list")
    object Search : Screen("search")
    object NewsDetail : Screen("news_detail/{articleUrl}") {
        fun createRoute(articleUrl: String) = "news_detail/${Uri.encode(articleUrl)}"
    }
}

@Composable
fun NewsNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.NewsList.route
    ) {
        composable(Screen.NewsList.route) {
            NewsListScreen(
                onArticleClick = { articleUrl ->
                    navController.navigate(Screen.NewsDetail.createRoute(articleUrl))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { articleUrl ->
                    navController.navigate(Screen.NewsDetail.createRoute(articleUrl))
                }
            )
        }

        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(
                navArgument("articleUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl") ?: ""
            NewsDetailScreen(
                articleUrl = Uri.decode(articleUrl),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}