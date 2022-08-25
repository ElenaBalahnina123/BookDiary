package com.elena_balakhnina.bookdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme

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
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "books") {
                        composable("books") {
                            BookList(
                                navController = navController,
                            )
                        }
                        composable(
                            "books/{book_id}",
                            arguments = listOf(
                                navArgument("book_id") { type = NavType.LongType }
                            )
                        ) {
                            val bookId = it.arguments?.getLong("book_id") ?: 0L
                            ViewElement(
                                navController = navController,
                                viewModel = viewModel<ViewElementVM>(
                                    factory = object : ViewModelProvider.Factory {
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return ViewElementVM(bookId) as T
                                        }
                                    }
                                )
                            )
                        }
                        composable(
                            "books/{book_id}/edit",
                            arguments = listOf(
                                navArgument("book_id") { type = NavType.LongType }
                            )
                        ) {
                            val bookId = it.arguments?.getLong("book_id") ?: 0L
                            EditElement(
                                navController = navController,
                                viewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return EditElementViewModel(bookId) as T
                                        }
                                    }
                                )
                            )
                        }
                        composable(
                            route = "books/new"
                        ) {
                            EditElement(
                                navController = navController,
                                viewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return EditElementViewModel(null) as T
                                        }
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// books
// books/1
// books/1/edit
