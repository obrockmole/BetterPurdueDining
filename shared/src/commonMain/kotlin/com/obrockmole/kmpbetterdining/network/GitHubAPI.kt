package com.obrockmole.kmpbetterdining.network

import com.obrockmole.kmpbetterdining.models.GitHubRelease
import io.ktor.client.call.*
import io.ktor.client.request.*

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"

    suspend fun getLatestRelease(): GitHubRelease {
        return httpClient.get("${BASE_URL}repos/obrockmole/kmpbetterdining/releases/latest").body()
    }
}