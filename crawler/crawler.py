import asyncio
import httpx
import utils

ROUTES_URL = "http://apicms.ebms.vn/businfo/getallroute"
ROUTE_INFO_URL = "http://apicms.ebms.vn/businfo/getroutebyid"
TIME_TABLE_URL = "http://apicms.ebms.vn/businfo/gettimetablebyroute"
BOUND_URL = "http://apicms.ebms.vn/businfo/getvarsbyroute"
STOP_URL = "http://apicms.ebms.vn/businfo/getstopsbyvar"
PATHS_URL = "http://apicms.ebms.vn/businfo/getpathsbyvar"
TRIPS_URL = "http://apicms.ebms.vn/businfo/gettripsbytimetable"

stations = {}


def get_bounds_idx(data: list) -> tuple:
    outbound_idx = None
    inbound_idx = None
    for idx, item in enumerate(data):
        if item.get("Outbound"):
            outbound_idx = idx
        else:
            inbound_idx = idx
    res = outbound_idx, inbound_idx
    if outbound_idx is None or inbound_idx is None:
        raise ValueError(
            f"Invalid data format: missing outbound or inbound index {res}"
        )
    return res


def extract_bound_data(bound_data):
    return {
        "distance": bound_data["Distance"],
        "duration": bound_data["RunningTime"],
        "start_stop": bound_data["StartStop"],
        "end_stop": bound_data["EndStop"],
        "bound_id": bound_data["RouteVarId"],
    }


async def extract_time_table_data(time_table_data, route_id, client):
    trips = (
        await client.get(f"{TRIPS_URL}/{route_id}/{time_table_data['TimeTableId']}")
    ).json()

    return {
        "apply_dates": time_table_data["ApplyDates"],
        "operation_time": time_table_data["OperationTime"],
        "total_trip": time_table_data["TotalTrip"],
        "trips": [
            {"order": idx, "start_time": trip["StartTime"], "end_time": trip["EndTime"]}
            for idx, trip in enumerate(trips, start=1)
        ],
    }


def extract_stop_data(stop_data):
    return {
        "stop_name": stop_data["Name"],
        "stop_code": stop_data["Code"],
        "address": stop_data["AddressNo"],
        "street": stop_data["Street"],
        "ward": stop_data["Ward"],
        "zone": stop_data["Zone"],
        "lat": stop_data["Lat"],
        "lng": stop_data["Lng"],
    }


def cache_stop(stop_data):
    stop_id = str(stop_data["StopId"])
    if stop_id not in stations:
        stations[stop_id] = extract_stop_data(stop_data)
    return stop_id


async def get_stops_id_or_cache(client: httpx.AsyncClient, route_id, bound_id) -> list:
    """get stops id by route id and bound id
    - if stops id is already in cache, return it
    - else, get stops id from api and cache it
    """
    res = (await client.get(f"{STOP_URL}/{route_id}/{bound_id}")).json()
    return [cache_stop(stop) for stop in res]


async def get_bounds_info(client: httpx.AsyncClient, route_id) -> list:
    response = (await client.get(f"{BOUND_URL}/{route_id}")).json()
    outbound_idx, inbound_idx = get_bounds_idx(response)

    res = {
        "outbound": extract_bound_data(response[outbound_idx]),
        "inbound": extract_bound_data(response[inbound_idx]),
    }

    outbound_id = res["outbound"]["bound_id"]
    inbound_id = res["inbound"]["bound_id"]

    time_table_data = (await client.get(f"{TIME_TABLE_URL}/{route_id}")).json()
    outbound_data = next(
        item for item in time_table_data if item["RouteVarId"] == outbound_id
    )
    inbound_data = next(
        item for item in time_table_data if item["RouteVarId"] == inbound_id
    )

    res["outbound"].update(
        (await extract_time_table_data(outbound_data, route_id, client))
    )
    res["outbound"]["stops"] = await get_stops_id_or_cache(
        client, route_id, outbound_id
    )
    res["inbound"].update(
        (await extract_time_table_data(inbound_data, route_id, client))
    )
    res["inbound"]["stops"] = await get_stops_id_or_cache(client, route_id, inbound_id)

    return res


async def get_route_info(client: httpx.AsyncClient, route_id) -> dict:
    response = (await client.get(f"{ROUTE_INFO_URL}/{route_id}")).json()
    transformed_route_info = {
        "inbound_desc": str(response["InBoundDescription"]).strip("¿"),
        "inbound_name": str(response["InBoundName"]).strip("¿"),
        "outbound_desc": str(response["OutBoundDescription"]).strip("¿"),
        "outbound_name": str(response["OutBoundName"]).strip("¿"),
        "num_of_seats": int(response["NumOfSeats"].split(" - ")[-1]),
    }
    return transformed_route_info  # It's good practice to return the processed data


def extract_route_data(route_data):
    return {
        "raw_id": route_data["RouteId"],
        "name": route_data["RouteName"],
        "code": route_data["RouteNo"],
    }


async def get_routes(client: httpx.AsyncClient) -> list:
    response = (await client.get(ROUTES_URL)).json()
    return [extract_route_data(route) for route in response]


async def get_route_info_and_bounds(client: httpx.AsyncClient, route_id) -> dict:
    bounds_info = await get_bounds_info(client, route_id)
    route_info = await get_route_info(client, route_id)
    return {**route_info, **bounds_info}


async def update_route_info(client: httpx.AsyncClient, route: dict) -> dict:
    try:
        route.update(await get_route_info_and_bounds(client, route["raw_id"]))
        return route
    except Exception as e:
        print(f"Error processing route {route['raw_id']}: {e}")
        return None


async def main():
    limits = httpx.Limits(max_connections=50, max_keepalive_connections=20)
    timeout = httpx.Timeout(10)
    async with httpx.AsyncClient(timeout=timeout, limits=limits) as client:
        basic_routes = await get_routes(client)
        tasks = [update_route_info(client, route) for route in basic_routes]
        routes = list(filter(lambda x: x is not None, await asyncio.gather(*tasks)))
        utils.write_json_file("./data/raw/routes.json", routes)
        utils.write_json_file("./data/raw/stations.json", stations)
        print(f"Total routes: {len(routes)}")


if __name__ == "__main__":
    asyncio.run(main())
