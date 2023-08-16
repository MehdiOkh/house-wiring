package sample.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.model.HoughLines;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TemplateController {

    @FXML
    private ImageView image;
    @FXML
    private AnchorPane main;



    public void initialize() throws FileNotFoundException {

        InputStream stream = new FileInputStream("src/sample/util/template.png");
        Image img = new Image(stream);
        image.setFitWidth(img.getWidth());
        image.setFitHeight(img.getHeight());
        image.setImage(img);

    }
}
