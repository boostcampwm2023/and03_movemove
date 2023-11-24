package com.everyone.data.remote

class UrlParamsBuilder {
    private var pathList = mutableListOf<String>()
    private var queryList = mutableListOf<Pair<String, String>>()

    fun addPaths(vararg paths: String): UrlParamsBuilder {
        pathList.addAll(paths)
        return this
    }

    fun addQueries(vararg queries: Pair<String, String>): UrlParamsBuilder {
        queryList.addAll(queries)
        return this
    }

    fun build() = UrlParams(
        pathArray = pathList.toTypedArray(),
        queryList = queryList
    )
}