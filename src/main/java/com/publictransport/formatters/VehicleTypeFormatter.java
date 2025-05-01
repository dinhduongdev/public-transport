package com.publictransport.formatters;

import com.publictransport.models.VehicleType;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class VehicleTypeFormatter implements Formatter<VehicleType> {
    @Override
    public VehicleType parse(String vehicleTypeId, Locale locale) throws ParseException {
        VehicleType vehicleType = new VehicleType();
        vehicleType.setId(Long.valueOf(vehicleTypeId));
        return vehicleType;
    }

    @Override
    public String print(VehicleType vehicleType, Locale locale) {
        return String.valueOf(vehicleType.getId());
    }
}
