package com.publictransport.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class VehicleFormatter implements Formatter<Vehicle> {
    @Override
    public Vehicle parse(String vehicleId, Locale locale) throws ParseException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(Long.valueOf(vehicleId));
        return vehicle;
    }

    @Override
    public String print(Vehicle vehicle, Locale locale) {
        return String.valueOf(vehicle.getId());
    }
}
