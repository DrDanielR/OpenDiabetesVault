/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.container.VaultEntryTypeGroup;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.processing.filter.FilterResult;
import de.opendiabetes.vault.processing.filter.TypeGroupFilter;
import de.opendiabetes.vault.processing.filter.VaultEntryTypeFilter;
import de.opendiabetes.vault.processing.filter.options.TypeGroupFilterOption;
import de.opendiabetes.vault.processing.filter.options.VaultEntryTypeFilterOption;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import jdk.nashorn.internal.objects.NativeArray;
import opendiabetesvaultgui.launcher.FatherController;
import opendiabetesvaultgui.launcher.MainWindowController;

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
    private LineChart<Number, Number> filterchart;

    @FXML
    private NumberAxis filterChartXaxis;

    @FXML
    private NumberAxis filterChartYaxis;

    @FXML
    private ChoiceBox firstColumnChoiceBox;

    @FXML
    private ChoiceBox secondColumnChoiceBox;

    @FXML
    private ChoiceBox thirdColumnChoiceBox;

    @FXML
    private ChoiceBox fourthColumnChoiceBox;

    @FXML
    private ChoiceBox fifthColumnChoiceBox;

    @FXML
    private Separator firstColumnSeparator;

    @FXML
    private Separator secondColumnSeparator;

    @FXML
    private Separator thirdColumnSeparator;

    @FXML
    private Separator fourthColumnSeparator;

    private double mousePositionX;

    private double mousePositionY;

    private int filtercombinationfieldComponents;

    private List<FilterNode> firstColumnFilterNodes = new ArrayList<>();
    private List<FilterNode> secondColumnFilterNodes = new ArrayList<>();    
    private List<FilterNode> thirdColumnFilterNodes = new ArrayList<>();
    private List<FilterNode> fourthColumnFilterNodes = new ArrayList<>();
    private List<FilterNode> fifthColumnFilterNodes = new ArrayList<>();
    

    private boolean combineMode = false;

    private List<VaultEntry> importedData;

    private VaultDao vaultDao;
    private FilterDummyUtil filterDummyUtil;

    @FXML
    private void doFilter(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);

        String nodes = "";
        //Iterate Over all Columns
        for (FilterNode filterNode : firstColumnFilterNodes) {

            nodes += "#"+ firstColumnChoiceBox.getSelectionModel().getSelectedItem() +"#" + filterNode.getName();
            if (filterNode.getParameters() != null) {
                nodes += filterNode.getParameters();
            }
        }
        for (FilterNode filterNode : secondColumnFilterNodes) {

            nodes += "#"+ secondColumnChoiceBox.getSelectionModel().getSelectedItem() +"#" + filterNode.getName();
            if (filterNode.getParameters() != null) {
                nodes += filterNode.getParameters();
            }
        }
        for (FilterNode filterNode : thirdColumnFilterNodes) {

            nodes += "#"+ thirdColumnChoiceBox.getSelectionModel().getSelectedItem() +"#" + filterNode.getName();
            if (filterNode.getParameters() != null) {
                nodes += filterNode.getParameters();
            }
        }
        for (FilterNode filterNode : fourthColumnFilterNodes) {

            nodes += "#"+ fourthColumnChoiceBox.getSelectionModel().getSelectedItem() +"#" + filterNode.getName();
            if (filterNode.getParameters() != null) {
                nodes += filterNode.getParameters();
            }
        }
        for (FilterNode filterNode : fifthColumnFilterNodes) {

            nodes += "#"+ fifthColumnChoiceBox.getSelectionModel().getSelectedItem() +"#" + filterNode.getName();
            if (filterNode.getParameters() != null) {
                nodes += filterNode.getParameters();
            }
        }

        alert.setContentText(nodes);
        alert.show();

        VaultEntry vaultEntry = new VaultEntry();
    }

    @FXML
    private void undoFiltercombinationfield(ActionEvent event) {

        if (filtercombinationfield.getChildren().size() > filtercombinationfieldComponents) {
            if (filtercombinationfield.getChildren().size() == filtercombinationfieldComponents + 1) {
                filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size() - 1);
            } else {
                //remove line and Node
                filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size() - 1);
                filtercombinationfield.getChildren().remove(filtercombinationfield.getChildren().size() - 1);
            }

            /**
            if (filterNodes.get(filterNodes.size() - 1).getFilterNodes().size() == 0) {
                filterNodes.remove(filterNodes.size() - 1);
            } else {
                filterNodes.get(filterNodes.size() - 1).getFilterNodes().remove(filterNodes.get(filterNodes.size() - 1).getFilterNodes().size() - 1);
            }
            **/

        }

    }

    private void populateChart() {
        //Teststuff
        VaultEntryTypeFilter vaultEntryTypeFilter = new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(VaultEntryType.EXERCISE_LOW));

        XYChart.Series series = new XYChart.Series();
        series.setName("EXERCISE_LOW");
        XYChart.Data data;

        FilterResult filterResult = vaultEntryTypeFilter.filter(importedData);

        int index = 0;
        for (VaultEntry vaultEntry : filterResult.filteredData) {
            data = new Data(index, vaultEntry.getValue());
            series.getData().add(data);
            index++;
        }

        filterchart.getData().add(series);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        //Daten importieren
        vaultDao = VaultDao.getInstance();
        importedData = vaultDao.queryAllVaultEntries();

        filterDummyUtil = new FilterDummyUtil();
        
        
        //Grafik laden ggf. erstmal heutigen Tag
        populateChart();

        //ToDo ChocieBox füllen Mit registrierten Combine Filter
        ObservableList itemsForChocieBox = FXCollections.observableArrayList(filterDummyUtil.getCombineFilter());        
        
        firstColumnChoiceBox.setItems(itemsForChocieBox);
        firstColumnChoiceBox.getSelectionModel().selectFirst();

        secondColumnChoiceBox.setItems(itemsForChocieBox);
        secondColumnChoiceBox.getSelectionModel().selectFirst();

        thirdColumnChoiceBox.setItems(itemsForChocieBox);
        thirdColumnChoiceBox.getSelectionModel().selectFirst();

        fourthColumnChoiceBox.setItems(itemsForChocieBox);
        fourthColumnChoiceBox.getSelectionModel().selectFirst();

        fifthColumnChoiceBox.setItems(itemsForChocieBox);
        fifthColumnChoiceBox.getSelectionModel().selectFirst();

        //Backup für undo
        filtercombinationfieldComponents = filtercombinationfield.getChildren().size();

        //addItems to listview
        ObservableList<String> items = listviewfilterelements.getItems();
        items.addAll(filterDummyUtil.getAllNotCombineFilters());

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

                    //filterbutton.setText("X: " + mousePositionX + " Y: " + mousePositionY);
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

                    String name = db.getString();
                    TextInputDialog dialog = new TextInputDialog();

                    //Input für Values
                    Optional<String> values = null;
                    if (filterDummyUtil.getParametersFromName(name) != null) {
                        dialog = new TextInputDialog(filterDummyUtil.getParametersFromName(name));
                        dialog.setContentText("Werte:");
                        values = dialog.showAndWait();
                    }

                    FilterNode tmpNode = new FilterNode(name, mousePositionX, mousePositionY);
                    if (values != null && values.isPresent()) {
                        tmpNode.setParameters(values.get());
                    }
                    Label label = new Label();
                    if (tmpNode.getParameters() != null) {
                        label.setText(db.getString() + "\n" + tmpNode.getParameters());
                    } else {
                        label.setText(db.getString());
                    }

                    label.setStyle("-fx-border-color:black;");

                    calculateLabelPosition(tmpNode);

                    label.setLayoutX(tmpNode.getPositionX());
                    label.setLayoutY(tmpNode.getPositionY());

                    filtercombinationfield.getChildren().add(label);

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }

            private void calculateLabelPosition(FilterNode tmpNode) {

                int paddingleft = 150;
                int paddingright = 10;
                
                tmpNode.setPositionY(mousePositionY);
                
                if (mousePositionX < firstColumnSeparator.getLayoutX()) {
                    tmpNode.setPositionX(firstColumnSeparator.getLayoutX()-paddingleft);
                    firstColumnFilterNodes.add(tmpNode);
                } else if (mousePositionX < secondColumnSeparator.getLayoutX()) {
                    tmpNode.setPositionX(secondColumnSeparator.getLayoutX()-paddingleft);
                    secondColumnFilterNodes.add(tmpNode);
                } else if (mousePositionX < thirdColumnSeparator.getLayoutX()) {
                    tmpNode.setPositionX(thirdColumnSeparator.getLayoutX()-paddingleft);
                    thirdColumnFilterNodes.add(tmpNode);
                } else if (mousePositionX < fourthColumnSeparator.getLayoutX()) {
                    tmpNode.setPositionX(fourthColumnSeparator.getLayoutX()-paddingleft);
                    fourthColumnFilterNodes.add(tmpNode);
                }else
                {
                    tmpNode.setPositionX(fourthColumnSeparator.getLayoutX()+paddingright);
                    fifthColumnFilterNodes.add(tmpNode);
                }

            }
        });
    }
}
