package com.elena_balakhnina.bookdiary.compose.component

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import java.util.GregorianCalendar

@Composable
fun Calendar(
    date: Long,
    onDateChanged: (Long) -> Unit
) {
BookDiaryTheme {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val context = LocalContext.current
            TextButton(
                onClick = {
                    val calendar = GregorianCalendar.getInstance()
                    calendar.timeInMillis = date
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            onDateChanged.invoke(calendar.timeInMillis)
                        },
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                Text(
                    text = String.format("%1\$td.%1\$tm.%1\$ty", date),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif
                )
            }
            Icon(imageVector = Icons.Default.DateRange, contentDescription = "calendar")
        }
    }
}
}
