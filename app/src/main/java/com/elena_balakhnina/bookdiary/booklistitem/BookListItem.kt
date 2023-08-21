package com.elena_balakhnina.bookdiary.booklistitem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.elena_balakhnina.bookdiary.R


class BookItemDataPreviewProvider : PreviewParameterProvider<BookListItemData> {
    override val count: Int
        get() = 1

    override val values: Sequence<BookListItemData>
        get() = sequence {
            yield(
                BookListItemData(
                    bookTitle = "Королевство шипов и роз gggggggggfgf gfgfgfgfgf",
                    author = "Сара Дж. Маас",
                    description = "Могла ли знать девятнадцатилетняя Фейра, что огромный волк, убитый девушкой на охоте, — на самом деле преображенный фэйри. Расплата не заставила себя ждать. Она должна или заплатить жизнью, или переселиться за стену — волшебную невидимую преграду, отделяющую владения смертных от Притиании, королевства фэйри. Фейра выбирает второе. Тамлин, владелец замка, куда девушка попадает, не простой фэйри, он — верховный правитель Двора весны, одного из могущественных Дворов, на которые поделено королевство. Однажды Фейра узнает тайну: на Двор весны и на Тамлина, ее покровителя, злые силы наложили заклятье, снять которое способна только смертная девушка",
                    date = "11.12.2007",
                    rating = 9,
                    genre = "Романтика",
                    image = null,
                    showRatingAndData = true,
                    isFavorite = false
                )
            )
        }
}


@Composable
fun BookListItem(
    @PreviewParameter(BookItemDataPreviewProvider::class)
    itemData: BookListItemData,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit = {},
    showRatingAndData: Boolean = true
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick)
    ) {
        val (bookTitle, author, description, date, rating, image, favoriteButton, ratingImage) = createRefs()

        if (itemData.image != null) {
            Image(
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(image) {
                        start.linkTo(parent.start, 8.dp)
                        top.linkTo(parent.top, 8.dp)
                    }
                    .width(96.dp)
                    .aspectRatio(0.65f),
                contentScale = ContentScale.Crop,
                bitmap = itemData.image
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.my_books), contentDescription = null,
                modifier = Modifier
                    .constrainAs(image) {
                        start.linkTo(parent.start, 9.dp)
                        top.linkTo(parent.top, 8.dp)
                    }
                    .width(96.dp)
                    .aspectRatio(0.65f)
            )
        }
        Text(
            text = itemData.bookTitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(bookTitle) {
                start.linkTo(image.end, 16.dp)
                end.linkTo(parent.end, 16.dp)
                top.linkTo(parent.top, 8.dp)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = itemData.author,
            color = Color(0xFF03A9F4),
            modifier = Modifier.constrainAs(author) {
                start.linkTo(bookTitle.start)
                top.linkTo(bookTitle.bottom, 2.dp)
            }
        )

        if (showRatingAndData) {
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .constrainAs(favoriteButton) {
                        top.linkTo(parent.top, 8.dp)
                        start.linkTo(parent.start, 8.dp)

                        width = Dimension.value(32.dp)
                        height = Dimension.value(32.dp)
                    }
                    .background(
                        color = Color(0x77000000),
                        shape = CircleShape
                    )
                    .padding(4.dp),
            ) {
                Icon(
                    tint = Color(0xFFFF001E),
                    modifier = Modifier,
                    imageVector = if (itemData.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = null
                )
            }
        }
        Text(
            text = itemData.description,
            fontSize = 12.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(description) {
                start.linkTo(image.end, 16.dp)
                end.linkTo(parent.end, 16.dp)
                top.linkTo(author.bottom, 4.dp)
                width = Dimension.fillToConstraints
            }
        )
        if (showRatingAndData) {
            if (itemData.date != null) {
                Text(
                    text = itemData.date/*String.format("%1\$td.%1\$tm.%1\$ty", itemData.date)*/,
                    modifier = Modifier.constrainAs(date) {
                        start.linkTo(image.end, 16.dp)
                        top.linkTo(description.bottom, 8.dp)
                    }
                )
            }
            if (itemData.rating != null) {
                Image(
                    painter = painterResource(id = R.drawable.star_rate_white_24dp),
                    contentDescription = null,
                    modifier = Modifier.constrainAs(ratingImage) {
                        top.linkTo(description.bottom, 8.dp)
                        end.linkTo(rating.start, 4.dp)
                        width = Dimension.value(24.dp)
                        height = Dimension.value(24.dp)
                    }
                )
                Text(
                    text = itemData.rating.toString(),
                    modifier = Modifier.constrainAs(rating) {
                        top.linkTo(ratingImage.top)
                        bottom.linkTo(ratingImage.bottom)
                        end.linkTo(parent.end, 16.dp)
                    }
                )
            }
        }

    }
    Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
}




