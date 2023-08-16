package sample.model;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class MatchTemplate implements Runnable{
    private String image;
    private boolean showTemplate;
    private double threshold;

    public MatchTemplate(String image,double threshold,boolean showTemplate) {
        this.showTemplate = showTemplate;
        this.image = image;
        this.threshold = threshold;
    }

    public ArrayList<double[]> matching() {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat img = Imgcodecs.imread(image, Imgcodecs.IMREAD_COLOR);
        Mat templ = Imgcodecs.imread("C:\\Users\\Mahdi\\Desktop\\untitled2\\src\\sample\\util\\key_template.png", Imgcodecs.IMREAD_COLOR);
        int match_method = 4;

        Mat result = new Mat();
        Mat img_display = new Mat();
        img.copyTo(img_display);
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, templ, result, match_method);

        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Point matchLoc;
        ArrayList<double[]> previousRecs = new ArrayList<>();

        while (true){
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            matchLoc = mmr.maxLoc;

            if (mmr.maxVal >= threshold){
                if (previousRecs.isEmpty()){
                    previousRecs.add(new double[]{matchLoc.x ,matchLoc.y});
                    Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                            new Scalar(0, 0, 255), 2);
                    Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                            new Scalar(0, 0, 255), -1);


                }else {
                    for (int x=0; x<previousRecs.size();x++){
                        if (Math.sqrt(Math.pow(previousRecs.get(x)[0]-(matchLoc.x),2)+Math.pow(previousRecs.get(x)[1]-(matchLoc.y),2))>50){
                            if (x+1 == previousRecs.size()){
                                Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                                        new Scalar(0, 0, 255), 2);
                                Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                                        new Scalar(0, 0, 255), -1);
                                previousRecs.add(new double[]{matchLoc.x ,matchLoc.y});
                                break;
                            }
                        }else {
                            Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                                    new Scalar(0, 0, 255), -1);
                            break;
                        }
                    }
                }

            }else break;

        }


        Imgcodecs.imwrite("src/sample/util/template.png",img_display);
        return previousRecs;


    }


    @Override
    public void run() {
        this.matching();
    }
}
