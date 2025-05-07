import json
from datetime import datetime, UTC
import utils

DATE_NOW = datetime.now(UTC).strftime("%d/%m/%Y")

DAY_OF_WEEK_MAPPER = {
    "T2": "MONDAY",
    "T3": "TUESDAY",
    "T4": "WEDNESDAY",
    "T5": "THURSDAY",
    "T6": "FRIDAY",
    "T7": "SATURDAY",
    "CN": "SUNDAY",
}

ROUTE_EXCLUDED_KEYS = [
    "raw_id",
    "num_of_seats",
    "outbound",
    "inbound",
    "inbound_desc",
    "inbound_name",
    "outbound_desc",
    "outbound_name",
]
new_routes = []

ROUTE_VARIANT_EXCLUDED_KEYS = [
    "duration",
    "bound_id",
    "apply_dates",
    "operation_time",
    "total_trip",
    "trips",
    "stops",
]
route_variants_counter = 0
route_variants = []

stops = []

schedules_counter = 0
schedules = []

schedule_day = []

trips = []

with open("./data/raw/routes.json", "r", encoding="utf-8") as f:
    raw_routes: list[dict] = json.load(f)

with open("./data/raw/stations.json", "r", encoding="utf-8") as f:
    raw_stations: dict[dict] = json.load(f)
    for idx, key in enumerate(raw_stations, start=1):
        raw_stations[key]["id"] = idx


def process_stops(route_variant_id, list_of_stops):
    global stops
    # process stops
    stops.extend(
        [
            {
                "route_variant_id": route_variant_id,
                "station_id": raw_stations[stop]["id"],
                "order": idx,
            }
            for idx, stop in enumerate(list_of_stops, start=1)
        ]
    )


def process_schedule(
    route_variant_id, apply_days_of_week, start_date=DATE_NOW, end_date=None
):
    global schedules
    global schedules_counter

    schedules_counter += 1

    # process schedules
    schedules.append(
        {
            "id": schedules_counter,
            "route_variant_id": route_variant_id,
            "start_date": start_date,
            "end_date": end_date,
            "priority": 1,
        }
    )

    # process schedule day
    for day in apply_days_of_week.split(", "):
        schedule_day.append(
            {
                "schedule_id": schedules_counter,
                "day_of_week": DAY_OF_WEEK_MAPPER[day],
            }
        )
    return schedules_counter


def process_trips(schedule_id, list_of_trips):
    global trips
    # process trips
    trips.extend([{"schedule_id": schedule_id, **trip} for trip in list_of_trips])


def process_route_variants(route_id, is_outbound, data):
    global route_variants_counter
    global route_variants
    # process route variants
    route_variants_counter += 1
    route_variants.append(
        {
            "id": route_variants_counter,
            "route_id": route_id,
            "is_outbound": is_outbound,
            "name": "Lượt đi" if is_outbound else "Lượt về",
            **{k: v for k, v in data.items() if k not in ROUTE_VARIANT_EXCLUDED_KEYS},
        }
    )
    # process stops
    process_stops(route_variants_counter, data["stops"])
    # process schedules
    schedule_id = process_schedule(
        route_variants_counter,
        data["apply_dates"],
    )
    # process trips
    process_trips(schedule_id, data["trips"])


if __name__ == "__main__":
    for idx, raw_route in enumerate(raw_routes, start=1):
        new_routes.append(
            {
                "id": idx,
                "type": "BUS",
                **{k: v for k, v in raw_route.items() if k not in ROUTE_EXCLUDED_KEYS},
            }
        )
        # add outbound
        process_route_variants(
            route_id=idx, is_outbound=True, data=raw_route["outbound"]
        )
        # add inbound
        process_route_variants(
            route_id=idx, is_outbound=False, data=raw_route["inbound"]
        )

    PROCESSED_DATA_DIR = "./data/processed"

    # save to json files using utils module
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/routes.json", new_routes)
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/route_variants.json", route_variants)
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/stops.json", stops)
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/schedules.json", schedules)
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/schedule_days.json", schedule_day)
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/trips.json", trips)

    # save stations
    stations = list(raw_stations.values())
    utils.write_json_file(f"{PROCESSED_DATA_DIR}/stations.json", stations)
