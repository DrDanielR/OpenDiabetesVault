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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import javax.activation.MimetypesFileTypeMap;
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
    private ListView<Node> listviewfilterelements;

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

    @FXML
    private ProgressBar importprogressbar;

    @FXML
    private Label maximportnumber;

    @FXML
    private Button nextbutton;

    @FXML
    private Button previousbutton;

    @FXML
    private Spinner currentimport;

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

            //Element für listview erstellen
            HBox hBox = new HBox();
            ImageView imageView = new ImageView();
            imageView.setFitHeight(25);
            imageView.setFitWidth(25);

            File imageFile = new File("src/opendiabetesvaultgui/shapes/gear.png");
            Image image = new Image(imageFile.toURI().toString());
            imageView.setImage(image);

            hBox.getChildren().add(imageView);

            Label label = new Label();
            label.setText(file.getName());
            hBox.getChildren().add(label);;

            listviewfilterelements.getItems().add(hBox);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @FXML
    private void doFilter(ActionEvent event) {

        List<Filter> filters = getFiltersFromCurrentState();
        if (filters != null) {
            //null entfernen normalerweise keine drinnen
            while (filters.remove(null));

            FilterResult filterResult = filterManagementUtil.sliceVaultEntries(filters, importedData);

            if (filterResult != null) {
                //For JavaFx
                populateChart(filterResult);

                //Plotteria
                generateGraphs(filterResult);
            }
        }
    }

    @FXML
    private void doReset(ActionEvent event) {

        //Alle Filter löschen      
        for (VBox filterColumnVBoxe : filterColumnVBoxes) {
            filterColumnVBoxe.getChildren().removeAll(filterColumnVBoxe.getChildren());
        }
        filterCombinationHbox.getChildren().removeAll(filterCombinationHbox.getChildren());
        filterColumnVBoxes.clear();
        filterColumnSeparator.clear();
        filterColumnChoiceBoxes.clear();
        columnFilterNodes.clear();

        addNewChoiceBoxAndSeperator();
    }

    @FXML
    private void changeCurrentImage(ActionEvent event) {
        int tmp = Integer.parseInt(currentimport.getValue().toString()) - 1;

        if (tmp <= currentMaxImportNumber && tmp > 0) {
            try {
                directoryPosition = tmp;
                if (directoryListing != null) {
                    imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                }

                if (directoryPosition == 0) {
                    previousbutton.setDisable(true);
                    nextbutton.setDisable(false);
                } else if (directoryPosition == currentMaxImportNumber) {
                    nextbutton.setDisable(true);
                    previousbutton.setDisable(false);
                } else {
                    nextbutton.setDisable(false);
                    previousbutton.setDisable(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void getPreviousImage(ActionEvent event) {
        currentimport.decrement();
    }

    @FXML
    private void getNextImage(ActionEvent event) {
        currentimport.increment();
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
        exportDirectory = new File(exportFileDir);
        emptyDirectoryForGraphs(exportDirectory);
        FilterResult filterResult = filterManagementUtil.getLastDay(importedData);
        populateChart(filterResult);
        generateGraphs(filterResult);

        //ToDo ChocieBox füllen Mit registrierten Combine Filter        
        itemsForChocieBox = FXCollections.observableArrayList(filterManagementUtil.getCombineFilter());

        addNewChoiceBoxAndSeperator();

        //ValueFactory for Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                for (int i = 0; i < steps; i++) {
                    decrementImageView();
                    this.setValue(this.getValue() - 1);
                }
                if (this.getValue() <= 0) {
                    this.setValue(1);
                }
            }

            @Override
            public void increment(int steps) {
                for (int i = 0; i < steps; i++) {
                    incrementImageView();
                    this.setValue(this.getValue() + 1);
                }
                if (this.getValue() > currentMaxImportNumber) {
                    this.setValue(currentMaxImportNumber);
                }
            }
        };
        valueFactory.setValue(1);
        currentimport.setValueFactory(valueFactory);

        //addItems to listview Filter
        ObservableList<Node> items = listviewfilterelements.getItems();
        items.addAll(getItemsForFilterListView());

        //Dragevent for Listview
        listviewfilterelements.setOnDragDetected(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                Dragboard dragBoard = listviewfilterelements.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();

                Label label = (Label) ((HBox) listviewfilterelements.getSelectionModel().getSelectedItem()).getChildren().get(1);

                content.putString(label.getText());
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
                    VBox tmpInputVBox = new VBox();
                    tmpInputVBox.setSpacing(10);

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

                    tmpInputVBox.setStyle("-fx-border-color:grey; -fx-background-radius: 10; -fx-border-radius: 10; -fx-box-shadow: 2 3 #888888;");

                    calculateLabelPosition(tmpNode);

                    tmpInputVBox.getChildren().add(label);

                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();

                        final String simpleName = (String) pair.getKey();
                        final Class typeClass = (Class) pair.getValue();

                        HBox tmpHBox = new HBox();
                        tmpHBox.setAlignment(Pos.CENTER);
                        tmpHBox.setMaxWidth(200);

                        tmpHBox.getChildren().add(new Label(simpleName));

                        if (typeClass.getSimpleName().equals("Date")) {

                            final Date dummyDate = importedData.get(0).getTimestamp();

                            DatePicker datePicker = new DatePicker();
                            datePicker.setMaxWidth(100);
                            datePicker.setPromptText("Date");
                            datePicker.setValue(dummyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                            Callback<DatePicker, DateCell> dayCellFactory = getDayCellFactory();
                            datePicker.setDayCellFactory(dayCellFactory);

                            tmpNode.addParam(simpleName, datePicker.getValue().toString());

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
                            tmpNode.addParam(simpleName, "" + checkBox.isSelected());

                            //ActionEvent for params
                            checkBox.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    tmpNode.addParam(simpleName, "" + checkBox.isSelected());
                                }
                            });
                        } else if (typeClass.getSimpleName().equals("Map")) {
                            ChoiceBox choiceBox = new ChoiceBox();

                            final FilterOption filterOption = filterManagementUtil.getFilterAndOptionFromName(name).getOption();

                            ObservableList itemsForTmpChocieBox = FXCollections.observableArrayList(filterOption.getDropDownEntries().keySet());
                            choiceBox.setItems(itemsForTmpChocieBox);
                            choiceBox.getSelectionModel().selectFirst();

                            tmpHBox.getChildren().add(choiceBox);
                            tmpNode.addParam(simpleName, filterOption.getDropDownEntries().get(choiceBox.getSelectionModel().getSelectedItem()));

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
                                tmpNode.addParam(simpleName, "00:00");
                            } else if (typeClass.getSimpleName().equals("int") || typeClass.getSimpleName().equals("long")) {
                                tmpTextField.setPromptText("0");
                                TextFormatter textFormatter = new TextFormatter(new NumberStringConverter());
                                tmpTextField.setTextFormatter(textFormatter);
                                tmpNode.addParam(simpleName, "0");
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

                        tmpInputVBox.getChildren().add(tmpHBox);

                    }
                    Button deleteButton = new Button();
                    deleteButton.setText("Entfernen");
                    deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            filterColumnVBoxes.get(tmpNode.getColumnNumber()).getChildren().remove(tmpInputVBox);

                            for (List<FilterNode> columnFilterNode : columnFilterNodes) {
                                columnFilterNode.remove(tmpNode);
                            }
                        }
                    });

                    tmpInputVBox.getChildren().add(deleteButton);

                    if (tmpNode.getColumnNumber() + 1 == columnFilterNodes.size()) {
                        addNewChoiceBoxAndSeperator();
                    }

                    filterColumnVBoxes.get(tmpNode.getColumnNumber()).getChildren().add(tmpInputVBox);
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

        vBox.setSpacing(5);
        ChoiceBox choiceBox = new ChoiceBox(itemsForChocieBox);
        choiceBox.getSelectionModel().selectFirst();
        filterColumnChoiceBoxes.add(choiceBox);

        vBox.getChildren().add(choiceBox);
        filterColumnVBoxes.add(vBox);

        filterCombinationHbox.getChildren().add(vBox);

        //Für FilterNode vorbereitung
        columnFilterNodes.add(new ArrayList<>());

    }

    //import stuff
    private String userDir = System.getProperty("user.dir");
    private String plotteriaPath = userDir + File.separator + "plotteria";
    private String plotPyPath = plotteriaPath + File.separator + "plot.py";
    private String configIniPath = plotteriaPath + File.separator + "config.ini";
    private String exportFileDir = plotteriaPath + File.separator + "temp";
    private String exportFilePath = plotteriaPath + File.separator + "export.csv";
    private File exportDirectory;
    private int directoryPosition = 0;
    private Thread exportThread;
    private float currentProgress = -1;
    File[] directoryListing;
    int currentMaxImportNumber = 0;
    Process exportProcess;

    private void generateGraphs(FilterResult filterResult) {
        try {
            exportDirectory = new File(exportFileDir);
            emptyDirectoryForGraphs(exportDirectory);

            //Exportieren
            //Exporter exporter = getVaultCSVExporter();
            Exporter exporter = new VaultCsvExporterExtended();
            File file = new File(exportFilePath);
            file.createNewFile();
            exporter.exportDataToFile(exportFilePath, filterResult.filteredData);

            String command = "python " + plotPyPath + " -c " + configIniPath + " -d -f " + exportFilePath + " -o " + exportFileDir;

            if (exportThread != null) {
                importprogressbar.progressProperty().unbind();
                if (exportProcess != null) {
                    exportProcess.destroyForcibly();
                }
            }

            currentProgress = 0;
            importprogressbar.setProgress(currentProgress);
            //python command
            Task<Void> exportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        exportProcess = runtime.exec(command);

                        while (currentProgress < 1) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(exportProcess.getInputStream()));
                            String line;
                            while ((line = in.readLine()) != null) {
                                if (line.endsWith("%")) {
                                    line = line.replace("%", "");
                                    line = line.trim();
                                    currentProgress = Float.parseFloat(line) / 100;
                                    this.updateProgress(currentProgress, 1);

                                    //laden und nur pngs anzeigen
                                    directoryListing = exportDirectory.listFiles(new FileFilter() {
                                        public boolean accept(File file) {
                                            return file.isFile() && file.getName().toLowerCase().endsWith(".png");
                                        }
                                    });

                                    if (directoryListing != null && directoryListing.length > 0) {
                                        imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                                    }
                                }
                            }
                        }
                        currentProgress = 1;
                        succeeded();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        updateMessage(t.getMessage());
                        failed();
                    }
                    return null;
                }

            };

            exportTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    directoryListing = exportDirectory.listFiles(new FileFilter() {
                        public boolean accept(File file) {
                            return file.isFile() && file.getName().toLowerCase().endsWith(".png");
                        }
                    });

                    currentMaxImportNumber = directoryListing.length;
                    maximportnumber.setText("/ " + currentMaxImportNumber);
                }
            });

            exportTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    String message = exportTask.getMessage();

                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Import Fehlgeschlagen");
                    alert.setHeaderText("Import Fehlgeschlagen:");
                    alert.setContentText(message);

                    alert.showAndWait();
                }
            });

            importprogressbar.progressProperty().bind(exportTask.progressProperty());

            exportThread = new Thread(exportTask);
            exportThread.setDaemon(true);
            exportThread.start();

        } catch (Throwable t) {
            t.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Import Fehlgeschlagen");
            alert.setHeaderText("Import Fehlgeschlagen:");
            alert.setContentText(t.getMessage());

            alert.showAndWait();

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

    //DateTimePicker avaible Dates
    private Callback<DatePicker, DateCell> getDayCellFactory() {

        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {

            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        LocalDate firstDate = importedData.get(0).getTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        LocalDate lastDate = importedData.get(importedData.size() - 1).getTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        if (item.isBefore(firstDate) || item.isAfter(lastDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        };
        return dayCellFactory;
    }

    private void emptyDirectoryForGraphs(File exportDirectory) {
        try {
            imageViewForFilter.setImage(null);
            FileUtils.cleanDirectory(exportDirectory);
        } catch (IOException ex) {
            Logger.getLogger(SliceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void incrementImageView() {
        try {

            if (exportDirectory != null) {

                directoryListing = exportDirectory.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isFile() && file.getName().toLowerCase().endsWith(".png");
                    }
                });

                if (directoryListing != null && directoryListing.length > 0 && directoryListing.length - 1 > directoryPosition) {
                    directoryPosition++;
                    imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                    currentMaxImportNumber = directoryListing.length;
                    maximportnumber.setText("/ " + currentMaxImportNumber);

                    previousbutton.setDisable(false);
                    if (directoryListing.length - 1 == directoryPosition) {
                        nextbutton.setDisable(true);
                    }

                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void decrementImageView() {
        try {
            if (exportDirectory != null && directoryPosition > 0) {
                directoryPosition--;

                directoryListing = exportDirectory.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isFile() && file.getName().toLowerCase().endsWith(".png");
                    }
                });

                if (directoryListing != null && directoryListing.length > 0) {
                    imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                }

                currentMaxImportNumber = directoryListing.length;
                maximportnumber.setText("/ " + currentMaxImportNumber);

                if (directoryListing != null) {
                    imageViewForFilter.setImage(new Image(new FileInputStream(directoryListing[directoryPosition])));
                }

                nextbutton.setDisable(false);
                if (0 >= directoryPosition) {
                    previousbutton.setDisable(true);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private List<Node> getItemsForFilterListView() {
        List<Node> result = new ArrayList<Node>();
        List<String> filterNames = filterManagementUtil.getAllNotCombineFilters();

        for (String filterName : filterNames) {
            HBox hBox = new HBox();
            ImageView imageView = new ImageView();
            imageView.setFitHeight(25);
            imageView.setFitWidth(25);

            if (filterName.toLowerCase().contains("time")) {
                File file = new File("src/opendiabetesvaultgui/shapes/time.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else if (filterName.toLowerCase().contains("date")) {
                File file = new File("src/opendiabetesvaultgui/shapes/calendar.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else if (filterName.toLowerCase().contains("type")) {
                File file = new File("src/opendiabetesvaultgui/shapes/value.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else if (filterName.toLowerCase().contains("thres")) {
                File file = new File("src/opendiabetesvaultgui/shapes/loading.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else {
                File file = new File("src/opendiabetesvaultgui/shapes/gear.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }

            hBox.getChildren().add(imageView);

            Label label = new Label();
            label.setText(filterName);
            hBox.getChildren().add(label);
            result.add(hBox);

        }

        return result;
    }

}
