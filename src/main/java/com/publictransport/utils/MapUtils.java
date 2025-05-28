package com.publictransport.utils;

import com.publictransport.models.Coordinates;

import java.util.Map;
import java.util.Optional;

import static com.publictransport.utils.StringUtils.isEmpty;

public class MapUtils {
    public static void putIfNotEmpty(Map map, Object key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            map.put(key, value);
        }
    }

    /**
     * Chuyển đổi chuỗi tọa độ từ định dạng "lat;lng" sang đối tượng Coordinates.
     * Nếu chuỗi không hợp lệ, trả về Optional.empty().
     *
     * @param coordinatesStr Chuỗi tọa độ cần chuyển đổi
     * @return Optional chứa đối tượng Coordinates nếu chuỗi hợp lệ, ngược lại là Optional.empty()
     */
    public static Optional<Coordinates> convertToCoordinates(String coordinatesStr) {
        if (isEmpty(coordinatesStr)) {
            return Optional.empty();
        }
        String[] parts = coordinatesStr.split(";");
        if (parts.length != 2) {
            return Optional.empty();
        }
        double lat, lng;
        try {
            lat = Double.parseDouble(parts[0]);
            lng = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
            return Optional.of(new Coordinates(lat, lng));
        }
        return Optional.empty();
    }

    public static boolean isValidCoordinates(String coordinatesStr) {
        return convertToCoordinates(coordinatesStr).isPresent();
    }

    public static double haversineDistance(Coordinates c1, Coordinates c2) {
        final int R = 6371; // Bán kính Trái Đất (km)
        double latDistance = Math.toRadians(c2.getLat() - c1.getLat());
        double lonDistance = Math.toRadians(c2.getLng() - c1.getLng());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(c1.getLat())) * Math.cos(Math.toRadians(c2.getLat()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // km
    }
}
