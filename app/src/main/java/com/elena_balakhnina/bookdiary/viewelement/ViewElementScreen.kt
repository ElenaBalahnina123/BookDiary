package com.elena_balakhnina.bookdiary.viewelement

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.R
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme


@Preview
@Composable
fun ViewElementScreen(
    navController: NavController = rememberNavController(),
    onEditClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClickRead: () -> Unit = {},
    viewElementData: ViewElementData = ViewElementData(),
) {
    BookDiaryTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = "Book diary", fontFamily = FontFamily.Cursive, fontSize = 30.sp)
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }, actions = {
                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "edit")
                }
                val openDialog = remember { mutableStateOf(false) }

                IconButton(onClick = { openDialog.value = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                }
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Text(text = "Удаление")
                        },
                        text = {
                            Text(text = "Вы точно хотите удалить книгу?")
                        },
                        confirmButton = {
                            Button(
                                onClick = { openDialog.value = false }) {
                                Text("Отмена")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = onDelete
                            ) {
                                Text("Удалить")
                            }
                        }
                    )
                }
            })
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(start = 12.dp, end = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {

                Text(
                    text = viewElementData.bookTitle,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = viewElementData.author,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row() {

                    if (viewElementData.image != null) {
                        Image(
                            bitmap = viewElementData.image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .aspectRatio(0.65f),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.my_books),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .aspectRatio(0.65f),
                        )
                    }

                    Column() {
                        if (viewElementData.allowRate) {
                            Row(modifier = Modifier.padding(start = 48.dp)) {
                                Image(
                                    painter = painterResource(id = R.drawable.star_rate_white_24dp),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(42.dp)
                                        .aspectRatio(0.7f)
                                )
                                Text(
                                    text = viewElementData.rating.toString(),
                                    fontSize = 40.sp
                                )
                            }
                        }
                        Text(
                            text = "Жанр: ${viewElementData.genre}",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )

                        if (viewElementData.allowRate) {

                            Text(
                                text = String.format(
                                    "%1\$td.%1\$tm.%1\$ty", viewElementData.date
                                ), modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                            )
                        }
                    }

                }


                    Text(
                        text = viewElementData.description,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp)

                    )



                val openDialog = remember { mutableStateOf(false) }

                if (!viewElementData.allowRate) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { openDialog.value = true },
                        ) {
                            Text(text = "Прочитано")
                        }
                    }

                    if (openDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog.value = false
                            },
                            title = {
                                Text(text = "Добавить в прочитанное")
                            },
                            text = {
                                Text(text = "Добавить книгу в прочитанное?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = { openDialog.value = false }) {
                                    Text("Отмена")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = onClickRead
                                ) {
                                    Text("Добавить")
                                }
                            }
                        )
                    }
                }

            }

        }
    }
}