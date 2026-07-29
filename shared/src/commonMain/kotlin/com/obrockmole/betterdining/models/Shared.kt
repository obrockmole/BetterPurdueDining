package com.obrockmole.betterdining.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GraphQLRequest(
    val operationName: String? = null,
    val variables: Map<String, JsonElement>,
    val query: String
)
