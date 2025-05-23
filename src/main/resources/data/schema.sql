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

-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;

-- === TẠO CÁC BẢNG ===

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

CREATE TABLE StatusReport
(
    name VARCHAR(20) PRIMARY KEY
);
INSERT INTO StatusReport (name)
VALUES ('PENDING'),
       ('VERIFIED');

-- Report
CREATE TABLE Report
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    latitude    DOUBLE NOT NULL,
    longitude   DOUBLE NOT NULL,
    description TEXT,
    image       VARCHAR(255),
    status      VARCHAR(20),
    user_id     BIGINT,
    FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE,
    FOREIGN KEY (status) REFERENCES StatusReport (name) ON DELETE CASCADE
);

-- Notification
CREATE TABLE Notification
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    time    DATETIME NOT NULL,
    message TEXT     NOT NULL,
    user_id BIGINT,
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
-- INSERT INTO routevariant (route_id, is_outbound, name, distance, start_stop, end_stop)
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
-- INSERT INTO schedule (routevariant_id, start_date, end_date, priority)
-- Schedule_DayOfWeek
CREATE TABLE ScheduleDay
(
    schedule_id BIGINT,
    day         ENUM ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'),
    PRIMARY KEY (schedule_id, day),
    FOREIGN KEY (schedule_id) REFERENCES Schedule (id)
);
-- INSERT INTO schedule_day (schedule_id, day)

-- ScheduleTrip
CREATE TABLE ScheduleTrip
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id BIGINT,
    start_time  TIME,
    end_time    TIME,
    trip_order  INT,
    license_plate VARCHAR(20),
    FOREIGN KEY (schedule_id) REFERENCES Schedule (id)
);
-- INSERT INTO scheduletrip (schedule_id, start_time, end_time) VALUES

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
-- INSERT INTO stop (route_variant_id, station_id, stop_order)

CREATE TABLE Favorite
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT,
    target_id   BIGINT,
    target_type ENUM ('ROUTE', 'SCHEDULE'),
    is_observed BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE
)