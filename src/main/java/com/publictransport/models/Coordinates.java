package com.publictransport.models;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Coordinates {
    private Double lat;
    private Double lng;
}
