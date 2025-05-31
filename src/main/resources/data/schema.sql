USE publictransport;

-- Tạm thời tắt kiểm tra khóa ngoại để xóa bảng
SET FOREIGN_KEY_CHECKS = 0;

-- XÓA BẢNG NẾU TỒN TẠI (theo thứ tự phụ thuộc ngược)
DROP TABLE IF EXISTS Stop;
DROP TABLE IF EXISTS Station;
DROP TABLE IF EXISTS Location;
DROP TABLE IF EXISTS Coordinates;
DROP TABLE IF EXISTS ScheduleTrip;
DROP TABLE IF EXISTS ScheduleDay;
DROP TABLE IF EXISTS Schedule;
DROP TABLE IF EXISTS RouteVariant;
DROP TABLE IF EXISTS Route;
DROP TABLE IF EXISTS Notification;
DROP TABLE IF EXISTS Report;
DROP TABLE IF EXISTS StatusReport;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Role;
DROP TABLE IF EXISTS Favorite;
DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS TrafficReport;
-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;

-- ==================== TẠO CÁC BẢNG ====================

-- Role
CREATE TABLE Role
(
    name VARCHAR(20) PRIMARY KEY
);
INSERT INTO Role (name)
VALUES ('ADMIN'),
       ('USER');


-- User
CREATE TABLE User
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    password   VARCHAR(255),
    role       VARCHAR(20),
    avatar     VARCHAR(255),
    CONSTRAINT uk_user_email UNIQUE (email),
    FOREIGN KEY (role) REFERENCES Role (name)
);


-- Report
CREATE TABLE TrafficReport
(
    id              BIGINT                                       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT,
    location        VARCHAR(255)                                 NOT NULL,
    latitude        DOUBLE,
    longitude       DOUBLE,
    description     TEXT                                         NOT NULL,
    status          ENUM ('CLEAR', 'MODERATE', 'HEAVY', 'STUCK') NOT NULL,
    approval_status ENUM ('PENDING', 'APPROVED', 'REJECTED')     NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    image_url       VARCHAR(500),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE SET NULL
);


-- Notification
CREATE TABLE Notification
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    title      VARCHAR(255),
    message    TEXT     NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read    BOOLEAN           DEFAULT FALSE,
    user_id    BIGINT   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE
);


-- Route
CREATE TABLE Route
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20),
    name VARCHAR(100),
    type ENUM ('BUS', 'ELECTRIC_TRAIN') NOT NULL
);


-- RouteVariant
CREATE TABLE RouteVariant
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id    BIGINT,
    is_outbound BOOLEAN,
    name        VARCHAR(255),
    distance    FLOAT,
    start_stop  nvarchar(255),
    end_stop    nvarchar(255),
    FOREIGN KEY (route_id) REFERENCES Route (id)
);


-- Schedule
CREATE TABLE Schedule
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_variant_id BIGINT,
    start_date       DATETIME,
    end_date         DATETIME,
    priority         INT,
    FOREIGN KEY (route_variant_id) REFERENCES RouteVariant (id)
);


-- Schedule_DayOfWeek
CREATE TABLE ScheduleDay
(
    schedule_id BIGINT,
    day         ENUM ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'),
    PRIMARY KEY (schedule_id, day),
    FOREIGN KEY (schedule_id) REFERENCES Schedule (id)
);


-- ScheduleTrip
CREATE TABLE ScheduleTrip
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id   BIGINT,
    start_time    TIME,
    end_time      TIME,
    trip_order    INT,
    license_plate VARCHAR(20),
    FOREIGN KEY (schedule_id) REFERENCES Schedule (id)
);


-- Station
CREATE TABLE Station
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(255),
    lat     DOUBLE,
    lng     DOUBLE,
    address VARCHAR(255),
    street  VARCHAR(100),
    ward    VARCHAR(100),
    zone    VARCHAR(100)
);


-- Stop
CREATE TABLE Stop
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_variant_id BIGINT,
    station_id       BIGINT,
    stop_order       INT,
    FOREIGN KEY (station_id) REFERENCES Station (id),
    FOREIGN KEY (route_variant_id) REFERENCES RouteVariant (id)
);


CREATE TABLE Favorite
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT,
    target_id   BIGINT,
    target_type ENUM ('ROUTE', 'SCHEDULE'),
    is_observed BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE
);


CREATE TABLE Rating
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    route_id   BIGINT   NOT NULL,
    score      INT      NOT NULL CHECK (score >= 1 AND score <= 5),
    comment    TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rating_user FOREIGN KEY (user_id) REFERENCES User (id),
    CONSTRAINT fk_rating_route FOREIGN KEY (route_id) REFERENCES Route (id),
    CONSTRAINT uk_rating_user_route UNIQUE (user_id, route_id)
);