package sample.model;

import java.util.ArrayList;
import java.util.LinkedList;

public class GraphGenerator {
    public Graph graphGenerator(LinkedList<double[]> lines,ArrayList<double[]> keyList){
        Graph graph = new Graph();
        LinkedList<Graph.Node> nodes = new LinkedList<>();

//        part one
        for (double[] line1 : lines){
            for (double[] line2 : lines) {
                double dist1 = Math.sqrt(Math.pow(line1[0]-line2[0],2)+Math.pow(line1[1]-line2[1],2));
                double dist2 = Math.sqrt(Math.pow(line1[2]-line2[2],2)+Math.pow(line1[3]-line2[3],2));
                double dist3 = Math.sqrt(Math.pow(line1[0]-line2[2],2)+Math.pow(line1[1]-line2[3],2));
                double dist4 = Math.sqrt(Math.pow(line1[2]-line2[0],2)+Math.pow(line1[3]-line2[1],2));
                double min1 = Math.min(dist1,dist2);
                double min2 = Math.min(dist3,dist4);

                if(dist1!=0&&dist2!=0){
                    if (Math.min(min1, min2) == dist1) {

                        double x = Math.abs((line1[0]+line2[0])/2);
                        double y = Math.abs((line1[1]+line2[1])/2);
                        nodeGenerator(dist1,line1,line2,nodes,x,y);
                    }else if (Math.min(min1, min2) == dist2){

                        double x = Math.abs((line1[2]+line2[2])/2);
                        double y = Math.abs((line1[3]+line2[3])/2);
                        nodeGenerator(dist2,line1,line2,nodes,x,y);
                    }else if (Math.min(min1, min2) == dist3){

                        double x = Math.abs((line1[0]+line2[2])/2);
                        double y = Math.abs((line1[1]+line2[3])/2);
                        nodeGenerator(dist3,line1,line2,nodes,x,y);
                    }else if (Math.min(min1, min2) == dist4){

                        double x = Math.abs((line1[2]+line2[0])/2);
                        double y = Math.abs((line1[3]+line2[1])/2);
                        nodeGenerator(dist4,line1,line2,nodes,x,y);
                    }

                }
            }

        }

        edgeConnector(graph,lines,nodes);
        //add source
        double[] source = null;
        for (Graph.Node node : graph.getNodes()){
            for (double[] key : keyList){
                if (Math.sqrt(Math.pow(node.getX()-key[0],2)+Math.pow(node.getY()-key[1],2))<=55){
                    node.setType("source");
                    source = key;
                    break;
                }
            }
            if (source != null) break;
        }
        keyList.remove(source);

        // part two
        for (double[] line : lines){
            Graph.Node previousNode = null;

            int i = 0;
            while (i<=2){
                boolean state = false;

                for (Graph.Node node : graph.getNodes()) {
                    if (Math.sqrt(Math.pow(node.getX()-line[i],2)+Math.pow(node.getY()-line[i+1],2))<=50) {
                        state = true;
                        if (previousNode != null){
                            graph.addEdge(previousNode,node,Math.sqrt(Math.pow(node.getX()- previousNode.getX(),2)+Math.pow(node.getY()- previousNode.getY(),2)));
                        }
                        onLineEdges(node,graph);
                        previousNode = node;
                        break;
                    }
                }
                if (!state){

                    Graph.Node nd = new Graph.Node(""+graph.getNodes().size()+1,line[i],line[i+1]);
                    if (previousNode != null){
                        graph.addEdge(nd,previousNode,Math.sqrt(Math.pow(nd.getX()- previousNode.getX(),2)+Math.pow(nd.getY()- previousNode.getY(),2)));
                    }
                    onLineEdges(nd,graph);
                    previousNode = nd;
                }
                i+=2;
            }


        }


        // add keys

        for (double[] key : keyList){

            for (Graph.Node nd : graph.getNodes()){
                boolean state = false;
                for (Graph.Edge edge : nd.edges){
                    double x = Math.abs(edge.source.getX() - edge.destination.getX());
                    double y = Math.abs(edge.source.getY() - edge.destination.getY());
                    if (Math.min(x,y)==x) {

                        if (Math.abs(edge.source.getX() - key[0]) <= 50 && Math.max(edge.source.getY(), edge.destination.getY()) > key[1] && Math.min(edge.source.getY(), edge.destination.getY()) < key[1]) {
                            Graph.Node node = new Graph.Node(""+graph.getNodes().size()+1,edge.source.getX(),key[1]);
                            node.setType("key");
                            graph.addEdge(node,edge.source,Math.sqrt(Math.pow(node.getX()- edge.source.getX(),2)+Math.pow(node.getY()- edge.source.getY(),2)));
                            graph.addEdge(node,edge.destination,Math.sqrt(Math.pow(node.getX()- edge.destination.getX(),2)+Math.pow(node.getY()- edge.destination.getY(),2)));
                            state = true;
                            break;
                        }

                    }else {
                        if (Math.abs(edge.source.getY() - key[1])<=50 && Math.max(edge.source.getX(),edge.destination.getX())> key[0] && Math.min(edge.source.getX(),edge.destination.getX())< key[0]){
                            Graph.Node node = new Graph.Node(""+graph.getNodes().size()+1,key[0],edge.source.getY());
                            node.setType("key");
                            graph.addEdge(node,edge.source,Math.sqrt(Math.pow(node.getX()- edge.source.getX(),2)+Math.pow(node.getY()- edge.source.getY(),2)));
                            graph.addEdge(node,edge.destination,Math.sqrt(Math.pow(node.getX()- edge.destination.getX(),2)+Math.pow(node.getY()- edge.destination.getY(),2)));
                            state = true;
                            break;
                        }
                    }
                }
                if (state){
                    break;
                }
            }
        }

        return graph;
    }
    public void nodeGenerator(double dist, double[] line1, double[] line2, LinkedList<Graph.Node> nodes, double x, double y){
        double slope1 = Math.abs((line1[3]-line1[1])/(line1[2]-line1[0]))>=100?100:Math.abs((line1[3]-line1[1])/(line1[2]-line1[0]));
        double slope2 = Math.abs((line2[3]-line2[1])/(line2[2]-line2[0]))>=100?100:Math.abs((line2[3]-line2[1])/(line2[2]-line2[0]));
        if (dist<=50 && nodes.isEmpty()){
            if (slope1!= 100 || slope2!= 100){
                if (slope1!= 0 || slope2!= 0){
                    if ((Math.min(slope1,slope2)/Math.max(slope1,slope2))<=0.3){

                        Graph.Node nd = new Graph.Node(""+nodes.size()+1,x,y);
                        nodes.add(nd);

                    }
                }
            }
        }else if (dist<=50 && !nodes.isEmpty()){
            boolean state = false;
            for (Graph.Node node : nodes){
                double nearNodeDis = Math.sqrt(Math.pow(node.getX()-x,2)+Math.pow(node.getY()-y,2));
                if (nearNodeDis<=50){
                    state = true;
                    break;
                }
            }

            if ((slope1!= 100 || slope2!= 100) && !state){
                if (slope1!= 0 || slope2!= 0){
                    if ((Math.min(slope1,slope2)/Math.max(slope1,slope2))<=0.3){

                        Graph.Node nd = new Graph.Node(""+nodes.size()+1,x,y);
                        nodes.add(nd);


                    }
                }
            }
        }

    }
    public void edgeConnector(Graph graph, LinkedList<double[]> lines, LinkedList<Graph.Node>nodes){

        for (Graph.Node node : nodes){

            for (double[] line : lines){
                double dist1 = Math.sqrt(Math.pow(node.getX()-line[0],2)+Math.pow(node.getY()-line[1],2));
                double dist2 = Math.sqrt(Math.pow(node.getX()-line[2],2)+Math.pow(node.getY()-line[3],2));
                if (dist1 <=50){
                    for (Graph.Node otherNode : nodes){
                        double tempDis = Math.sqrt(Math.pow(otherNode.getX()-line[2],2)+Math.pow(otherNode.getY()-line[3],2));
                        if (tempDis<=50){
                            graph.addEdge(node,otherNode,Math.sqrt(Math.pow(node.getX()- otherNode.getX(),2)+Math.pow(node.getY()- otherNode.getY(),2)));
                        }

                    }
                }else if (dist2 <= 50){
                    for (Graph.Node otherNode : nodes){
                        double tempDis = Math.sqrt(Math.pow(otherNode.getX()-line[0],2)+Math.pow(otherNode.getY()-line[1],2));
                        if (tempDis<=50){
                            graph.addEdge(node,otherNode,Math.sqrt(Math.pow(node.getX()- otherNode.getX(),2)+Math.pow(node.getY()- otherNode.getY(),2)));
                        }

                    }
                }
            }
        }
    }
    public void onLineEdges(Graph.Node node, Graph graph){
        for (Graph.Node nd : graph.getNodes()){
            boolean state = false;
            for (Graph.Edge edge : nd.edges){
                double x = Math.abs(edge.source.getX() - edge.destination.getX());
                double y = Math.abs(edge.source.getY() - edge.destination.getY());
                if (Math.min(x,y)==x) {

                    if (Math.abs(edge.source.getX() - node.getX()) <= 50 && Math.max(edge.source.getY(), edge.destination.getY()) > node.getY() && Math.min(edge.source.getY(), edge.destination.getY()) < node.getY()) {
                        graph.addEdge(edge.source,node,Math.sqrt(Math.pow(node.getX()- edge.source.getX(),2)+Math.pow(node.getY()- edge.source.getY(),2)));
                        graph.addEdge(edge.destination,node,Math.sqrt(Math.pow(node.getX()- edge.destination.getX(),2)+Math.pow(node.getY()- edge.destination.getY(),2)));
//                        Imgproc.line(img,new Point(node.getX(),node.getY()),new Point(edge.destination.getX(),edge.destination.getY()), new Scalar(0, 255, 0),1);

                        state = true;
                        break;
                    }

                }else {
                    if (Math.abs(edge.source.getY() - node.getY())<=50 && Math.max(edge.source.getX(),edge.destination.getX())> node.getX() && Math.min(edge.source.getX(),edge.destination.getX())< node.getX()){
                        graph.addEdge(edge.source,node,Math.sqrt(Math.pow(node.getX()- edge.source.getX(),2)+Math.pow(node.getY()- edge.source.getY(),2)));
                        graph.addEdge(edge.destination,node,Math.pow(node.getX()- edge.destination.getX(),2)+Math.pow(node.getY()- edge.destination.getY(),2));
//                        Imgproc.line(img,new Point(node.getX(),node.getY()),new Point(edge.destination.getX(),edge.destination.getY()), new Scalar(0, 255, 0),1);

                        state = true;
                        break;
                    }
                }
            }
            if (state){
                break;
            }
        }

    }
}
