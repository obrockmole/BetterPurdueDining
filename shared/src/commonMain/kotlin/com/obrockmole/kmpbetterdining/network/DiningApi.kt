package com.obrockmole.kmpbetterdining.network

import com.obrockmole.kmpbetterdining.models.GraphQLRequest
import com.obrockmole.kmpbetterdining.models.MultiItemResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

object DiningApi {
    private const val BASE_URL = "https://api.hfs.purdue.edu/"

    suspend fun getMultipleItems(requestBody: GraphQLRequest): MultiItemResponse {
        return httpClient.post("${BASE_URL}menus/v3/GraphQL") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }
}