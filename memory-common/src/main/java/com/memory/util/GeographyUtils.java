package com.memory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 지리학적 계산을 위한 유틸리티 클래스
 */
public class GeographyUtils {

    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (킬로미터)

    /**
     * Haversine 공식을 사용하여 두 지점 간의 거리를 계산합니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (킬로미터)
     */
    public static BigDecimal calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("위도와 경도는 null일 수 없습니다.");
        }

        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 거리 기반으로 점수를 계산합니다.
     * maxDistanceForFullScore 이내: 1000점
     * 이후 10km당 1점씩 감점
     *
     * @param distanceKm 거리 (킬로미터)
     * @param maxDistanceForFullScore 만점을 받을 수 있는 최대 거리 (킬로미터)
     * @return 계산된 점수
     */
    public static Integer calculateScore(BigDecimal distanceKm, int maxDistanceForFullScore) {
        if (distanceKm == null) {
            return 0;
        }

        double distance = distanceKm.doubleValue();

        if (distance <= maxDistanceForFullScore) {
            return 1000;
        }

        // 10km 당 1점 감점
        double overDistance = distance - maxDistanceForFullScore;
        int penalty = (int) Math.floor(overDistance / 10.0);

        return Math.max(0, 1000 - penalty);
    }
}
