package com.obrockmole.betterdining.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val PURDUE_BASE_URL = "https://api.hfs.purdue.edu/"
    private const val GITHUB_BASE_URL = "https://api.github.com/"

    val diningApi: DiningApi by lazy {
        Retrofit.Builder()
            .baseUrl(PURDUE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiningApi::class.java)
    }

    val gitHubApi: GitHubAPI by lazy {
        Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubAPI::class.java)
    }
}
