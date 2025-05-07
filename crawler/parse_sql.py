import json
import os

PROCESSED_DATA_DIR = "./data/processed"


def parse_route():
    with open(f"{PROCESSED_DATA_DIR}/routes.json", "r", encoding="utf-8") as f:
        routes = json.load(f)
    values = ", ".join(
        f"('{route['type']}', '{route['name']}', '{route['code']}')" for route in routes
    )
    return f"INSERT INTO route (type, name, code) VALUES {values};\n\n"


def parse_stations():
    with open(f"{PROCESSED_DATA_DIR}/stations.json", "r", encoding="utf-8") as f:
        stations = json.load(f)
    values = ", ".join(
        f"('{station['stop_name']}', '{station['address']}', '{station['street']}', "
        f"'{station['ward']}', '{station['zone']}', {station['lat']}, {station['lng']})"
        for station in stations
    )
    return f"INSERT INTO station (name,address,street,ward,zone,lat,lng) VALUES {values};\n\n"


def parse_route_variants():
    with open(f"{PROCESSED_DATA_DIR}/route_variants.json", "r", encoding="utf-8") as f:
        route_variants = json.load(f)
    values = ", ".join(
        f"({variant['route_id']}, {variant['is_outbound']}, "
        f"'{variant['name']}', {variant['distance']}, '{variant['start_stop']}', '{variant['end_stop']}')"
        for variant in route_variants
    )
    return f"INSERT INTO route_variant (route_id, is_outbound, name, distance, start_stop, end_stop) VALUES {values};\n\n"


def parse_schedules():
    with open(f"{PROCESSED_DATA_DIR}/schedules.json", "r", encoding="utf-8") as f:
        schedules = json.load(f)
    values = ", ".join(
        f"({schedule['route_variant_id']}, "
        f"'{'-'.join(schedule['start_date'].split('/')[::-1])}', "
        f"NULL, "
        f"{schedule['priority']})"
        for schedule in schedules
    )
    return f"INSERT INTO schedule (route_variant_id, start_date, end_date, priority) VALUES {values};\n\n"


def parse_schedule_schedule_days():
    with open(f"{PROCESSED_DATA_DIR}/schedule_days.json", "r", encoding="utf-8") as f:
        schedule_days = json.load(f)
    values = ", ".join(
        f"({schedule_day['schedule_id']}, '{schedule_day['day_of_week']}')"
        for schedule_day in schedule_days
    )
    return f"INSERT INTO schedule_day (schedule_id, day) VALUES {values};\n\n"


def parse_schedule_trips():
    with open(f"{PROCESSED_DATA_DIR}/trips.json", "r", encoding="utf-8") as f:
        schedule_trips = json.load(f)
    values = ", ".join(
        f"({schedule_trip['schedule_id']}, {schedule_trip['order']}, "
        f"'{schedule_trip['start_time']}', '{schedule_trip['end_time']}')"
        for schedule_trip in schedule_trips
    )
    return f"INSERT INTO schedule_trip (schedule_id, order, start_time, end_time) VALUES {values};\n\n"


def parse_stops():
    with open(f"{PROCESSED_DATA_DIR}/stops.json", "r", encoding="utf-8") as f:
        stops = json.load(f)
    values = ", ".join(
        f"({stop['route_variant_id']}, {stop['station_id']}, {stop['order']})"
        for stop in stops
    )
    return (
        f"INSERT INTO stop (route_variant_id, station_id, order) VALUES {values};\n\n"
    )


if __name__ == "__main__":
    os.makedirs("./sql", exist_ok=True)
    with open("./sql/import.sql", "w", encoding="utf-8") as f:
        f.writelines(
            [
                parse_route(),
                parse_stations(),
                parse_route_variants(),
                parse_schedules(),
                parse_schedule_schedule_days(),
                parse_schedule_trips(),
                parse_stops(),
            ]
        )
