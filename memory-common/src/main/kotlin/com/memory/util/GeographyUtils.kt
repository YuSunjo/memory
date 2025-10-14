package com.memory.util

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

object GeographyUtils {

    private const val EARTH_RADIUS_KM = 6371.0

    @JvmStatic
    fun calculateDistance(lat1: BigDecimal, lon1: BigDecimal, lat2: BigDecimal, lon2: BigDecimal): BigDecimal {
        val lat1Rad = Math.toRadians(lat1.toDouble())
        val lon1Rad = Math.toRadians(lon1.toDouble())
        val lat2Rad = Math.toRadians(lat2.toDouble())
        val lon2Rad = Math.toRadians(lon2.toDouble())

        val deltaLat = lat2Rad - lat1Rad
        val deltaLon = lon2Rad - lon1Rad

        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = EARTH_RADIUS_KM * c

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP)
    }

    @JvmStatic
    fun calculateScore(distanceKm: BigDecimal?, maxDistanceForFullScore: Int): Int {
        if (distanceKm == null) {
            return 0
        }

        val distance = distanceKm.toDouble()

        if (distance <= maxDistanceForFullScore) {
            return 1000
        }

        val overDistance = distance - maxDistanceForFullScore
        val penalty = floor(overDistance / 10.0).toInt()

        return maxOf(0, 1000 - penalty)
    }
}
