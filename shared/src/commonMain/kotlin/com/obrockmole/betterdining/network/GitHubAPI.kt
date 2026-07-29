package com.obrockmole.betterdining.network

import com.obrockmole.betterdining.models.GitHubRelease
import io.ktor.client.call.*
import io.ktor.client.request.*

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"

    suspend fun getLatestRelease(): GitHubRelease {
        return httpClient.get("${BASE_URL}repos/obrockmole/betterpurduedining/releases/latest").body()
    }
}