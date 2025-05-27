package com.publictransport.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "RouteVariant")
public class RouteVariant {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "route_id")
    @JsonBackReference
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

//    @OneToMany(mappedBy = "routeVariant", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("stopOrder ASC")
//    private List<Stop> stops;
}