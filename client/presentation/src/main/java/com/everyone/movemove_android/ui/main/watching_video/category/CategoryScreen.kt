package com.everyone.movemove_android.ui.main.watching_video.category

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.everyone.movemove_android.R
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Event.OnClickedCategory
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Event.OnCategorySelected
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoViewModel
import com.everyone.movemove_android.ui.theme.CategoryBackgroundInDark
import com.everyone.movemove_android.ui.util.clickableWithoutRipple


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CategoryScreen(viewModel: WatchingVideoViewModel = hiltViewModel()) {

    val (state, event, effect) = use(viewModel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CategoryBackgroundInDark.copy(0.95f))
            .clickable(enabled = false) {}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            StyledText(
                modifier = Modifier.padding(top = 28.dp),
                text = stringResource(R.string.category_change_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(state.categoryList) { category ->
                    CategoryItem(
                        category = category.name,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .clickableWithoutRipple {
                                event(OnCategorySelected(selectedCategory = category))
                            }
                    )
                }
            }
        }

        MoveMoveIconButton(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .size(40.dp)
                .background(Color.White),
            iconButtonRes = R.drawable.ic_close,
            padding = PaddingValues(
                start = 21.dp,
                top = 21.dp
            ),
            tint = Color.Black,
            onClick = { event(OnClickedCategory) }
        )
    }

}

@Composable
fun CategoryItem(category: String, modifier: Modifier) {
    StyledText(
        modifier = modifier,
        text = category,
        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
    )
}

@Composable
fun MoveMoveIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconButtonRes: Int,
    padding: PaddingValues,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(id = iconButtonRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = tint
            )
        }
    }
}
