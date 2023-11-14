package com.elena_balakhnina.bookdiary.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.elena_balakhnina.bookdiary.R

@Composable
fun HomeScreen(viewModel: GoogleAuthViewModel?, navController: NavHostController) {
    viewModel?.currentUser?.let {
        UserInfo(viewModel = viewModel, navController = navController, name = it.displayName.toString(), email = it.email.toString())
    }
}

@Composable
fun UserInfo(viewModel: GoogleAuthViewModel?, navController: NavController, name: String, email: String) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.welcome_back),
        )

        Text(
            text = name,
        )

        Image(
            painter = painterResource(id = R.drawable.kingdom),
            contentDescription = stringResource(id = R.string.empty),
            modifier = Modifier.size(128.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.name),
                    modifier = Modifier.weight(0.3f),
                )

                Text(
                    text = name,
                    modifier = Modifier.weight(0.7f),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.email),

                    modifier = Modifier.weight(0.3f),

                )

                Text(
                    text = email,
                    modifier = Modifier.weight(0.7f),
                )
            }

            Button(
                onClick = {
                    viewModel?.logout()
                    navController.navigate("ROUTE_LOGIN") {
                        popUpTo("ROUTE_HOME") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)

            ) {
                Text(text = stringResource(id = R.string.logout))
            }
        }
    }
}