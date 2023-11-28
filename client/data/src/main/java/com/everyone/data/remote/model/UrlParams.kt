package com.everyone.data.remote.model

data class UrlParams(
    val pathArray: Array<out String>,
    val queryList: List<Pair<String, String>>
)