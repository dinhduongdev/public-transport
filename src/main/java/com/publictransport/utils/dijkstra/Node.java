package com.publictransport.utils.dijkstra;

import com.publictransport.models.Stop;
import lombok.Data;

import java.util.List;

@Data
public class Node {
    private Stop stop;
    private List<Edge> edges;

    public Node(Stop stop) {
        this.stop = stop;
        this.edges = new java.util.ArrayList<>();
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
}
