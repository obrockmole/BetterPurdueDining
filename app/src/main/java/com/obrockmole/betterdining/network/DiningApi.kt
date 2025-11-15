package com.obrockmole.betterdining.network

import com.obrockmole.betterdining.models.GraphQLRequest
import com.obrockmole.betterdining.models.MultiItemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DiningApi {
    @POST("menus/v3/GraphQL")
    suspend fun getMultipleItems(@Body body: GraphQLRequest): Response<MultiItemResponse>
}
