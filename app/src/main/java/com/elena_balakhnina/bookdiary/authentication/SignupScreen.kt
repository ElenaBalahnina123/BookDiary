package com.elena_balakhnina.bookdiary.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.elena_balakhnina.bookdiary.R

@Composable
fun SignupScreen(viewModel: GoogleAuthViewModel?, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authResource = viewModel?.signupFlow?.collectAsState()

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (refHeader, refName, refEmail, refPassword, refButtonSignup, refTextSignup, refLoading) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(refHeader) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .wrapContentSize()
        ) {

        }

        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text(text = stringResource(id = R.string.name))
            },
            modifier = Modifier.constrainAs(refName) {
                top.linkTo(refHeader.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = stringResource(id = R.string.email))
            },
            modifier = Modifier.constrainAs(refEmail) {
                top.linkTo(refName.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = stringResource(id = R.string.password))
            },
            modifier = Modifier.constrainAs(refPassword) {
                top.linkTo(refEmail.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Button(
            onClick = {
                viewModel?.signupUser(name, email, password)
            },
            modifier = Modifier.constrainAs(refButtonSignup) {
                top.linkTo(refPassword.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(text = stringResource(id = R.string.signup), style = MaterialTheme.typography.subtitle1)
        }


        Text(
            modifier = Modifier
                .constrainAs(refTextSignup) {
                    top.linkTo(refButtonSignup.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clickable {
                    navController.navigate("ROUTE_LOGIN") {
                        popUpTo("ROUTE_SIGNUP") { inclusive = true }
                    }
                },
            text = stringResource(id = R.string.already_have_account),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )

        authResource?.value?.let {

                    LaunchedEffect(Unit) {
                        navController.navigate("ROUTE_HOME") {
                            popUpTo("ROUTE_SIGNUP") { inclusive = true }

                }
            }
        }
    }
}