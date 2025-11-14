package com.obrockmole.betterdining.network

import com.obrockmole.betterdining.models.GraphQLRequest
import com.obrockmole.betterdining.models.MultiItemResponse
import com.obrockmole.betterdining.models.SearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface DiningApi {
    @POST("menus/v3/GraphQL")
    suspend fun getMultipleItems(@Body body: GraphQLRequest): Response<MultiItemResponse>

    @Headers("Accept: application/json")
    @GET("menus/v2/items/searchUpcoming/{query}")
    suspend fun searchUpcoming(@Path("query") query: String): Response<SearchResponse>
}
