package com.publictransport.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

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
    private Float distance; // meters

    @Size(max = 255)
    @Column(name = "start_stop")
    private String startStop;

    @Size(max = 255)
    @Column(name = "end_stop")
    private String endStop;

    @OneToMany(mappedBy = "routeVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    @JsonBackReference
    private List<Stop> stops;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteVariant that = (RouteVariant) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
