package com.everyone.movemove_android.ui.image_cropper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickSectionSelector
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.State
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.ClipOpDim
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.util.BranchedModifier
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.util.pxToDp

private const val DEFAULT_SECTION_SELECTOR_SIZE = 500f
private const val BORDER_PX = 8f

@Composable
fun ImageCropperScreen(viewModel: ImageCropperViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        ImageBoard(state, event)

        Divider(color = if (isSystemInDarkTheme()) BorderInDark else Color.White) // todo : 라이트 모드의 Border 색상이 정해지지 않음

        ControlBoard()
    }
}

@Composable
private fun ColumnScope.ImageBoard(
    state: State,
    event: (Event) -> Unit
) {
    var offsetState by remember { mutableStateOf(Offset(0f, 0f)) }
    var scaleState by remember { mutableFloatStateOf(1f) }
    var rotationState by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = BranchedModifier(
            value = state.isSectionSelectorSelected,
            onDefault = {
                Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(color = MaterialTheme.colorScheme.surface)
            },
            onFalse = { modifier ->
                modifier
                    .border(
                        width = BORDER_PX.pxToDp(),
                        color = Point
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, offset, zoom, rotation ->
                            offsetState += offset
                            if (scaleState * zoom > 0.5f) scaleState *= zoom
                            rotationState += rotation
                        }
                    }
                    .clickableWithoutRipple { event(OnClickImage) }
            }
        )
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .absoluteOffset(
                    x = offsetState.x.pxToDp(),
                    y = offsetState.y.pxToDp()
                )
                .graphicsLayer(
                    scaleX = scaleState,
                    scaleY = scaleState,
                    rotationZ = rotationState
                ),
            model = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcbm2On%2FbtsAMUCNzYT%2FOMfDTvo682j6X0xiKv3a20%2Fimg.jpg",
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        SectionSelector(state, event)
    }
}

@Composable
private fun BoxScope.SectionSelector(
    state: State,
    event: (Event) -> Unit
) {
    var sizeState by remember { mutableFloatStateOf(DEFAULT_SECTION_SELECTOR_SIZE) }
    var offsetState by remember { mutableStateOf(Offset(DEFAULT_SECTION_SELECTOR_SIZE / 2f, DEFAULT_SECTION_SELECTOR_SIZE / 2f)) }
    var displayOffsetState by remember { mutableStateOf(Offset(0f, 0f)) }
    var boardSizeState by remember { mutableStateOf(Size(0f, 0f)) }

    displayOffsetState = Offset(
        x = offsetState.x - sizeState / 2f,
        y = offsetState.y - sizeState / 2f
    )

    Canvas(
        modifier = BranchedModifier(
            value = state.isSectionSelectorSelected,
            onDefault = {
                Modifier
                    .align(Center)
                    .fillMaxSize()
                    .onGloballyPositioned { boardSizeState = Size(it.size.width.toFloat(), it.size.height.toFloat()) }
            },
            onTrue = { modifier ->
                modifier
                    .pointerInput(Unit) {
                        detectTransformGestures { _, offset, zoom, _ ->
                            if (displayOffsetState.x + offset.x in 0f..boardSizeState.width - sizeState) {
                                offsetState = offsetState.copy(x = offsetState.x + offset.x)
                            }

                            if (displayOffsetState.y + offset.y in 0f..boardSizeState.height - sizeState) {
                                offsetState = offsetState.copy(y = offsetState.y + offset.y)
                            }

                            if (displayOffsetState.x in 0f..boardSizeState.width - sizeState * zoom &&
                                displayOffsetState.y in 0f..boardSizeState.height - sizeState * zoom
                            ) {
                                sizeState *= zoom
                            }
                        }
                    }
                    .clickableWithoutRipple { event(OnClickSectionSelector) }
            }
        ),
        onDraw = {
            val circlePath = Path().apply {
                addOval(
                    Rect(
                        offset = displayOffsetState,
                        size = Size(sizeState, sizeState)
                    )
                )
            }

            if (state.isSectionSelectorSelected) {
                drawRect(
                    color = Point,
                    topLeft = displayOffsetState,
                    size = Size(sizeState, sizeState),
                    style = Stroke(width = BORDER_PX)
                )

                drawOval(
                    color = Point,
                    topLeft = displayOffsetState,
                    size = Size(sizeState, sizeState),
                    style = Stroke(width = BORDER_PX),
                )
            }

            clipPath(circlePath, clipOp = ClipOp.Difference) {
                drawRect(brush = SolidColor(ClipOpDim))
            }
        }
    )
}

@Composable
private fun ColumnScope.ControlBoard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.4f)
            .background(color = MaterialTheme.colorScheme.background)
    ) {

    }
}