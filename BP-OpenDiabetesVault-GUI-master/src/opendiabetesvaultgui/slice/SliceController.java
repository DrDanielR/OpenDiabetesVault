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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
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
import javafx.stage.FileChooser;
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
    private Button filterbutton;

    @FXML
    private LineChart<Number, Number> filterchart;

    @FXML
    private CategoryAxis filterChartXaxis;

    @FXML
    private NumberAxis filterChartYaxis;

    @FXML
    private ScrollPane filtercombinationfield;

    @FXML
    private HBox filterCombinationHbox;

    private List<VBox> filterColumnVBoxes = new ArrayList<>();

    private List<ChoiceBox> filterColumnChoiceBoxes = new ArrayList<>();

    private List<Separator> filterColumnSeparator = new ArrayList<>();

    private ObservableList itemsForChocieBox;

    private double mousePositionX;

    private double mousePositionY;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd.MM.yy");

    private List<List<FilterNode>> columnFilterNodes = new ArrayList<>();

    private boolean combineMode = false;

    private List<VaultEntry> importedData;

    private VaultDao vaultDao;
    private FilterManagementUtil filterManagementUtil;

    @FXML
    private void doSaveFilterCombination(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showSaveDialog(MAIN_STAGE);
        
        //ToDo: Filter speichern
        
        
    }
    
    @FXML
    private void doLoadFilterCombination(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(MAIN_STAGE);
        
        //ToDo: Filter laden
    }
    
    
    
    @FXML
    private void doFilter(ActionEvent event) {

        List<Filter> filters = getFiltersFromCurrentState();
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

    private List<Filter> getFiltersFromCurrentState() {
        List<String> combineFilters = new ArrayList<>();
        for (ChoiceBox choiceBox : filterColumnChoiceBoxes) {
            combineFilters.add(choiceBox.getSelectionModel().getSelectedItem().toString());
        }
        List<Filter> filters = filterManagementUtil.combineFilters(combineFilters, columnFilterNodes);
        return filters;
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
        itemsForChocieBox = FXCollections.observableArrayList(filterManagementUtil.getCombineFilter());

        addNewChoiceBoxAndSeperator();

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

                    FilterNode tmpNode = new FilterNode(name, 0);
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
                        } else if (typeClass.getSimpleName().equals("Map")) {
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
                        } else {
                            TextField tmpTextField = new TextField();
                            tmpTextField.setMaxWidth(100);
                            tmpTextField.setPromptText("Value");

                            if (typeClass.getSimpleName().equals("LocalTime")) {
                                tmpTextField.setPromptText("00:00");
                                TextFormatter textFormatter = new TextFormatter(new LocalTimeStringConverter());
                                tmpTextField.setTextFormatter(textFormatter);
                            } else if (typeClass.getSimpleName().equals("int") || typeClass.getSimpleName().equals("long")) {
                                tmpTextField.setPromptText("0000");
                                TextFormatter textFormatter = new TextFormatter(new NumberStringConverter());
                                tmpTextField.setTextFormatter(textFormatter);
                            }

                            tmpHBox.getChildren().add(tmpTextField);

                            //ActionEvent for params
                            tmpTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                @Override
                                public void handle(KeyEvent event) {
                                    tmpNode.addParam(simpleName, tmpTextField.getText() + event.getText());
                                }
                            });
                        }

                        tempInputPane.getChildren().add(tmpHBox);
                    }
                            
                    if(tmpNode.getColumnNumber()+1 == columnFilterNodes.size())
                        addNewChoiceBoxAndSeperator();
                    
                    filterColumnVBoxes.get(tmpNode.getColumnNumber()).getChildren().add(tempInputPane);
                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }

            private void calculateLabelPosition(FilterNode tmpNode) {                

                for (Separator separator : filterColumnSeparator) {
                    if (mousePositionX > separator.getLayoutX()) {
                        int index = filterColumnSeparator.indexOf(separator);
                        tmpNode.setColumnNumber(index);
                        
                        if(columnFilterNodes.get(index) == null)
                            columnFilterNodes.add(new ArrayList<>());
                        
                        columnFilterNodes.get(index).add(tmpNode);
                        
                    }
                }

            }
        });
    }

    private void addNewChoiceBoxAndSeperator() {
        
        //Separator hinzufügen
        Separator separator = new Separator(Orientation.HORIZONTAL);
        filterColumnSeparator.add(separator);
        filterCombinationHbox.getChildren().add(separator);

        //Vbox für Filter und ChoiceBox
        VBox vBox = new VBox();

        ChoiceBox choiceBox = new ChoiceBox(itemsForChocieBox);
        choiceBox.getSelectionModel().selectFirst();
        filterColumnChoiceBoxes.add(choiceBox);

        vBox.getChildren().add(choiceBox);
        filterColumnVBoxes.add(vBox);

        filterCombinationHbox.getChildren().add(vBox);
        
        
        //Für FilterNode vorbereitung
        columnFilterNodes.add(new ArrayList<>());

    }
}
