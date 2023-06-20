package com.elena_balakhnina.bookdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elena_balakhnina.bookdiary.booklist.BookList
import com.elena_balakhnina.bookdiary.booklist.BookListViewModel
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomMenuCompose
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomNavItem
import com.elena_balakhnina.bookdiary.favoritebooklist.FavoriteBookListViewModel
import com.elena_balakhnina.bookdiary.favoritebooklist.FavoriteList
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
                val viewModel = hiltViewModel<BookListViewModel>()
                BookList(
                    onAddClick = { navController.navigate("editor?allowRate=true") },
                    stateFlow = viewModel.booksFlow(),
                    onBookClick = { bookIndexInList -> viewModel.onBookClick(bookIndexInList, navController) },
                    onToggleFavorite = { viewModel.onToggleFavorite(it) }
                )
            }
            composable("favorite") {
                val viewModel = hiltViewModel<FavoriteBookListViewModel>()
                FavoriteList(
                    stateFlow = viewModel.booksFlow(),
                    onBookClick = { bookIndexInList -> viewModel.onBookClick(bookIndexInList, navController) },
                    onToggleFavorite = { viewModel.onToggleFavorite(it) }
                )
            }
            composable("planned") {
                val viewModel = hiltViewModel<BookListViewModelPlanned>()
                PlannedBooks(
                    onAddPlannedBook = { navController.navigate("editor") },
                    onBookClick = { bookIndexInList -> viewModel.onBookClick(bookIndexInList, navController) },
                    stateFlow = viewModel.booksFlow(),
                )
            }
            composable(
                "books/{book_id}?planned={planned_mode}",
                arguments = listOf(
                    navArgument("book_id") { type = NavType.LongType },
                    navArgument("planned_mode") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                val bookId = it.arguments?.getLong("book_id") ?: 0L
                val viewModel = hiltViewModel<ViewElementVM>()
                val state by viewModel.uiFlow().collectAsState(ViewElementScreenData())
                ViewElementScreen(
                    navController = navController,
                    onEditClick = { navController.navigate("editor?bookId=${bookId}&allowRate=true") },
                    onDelete = { viewModel.onDelete(navController) },
                    viewElementScreenData = state
                )
            }
            composable(
                "editor?bookId={book_id}&allowRate={allow_rate}",
                arguments = listOf(
                    navArgument("book_id"){
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument("allow_rate") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                BookEditor(navController = navController)
            }
        }
    }
}
