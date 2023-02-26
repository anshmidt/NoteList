
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.R

@Preview
@Composable
fun BottomSheet() {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        BottomSheetItem(
            icon = Icons.Filled.LowPriority,
            title = stringResource(R.string.priority_title_bottom_sheet),
            content = {
                Spacer(Modifier.weight(1f))
                PrioritySelector()
            }
        )

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.16f))

        BottomSheetItem(
            icon = Icons.Filled.ArrowForward,
            title = stringResource(R.string.move_title_bottom_sheet),
            content = {}
        )
    }
}

@Composable
private fun BottomSheetItem(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(color = MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 20.sp
        )
        content()
    }
}

@Composable
private fun PrioritySelector() {
    PrioritySelectorIcon(imageVector = Icons.Filled.KeyboardArrowDown)
    Text(
        text = "Normal",
        fontSize = 20.sp
    )
    PrioritySelectorIcon(imageVector = Icons.Filled.KeyboardArrowUp)
}

@Composable
private fun PrioritySelectorIcon(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = MaterialTheme.colors.onBackground,
        modifier = Modifier.padding(12.dp)
    )
}
