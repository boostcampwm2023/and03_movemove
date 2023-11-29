package com.everyone.movemove_android.ui.image_cropper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect.CropImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickCompleteButton
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickSectionSelector
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetBoardSize
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageOffset
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageRotation
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageScale
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorOffsetX
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorOffsetY
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorSize
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.State
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.ClipOpDim
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.BranchedModifier
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.util.pxToDp
import kotlinx.coroutines.flow.collectLatest

private const val BORDER_PX = 8f

@Composable
fun ImageCropperScreen(viewModel: ImageCropperViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is CropImage -> {

                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        ImageBoard(state, event)

        Divider(color = if (isSystemInDarkTheme()) BorderInDark else Color.White) // todo : 라이트 모드의 Border 색상이 정해지지 않음

        DescriptionSection()

        CropSection(state, event)

        CompleteButtonSection(state, event)
    }
}

@Composable
private fun ColumnScope.ImageBoard(
    state: State,
    event: (Event) -> Unit
) {
    with(state) {
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
                                event(SetImageOffset(offset))
                                event(SetImageScale(zoom))
                                event(SetImageRotation(rotation))
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
                        x = imageState.offset.x.pxToDp(),
                        y = imageState.offset.y.pxToDp()
                    )
                    .graphicsLayer(
                        scaleX = imageState.scale,
                        scaleY = imageState.scale,
                        rotationZ = imageState.rotation
                    ),
                model = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcbm2On%2FbtsAMUCNzYT%2FOMfDTvo682j6X0xiKv3a20%2Fimg.jpg",
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            SectionSelector(state, event)
        }
    }
}

@Composable
private fun BoxScope.SectionSelector(
    state: State,
    event: (Event) -> Unit
) {
    with(state) {
        Canvas(
            modifier = BranchedModifier(
                value = state.isSectionSelectorSelected,
                onDefault = {
                    Modifier
                        .align(Center)
                        .fillMaxSize()
                        .onGloballyPositioned {
                            event(
                                SetBoardSize(
                                    Size(
                                        width = it.size.width.toFloat(),
                                        height = it.size.height.toFloat()
                                    )
                                )
                            )
                        }
                },
                onTrue = { modifier ->
                    modifier
                        .pointerInput(Unit) {
                            detectTransformGestures { _, offset, zoom, _ ->
                                event(SetSectionSelectorOffsetX(offset.x))
                                event(SetSectionSelectorOffsetY(offset.y))
                                event(SetSectionSelectorSize(zoom))
                            }
                        }
                        .clickableWithoutRipple { event(OnClickSectionSelector) }
                }
            ),
            onDraw = {
                val circleOffset = Offset(
                    x = sectionSelectorState.offsetX - sectionSelectorState.size / 2f,
                    y = sectionSelectorState.offsetY - sectionSelectorState.size / 2f
                )

                val circleSize = Size(
                    width = sectionSelectorState.size,
                    height = sectionSelectorState.size
                )

                val circlePath = Path().apply {
                    addOval(
                        Rect(
                            offset = circleOffset,
                            size = circleSize
                        )
                    )
                }

                if (state.isSectionSelectorSelected) {
                    drawRect(
                        color = Point,
                        topLeft = circleOffset,
                        size = circleSize,
                        style = Stroke(width = BORDER_PX)
                    )

                    drawOval(
                        color = Point,
                        topLeft = circleOffset,
                        size = circleSize,
                        style = Stroke(width = BORDER_PX),
                    )
                }

                clipPath(circlePath, clipOp = ClipOp.Difference) {
                    drawRect(brush = SolidColor(ClipOpDim))
                }
            }
        )
    }
}

@Composable
private fun DescriptionSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        StyledText(
            modifier = Modifier.align(Center),
            text = stringResource(id = R.string.image_cropper_description),
            style = Typography.bodyMedium
        )
    }
}

@Composable
private fun ColumnScope.CropSection(
    state: State,
    event: (Event) -> Unit,
) {
    Column(
        modifier = Modifier
            .align(CenterHorizontally)
            .fillMaxWidth()
            .weight(0.4f)
            .background(color = MaterialTheme.colorScheme.background)
            .clickableWithoutRipple { },
        horizontalAlignment = CenterHorizontally
    ) {
        StyledText(
            text = stringResource(id = R.string.crop),
            style = Typography.labelLarge,
            color = Point
        )

        if (state.croppedImage != null) {
            Image(
                modifier = Modifier
                    .padding(24.dp)
                    .weight(1f),
                bitmap = state.croppedImage,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CompleteButtonSection(
    state: State,
    event: (Event) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        RoundedCornerButton(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .height(48.dp)
                .background(color = MaterialTheme.colorScheme.background),
            buttonText = stringResource(id = R.string.complete),
            isEnabled = state.croppedImage != null,
            onClick = {
                event(OnClickCompleteButton)
            }
        )
    }
}