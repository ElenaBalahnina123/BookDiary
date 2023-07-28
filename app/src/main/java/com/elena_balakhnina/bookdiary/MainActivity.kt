package com.elena_balakhnina.bookdiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.elena_balakhnina.bookdiary.booklist.BookListScreen
import com.elena_balakhnina.bookdiary.booklist.BookListViewModel
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomMenuCompose
import com.elena_balakhnina.bookdiary.compose.component.bottommenu.BottomNavItem
import com.elena_balakhnina.bookdiary.domain.GenresRepository
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val isReady by viewModel.stateFlow.collectAsState()
                    if (isReady) {
                        navigationContent()
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                painterResource(id = R.drawable.my_books),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val genresRepository: GenresRepository,
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow(false)

    val stateFlow get() = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            genresRepository.awaitInit()
            mutableStateFlow.value = true
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
                BookListScreen(
                    onAddClick = { navController.navigate("editor?allowRate=true") },
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
                Log.d("ViewElement", "$plannedMode")

                val viewModel = hiltViewModel<ViewElementVM>()
                val state by viewModel.uiFlow().collectAsState(ViewElementData())

                ViewElementScreen(
                    navController = navController,
                    onEditClick = { navController.navigate("editor?bookId=${bookId}&allowRate=${!plannedMode}") },
                    onDelete = { viewModel.onDelete(navController) },
                    viewElementData = state,
                    onClickRead = { navController.navigate(
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
