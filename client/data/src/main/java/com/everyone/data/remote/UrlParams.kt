package com.everyone.data.remote

data class UrlParams(
    val pathArray: Array<String>,
    val queryList: List<Pair<String, String>>
)