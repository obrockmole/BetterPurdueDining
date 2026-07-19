package com.obrockmole.kmpbetterdining.models

data class GraphQLRequest(
    val operationName: String? = null,
    val variables: Any,
    val query: String
)
