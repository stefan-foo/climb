package rs.elfak.climb.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultipleChipSelector(
    modifier: Modifier, 
    items: List<T>, 
    value: List<T>,
    onSelectionChange: (List<T>) -> Unit, 
    display: (T) -> String,
    trailingIcons: List<ImageVector> = listOf()
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEachIndexed { i, it ->
            val isSelected = value.contains(it)
            ElevatedFilterChip(
                label = { Text(text = display(it)) },
                selected =isSelected,
                trailingIcon = if (i < trailingIcons.size) ({
                    Icon(imageVector = trailingIcons[i], contentDescription = null)
                }) else null,
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    val selectedValue = value.toMutableList()
                    if (isSelected) {
                        selectedValue.remove(it)
                    } else {
                        selectedValue.add(it)
                    }
                    onSelectionChange(selectedValue)
                }
            )
        }
    }
}