package com.publictransport.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "route")
public class Route {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @Column(name = "code", length = 20)
    private String code;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "type")
    private String type;

    @JsonIgnore
    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private Set<RouteVariant> routeVariants;
}