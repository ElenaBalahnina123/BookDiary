package com.elena_balakhnina.bookdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.booklist.BookList
import com.elena_balakhnina.bookdiary.booklist.BookListViewModel
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomMenuCompose
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomNavItem
import com.elena_balakhnina.bookdiary.edit.ARG_RATE_MODE
import com.elena_balakhnina.bookdiary.plannedbooklist.BookListViewModelPlanned
import com.elena_balakhnina.bookdiary.plannedbooklist.PlannedBooks
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookDiaryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navigationContent()
                }
            }
        }
    }
}

private val MainRoutes = setOf(
    "books",
    "favorite",
    "planned"
)

@Composable
private fun navigationContent() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val currentRoute by navController.currentBackStackEntryFlow.collectAsState(initial = null)

            val bottomMenuIsVisible = currentRoute?.destination?.route.toString() in MainRoutes
            if (bottomMenuIsVisible) {
                BottomMenuCompose(navController = navController)
            }
        },
    ) { it ->
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = BottomNavItem.Books.screen_route,
        ) {
            composable("books") {
                it.arguments?.putBoolean(ARG_RATE_MODE, true)
                val viewModel = hiltViewModel<BookListViewModel>()
                BookList(
                    onAddClick = { navController.navigate("books/new") },
                    stateFlow = viewModel.booksFlow(),
                    onBookClick = { navController.navigate("books/$it") }
                )
            }
            composable(BottomNavItem.Favorite.screen_route) {
                FavoriteBooks(navController)
            }
            composable("planned") {
                val viewModel = hiltViewModel<BookListViewModelPlanned>()
                PlannedBooks(
                    onAddPlannedBook = { navController.navigate("planned/new") },
                    onBookClick =  { navController.navigate("planned/$it") },
                    stateFlow = viewModel.booksFlow(),
                )
            }
            composable(
                "books/{book_id}",
                arguments = listOf(
                    navArgument("book_id") { type = NavType.LongType }
                )
            ) {
                val bookId = it.arguments?.getLong("book_id") ?: 0L
                it.arguments?.putBoolean(ARG_RATE_MODE, true)
                val viewModel = hiltViewModel<ViewElementVM>()

                val state by viewModel.uiFlow().collectAsState(ViewElementScreenData())
                ViewElementScreen(
                    navController = navController,
                    onEditClick = { navController.navigate("books/$bookId/edit") },
                    onDelete = { viewModel.onDelete(navController) },
                    viewElementScreenData = state
                )
            }
            composable(
                "planned/{book_id}",

                arguments = listOf(
                    navArgument("book_id") { type = NavType.LongType },
                )
            ) {
                val bookId = it.arguments?.getLong("book_id") ?: 0L
                val viewModel = hiltViewModel<ViewElementVM>()

                val state by viewModel.uiFlow().collectAsState(ViewElementScreenData())


                ViewElementScreen(
                    navController = navController,
                    onEditClick = { navController.navigate("planned/$bookId/edit") },
                    onDelete = { viewModel.onDelete(navController) },
                    viewElementScreenData = state
                )
            }
            composable(
                "books/{book_id}/edit",
                arguments = listOf(
                    navArgument("book_id") { type = NavType.LongType },
                )
            ) {
                it.arguments?.putBoolean(ARG_RATE_MODE, true)
                BookEditor(navController)
            }
            composable(
                "planned/{book_id}/edit",
                arguments = listOf(
                    navArgument("book_id") { type = NavType.LongType }
                )
            ) {
                BookEditor(navController)
            }
            composable(
                route = "books/new"
            ) {
                it.arguments?.putBoolean(ARG_RATE_MODE, true)
                BookEditor(navController)
            }
            composable(
                route = "planned/new"
            ) {
                BookEditor(navController)
            }
        }
    }
}

// books
// books/1
// books/1/edit
