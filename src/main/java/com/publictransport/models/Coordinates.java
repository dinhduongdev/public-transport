package com.publictransport.models;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    private Double lat;
    private Double lng;

    public String toLatLngString() {
        return lat + ";" + lng;
    }

    public String toLatLngString(String delimiter) {
        return lat + delimiter + lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return lat.equals(that.lat) && lng.equals(that.lng);
    }
}
