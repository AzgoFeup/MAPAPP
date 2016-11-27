package com.azgo.mapapp;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by Paulo on 25-Nov-16.
 */

public class MatrixGraphAlgorithms {
    /**
     * Determine the shortest path to all vertices from a vertex using Dijkstra's algorithm
     */
    private static <V> void shortestPath(boolean[][] graph, Graph grafo, int sourceIdx, boolean[] knownVertices, int[] verticesIndex, double[] minDist) {
        int j=0;
        minDist[sourceIdx] = 0;
        while (sourceIdx != -1) {
            knownVertices[sourceIdx] = true;
            for (int i = 0; i < 69; i++) {
                if (graph[sourceIdx][i] == true) { //ramo
                    j= 1;
                    if ((knownVertices[i]==false) && minDist[i] > minDist[sourceIdx] + j) {
                        minDist[i] = minDist[sourceIdx] + j;
                        verticesIndex[i] = sourceIdx;
                    }
                }
            }
            Double min = Double.MAX_VALUE;
            sourceIdx = -1;
            for (int i = 0; i < 69; i++) {
                if ((knownVertices[i]==false) && minDist[i] < min) {
                    min = minDist[i];
                    sourceIdx = i;
                }
            }
        }
    }

    /**
     * Determine the shortest path between two vertices using Dijkstra's algorithm
     *
     * @param graph Graph object
     * @param source Source vertex
     * @param dest Destination vertices
     * @param path Returns the vertices in the path (empty if no path)
     * @return minimum distance, -1 if vertices not in graph or no path
     *
     */
    public static <V> double shortestPath(boolean[][] graph, Graph grafo, Graph.Node source, Graph.Node dest, LinkedList<Graph.Node> path) {
        int sourceIdx = source.getIndex();
        boolean[] knownVertices = new boolean[69];
        int[] verticesIndex = new int[69];
        double[] minDist = new double[69];
        for (int i = 0; i < 69; i++) {
            knownVertices[i] = false;
            verticesIndex[i] = -1;
            minDist[i] = Double.MAX_VALUE;
        }
        shortestPath(graph, grafo, sourceIdx, knownVertices, verticesIndex, minDist);
        path.clear();
        int destIdx = dest.getIndex();
        if (knownVertices[destIdx]==false) {
            return -1;
        }
        recreatePath(graph, grafo, sourceIdx, destIdx, verticesIndex, path);
        Collections.reverse(path);
        return minDist[destIdx];
    }

    /**
     * Recreates the minimum path between two vertex, from the result of Dikstra's algorithm
     */
    private static <V> void recreatePath(boolean[][] graph, Graph grafo, int sourceIdx, int destIdx, int[] verticesIndex, LinkedList<Graph.Node> path) {
        path.add(grafo.getListNodes().get(destIdx));
        if (sourceIdx != destIdx) {
            destIdx = verticesIndex[destIdx];
            recreatePath(graph, grafo, sourceIdx, destIdx, verticesIndex, path);
        }
    }
}