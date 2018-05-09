/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import opendiabetesvaultgui.launcher.FatherController;

/**
 *
 * @author Daniel Schäfer, Martin Steil, Julian Schwind, Kai Worsch
 */
public class SliceController extends FatherController implements Initializable {

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private ListView<String> listviewfilterelements;

    @FXML
    private Pane filtercombinationfield;

    private double mousePositionX;

    private double mousePositionY;

    @FXML
    private void doFilter(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //addItems to listview
        ObservableList<String> items = listviewfilterelements.getItems();
        items.add("AndFilter");
        items.add("OrFilter");
        items.add("TimestampFilter");
        items.add("TypeFilter");

        //Dragevent for Listview
        listviewfilterelements.setOnDragDetected(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                Dragboard dragBoard = listviewfilterelements.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(listviewfilterelements.getSelectionModel().getSelectedItem());
                dragBoard.setContent(content);

                event.consume();
            }
        });

        //Nach dem Drag and drop
        listviewfilterelements.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

            }
        });

        //DragOver for scrollpane
        filtercombinationfield.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != filtercombinationfield
                        && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }

                event.consume();
            }
        });

        //Drag Bereich betreten
        filtercombinationfield.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != filtercombinationfield
                        && event.getDragboard().hasString()) {

                }

                event.consume();
            }
        });

        //Drag Bereich verlassen
        filtercombinationfield.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */

                event.consume();
            }
        });

        //Drag loslassen
        filtercombinationfield.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Label test = new Label();
                    test.setText(db.getString());
                    test.setLayoutX(mousePositionX);
                    test.setLayoutY(mousePositionY);

                    filtercombinationfield.getChildren().add(test);
                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });

        filtercombinationfield.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePositionX = event.getSceneX();
                System.out.println(mousePositionX);
                mousePositionY = event.getSceneY();
                System.out.println(mousePositionX);
            }
        });

    }

}
