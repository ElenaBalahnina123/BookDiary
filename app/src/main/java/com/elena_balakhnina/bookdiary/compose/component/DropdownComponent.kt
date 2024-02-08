package com.elena_balakhnina.bookdiary.compose.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.elena_balakhnina.bookdiary.collectMappedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

data class DropDownState(
    val options: List<String>,
    val hint: String,
    val selectedOption: Int,
)

@Composable
fun DropDownComponent(
    flow: Flow<DropDownState>,
    onSelectedOptionChange: (Int) -> Unit,
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
        val hintText by remember {
            flow.map { it.options.getOrElse(it.selectedOption) { _ -> it.hint } }
                .distinctUntilChanged()
                .flowOn(Dispatchers.IO)
        }.collectAsState(initial = "")

        val materialColors = MaterialTheme.colors

        val hintColor by remember {
            flow.map { it.selectedOption in it.options.indices }
                .distinctUntilChanged()
                .map {
                    if(it) {
                        materialColors.onSurface
                    } else {
                        materialColors.onSurface.copy(alpha = 0.5f)
                    }
                }
                .flowOn(Dispatchers.IO)
        }.collectAsState(initial = materialColors.onSurface.copy(alpha = 0.5f))

        Text(
            text = hintText,
            color = hintColor,
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
                if (expanded) 180f else 0f
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
            val options by flow.collectMappedState(initialValue = emptyList()) {
                it.options
            }

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
                if (expanded) 180f else 0f
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