package com.elena_balakhnina.bookdiary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme

//@Preview
//@Composable
//fun SearchAppbarPreview() {
//    var text by remember {
//        mutableStateOf("")
//    }
//    BookDiaryTheme {
//        Scaffold(
//            topBar = {
//                SearchAppbar(
//                    searchText = text,
//                    onSearchChanged = { text = it }
//                )
//            }
//        ) {
//            Box(modifier = Modifier.padding(it))
//        }
//    }
//}

@Composable
fun SearchAppbar(
    searchText: TextFieldValue,
    onSearchChanged: (TextFieldValue) -> Unit,
    title: String = "Book diary",
) {

    BookDiaryTheme {
        var isNowSearching by remember { mutableStateOf(false) }
        if (!isNowSearching) {
            TopAppBar(
                title = { Text(text = title, fontFamily = FontFamily.Cursive, fontSize = 30.sp) },
                actions = {
                    IconButton(onClick = { isNowSearching = true }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        } else {
            TopAppBar(
                {

                    TextField(
                        value = searchText,
                        onValueChange = onSearchChanged,
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                        singleLine = true,
                        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            leadingIconColor = Color.White,
                            trailingIconColor = Color.White,
                            backgroundColor = Color.Transparent,

                        ),
                        label = { Text(text = "Поиск по автору и названию", color = Color.White)},

                        leadingIcon = {
                            IconButton(onClick = { isNowSearching = false }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { onSearchChanged(TextFieldValue()) }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    )

                }
            )
        }
    }

}
