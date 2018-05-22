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
import javafx.scene.Node;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import opendiabetesvaultgui.launcher.FatherController;

/**
 *
 * @author Daniel Schäfer, Martin Steil, Julian Schwind, Kai Worsch
 */
public class SliceController extends FatherController implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<String> listviewfilterelements;

    @FXML
    private Pane filtercombinationfield;

    @FXML
    private Button filterbutton;

    private double mousePositionX;

    private double mousePositionY;
    
    private double lastmousePositionX;

    private double lastmousePositionY;
    
    private int filtercombinationfieldComponents;

    @FXML
    private void doFilter(ActionEvent event) {
        
    }
    
    @FXML
    private void undoFiltercombinationfield(ActionEvent event)
    {
        
        if(filtercombinationfield.getChildren().size() > filtercombinationfieldComponents)
        {
            filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size()-1);
        }
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        filtercombinationfieldComponents = filtercombinationfield.getChildren().size();

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
                    mousePositionX = event.getX();
                    mousePositionY = event.getY();

                    filterbutton.setText("X: " + mousePositionX + " Y: " + mousePositionY);
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
                    
                    if(lastmousePositionX != 0 || lastmousePositionY != 0 )
                    {                        
                        Path connectingLines = new Path(
                                new MoveTo(lastmousePositionX, lastmousePositionY), 
                                new LineTo(mousePositionX, mousePositionY), 
                                new ClosePath()
                        );
                        
                        filtercombinationfield.getChildren().add(connectingLines);
                    }
                    
                    lastmousePositionX = mousePositionX;
                    lastmousePositionY = mousePositionY;
                    
                    
                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });        
    }

}
