package com.elena_balakhnina.bookdiary.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow


@Composable
fun LoginScreen(
    navController: NavController,
    signInFlow: StateFlow<FirebaseUser?>,
    loginUser : (String, String) -> Unit,
    onClickGoogleOneTap : () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    BookDiaryTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Book diary",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 30.sp
                        )
                    }
                )
            }

        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text(text = "Введите email")
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                )
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = {
                        Text(text = "Введите пароль")
                    },

                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
             Box() {
                 Button(
                     onClick = {
                         loginUser(email, password)
                     },

                     ) {
                     Text(text = "Войти", style = MaterialTheme.typography.h4)
                 }
             }
                Text(
                    modifier = Modifier
                        .clickable {
                            navController.navigate("ROUTE_SIGNUP") {
                                popUpTo("ROUTE_LOGIN") { inclusive = true }
                            }
                        },
                    text = "Еще нет аккаунта? Зарегистрируйтесь",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
                Button(onClick =  onClickGoogleOneTap) {
                    Text(text = "Войти с помощью аккаунта Google")
                }
                Box() {
                    val signIn by signInFlow.collectAsState()

                    signIn?.let {
                        LaunchedEffect(Unit) {
                            navController.navigate("ROUTE_HOME") {
                                popUpTo("ROUTE_LOGIN") { inclusive = true }
                            }

                        }
                    }
                }
            }
        }
    }
}