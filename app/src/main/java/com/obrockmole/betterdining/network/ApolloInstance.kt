package com.obrockmole.betterdining.network

import com.apollographql.apollo.ApolloClient

object ApolloInstance {
    private const val BASE_URL = "https://api.hfs.purdue.edu/menus/v3/GraphQL"

    val apolloClient = ApolloClient.Builder()
        .serverUrl(BASE_URL)
        .build()
}