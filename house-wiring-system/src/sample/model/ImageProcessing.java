package sample.model;

import java.awt.*;

public class ImageProcessing {

    private Picture picture;

    public ImageProcessing(Picture picture){
        this.picture = picture;
    }

    public static void removeDetails(Picture picture) {
        for (int x = 0; x < picture.width(); x++) {
            for (int y = 0; y < picture.height(); y++) {
                if (getAverageLuminance(x, y, picture) > 135) {
                    picture.set(x, y, new Color(255, 255, 255));
                }
            }
        }
    }

    private static double getAverageLuminance(int x, int y, Picture picture) {
        double lumSum = 0;
        int xStepF = 0, xStepB = 0;
        int yStepF = 0, yStepB = 0;
        if (x + 10 >= picture.width()) {
            xStepF = picture.width() - 1 - x;
            xStepB = 10;
        } else if (x - 10 < 0) {
            xStepB = x;
            xStepF = 10;
        } else {
            xStepB = 10;
            xStepF = 10;
        }
        if (y + 10 >= picture.height()) {
            yStepF = picture.height() - 1 - y;
            yStepB = 10;
        } else if (y - 10 < 0) {
            yStepB = y;
            yStepF = 10;
        } else {
            yStepB = 10;
            yStepF = 10;
        }

        int stepSum = xStepB + xStepF + yStepF + yStepB;

        for (int i = x; i < x + xStepF; i++) {
            Color color = picture.get(i + 1, y);
            lumSum += Luminance.intensity(color);
        }
        for (int i = x; i > x - xStepB; i--) {
            Color color = picture.get(i - 1, y);
            lumSum += Luminance.intensity(color);
        }

        for (int j = y; j < y + yStepF; j++) {
            Color color = picture.get(x, j + 1);
            lumSum += Luminance.intensity(color);
        }
        for (int j = y; j > y - yStepB; j--) {
            Color color = picture.get(x, j - 1);
            lumSum += Luminance.intensity(color);
        }
        return lumSum / stepSum;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
