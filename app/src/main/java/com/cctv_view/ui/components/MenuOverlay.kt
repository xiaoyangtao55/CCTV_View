package com.cctv_view.ui.components

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MenuItem(
    val id: Int,
    val name: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MenuOverlay(
    items: List<MenuItem>,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var selectedIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    var lastKeyTime by remember { mutableLongStateOf(0L) }

    // 菜单项显示后自动请求焦点
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        focusRequester.requestFocus()
    }

    fun navigate(direction: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastKeyTime < 200) return
        lastKeyTime = currentTime

        selectedIndex = ((selectedIndex + direction) % items.size + items.size) % items.size
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .shadow(8.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.nativeKeyEvent.action != KeyEvent.ACTION_DOWN) return@onKeyEvent false

                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            navigate(-1)
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            navigate(1)
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_UP,
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            // 上下键不处理，让它返回给上层
                            false
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER -> {
                            if (selectedIndex in items.indices) {
                                items[selectedIndex].onClick()
                            }
                            true
                        }
                        KeyEvent.KEYCODE_BACK,
                        KeyEvent.KEYCODE_MENU -> {
                            onDismiss()
                            true
                        }
                        else -> false
                    }
                },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEachIndexed { index, item ->
                    MenuButton(
                        item = item,
                        isSelected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                            item.onClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuButton(
    item: MenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasFocus by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .height(80.dp)
            .onFocusChanged { focusState ->
                hasFocus = focusState.isFocused
            },
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            hasFocus -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        tonalElevation = if (isSelected || hasFocus) 0.dp else 4.dp,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.size(32.dp)) {
                item.icon()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                item.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected || hasFocus) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isSelected -> Color.White
                    hasFocus -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
