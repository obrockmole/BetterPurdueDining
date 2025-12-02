package com.obrockmole.betterdining.models

data class GraphQLRequest(
    val operationName: String? = null,
    val variables: Any,
    val query: String
)
