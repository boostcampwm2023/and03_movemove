package com.everyone.data.remote.model

class UrlParamsBuilder {
    private var pathArray: Array<out String> = arrayOf()
    private var queryList = mutableListOf<Pair<String, String>>()

    fun addPaths(vararg paths: String): UrlParamsBuilder {
        pathArray = paths
        return this
    }

    fun addQueries(vararg queries: Pair<String, String>): UrlParamsBuilder {
        queryList.addAll(queries)
        return this
    }

    fun build() = UrlParams(
        pathArray = pathArray,
        queryList = queryList
    )
}