package com.publictransport.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "status_report")
public class StatusReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Size(max = 20)
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    //TODO [Reverse Engineering] generate columns from DB
}