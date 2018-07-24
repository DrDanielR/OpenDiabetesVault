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
import de.opendiabetes.vault.container.csv.VaultCSVEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.exporter.VaultExporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import de.opendiabetes.vault.processing.filter.Filter;
import de.opendiabetes.vault.processing.filter.FilterResult;
import de.opendiabetes.vault.processing.filter.VaultEntryTypeFilter;
import de.opendiabetes.vault.processing.filter.options.FilterOption;
import de.opendiabetes.vault.processing.filter.options.VaultEntryTypeFilterOption;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import opendiabetesvaultgui.launcher.FatherController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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

    @FXML
    private ImageView imageViewForFilter;

    private static final String FILTER_NAME = "FilterName";
    private static final String SEPARATOR = ":";
    private static final String COMBINE_FILTER = "CombineFilter";

    private List<VBox> filterColumnVBoxes = new ArrayList<>();

    private List<ChoiceBox> filterColumnChoiceBoxes = new ArrayList<>();

    private List<Separator> filterColumnSeparator = new ArrayList<>();

    private ObservableList itemsForChocieBox;

    private double mousePositionX;

    private double mousePositionY;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd.MM.yy");

    private List<List<FilterNode>> columnFilterNodes = new ArrayList<>();

    private Map<String, FilterNode> importedFilterNodes = new HashMap<>();

    private boolean combineMode = false;

    private List<VaultEntry> importedData;

    private VaultDao vaultDao;
    private FilterManagementUtil filterManagementUtil;

    @FXML
    private void doSaveFilterCombination(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showSaveDialog(MAIN_STAGE);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            int counter = 0;
            for (String currentCombineFilter : getCurrentCombineFilters()) {
                bufferedWriter.write(COMBINE_FILTER + SEPARATOR + currentCombineFilter);
                bufferedWriter.newLine();

                for (FilterNode filterNode : columnFilterNodes.get(counter)) {
                    bufferedWriter.write(FILTER_NAME + SEPARATOR + filterNode.getName());
                    bufferedWriter.newLine();

                    Iterator iterator = filterNode.getParameterAndValues().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();
                        bufferedWriter.write(pair.getKey() + SEPARATOR + pair.getValue());
                        bufferedWriter.newLine();
                    }

                }

                counter++;
            }

            bufferedWriter.close();
            fileOutputStream.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @FXML
    private void doLoadFilterCombination(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(MAIN_STAGE);

        //ToDo: Filter laden        
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            List<String> combineFilters = new ArrayList<>();
            List<List<FilterNode>> filterNodes = new ArrayList<>();

            int counter = -1;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineSplit = line.split(SEPARATOR);

                if (lineSplit[0].equals(COMBINE_FILTER)) {
                    combineFilters.add(lineSplit[1]);
                    filterNodes.add(new ArrayList<>());
                    counter++;
                } else if (lineSplit[0].equals(FILTER_NAME)) {
                    filterNodes.get(counter).add(new FilterNode(lineSplit[1], 0));
                } else {
                    filterNodes.get(counter).get(filterNodes.get(counter).size() - 1).addParam(lineSplit[0], lineSplit[1]);
                }

            }
            bufferedReader.close();

            List<Filter> filters = getFiltersFromLists(combineFilters, filterNodes);
            importedFilterNodes.put(file.getName(), new FilterNode(file.getName(), filters));
            listviewfilterelements.getItems().add(file.getName());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @FXML
    private void doFilter(ActionEvent event) {

        List<Filter> filters = getFiltersFromCurrentState();
        FilterResult filterResult = filterManagementUtil.sliceVaultEntries(filters, importedData);

        //Gefilterte Daten anzeigen
        System.out.println(filterResult);

        //For JavaFx
        populateChart(filterResult);

        //Plotteria
        generateGraphs(filterResult);

    }

    @FXML
    private void getPreviousImage(ActionEvent event) {

        try {
            if (exportDirectory != null && directoryPosition > 0) {
                directoryPosition--;

                File[] directoryListing = exportDirectory.listFiles();

                if (directoryListing != null) {
                    imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @FXML
    private void getNextImage(ActionEvent event) {
        try {
            if (exportDirectory != null) {
                directoryPosition++;

                File[] directoryListing = exportDirectory.listFiles();

                if (directoryListing != null) {
                    imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private List<Filter> getFiltersFromCurrentState() {
        List<Filter> filters = filterManagementUtil.combineFilters(getCurrentCombineFilters(), columnFilterNodes);
        return filters;
    }

    private List<String> getCurrentCombineFilters() {
        List<String> combineFilters = new ArrayList<>();
        for (ChoiceBox choiceBox : filterColumnChoiceBoxes) {
            combineFilters.add(choiceBox.getSelectionModel().getSelectedItem().toString());
        }
        return combineFilters;
    }

    private List<Filter> getFiltersFromLists(List<String> combineFilters, List<List<FilterNode>> filterNodes) {
        return filterManagementUtil.combineFilters(combineFilters, filterNodes);
    }

    private void populateChart(FilterResult filterResult) {

        filterchart.getData().removeAll(filterchart.getData());

        if (filterResult == null) {
            filterResult = filterManagementUtil.getLastDay(importedData);
        }

        Map<String, List<VaultEntry>> clusteredVaultEnries = new HashMap<>();

        for (VaultEntry vaultEntry : filterResult.filteredData) {
            if (!clusteredVaultEnries.containsKey(vaultEntry.getType().toString())) {
                List<VaultEntry> vaultEntrys = new ArrayList<>();
                vaultEntrys.add(vaultEntry);
                clusteredVaultEnries.put(vaultEntry.getType().toString(), vaultEntrys);
            } else {
                clusteredVaultEnries.get(vaultEntry.getType().toString()).add(vaultEntry);
            }

        }

        for (String key : clusteredVaultEnries.keySet()) {
            XYChart.Series series = new XYChart.Series();
            series.setName(key);
            XYChart.Data data;

            for (VaultEntry vaultEntry : clusteredVaultEnries.get(key)) {
                data = new Data(simpleDateFormat.format(vaultEntry.getTimestamp()), vaultEntry.getValue());
                series.getData().add(data);
            }

            filterchart.getData().add(series);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Daten importieren
        vaultDao = VaultDao.getInstance();
        importedData = vaultDao.queryAllVaultEntries();

        filterManagementUtil = new FilterManagementUtil();

        //Grafik laden ggf. erstmal heutigen Tag
        populateChart(null);

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

                    FilterNode tmpNode;
                    Map<String, Class> parameterClasses = filterManagementUtil.getParametersFromName(name);
                    Iterator iterator = iterator = parameterClasses.entrySet().iterator();

                    if (importedFilterNodes.get(name) != null) {
                        tmpNode = importedFilterNodes.get(name);
                    } else {
                        tmpNode = new FilterNode(name, 0);
                    }

                    //Input für Values                    
                    Label label = new Label();
                    label.setText(name);

                    tempInputPane.setStyle("-fx-border-color:grey;");

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

                    if (tmpNode.getColumnNumber() + 1 == columnFilterNodes.size()) {
                        addNewChoiceBoxAndSeperator();
                    }

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

                        if (columnFilterNodes.get(index) == null) {
                            columnFilterNodes.add(new ArrayList<>());
                        }

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

    private String userDir = System.getProperty("user.dir");
    private String plotteriaPath = userDir + File.separator + "plotteria";
    private String plotPyPath = plotteriaPath + File.separator + "plot.py";
    private String configIniPath = plotteriaPath + File.separator + "config.ini";
    private String exportFileDir = plotteriaPath + File.separator + "temp";
    private String exportFilePath = plotteriaPath + File.separator + "export.csv";
    private File exportDirectory;
    private int directoryPosition = 0;

    private void generateGraphs(FilterResult filterResult) {

        try {
            //Exportieren            
            Exporter exporter = getVaultCSVExporter();
            File file = new File(exportFilePath);
            file.createNewFile();
            exporter.exportDataToFile(exportFilePath, filterResult.filteredData);

            //python command
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("python " + plotPyPath + " -c " + configIniPath + " -d -f " + exportFilePath + " -o " + exportFileDir);
            
            //laden und anzeigen
            exportDirectory = new File(exportFileDir);
            File[] directoryListing = exportDirectory.listFiles();

            if (directoryListing != null) {
                imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private Exporter getVaultCSVExporter() {
        Exporter result = null;
        OpenDiabetesPluginManager pluginManager;
        pluginManager = OpenDiabetesPluginManager.getInstance();
        List<String> exportPlugins = pluginManager.getPluginIDsOfType(Exporter.class
        );

        for (int i = 0; i < exportPlugins.size(); i++) {
            Exporter plugin = pluginManager.getPluginFromString(Exporter.class,
                    exportPlugins.get(i));
            if (exportPlugins.get(i).equals("VaultCSVExporter")) {
                result = plugin;
            }
        }

        return result;
    }
}
