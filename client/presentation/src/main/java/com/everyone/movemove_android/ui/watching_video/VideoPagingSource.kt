package com.everyone.movemove_android.ui.watching_video

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.everyone.domain.model.Videos
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetVideosRandomUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun createVideoPagingSource(getVideosRandomUseCase: GetVideosRandomUseCase): Pager<Int, Videos> {
    return Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10,
            enablePlaceholders = true
        ),
        initialKey = 0,
        pagingSourceFactory = { VideoPagingSource(getVideosRandomUseCase = getVideosRandomUseCase) }
    )
}

class VideoPagingSource(private val getVideosRandomUseCase: GetVideosRandomUseCase) :
    PagingSource<Int, Videos>() {

    override fun getRefreshKey(state: PagingState<Int, Videos>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Videos> {
        val pageIndex = params.key ?: 0

        return try {
            var data: List<Videos>? = null

            getVideosRandomUseCase(
                limit = "10",
                category = WatchingVideoContract.Category.TOTAL.displayName,
                seed = ""
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        data = result.data.videos
                    }

                    is DataState.Failure -> {}
                }
            }.launchIn(CoroutineScope(Dispatchers.IO)).join()

            LoadResult.Page(
                data = data!!,
                prevKey = null,
                nextKey = if (data!!.isNotEmpty()) pageIndex + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}