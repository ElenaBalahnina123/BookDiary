package com.elena_balakhnina.bookdiary.compose.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

class DropdownOptionsPreviewProvider : PreviewParameterProvider<List<String>> {
    override val values: Sequence<List<String>>
        get() = sequenceOf(
            listOf(
                "option A",
                "option B",
                "option C"
            )
        )
}

@Preview()
@Composable
private fun dropdownPreview() {
    DropdownComponent(
        options = listOf(
            "option A",
            "option B",
            "option C"
        ), hint = "hint", selectedOption = -1, onSelectedOptionChange = {}
    )
}

@Composable
fun DropdownComponent(
    options: List<String>,
    hint: String,
    selectedOption: Int,
    onSelectedOptionChange: (Int) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                expanded = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = options.getOrElse(selectedOption) {
                hint
            },
            color = if (selectedOption in options.indices) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    start = 16.dp
                )
                .padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier.padding(end = 16.dp)
        ) {
            val animatedAngle by animateFloatAsState(
                if(expanded) 180f else 0f
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .rotate(animatedAngle)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(),
            offset = DpOffset(0.dp, (-8).dp)
        ) {
            options.forEachIndexed { index, label ->
                DropdownMenuItem(onClick = {
                    onSelectedOptionChange(index)
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}