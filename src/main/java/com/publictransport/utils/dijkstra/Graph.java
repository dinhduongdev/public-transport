package com.publictransport.utils.dijkstra;

import com.publictransport.models.Coordinates;
import com.publictransport.models.Stop;
import com.publictransport.utils.MapUtils;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class Graph {
    private Map<Long, Node> map;

    public Graph() {
        this.map = new HashMap<>();
    }

    public Node addNode(Stop stop) {
        Node newNode = new Node(stop);
        map.put(stop.getId(), newNode);
        return newNode;
    }

    public void addEdge(Stop from, Stop to) {
        Coordinates fromCoords = from.getStation().getCoordinates();
        Coordinates toCoords = to.getStation().getCoordinates();
        addEdge(from, to, MapUtils.haversineDistance(fromCoords, toCoords));
    }

    public void addEdge(Stop from, Stop to, double weight) {
        Node fromNode = map.get(from.getId());
        Node toNode = map.get(to.getId());
        Edge edge = new Edge(toNode, weight);
        fromNode.addEdge(edge);
    }

    public void addEdge(Node from, Node to, double weight) {
        Edge edge = new Edge(to, weight);
        from.addEdge(edge);
    }

    public void addEdge(Node from, Node to) {
        Coordinates fromCoords = from.getStop().getStation().getCoordinates();
        Coordinates toCoords = to.getStop().getStation().getCoordinates();
        addEdge(from, to, MapUtils.haversineDistance(fromCoords, toCoords));
    }

    public Node getNode(Long id) {
        return map.get(id);
    }
}
