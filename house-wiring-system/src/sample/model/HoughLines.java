package sample.model;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;

public class HoughLines implements Runnable {

    public static LinkedList<double[]> cost = new LinkedList<>();
    private String imgPath;
    private ArrayList<double[]> keyList;
    private int threshold;
    private int minLine;
    private int maxLine;
    private boolean showNodes;
    private boolean showResult;

    public HoughLines(String imgPath, int threshold, int minLine, int maxLine,ArrayList<double[]> keyList, boolean showNodes, boolean showResult) {
        this.keyList = keyList;
        this.imgPath = imgPath;
        this.threshold = threshold;
        this.minLine = minLine;
        this.maxLine = maxLine;
        this.showNodes = showNodes;
        this.showResult = showResult;
    }

    public void findLines(String imgPath, int threshold, int minLine, int maxGap){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat dst = new Mat();
        Mat cdstP ;
        Mat cdst = new Mat();
        Mat linesP = new Mat();
        Mat src = Imgcodecs.imread(imgPath);
        Imgproc.Canny(src, dst, 20, 100, 3, false);
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI/180, threshold, minLine,maxGap); // runs the actual detection
        ArrayList<Integer> rep = new ArrayList<>();
        for (int x = 0; x < linesP.rows(); x++){
            for (int y = x+1; y < linesP.rows(); y++){
                double dist1 = Math.sqrt(Math.pow(linesP.get(x, 0)[0]-linesP.get(y, 0)[0],2)+Math.pow(linesP.get(x, 0)[1]-linesP.get(y, 0)[1],2));
                double dist2 = Math.sqrt(Math.pow(linesP.get(x, 0)[2]-linesP.get(y, 0)[2],2)+Math.pow(linesP.get(x, 0)[3]-linesP.get(y, 0)[3],2));
                double avr = (dist1+dist2)/2;
                if(avr<20){
                    rep.add(x);
                }
            }
        }
        LinkedList<double[]> lines = new LinkedList<>();
        // Draw the lines
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            if (!rep.contains(x)){
                lines.add(l);
                Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 1, Imgproc.LINE_AA, 0);

            }
        }

        Graph graph = new GraphGenerator().graphGenerator(lines,keyList);

        Graph copy = new Graph(graph);
        for (Graph.Node source : graph.getNodes()){
            if (source.getType().equals("source") && showResult){
                for (Graph.Node key : graph.getNodes()){
                    if (key.getType().equals("key")){
                        copy.DijkstraShortestPath(source,key);
                        double length = 0;
                        for (int i=0; i<copy.getPathNodes().size()-1;i++){
                            Imgproc.line(src,new Point(copy.getPathNodes().get(i).getX(),copy.getPathNodes().get(i).getY()),new Point(copy.getPathNodes().get(i+1).getX(),copy.getPathNodes().get(i+1).getY()), new Scalar(63,171,56),3);
                            length += Math.sqrt(Math.pow(copy.getPathNodes().get(i).getX()-copy.getPathNodes().get(i+1).getX(),2)+Math.pow(copy.getPathNodes().get(i).getY()-copy.getPathNodes().get(i+1).getY(),2));
                        }
                        cost.add(new double[]{copy.getPathNodes().getLast().getX()+15,copy.getPathNodes().getLast().getY()+10,length});

                    }
                }
            }
        }

        for (Graph.Node node : graph.getNodes()){
            if (node.getType().equals("key") && showResult){
                Imgproc.circle(src, new Point(node.getX(), node.getY()), 17, new Scalar(43, 211,236),Imgproc.FILLED);
            }else if (node.getType().equals("") && showNodes){
                for (Graph.Edge edge : node.edges){
                    Imgproc.line(src,new Point(node.getX(),node.getY()),new Point(edge.destination.getX(),edge.destination.getY()), new Scalar(0, 0, 255),2);
                }
                Imgproc.circle(src, new Point(node.getX(), node.getY()), 11, new Scalar(213, 179, 129),Imgproc.FILLED);

            }else if (node.getType().equals("source") && showResult){
                Imgproc.circle(src, new Point(node.getX(), node.getY()), 17, new Scalar(36, 51, 197),Imgproc.FILLED);
            }
        }

        Imgcodecs.imwrite("src/sample/util/result.png",src);

    }

    @Override
    public void run() {
        this.findLines(imgPath,threshold,minLine,maxLine);
    }
}
