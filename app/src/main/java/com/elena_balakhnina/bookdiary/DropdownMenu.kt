package com.elena_balakhnina.bookdiary

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class DropdownOptionsPreviewProvider : PreviewParameterProvider<List<String>> {
    override val values: Sequence<List<String>>
        get() = sequenceOf(listOf(
            "option A",
            "option B",
            "option C"
        ))
}

@Preview
@Composable
fun DropdownMenu(
    @PreviewParameter(provider = DropdownOptionsPreviewProvider::class)
    options: List<String>,
    hint: String = "hint",
    selectedOption: Int = -1,
    onSelectedOptionChange: (Int)->Unit = {}
) {
    Row {
        Text(
            text = options.getOrElse(selectedOption) {
                hint
            },
            color = if(selectedOption in options.indices) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
    }
}