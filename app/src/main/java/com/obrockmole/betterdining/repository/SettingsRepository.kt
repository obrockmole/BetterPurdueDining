package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.models.GitHubRelease
import com.obrockmole.betterdining.network.RetrofitInstance
import com.obrockmole.betterdining.utils.Logger

private const val LOG_TAG = "SettingsRepository"

class SettingsRepository {
    suspend fun getLatestRelease(): GitHubRelease? {
        var latestRelease: GitHubRelease? = null;

        try {
            latestRelease = RetrofitInstance.gitHubApi.getLatestRelease()
            Logger.LogDebug(LOG_TAG, "Got latest release ${latestRelease.tag_name}")
        } catch (e: Exception) {
            Logger.LogError(LOG_TAG, "Error fetching latest release", e)
        }

        return latestRelease
    }
}
