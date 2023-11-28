package com.everyone.movemove_android.ui.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.everyone.movemove_android.R
import com.everyone.movemove_android.R.drawable.ic_heart
import com.everyone.movemove_android.R.drawable.img_basic_profile
import com.everyone.movemove_android.R.string.edit_profile
import com.everyone.movemove_android.R.string.profile_name
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple

@Composable
fun MyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Image(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .size(110.dp)
                .clip(shape = CircleShape),
            painter = painterResource(id = img_basic_profile),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledText(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            text = stringResource(id = profile_name),
            style = Typography.labelLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickableWithoutRipple {  },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_edit),
                contentDescription = null,
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(12.dp))

            StyledText(
                text = stringResource(edit_profile),
                style = Typography.bodyLarge
            )
        }


        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickableWithoutRipple {  },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_my_video),
                contentDescription = null,
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(12.dp))

            StyledText(
                text = stringResource(R.string.my_video),
                style = Typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 4.dp)
                .clickableWithoutRipple {  },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = ic_heart),
                contentDescription = null,
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(12.dp))

            StyledText(
                text = stringResource(R.string.my_scored_video),
                style = Typography.bodyLarge
            )
        }
    }
}