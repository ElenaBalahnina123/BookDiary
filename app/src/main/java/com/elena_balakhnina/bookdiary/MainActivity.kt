package com.elena_balakhnina.bookdiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.elena_balakhnina.bookdiary.authentication.GoogleAuthViewModel
import com.elena_balakhnina.bookdiary.authentication.HomeScreen
import com.elena_balakhnina.bookdiary.authentication.LoginScreen
import com.elena_balakhnina.bookdiary.authentication.SignupScreen
import com.elena_balakhnina.bookdiary.booklist.BookListScreen
import com.elena_balakhnina.bookdiary.booklist.BookListViewModel
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomMenuCompose
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomNavItem
import com.elena_balakhnina.bookdiary.editor.BookEditor
import com.elena_balakhnina.bookdiary.favoritebooklist.FavoriteBookListViewModel
import com.elena_balakhnina.bookdiary.favoritebooklist.FavoriteListScreen
import com.elena_balakhnina.bookdiary.plannedbooklist.BookListViewModelPlanned
import com.elena_balakhnina.bookdiary.plannedbooklist.PlannedBooks
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import com.elena_balakhnina.bookdiary.viewelement.ViewElementData
import com.elena_balakhnina.bookdiary.viewelement.ViewElementScreen
import com.elena_balakhnina.bookdiary.viewelement.ViewElementVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel by viewModels<GoogleAuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppNavHost(viewModel = authViewModel)
//                    navigationContent()
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
fun AppNavHost(
    viewModel: GoogleAuthViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "ROUTE_LOGIN"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("ROUTE_LOGIN") {
            LoginScreen(navController, viewModel.signInFlow, viewModel::loginUser)
        }
        composable("ROUTE_SIGNUP") {
            SignupScreen(viewModel, navController)
        }
        composable("ROUTE_HOME") {
            HomeScreen(viewModel, navController)
        }
    }
}
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
    ) {
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = BottomNavItem.Books.screen_route,
        ) {
            composable("books") {
                val viewModel = hiltViewModel<BookListViewModel>()
                BookListScreen(
                    onAddClick = { navController.navigate("editor?allowRate=true") },
                    onBookClick = { bookIndexInList ->
                        viewModel.onBookClick(
                            bookIndexInList,
                            navController
                        )
                    },
                    onToggleFavorite = viewModel::onToggleFavorite,
                    onQueryChanged = viewModel::onQueryChanged,
                    searchFlow = viewModel.searchFlow,
                    booksListFlow = viewModel.booksFlow()
                )
            }
            composable("favorite") {
                val viewModel = hiltViewModel<FavoriteBookListViewModel>()
                FavoriteListScreen(
                    stateFlow = viewModel.booksFlow(),
                    onBookClick = { bookIndexInList ->
                        viewModel.onBookClick(
                            bookIndexInList,
                            navController
                        )
                    },
                    onToggleFavorite = { viewModel.onToggleFavorite(it) }
                )
            }

            composable("planned") {
                val viewModel = hiltViewModel<BookListViewModelPlanned>()
                PlannedBooks(
                    onAddPlannedBook = { navController.navigate("editor") },
                    onBookClick = { bookIndexInList ->
                        viewModel.onBookClick(
                            bookIndexInList,
                            navController
                        )
                    },
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
                val plannedMode = it.arguments?.getBoolean("planned_mode") ?: false
                Log.d("ViewElement", "bookId: $bookId, planned: $plannedMode")

                val viewModel = hiltViewModel<ViewElementVM>()
                val state by viewModel.uiFlow().collectAsState(ViewElementData())

                ViewElementScreen(
                    navController = navController,
                    onEditClick = { navController.navigate("editor?bookId=${bookId}&allowRate=${!plannedMode}") },
                    onDelete = { viewModel.onDelete(navController) },
                    viewElementData = state,
                    onClickRead = {
                        navController.navigate(
                            route = "editor?bookId=${bookId}&allowRate=${plannedMode}",
                            navOptions = navOptions {
                                popUpTo("planned")
                            }
                        )
                    }
                )
            }
            composable(
                "editor?bookId={book_id}&allowRate={allow_rate}",
                arguments = listOf(
                    navArgument("book_id") {
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

