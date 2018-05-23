/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    
    @FXML
    private CheckBox checkboxcombinemode;
    
    private double mousePositionX;

    private double mousePositionY;    
    
    private int filtercombinationfieldComponents;
    
    private List<FilterNode> filterNodes;
    
    private boolean combineMode = false;

    @FXML
    private void doFilter(ActionEvent event) {
        
    }
    
    @FXML
    private void undoFiltercombinationfield(ActionEvent event)
    {
        
        if(filtercombinationfield.getChildren().size() > filtercombinationfieldComponents)
        {
            if(filtercombinationfield.getChildren().size() == filtercombinationfieldComponents+1)
                filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size()-1);
            else
            {
                //remove line and Node
                filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size()-1);
                filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size()-1);
            }
                
            if(filterNodes.get(filterNodes.size()-1).getFilterNodes().size() == 0)
                filterNodes.remove(filterNodes.size()-1);
            else
                filterNodes.get(filterNodes.size()-1).getFilterNodes().remove(filterNodes.get(filterNodes.size()-1).getFilterNodes().size()-1);
            
        }
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        filterNodes = new ArrayList<>();
        
        //Backup für undo
        filtercombinationfieldComponents = filtercombinationfield.getChildren().size();
        
        //checkbox am beginn off
        checkboxcombinemode.setSelected(false);

        //addItems to listview
        ObservableList<String> items = listviewfilterelements.getItems();
        items.addAll(FilterDummyUtil.getAllFilters());

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
                    
                    FilterNode tmpNode = new FilterNode(mousePositionX, mousePositionY, FilterDummyUtil.combinesFilter(db.getString()));
                    Label test = new Label();
                    test.setText(db.getString());
                    test.setLayoutX(tmpNode.getPositionX());
                    test.setLayoutY(tmpNode.getPositionY());                    
                    
                    filtercombinationfield.getChildren().add(test);
                    
                    if(filterNodes.size()>0 && !checkboxcombinemode.isSelected())
                    {                        
                        Path connectingLines = new Path(
                                new MoveTo(filterNodes.get(filterNodes.size()-1).getPositionX(), filterNodes.get(filterNodes.size()-1).getPositionY()), 
                                new LineTo(tmpNode.getPositionX(), tmpNode.getPositionY()), 
                                new ClosePath()
                        );
                        
                        filtercombinationfield.getChildren().add(connectingLines);                       
                        filterNodes.add(tmpNode);                       
                        
                    }
                    else if(filterNodes.size()>0 && checkboxcombinemode.isSelected())
                    {
                        Path connectingLines = new Path(
                                new MoveTo(filterNodes.get(filterNodes.size()-1).getPositionX(), filterNodes.get(filterNodes.size()-1).getPositionY()), 
                                new LineTo(tmpNode.getPositionX(), tmpNode.getPositionY()), 
                                new ClosePath()
                        );
                        
                        filtercombinationfield.getChildren().add(connectingLines);
                        
                        if(tmpNode.isCombineFilter())
                            filterNodes.add(tmpNode);
                        else
                            filterNodes.get(filterNodes.size()-1).getFilterNodes().add(tmpNode);                        
                    }
                    else
                        filterNodes.add(tmpNode);
                    
                    //alle zusammen???
                    
                    //erlauben und nicht erlauben undo überarbeiten
                    if(tmpNode.isCombineFilter())
                        checkboxcombinemode.setSelected(true);
                    
                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });        
    }

}
