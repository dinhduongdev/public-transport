package com.publictransport.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Location {

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    @Column(length = 100)
    private String street;

    @Size(max = 100)
    @Column(length = 100)
    private String ward;

    @Size(max = 100)
    @Column(length = 100)
    private String zone;

    @Override
    public String toString() {
        return this.address + ", " + this.street + ", " + this.ward + " ," + this.zone;
    }
}