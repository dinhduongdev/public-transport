package com.publictransport.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statusreport")
public class Statusreport {
    @Id
    @Size(max = 20)
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    //TODO [Reverse Engineering] generate columns from DB
}