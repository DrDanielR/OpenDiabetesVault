/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import de.opendiabetes.vault.processing.filter.options.guibackend.FilterManagementUtil;
import de.opendiabetes.vault.processing.filter.options.guibackend.FilterNode;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.container.VaultEntryTypeGroup;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.processing.filter.Filter;
import de.opendiabetes.vault.processing.filter.FilterResult;
import de.opendiabetes.vault.processing.filter.TypeGroupFilter;
import de.opendiabetes.vault.processing.filter.VaultEntryTypeFilter;
import de.opendiabetes.vault.processing.filter.options.FilterOption;
import de.opendiabetes.vault.processing.filter.options.TypeGroupFilterOption;
import de.opendiabetes.vault.processing.filter.options.VaultEntryTypeFilterOption;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
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
    private CategoryAxis filterChartXaxis;

    @FXML
    private NumberAxis filterChartYaxis;

    @FXML
    private ChoiceBox firstColumnChoiceBox;

    @FXML
    private ChoiceBox secondColumnChoiceBox;

    @FXML
    private ChoiceBox thirdColumnChoiceBox;

    @FXML
    private Separator firstColumnSeparator;

    @FXML
    private Separator secondColumnSeparator;

    private double mousePositionX;

    private double mousePositionY;

    private int filtercombinationfieldComponents;
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd.MM.yy");

    private List<FilterNode> firstColumnFilterNodes = new ArrayList<>();
    private List<FilterNode> secondColumnFilterNodes = new ArrayList<>();
    private List<FilterNode> thirdColumnFilterNodes = new ArrayList<>();

    private boolean combineMode = false;

    private List<VaultEntry> importedData;

    private VaultDao vaultDao;
    private FilterManagementUtil filterManagementUtil;

    @FXML
    private void doFilter(ActionEvent event) {

        List<String> combineFilters = new ArrayList<>();
        List<List<FilterNode>> allColumnsFilterNodes = new ArrayList();
        //Iterate Over all Columns
        for (FilterNode filterNode : firstColumnFilterNodes) {
            allColumnsFilterNodes.add(firstColumnFilterNodes);
            combineFilters.add(firstColumnChoiceBox.getSelectionModel().getSelectedItem().toString());
        }
        for (FilterNode filterNode : secondColumnFilterNodes) {
            allColumnsFilterNodes.add(secondColumnFilterNodes);
            combineFilters.add(secondColumnChoiceBox.getSelectionModel().getSelectedItem().toString());
        }
        for (FilterNode filterNode : thirdColumnFilterNodes) {
            allColumnsFilterNodes.add(thirdColumnFilterNodes);
            combineFilters.add(thirdColumnChoiceBox.getSelectionModel().getSelectedItem().toString());
        }

        List<Filter> filters = filterManagementUtil.combineFilters(combineFilters, allColumnsFilterNodes);
        FilterResult filterResult = filterManagementUtil.sliceVaultEntries(filters, importedData);

        
        //Gefilterte Daten anzeigen
        System.out.println(filterResult); 
        
        XYChart.Series series = new XYChart.Series();        
        XYChart.Data data;
        
        filterchart.getData().removeAll(filterchart.getData());
        
        for (VaultEntry vaultEntry : filterResult.filteredData) {
            data = new Data(simpleDateFormat.format(vaultEntry.getTimestamp()), vaultEntry.getValue());
            series.getData().add(data);
        }

        filterchart.getData().add(series);
        

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
             * if (filterNodes.get(filterNodes.size() -
             * 1).getFilterNodes().size() == 0) {
             * filterNodes.remove(filterNodes.size() - 1); } else {
             * filterNodes.get(filterNodes.size() -
             * 1).getFilterNodes().remove(filterNodes.get(filterNodes.size() -
             * 1).getFilterNodes().size() - 1); }
             *
             */
        }

    }

    private void populateChart() {
        //Teststuff
        VaultEntryTypeFilter vaultEntryTypeFilter = new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(VaultEntryType.EXERCISE_LOW));

        XYChart.Series series = new XYChart.Series();        
        XYChart.Data data;

        FilterResult filterResult = vaultEntryTypeFilter.filter(importedData);
        
        for (VaultEntry vaultEntry : filterResult.filteredData) {
            data = new Data(simpleDateFormat.format(vaultEntry.getTimestamp()), vaultEntry.getValue());
            series.getData().add(data);
        }

        filterchart.getData().add(series);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Daten importieren
        vaultDao = VaultDao.getInstance();
        importedData = vaultDao.queryAllVaultEntries();

        filterManagementUtil = new FilterManagementUtil();

        //Grafik laden ggf. erstmal heutigen Tag
        populateChart();

        //ToDo ChocieBox füllen Mit registrierten Combine Filter
        ObservableList itemsForChocieBox = FXCollections.observableArrayList(filterManagementUtil.getCombineFilter());

        firstColumnChoiceBox.setItems(itemsForChocieBox);
        firstColumnChoiceBox.getSelectionModel().selectFirst();

        secondColumnChoiceBox.setItems(itemsForChocieBox);
        secondColumnChoiceBox.getSelectionModel().selectFirst();

        thirdColumnChoiceBox.setItems(itemsForChocieBox);
        thirdColumnChoiceBox.getSelectionModel().selectFirst();

        //Backup für undo
        filtercombinationfieldComponents = filtercombinationfield.getChildren().size();

        //addItems to listview
        ObservableList<String> items = listviewfilterelements.getItems();
        items.addAll(filterManagementUtil.getAllNotCombineFilters());

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
                    VBox tempInputPane = new VBox();

                    //Input für Values                    
                    Map<String, Class> parameterClasses = filterManagementUtil.getParametersFromName(name);
                    Iterator iterator = parameterClasses.entrySet().iterator();

                    FilterNode tmpNode = new FilterNode(name, mousePositionX, mousePositionY);
                    Label label = new Label();
                    label.setText(name);

                    tempInputPane.setStyle("-fx-border-color:black;");

                    calculateLabelPosition(tmpNode);

                    tempInputPane.getChildren().add(label);

                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();

                        final String simpleName = (String) pair.getKey();
                        final Class typeClass = (Class) pair.getValue();

                        HBox tmpHBox = new HBox();
                        tmpHBox.setMaxWidth(200);

                        tmpHBox.getChildren().add(new Label(simpleName));

                        if (typeClass.getSimpleName().equals("Date")) {
                            DatePicker datePicker = new DatePicker();
                            datePicker.setMaxWidth(100);
                            datePicker.setPromptText("Date");
                            tmpHBox.getChildren().add(datePicker);

                            //ActionEvent for params
                            datePicker.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    tmpNode.addParam(simpleName, datePicker.getValue().toString());
                                }
                            });
                        } else if (typeClass.getSimpleName().equals("boolean")) {
                            CheckBox checkBox = new CheckBox();
                            tmpHBox.getChildren().add(checkBox);

                            //ActionEvent for params
                            checkBox.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    tmpNode.addParam(simpleName, "" + checkBox.isSelected());
                                }
                            });
                        }                        
                        else if (typeClass.getSimpleName().equals("Map")) {
                            ChoiceBox choiceBox = new ChoiceBox();
                            choiceBox.getSelectionModel().selectFirst();
                            final FilterOption filterOption = filterManagementUtil.getFilterAndOptionFromName(name).getOption();
                            
                            ObservableList itemsForTmpChocieBox = FXCollections.observableArrayList(filterOption.getDropDownEntries().keySet());
                            choiceBox.setItems(itemsForTmpChocieBox);
                            
                            tmpHBox.getChildren().add(choiceBox);

                            //ActionEvent for params
                            choiceBox.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    tmpNode.addParam(simpleName, filterOption.getDropDownEntries().get(choiceBox.getSelectionModel().getSelectedItem()));
                                }
                            });
                        } 
                        else {
                            TextField tmpTextField = new TextField();
                            tmpTextField.setMaxWidth(100);
                            tmpTextField.setPromptText("Value");

                            if (typeClass.getSimpleName().equals("LocalTime")) {
                                tmpTextField.setPromptText("00:00");
                                TextFormatter textFormatter = new TextFormatter(new LocalTimeStringConverter());
                                tmpTextField.setTextFormatter(textFormatter);
                            }
                            else if (typeClass.getSimpleName().equals("int") || typeClass.getSimpleName().equals("long")) {
                                tmpTextField.setPromptText("0000");
                                TextFormatter textFormatter = new TextFormatter(new NumberStringConverter());
                                tmpTextField.setTextFormatter(textFormatter);
                            }

                            tmpHBox.getChildren().add(tmpTextField);

                            //ActionEvent for params
                            tmpTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    tmpNode.addParam(simpleName, tmpTextField.getText());
                                }
                            });
                        }

                        tempInputPane.getChildren().add(tmpHBox);
                    }

                    tempInputPane.setLayoutX(tmpNode.getPositionX());
                    tempInputPane.setLayoutY(tmpNode.getPositionY());

                    filtercombinationfield.getChildren().add(tempInputPane);

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }

            private void calculateLabelPosition(FilterNode tmpNode) {

                int paddingleft = 250;
                int paddingright = 10;

                tmpNode.setPositionY(mousePositionY);

                if (mousePositionX < firstColumnSeparator.getLayoutX()) {
                    tmpNode.setPositionX(firstColumnSeparator.getLayoutX() - paddingleft);
                    firstColumnFilterNodes.add(tmpNode);
                } else if (mousePositionX < secondColumnSeparator.getLayoutX()) {
                    tmpNode.setPositionX(secondColumnSeparator.getLayoutX() - paddingleft);
                    secondColumnFilterNodes.add(tmpNode);
                } else {
                    tmpNode.setPositionX(secondColumnSeparator.getLayoutX() + paddingright);
                    thirdColumnFilterNodes.add(tmpNode);
                }

            }
        });
    }
}
