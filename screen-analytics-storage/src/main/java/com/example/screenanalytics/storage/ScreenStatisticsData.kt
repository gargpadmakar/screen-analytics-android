package com.example.screenanalytics.storage

data class ScreenStatisticsData(
    val screenName: String,
    val viewCount: Long,
    val averageDurationMillis: Long,
    val minDurationMillis: Long?,
    val maxDurationMillis: Long?
)
