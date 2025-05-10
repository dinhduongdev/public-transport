package com.publictransport.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private com.publictransport.models.TypeRoute type;

}