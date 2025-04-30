// Constants
const ACCESS_TOKEN = 'WAehmMsAqDms9ahfWjFAq5a1E90zEKXJfFUChL0r';
const API_KEY = 'UJT54kxPWU3gD68kUfcAzWLx4Yqn885FsovZMk9y';
const MAP_CENTER = [106.660172, 10.762622];
const MAP_ZOOM = 13;
const MAP_STYLE = 'https://tiles.goong.io/assets/goong_map_web.json';
const ZOOM_LEVEL_ON_CLICK = 15;
const SELECTED_COLOR = 'red';
const DEFAULT_COLOR = 'black';

// Initialize Map
goongjs.accessToken = ACCESS_TOKEN;
const map = new goongjs.Map({
    container: 'map',
    style: MAP_STYLE,
    center: MAP_CENTER,
    zoom: MAP_ZOOM
});

// Keep track of the currently selected stop
let currentlySelectedStop = null;

/**
 * Decodes a polyline string into an array of coordinates.
 * @param {string} encoded - The encoded polyline string.
 * @returns {Array<Array<number>>} Array of [lng, lat] coordinates.
 */
function decodePolyline(encoded) {
    const points = [];
    let index = 0;
    let lat = 0, lng = 0;

    while (index < encoded.length) {
        let shift = 0, result = 0;
        let b;
        do {
            b = encoded.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        const dlat = (result & 1) ? ~(result >> 1) : (result >> 1);
        lat += dlat;

        shift = 0;
        result = 0;
        do {
            b = encoded.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        const dlng = (result & 1) ? ~(result >> 1) : (result >> 1);
        lng += dlng;

        points.push([lng / 1e5, lat / 1e5]);
    }
    return points;
}

/**
 * Fetches distance and duration between two points using Distance Matrix API.
 * @param {string} origins - Origin coordinates (lat,lng).
 * @param {string} destinations - Destination coordinates (lat,lng).
 * @returns {Promise<{distance: string, duration: string} | null>} Distance and duration or null if failed.
 */
async function getDistanceMatrix(origins, destinations) {
    const url = `https://rsapi.goong.io/DistanceMatrix?origins=${origins}&destinations=${destinations}&vehicle=car&api_key=${API_KEY}`;
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        const data = await response.json();
        if (data.rows?.[0]?.elements[0]?.status === 'OK') {
            return {
                distance: (data.rows[0].elements[0].distance.value / 1000).toFixed(2),
                duration: (data.rows[0].elements[0].duration.value / 60).toFixed(2)
            };
        }
        throw new Error('Distance Matrix API response invalid');
    } catch (error) {
        console.error('Error fetching Distance Matrix:', error.message);
        return null;
    }
}

/**
 * Fetches route between two points using Directions API.
 * @param {string} origin - Origin coordinates (lat,lng).
 * @param {string} destination - Destination coordinates (lat,lng).
 * @returns {Promise<Array<Array<number>> | null>} Array of coordinates or null if failed.
 */
async function fetchRoute(origin, destination) {
    const url = `https://rsapi.goong.io/Direction?origin=${origin}&destination=${destination}&vehicle=car&api_key=${API_KEY}`;
    try {
        const res = await fetch(url);
        const data = await res.json();
        if (data.routes?.[0]?.overview_polyline?.points) {
            return decodePolyline(data.routes[0].overview_polyline.points);
        }
        console.warn('No route found or missing polyline points');
        return null;
    } catch (err) {
        console.error('Error fetching route:', err.message);
        return null;
    }
}

/**
 * Adds a route to the map.
 * @param {Array<Array<number>>} coordinates - Array of [lng, lat] coordinates.
 */
function addRouteToMap(coordinates) {
    if (coordinates.length === 0) return;

    map.addSource('road-route', {
        type: 'geojson',
        data: {
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates
            }
        }
    });

    map.addLayer({
        id: 'road-route',
        type: 'line',
        source: 'road-route',
        layout: {
            'line-join': 'round',
            'line-cap': 'round'
        },
        paint: {
            'line-color': '#ff0000',
            'line-width': 4,
            'line-opacity': 0.9
        }
    });

    const bounds = new goongjs.LngLatBounds();
    coordinates.forEach(coord => bounds.extend(coord));
    map.fitBounds(bounds, { padding: 50 });
}

/**
 * Zooms the map to a specific stop.
 * @param {number} lng - Longitude of the stop.
 * @param {number} lat - Latitude of the stop.
 */
function zoomToStop(lng, lat) {
    map.flyTo({
        center: [lng, lat],
        zoom: ZOOM_LEVEL_ON_CLICK,
        essential: true
    });
}

/**
 * Changes the color of the selected stop and resets others.
 * @param {HTMLElement} stopElement - The stop element to highlight.
 */
function highlightStop(stopElement) {
    if (currentlySelectedStop) {
        currentlySelectedStop.style.color = DEFAULT_COLOR;
    }
    stopElement.style.color = SELECTED_COLOR;
    currentlySelectedStop = stopElement;
}

/**
 * Main function to initialize the map and display route details.
 */
async function initializeRouteMap() {
    const stops = document.querySelectorAll('#stops-list li');
    const coordinates = [];
    let totalDistance = 0;

    // Collect coordinates, add markers, set up click events
    stops.forEach(stop => {
        const lat = parseFloat(stop.getAttribute('data-lat'));
        const lon = parseFloat(stop.getAttribute('data-lon'));
        coordinates.push([lon, lat]);

        new goongjs.Marker().setLngLat([lon, lat]).addTo(map);

        stop.addEventListener('click', () => {
            zoomToStop(lon, lat);
            highlightStop(stop);
        });

        stop.style.cursor = 'pointer';
    });

    // Calculate distances between consecutive stops and total distance
    for (let i = 0; i < stops.length - 1; i++) {
        const lat1 = parseFloat(stops[i].getAttribute('data-lat'));
        const lon1 = parseFloat(stops[i].getAttribute('data-lon'));
        const lat2 = parseFloat(stops[i + 1].getAttribute('data-lat'));
        const lon2 = parseFloat(stops[i + 1].getAttribute('data-lon'));

        const origins = `${lat1},${lon1}`;
        const destinations = `${lat2},${lon2}`;
        const result = await getDistanceMatrix(origins, destinations);

        const distanceText = document.createElement('span');
        if (result) {
            distanceText.style.color = 'blue';
            distanceText.textContent = ` (Distance to next stop: ${result.distance} km, Duration: ${result.duration} min)`;
            totalDistance += parseFloat(result.distance); // Add to total distance
        } else {
            distanceText.style.color = 'red';
            distanceText.textContent = ` (Unable to calculate distance)`;
        }
        stops[i].appendChild(distanceText);
    }

    // Display number of transfers and total distance
    const transferCount = coordinates.length - 1; // Number of transfers = number of segments
    document.getElementById('transfer-count').textContent = transferCount;
    document.getElementById('total-distance').textContent = totalDistance > 0 ? `${totalDistance.toFixed(2)} km` : 'Unable to calculate';

    // Draw route on map after it loads
    map.on('load', async () => {
        coordinates.forEach(coord => new goongjs.Marker().setLngLat(coord).addTo(map));

        const fullRouteCoords = [];
        for (let i = 0; i < coordinates.length - 1; i++) {
            const origin = `${coordinates[i][1]},${coordinates[i][0]}`;
            const destination = `${coordinates[i + 1][1]},${coordinates[i + 1][0]}`;
            const segment = await fetchRoute(origin, destination);
            if (segment) {
                fullRouteCoords.push(...segment);
            }
        }

        addRouteToMap(fullRouteCoords);
    });
}

// Start the application
document.addEventListener('DOMContentLoaded', initializeRouteMap);