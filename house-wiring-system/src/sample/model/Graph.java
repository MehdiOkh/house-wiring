package sample.model;

import java.util.*;

public class Graph {

    private Set<Node> nodes;
    private LinkedList<Node> pathNodes = new LinkedList<>();

    public Graph() {
        this.nodes = new HashSet<>();
    }

    //Deep copy
    public Graph(Graph in) {
        this.nodes = in.nodes;
    }

    public void addNode(Node... n) {
        nodes.addAll(Arrays.asList(n));
    }

    public void addEdge(Node source, Node destination, double weight) {

        nodes.add(source);
        nodes.add(destination);

        addEdgeHelper(source, destination, weight);

        if (source != destination) {
            addEdgeHelper(destination, source, weight);
        }
    }

    private void addEdgeHelper(Node a, Node b, double weight) {
        for (Edge edge : a.edges) {
            if (edge.source == a && edge.destination == b) {
                edge.weight = weight;
                return;
            }
        }
        a.edges.add(new Edge(a, b, weight));
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void printEdges() {
        for (Node node : nodes) {
            LinkedList<Edge> edges = node.edges;

            if (edges.isEmpty()) {
                System.out.println("Node " + node.name + " has no edges.");
                continue;
            }
            System.out.print("Node " + node.name + " has edges to: ");

            for (Edge edge : edges) {
                System.out.print(edge.destination.name + "(" + edge.weight + ") ");
            }
            System.out.println();
        }
    }

    public void resetNodesVisited() {
        for (Node node : nodes) {
            node.unvisit();
        }
    }

    public LinkedList<Node> getPathNodes() {
        return pathNodes;
    }

    public Double DijkstraShortestPath(Node start, Node end) {
        pathNodes = new LinkedList<>();
        for (Node node :nodes){
            node.unvisit();
        }

        HashMap<Node, Node> changedAt = new HashMap<>();
        changedAt.put(start, null);

        HashMap<Node, Double> shortestPathMap = new HashMap<>();

        for (Node node : nodes) {
            if (node == start)
                shortestPathMap.put(start, 0.0);
            else shortestPathMap.put(node, Double.POSITIVE_INFINITY);
        }

        for (Edge edge : start.edges) {
            shortestPathMap.put(edge.destination, edge.weight);
            changedAt.put(edge.destination, start);
        }

        start.visit();

        while (true) {
            Node currentNode = closestReachableUnvisited(shortestPathMap);

            if (currentNode == null) {
                System.out.println("There isn't a path between " + start.name + " and " + end.name);
                return 0.0;
            }

            if (currentNode == end) {
                System.out.println("The path with the smallest weight between "
                        + start.name + " and " + end.name + " is:");

                Node child = end;

                String path = end.name;
                pathNodes.add(end);
                while (true) {
                    Node parent = changedAt.get(child);
                    if (parent == null) {
                        break;
                    }
                    pathNodes.addFirst(parent);
                    path = parent.name + " " + path;
                    child = parent;
                }
                System.out.println(path);
                System.out.println("The path costs: " + shortestPathMap.get(end));
                return shortestPathMap.get(end);
            }
            currentNode.visit();

            for (Edge edge : currentNode.edges) {
                if (edge.destination.isVisited())
                    continue;

                if (shortestPathMap.get(currentNode)
                        + edge.weight
                        < shortestPathMap.get(edge.destination)) {
                    shortestPathMap.put(edge.destination,
                            shortestPathMap.get(currentNode) + edge.weight);
                    changedAt.put(edge.destination, currentNode);
                }
            }
        }
    }

    private Node closestReachableUnvisited(HashMap<Node, Double> shortestPathMap) {

        double shortestDistance = Double.POSITIVE_INFINITY;
        Node closestReachableNode = null;
        for (Node node : nodes) {
            if (node.isVisited())
                continue;

            double currentDistance = shortestPathMap.get(node);
            if (currentDistance == Double.POSITIVE_INFINITY)
                continue;

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                closestReachableNode = node;
            }
        }
        return closestReachableNode;
    }

    public boolean hasEdge(Node source, Node destination) {
        LinkedList<Edge> edges = source.edges;
        for (Edge edge : edges) {
            if (edge.destination == destination) {
                return true;
            }
        }
        return false;
    }


    public static class Node {

        public String name;
        private double x;
        private double y;
        public LinkedList<Edge> edges;
        private boolean visited;
        private String type = "";

        public Node(String name,double x,double y) {
            this.x = x;
            this.y = y;
            this.name = name;
            edges = new LinkedList<>();
        }
        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        boolean isVisited() {
            return visited;
        }

        void visit() {
            visited = true;
        }

        void unvisit() {
            visited = false;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Edge implements Comparable<Edge> {

        public Node source;
        public Node destination;
        double weight;

        Edge(Node s, Node d, double w) {
            source = s;
            destination = d;
            weight = w;
        }

        public String toString() {
            return String.format("(%s -> %s, %f)", source.name, destination.name, weight);
        }

        public int compareTo(Edge otherEdge) {
            if (this.weight > otherEdge.weight) {
                return 1;
            } else return -1;
        }

    }
}
