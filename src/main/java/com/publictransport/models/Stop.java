/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.models;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author duong
 */
@Entity
@Table(name = "stop")
@NamedQueries({
    @NamedQuery(name = "Stop.findAll", query = "SELECT s FROM Stop s"),
    @NamedQuery(name = "Stop.findById", query = "SELECT s FROM Stop s WHERE s.id = :id"),
    @NamedQuery(name = "Stop.findByStopOrder", query = "SELECT s FROM Stop s WHERE s.stopOrder = :stopOrder")})
public class Stop implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stop_order")
    private int stopOrder;
    @JoinColumn(name = "route_id", referencedColumnName = "id")
    @ManyToOne
    private Route routeId;
    @JoinColumn(name = "station_id", referencedColumnName = "id")
    @ManyToOne
    private Station stationId;
    @OneToMany(mappedBy = "stopId")
    private Set<ScheduleStop> scheduleStopSet;

    public Stop() {
    }

    public Stop(Long id) {
        this.id = id;
    }

    public Stop(Long id, int stopOrder) {
        this.id = id;
        this.stopOrder = stopOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public Route getRouteId() {
        return routeId;
    }

    public void setRouteId(Route routeId) {
        this.routeId = routeId;
    }

    public Station getStationId() {
        return stationId;
    }

    public void setStationId(Station stationId) {
        this.stationId = stationId;
    }

    public Set<ScheduleStop> getScheduleStopSet() {
        return scheduleStopSet;
    }

    public void setScheduleStopSet(Set<ScheduleStop> scheduleStopSet) {
        this.scheduleStopSet = scheduleStopSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stop)) {
            return false;
        }
        Stop other = (Stop) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.publictransport.models.Stop[ id=" + id + " ]";
    }
    
}
