package rs.elfak.climb.ui.screens.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Filter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rs.elfak.climb.ui.theme.ClimbTheme

@Composable
fun MapTopBar() {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.requiredHeight(50.dp),
        elevation = CardDefaults.cardElevation(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            Text("Search tracks", modifier = Modifier.fillMaxSize())
            Icon(imageVector = Icons.Rounded.Filter, contentDescription = null)
        }
    }
}