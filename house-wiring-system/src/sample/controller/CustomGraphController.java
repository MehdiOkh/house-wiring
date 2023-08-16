package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import sample.model.Graph;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class CustomGraphController {
    @FXML
    private AnchorPane anchor;
    @FXML
    private AnchorPane edgeSizePane;
    @FXML
    private JFXButton edgeSizeBtn;
    @FXML
    private JFXTextField edgeSizeField;
    @FXML
    private JFXRadioButton div;
    @FXML
    private JFXRadioButton key;
    @FXML
    private JFXRadioButton source;
    @FXML
    private JFXRadioButton edge;
    @FXML
    private JFXRadioButton randomRadio;
    LinkedList<Graph.Node> neighbors = new LinkedList<>();
    private int id = 1;
    private int vector = 7;
    private final Graph graph = new Graph();

    public void initialize(){
        ToggleGroup toggleGroup = new ToggleGroup();
        div.setToggleGroup(toggleGroup);
        key.setToggleGroup(toggleGroup);
        source.setToggleGroup(toggleGroup);
        edge.setToggleGroup(toggleGroup);
        randomRadio.setToggleGroup(toggleGroup);

        anchor.setOnMouseClicked(event -> {
            randomRadio.setDisable(true);
            if (!edge.isSelected()){
                JFXButton btn = new JFXButton();
                Graph.Node node = new Graph.Node(Integer.toString(id),event.getSceneX(),event.getSceneY());
                if (div.isSelected()){
                    node.setType("");
                    btn.setBackground(new Background(new BackgroundFill(Color.web("#81b0a7"), new CornerRadii(15), Insets.EMPTY)));

                }else if (source.isSelected()){
                    node.setType("source");
                    btn.setBackground(new Background(new BackgroundFill(Color.web("#c53324"), new CornerRadii(15), Insets.EMPTY)));

                }else if (key.isSelected()){
                    node.setType("key");
                    btn.setBackground(new Background(new BackgroundFill(Color.web("#ecd32b"), new CornerRadii(15), Insets.EMPTY)));

                }
                graph.addNode(node);
                btn.setLayoutX(event.getSceneX());
                btn.setLayoutY(event.getSceneY());
                btn.setText(""+id++);
                btn.setOnMouseClicked(e->{
                    if (edge.isSelected()){
                        for (Graph.Node nd : graph.getNodes()){
                            if (nd.name.equals(btn.getText())){
                                neighbors.add(nd);
                            }
                        }
                        if (neighbors.size()==2){
                            edgeSizePane.toFront();
                            edgeSizeField.setText("");
                            edgeSizePane.setVisible(true);
                            edgeSizeBtn.setOnMouseClicked(event1 -> {
                                graph.addEdge(neighbors.get(0),neighbors.get(1),
                                        edgeSizeField.getText().equals("") ?
                                                Math.sqrt(Math.pow(neighbors.get(0).getX()-neighbors.get(1).getX(),2)+Math.pow(neighbors.get(0).getY()-neighbors.get(1).getY(),2)) :
                                                Double.parseDouble(edgeSizeField.getText()));
                                Line line = new Line(neighbors.get(0).getX()+12,neighbors.get(0).getY()+12,neighbors.get(1).getX()+12,neighbors.get(1).getY()+12);
                                line.setStroke(Color.web("#66d66d"));
                                anchor.getChildren().add(line);
                                anchor.getChildren().get(anchor.getChildren().size()-1).toBack();
                                neighbors.clear();
                                edgeSizePane.setVisible(false);
                            });
                        }
                    }
                });
                anchor.getChildren().add(btn);
            }
        });

        randomRadio.setOnMouseClicked(event -> {
            div.setDisable(true);
            source.setDisable(true);
            key.setDisable(true);
            edge.setDisable(true);
            randomCoordinate(graph,vector);
            randomEdges(vector);
            for (Graph.Node node : graph.getNodes()){
                for (Graph.Edge edge : node.edges){
                    Line line = new Line(edge.source.getX()+12,edge.source.getY()+12,edge.destination.getX()+12,edge.destination.getY()+12);
                    line.setStroke(Color.web("#66d66d"));
                    anchor.getChildren().add(line);
                    anchor.getChildren().get(anchor.getChildren().size()-1).toBack();
                }
                JFXButton btn = new JFXButton();
                btn.setLayoutX(node.getX());
                btn.setLayoutY(node.getY());
                if (node.getType().equals("source")){
                    btn.setBackground(new Background(new BackgroundFill(Color.web("#c53324"), new CornerRadii(15), Insets.EMPTY)));
                }else if (node.getType().equals("key")){
                    btn.setBackground(new Background(new BackgroundFill(Color.web("#ecd32b"), new CornerRadii(15), Insets.EMPTY)));
                }else btn.setBackground(new Background(new BackgroundFill(Color.web("#81b0a7"), new CornerRadii(15), Insets.EMPTY)));
                btn.setText(node.getName());
                anchor.getChildren().add(btn);
            }
        });

    }
    public void findPath(){
        Graph copy = new Graph(graph);
        for (Graph.Node source : graph.getNodes()){
            if (source.getType().equals("source")){
                for (Graph.Node key : graph.getNodes()){
                    if (key.getType().equals("key")){
                        Double costs = copy.DijkstraShortestPath(source,key);

                        for (int i=0; i<copy.getPathNodes().size()-1;i++){
                            Line line = new Line(copy.getPathNodes().get(i).getX()+12,copy.getPathNodes().get(i).getY()+12,copy.getPathNodes().get(i+1).getX()+12,copy.getPathNodes().get(i+1).getY()+12);
                            line.setStroke(Color.web("#369d53"));
                            line.setStrokeWidth(4);
                            anchor.getChildren().add(line);
                            anchor.getChildren().get(anchor.getChildren().size()-1).toBack();
                        }
                        Text cost = new Text();
                        cost.setText(String.format("%.1f", costs));
                        cost.setFill(Color.WHITE);
                        cost.setFont(Font.font("Segoe UI Black", FontWeight.NORMAL, FontPosture.REGULAR, 13));
                        GridPane pane = new GridPane();
                        pane.setLayoutX(copy.getPathNodes().getLast().getX()+20);
                        pane.setLayoutY(copy.getPathNodes().getLast().getY()+15);
                        pane.setMinSize(50,22);
                        pane.setBackground(new Background(new BackgroundFill(Color.web("#3B6D6C"), new CornerRadii(15), Insets.EMPTY)));
                        pane.getChildren().add(cost);
                        pane.setAlignment(Pos.CENTER);
                        anchor.getChildren().add(pane);

                    }
                }
            }
        }
    }
    public void randomCoordinate(Graph graph, int vector){
        int randomKeyNum = ThreadLocalRandom.current().nextInt(2, 4);
        int i = 1;
        while (i<=vector){
            boolean state = false;
            int randomX = ThreadLocalRandom.current().nextInt(200, 570);
            int randomY = ThreadLocalRandom.current().nextInt(28, 387);
            if (graph.getNodes().size()==0){
                System.out.println("hey");
                Graph.Node nd = new Graph.Node(""+id++,randomX,randomY);
                nd.setType("source");
                graph.addNode(nd);
                i++;
            }
            for (Graph.Node node : graph.getNodes()){
                if (Math.sqrt(Math.pow(node.getX()-randomX,2)+Math.pow(node.getY()-randomY,2))<80){
                    state = true;
                    break;
                }
            }
            if (!state){
                Graph.Node nd = new Graph.Node(""+id++,randomX,randomY);
                if (i<=randomKeyNum+1) nd.setType("key");
                graph.addNode(nd);
                i++;
            }

        }
    }
    public void randomEdges(int vector){
        for (Graph.Node node : graph.getNodes()){
            int nodeEdgesNum = ThreadLocalRandom.current().nextInt(1, vector-3);
            int temp = 0;
            for (Graph.Node neighbor : graph.getNodes()){
                if (node!=neighbor && nodeEdgesNum-temp>0){
                    graph.addEdge(node,neighbor,Math.sqrt(Math.pow(node.getX()-neighbor.getX(),2)+Math.pow(node.getY()-neighbor.getY(),2)));
                    temp++;
                }
            }
        }

    }
    public void retry(){
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
    }
}
