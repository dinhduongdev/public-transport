-- https://stackoverflow.com/questions/574691/mysql-great-circle-distance-haversine-formula

-- tạo func để tối ưu sử dụng
-- DETERMINISTIC: Trả về cùng kết quả cho cùng một đầu vào,
-- không phụ thuộc vào trạng thái của cơ sở dữ liệu.

DELIMITER //
DROP FUNCTION IF EXISTS haversine_km;
CREATE FUNCTION haversine_km(
    lat1 DOUBLE, lng1 DOUBLE,
    lat2 DOUBLE, lng2 DOUBLE
) RETURNS DOUBLE
    DETERMINISTIC
    NO SQL
BEGIN
    RETURN 6371 * ACOS(
        COS(RADIANS(lat1)) * COS(RADIANS(lat2)) *
        COS(RADIANS(lng2) - RADIANS(lng1)) +
        SIN(RADIANS(lat1)) * SIN(RADIANS(lat2))
    );
END;
//
DELIMITER ;
