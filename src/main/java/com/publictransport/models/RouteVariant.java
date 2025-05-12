package com.publictransport.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "route_variant")
public class RouteVariant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "is_outbound")
    private Boolean isOutbound;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Column(name = "distance")
    private Float distance;

    @Size(max = 255)
    @Column(name = "start_stop")
    private String startStop;

    @Size(max = 255)
    @Column(name = "end_stop")
    private String endStop;

    @OneToMany(mappedBy = "routeVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Stop> stops = new ArrayList<>();
}