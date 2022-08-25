package com.elena_balakhnina.bookdiary

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class BookItemData(
    val bookTitle: String,
    val author: String,
    val description: String,
    val date: Long,
    val rating: Int,
    val genre: String
)

class BookItemDataPreviewProvider : PreviewParameterProvider<BookItemData> {
    override val count: Int
        get() = 1

    override val values: Sequence<BookItemData>
        get() = sequence {
            yield(
                BookItemData(
                    bookTitle = "Королевство шипов и роз",
                    author = "Сара Дж. Маас",
                    description = "Могла ли знать девятнадцатилетняя Фейра, что огромный волк, убитый девушкой на охоте, — на самом деле преображенный фэйри. Расплата не заставила себя ждать. Она должна или заплатить жизнью, или переселиться за стену — волшебную невидимую преграду, отделяющую владения смертных от Притиании, королевства фэйри. Фейра выбирает второе. Тамлин, владелец замка, куда девушка попадает, не простой фэйри, он — верховный правитель Двора весны, одного из могущественных Дворов, на которые поделено королевство. Однажды Фейра узнает тайну: на Двор весны и на Тамлина, ее покровителя, злые силы наложили заклятье, снять которое способна только смертная девушка",
                    date = System.currentTimeMillis(),
                    rating = 9,
                    genre = "Романтика"
                )
            )
        }
}

@Preview
@Composable
fun ItemList(
    @PreviewParameter(BookItemDataPreviewProvider::class)
    itemData: BookItemData,
    onClick: (BookItemData)->Unit = {},
) {
    Card(
        modifier = Modifier.clickable(onClick = { onClick(itemData) })
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.kingdom),
                contentDescription = null,
                modifier = Modifier
                    .width(86.dp)
                    .aspectRatio(0.65f),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(start = 8.dp)) {
                Text(
                    text = itemData.bookTitle,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = itemData.author,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(text = itemData.description, fontSize = 12.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("%1\$td.%1\$tm.%1\$ty", itemData.date),
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = null
                    )
                    Text(
                        text = itemData.rating.toString(),
                    )

                }
            }
        }
    }
}




