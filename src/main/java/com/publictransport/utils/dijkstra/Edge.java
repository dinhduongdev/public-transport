package com.publictransport.utils.dijkstra;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    private Node target;
    private double weight;
}
