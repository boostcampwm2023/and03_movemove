package com.everyone.movemove_android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.everyone.movemove_android.R
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple

@Composable
inline fun DefaultDialog(
    properties: DialogProperties,
    noinline onDismissRequest: (() -> Unit)? = null,
    crossinline content: @Composable () -> Unit
) {
    Dialog(
        properties = properties,
        onDismissRequest = { onDismissRequest?.let { it() } }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Center
        ) {
            content()
        }
    }
}

@Composable
fun LoadingDialog() {
    DefaultDialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Point
        )
    }
}

@Composable
fun LoadingDialogWithText(text: String) {
    DefaultDialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(horizontalAlignment = CenterHorizontally) {
            StyledText(
                text = text,
                style = Typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Point
            )
        }
    }
}

@Composable
fun ErrorDialog(
    text: String,
    onDismissRequest: () -> Unit
) {
    DefaultDialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .width(300.dp)
                .height(200.dp)
                .background(color = MaterialTheme.colorScheme.surface),
            verticalArrangement = SpaceBetween,
            horizontalAlignment = CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                StyledText(
                    modifier = Modifier.align(Center),
                    text = stringResource(id = R.string.notice),
                    style = Typography.bodyLarge
                )
            }

            StyledText(
                text = text,
                style = Typography.bodyMedium
            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickableWithoutRipple { onDismissRequest() }
            ) {
                StyledText(
                    modifier = Modifier.align(Center),
                    text = stringResource(id = R.string.confirm),
                    style = Typography.bodyLarge,
                    color = Point
                )
            }
        }
    }
}

@Composable
inline fun SelectThumbnailDialog(
    thumbnailList: List<ImageBitmap>,
    selectedThumbnail: ImageBitmap?,
    crossinline onClickThumbnail: (ImageBitmap) -> Unit,
    crossinline onClickComplete: () -> Unit,
    crossinline onDismissRequest: () -> Unit,
) {
    DefaultDialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(shape = RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .height(420.dp)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            StyledText(
                modifier = Modifier
                    .padding(24.dp)
                    .align(CenterHorizontally),
                text = stringResource(id = R.string.select_thumbnail_description),
                style = Typography.bodyLarge
            )

            if (thumbnailList.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),

                    ) {
                    items(thumbnailList) { thumbnail ->
                        Box(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(12.dp))
                                .width(160.dp)
                                .fillMaxHeight()
                                .border(
                                    width = 2.dp,
                                    color = if (thumbnail == selectedThumbnail) Point else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickableWithoutRipple { onClickThumbnail(thumbnail) },
                                bitmap = thumbnail,
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Center)
                            .size(24.dp), color = Point
                    )
                }
            }

            RoundedCornerButton(
                modifier = Modifier.padding(24.dp),
                buttonText = stringResource(id = R.string.complete),
                isEnabled = selectedThumbnail != null,
                onClick = { onClickComplete() }
            )
        }
    }
}