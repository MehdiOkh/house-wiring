package sample.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.model.HoughLines;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResultController {
    @FXML
    private ImageView image;
    @FXML
    private AnchorPane main;



    public void initialize() throws FileNotFoundException {

        InputStream stream = new FileInputStream("src/sample/util/result.png");
        Image img = new Image(stream);
        image.setFitWidth(img.getWidth());
            image.setFitHeight(img.getHeight());
            for (double[] d : HoughLines.cost){
                Text cost = new Text();
                cost.setText(String.format("%.1f", d[2]));
                cost.setFill(Color.WHITE);
                cost.setFont(Font.font("Segoe UI Black", FontWeight.NORMAL, FontPosture.REGULAR, 13));
                GridPane pane = new GridPane();
                pane.setLayoutX(d[0]);
                pane.setLayoutY(d[1]);
                pane.setMinSize(50,22);
                pane.setBackground(new Background(new BackgroundFill(Color.web("#3B6D6C"), new CornerRadii(15), Insets.EMPTY)));
                pane.getChildren().add(cost);
                pane.setAlignment(Pos.CENTER);

            main.getChildren().add(pane);
        }

        image.setImage(img);
    }
}
