package com.obrockmole.betterdining.network

import com.obrockmole.betterdining.models.GitHubRelease
import retrofit2.http.GET

interface GitHubAPI {
    @GET("repos/obrockmole/betterpurduedining/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease
}
