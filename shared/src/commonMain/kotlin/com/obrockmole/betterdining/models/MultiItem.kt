package com.obrockmole.betterdining.models

import kotlinx.serialization.Serializable

@Serializable
data class MultiItemResponse(
    val data: Map<String, ItemDetails>
)
