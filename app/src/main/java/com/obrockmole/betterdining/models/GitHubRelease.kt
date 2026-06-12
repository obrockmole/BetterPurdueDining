package com.obrockmole.betterdining.models

import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    val name: String,
    val tag_name: String,
    val html_url: String
)