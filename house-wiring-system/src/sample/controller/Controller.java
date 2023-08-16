package sample.controller;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import sample.model.HoughLines;
import sample.model.ImageProcessing;
import sample.model.MatchTemplate;
import sample.model.Picture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Controller {

    private final double KEY_THRESHOLD = 0.90;
    private final double WIRE_THRESHOLD = 68.2;
    private final double MIN_LINE = 31.2;
    private final double MAX_GAP = 30.0;

    File selectedFile;
    FileChooser fileChooser = new FileChooser();


    @FXML
    private ImageView imgView;

    @FXML
    private Label picLabel;

    @FXML
    private JFXButton openImgButton;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private AnchorPane drawerPane;

    @FXML
    private JFXButton cleanUpBtn;

    @FXML
    private JFXSlider keythSlider;

    @FXML
    private JFXSlider wirethSlider;

    @FXML
    private Label thValue;

    @FXML
    private JFXComboBox<String> mtdCombo;

    @FXML
    private JFXSlider mLineSlider;

    @FXML
    private Label mLineValue;

    @FXML
    private JFXSlider gapSlider;

    @FXML
    private Label gapValue;

    @FXML
    private ImageView closeImg;

    @FXML
    private Label mLineLabel;

    @FXML
    private Label thLabel;

    @FXML
    private Label gapLabel;

    @FXML
    private JFXCheckBox nodesCheck;

    @FXML
    private JFXCheckBox resultCheck;


    public Controller() {
    }

    private ArrayList<double[]> keyList;

    private Thread keyThread;

    private Thread wireThread;

    @FXML
    void initialize() {

        File file = new File("src/sample/close.png");
        Image image = new Image(file.toURI().toString());
        closeImg.setImage(image);

        closeImg.setVisible(false);

        keythSlider.setValue(KEY_THRESHOLD);
        thValue.setText(String.format("%.1f", keythSlider.getValue()));

        wirethSlider.setValue(WIRE_THRESHOLD);
        mLineSlider.setValue(MIN_LINE);
        mLineValue.setText(String.format("%.1f", mLineSlider.getValue()));
        gapSlider.setValue(MAX_GAP);
        gapValue.setText(String.format("%.1f", gapSlider.getValue()));

        wirethSlider.setOnMouseReleased(mouseEvent -> {
            thValue.setText(String.format("%.1f", wirethSlider.getValue()));
            newStage("result","result");
        });


        mLineSlider.setOnMouseReleased(e -> {
            mLineValue.setText(String.format("%.1f", mLineSlider.getValue()));
            newStage("result","result");
        });


        gapSlider.setOnMouseReleased(e -> {
            gapValue.setText(String.format("%.1f", gapSlider.getValue()));
            newStage("result","result");
        });

        keythSlider.setOnMouseReleased(mouseEvent -> {
            newStage("template","template");
            thValue.setText(String.format("%.1f", keythSlider.getValue()));

        });

        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.setOnMouseClicked(e -> {
            drawer.setSidePane(drawerPane);
            drawerPane.setVisible(true);
            drawer.toggle();
        });
        drawer.setOnDrawerOpening(e -> {
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.toFront();
        });
        drawer.setOnDrawerClosed(e -> {
            drawer.toBack();
            task.setRate(task.getRate() * -1);
            task.play();
        });

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image files", "*.jpeg", "*.jpg", "*.png")
        );

        openImgButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                imgView.setImage(new Image(selectedFile.toURI().toString()));
                closeImg.setVisible(true);
                picLabel.setVisible(false);
                openImgButton.setVisible(false);
                cleanUpBtn.setDisable(false);
            } else {
                System.out.println("File is not valid!");
            }
        });

        cleanUpBtn.setOnAction(e -> {
            Picture picture = new Picture(selectedFile);
            ImageProcessing.removeDetails(picture);
            BufferedImage img = picture.getImage();
            imgView.setImage(SwingFXUtils.toFXImage(img, null));

            try {
                ImageIO.write(img, "png", new File(selectedFile.getAbsolutePath()));

            } catch (IOException ex) {
                System.out.println("Exception occured :" + ex.getMessage());
            }
            cleanUpBtn.setDisable(true);
        });

        closeImg.setOnMouseClicked(e -> {
            imgView.setImage(null);
            picLabel.setVisible(true);
            openImgButton.setVisible(true);
            closeImg.setVisible(false);
        });

        mtdCombo.getItems().addAll("Custom Graph","Auto-Detect", "Keys and Source", "Show Wires");
        mtdCombo.setOnAction(e -> {

            switch (mtdCombo.getValue()) {
                case "Custom Graph" -> {
                    if (Window.getWindows().size()>1){
                        Window.getWindows().get(1).hide();
                    }
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/customGraph.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("Result");
                        stage.setScene(scene);
                        stage.setHeight(448);
                        stage.setWidth(627);
                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    keythSlider.setVisible(false);
                    wirethSlider.setVisible(false);
                    thValue.setVisible(false);
                    thLabel.setVisible(false);
                    mLineLabel.setVisible(false);
                    mLineSlider.setVisible(false);
                    mLineValue.setVisible(false);
                    gapLabel.setVisible(false);
                    gapSlider.setVisible(false);
                    gapValue.setVisible(false);
                    resultCheck.setVisible(false);
                    nodesCheck.setVisible(false);
                }
                case "Auto-Detect" -> {
                    newStage("result","result");
                    keythSlider.setVisible(false);
                    wirethSlider.setVisible(false);
                    thValue.setVisible(false);
                    thLabel.setVisible(false);
                    mLineLabel.setVisible(false);
                    mLineSlider.setVisible(false);
                    mLineValue.setVisible(false);
                    gapLabel.setVisible(false);
                    gapSlider.setVisible(false);
                    gapValue.setVisible(false);
                    resultCheck.setVisible(false);
                    nodesCheck.setVisible(false);
                }
                case "Show Wires" -> {
                    thValue.setText(String.format("%.1f", wirethSlider.getValue()));
                    wirethSlider.setVisible(true);
                    keythSlider.setVisible(false);
                    thValue.setVisible(true);
                    thLabel.setVisible(true);
                    mLineLabel.setVisible(true);
                    mLineSlider.setVisible(true);
                    mLineValue.setVisible(true);
                    gapLabel.setVisible(true);
                    gapSlider.setVisible(true);
                    gapValue.setVisible(true);
                    resultCheck.setVisible(true);
                    nodesCheck.setVisible(true);

                }
                case "Keys and Source" -> {
                    newStage("template","template");
                    thValue.setText(String.format("%.1f", keythSlider.getValue()));
                    keythSlider.setVisible(true);
                    wirethSlider.setVisible(false);
                    thValue.setVisible(true);
                    thLabel.setVisible(true);
                    mLineLabel.setVisible(false);
                    mLineSlider.setVisible(false);
                    mLineValue.setVisible(false);
                    gapLabel.setVisible(false);
                    gapSlider.setVisible(false);
                    gapValue.setVisible(false);
                    resultCheck.setVisible(false);
                    nodesCheck.setVisible(false);
                }

            }
        });




    }
    private void newStage(String viewName, String pictureName) {


        try {

            if (viewName.equals("result") && pictureName.equals("result")){
                HoughLines.cost.clear();
                keyList = new MatchTemplate(selectedFile.getAbsolutePath(),keythSlider.getValue(),false).matching();
                wireThread = new Thread(new HoughLines(selectedFile.getAbsolutePath(),(int) wirethSlider.getValue(),(int) mLineSlider.getValue(),(int) gapSlider.getValue(),keyList, nodesCheck.isSelected(), resultCheck.isSelected()));
                wireThread.start();
                wireThread.join();
            }else {
                keyThread = new Thread(new MatchTemplate(selectedFile.getAbsolutePath(),keythSlider.getValue(),true));
                keyThread.start();
                keyThread.join();
            }

            if (Window.getWindows().size()>1){
                Window.getWindows().get(1).hide();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/"+viewName+".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Result");
            stage.setScene(scene);
            InputStream stream = new FileInputStream("src/sample/util/"+pictureName+".png");
            Image img = new Image(stream);
            stage.setHeight(img.getHeight()+55);
            stage.setWidth(img.getWidth()+15);
            stage.setResizable(false);
            stage.show();


        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }

}
