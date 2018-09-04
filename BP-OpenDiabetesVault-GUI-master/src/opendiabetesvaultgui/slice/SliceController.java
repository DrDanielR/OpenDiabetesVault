/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import com.google.gson.JsonObject;
import de.opendiabetes.vault.processing.filter.options.guibackend.FilterManagementUtil;
import de.opendiabetes.vault.processing.filter.options.guibackend.FilterNode;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.container.csv.VaultCSVEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.exporter.Exporter;
import de.opendiabetes.vault.plugin.exporter.VaultExporter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import de.opendiabetes.vault.processing.filter.DateTimeSpanFilter;
import de.opendiabetes.vault.processing.filter.Filter;
import de.opendiabetes.vault.processing.filter.FilterResult;
import de.opendiabetes.vault.processing.filter.VaultEntryTypeFilter;
import de.opendiabetes.vault.processing.filter.options.DateTimeSpanFilterOption;
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
import java.util.Calendar;
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
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    @FXML
    private HBox filteroptiontypehbox;

    @FXML
    private Spinner hourspinnerbefore;

    @FXML
    private Spinner minutespinnerbefore;

    @FXML
    private Spinner secondspinnerbefore;

    @FXML
    private Spinner hourspinnerafter;

    @FXML
    private Spinner minutespinnerafter;

    @FXML
    private Spinner secondspinnerafter;

    @FXML
    private CheckBox checkboxforsplit;

    private ChoiceBox filtertypechoiceboxforsplit = new ChoiceBox();

    private static final String FILTER_NAME = "FilterName";
    private static final String SEPARATOR = "%";
    private static final String COMBINE_FILTER = "CombineFilter";
    private static final String END_OF_SUBFILTER = "EndOfSubFilter";
    private static final String NO_SELECTION = "-----";

    private List<VBoxChoiceBoxFilterNodesContainer> vboxChoiceBoxFilterNodesContainers = new ArrayList<>();

    private ObservableList itemsForChocieBox;

    private double mousePositionX;

    private double mousePositionY;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd.MM.yy");

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

            for (VBoxChoiceBoxFilterNodesContainer vboxChoiceBoxFilterNodesContainer : vboxChoiceBoxFilterNodesContainers) {
                bufferedWriter.write(COMBINE_FILTER + SEPARATOR + vboxChoiceBoxFilterNodesContainer.getChoicebox().getSelectionModel().getSelectedItem().toString());
                bufferedWriter.newLine();

                for (FilterNode filterNode : vboxChoiceBoxFilterNodesContainer.getFilterNodes()) {
                    bufferedWriter.write(FILTER_NAME + SEPARATOR + filterNode.getName());
                    bufferedWriter.newLine();

                    Iterator iterator = filterNode.getParameterAndValues().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();
                        bufferedWriter.write(pair.getKey() + SEPARATOR + pair.getValue());
                        bufferedWriter.newLine();
                    }

                    //WeitereFilterNodes
                    iterator = filterNode.getParameterAndFilterNodes().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();
                        bufferedWriter.write(pair.getKey() + SEPARATOR);
                        bufferedWriter.newLine();

                        writeFilterNode(bufferedWriter, (List<FilterNode>) pair.getValue());

                        bufferedWriter.write(END_OF_SUBFILTER + SEPARATOR);
                        bufferedWriter.newLine();

                    }

                }

            }

            bufferedWriter.close();
            fileOutputStream.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private void writeFilterNode(BufferedWriter bufferedWriter, List<FilterNode> filterNodes) throws IOException {
        for (FilterNode filterNode : filterNodes) {
            bufferedWriter.write(FILTER_NAME + SEPARATOR + filterNode.getName());
            bufferedWriter.newLine();

            Iterator iterator = filterNode.getParameterAndValues().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                bufferedWriter.write(pair.getKey() + SEPARATOR + pair.getValue());
                bufferedWriter.newLine();
            }

            //WeitereFilterNodes
            iterator = filterNode.getParameterAndFilterNodes().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                bufferedWriter.write(pair.getKey() + SEPARATOR);
                bufferedWriter.newLine();

                writeFilterNode(bufferedWriter, (List<FilterNode>) pair.getValue());

                bufferedWriter.write(END_OF_SUBFILTER + SEPARATOR);
                bufferedWriter.newLine();

            }
        }

    }

    BufferedReader bufferedReader;

    @FXML
    private void doLoadFilterCombination(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(MAIN_STAGE);

        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            List<String> combineFiltersForImport = new ArrayList<>();
            List<List<FilterNode>> filterNodesForImport = new ArrayList<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineSplit = line.split(SEPARATOR);

                if (lineSplit[0].equals(COMBINE_FILTER)) {
                    combineFiltersForImport.add(lineSplit[1]);
                    filterNodesForImport.add(new ArrayList<>());
                } else if (lineSplit[0].equals(FILTER_NAME)) {
                    filterNodesForImport.get(filterNodesForImport.size() - 1).add(new FilterNode(lineSplit[1]));
                } else if (lineSplit.length == 1) {
                    FilterNode tempFilterNode = importSubFilter();
                    filterNodesForImport.get(filterNodesForImport.size() - 1).get(filterNodesForImport.get(filterNodesForImport.size() - 1).size() - 1).addParameterAndFilterNodes(lineSplit[0], tempFilterNode);
                } else {
                    filterNodesForImport.get(filterNodesForImport.size() - 1).get(filterNodesForImport.get(filterNodesForImport.size() - 1).size() - 1).addParam(lineSplit[0], lineSplit[1]);
                }

            }
            bufferedReader.close();

            List<Filter> filters = getFiltersFromLists(combineFiltersForImport, filterNodesForImport);
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

    private FilterNode importSubFilter() throws IOException {
        FilterNode result = new FilterNode("");

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] lineSplit = line.split(SEPARATOR);

            if (lineSplit[0].equals(FILTER_NAME)) {
                result.setName(lineSplit[1]);
            } else if (lineSplit.length == 1) {
                if (lineSplit[0].equals(END_OF_SUBFILTER)) {
                    break;
                } else {
                    FilterNode tempFilterNode = importSubFilter();
                    result.addParameterAndFilterNodes(lineSplit[0], tempFilterNode);
                }
            } else {
                result.addParam(lineSplit[0], lineSplit[1]);
            }

        }

        return result;
    }

    List<FilterResult> filterResultsForSplit;
    int filterResultPositionForSplit = -1;

    @FXML
    private void doSplitEntries(ActionEvent event) {
        Date beginDate = importedData.get(0).getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);

        filterResultsForSplit = new ArrayList<>();

        int hoursBefore = Integer.parseInt(hourspinnerbefore.getValue().toString());
        int minutesBefore = Integer.parseInt(minutespinnerbefore.getValue().toString());
        int secondsfore = Integer.parseInt(secondspinnerbefore.getValue().toString());
        int hoursAfter = Integer.parseInt(hourspinnerafter.getValue().toString());
        int minutesAfter = Integer.parseInt(minutespinnerafter.getValue().toString());
        int secondsAfter = Integer.parseInt(secondspinnerafter.getValue().toString());

        Date lastVaultEntryTimestamp = importedData.get(importedData.size() - 1).getTimestamp();;

        if (filterResultsForSplit.size() > 0) {
            lastVaultEntryTimestamp = filterResultsForSplit.get(filterResultsForSplit.size() - 1).filteredData.get(filterResultsForSplit.get(filterResultsForSplit.size() - 1).filteredData.size() - 1).getTimestamp();
        }

        if (filtertypechoiceboxforsplit.getSelectionModel().getSelectedItem().toString().equals(NO_SELECTION)) {

            while (lastVaultEntryTimestamp.after(calendar.getTime())) {
                Date startDate = calendar.getTime();
                calendar.add(Calendar.HOUR_OF_DAY, hoursAfter + hoursBefore);
                calendar.add(Calendar.MINUTE, minutesAfter + minutesBefore);
                calendar.add(Calendar.SECOND, secondsAfter + secondsfore);
                Date endDate = calendar.getTime();

                DateTimeSpanFilter dateTimeSpanFilter = new DateTimeSpanFilter(new DateTimeSpanFilterOption(startDate, endDate));

                filterResultsForSplit.add(dateTimeSpanFilter.filter(importedData));
            }

        } else {
            VaultEntryType vaultEntryType = VaultEntryType.valueOf(filtertypechoiceboxforsplit.getSelectionModel().getSelectedItem().toString());

            VaultEntryTypeFilter vaultEntryTypeFilter = new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(vaultEntryType));

            FilterResult tempFilterResult = vaultEntryTypeFilter.filter(importedData);

            for (VaultEntry vaultEntry : tempFilterResult.filteredData) {
                calendar.setTime(vaultEntry.getTimestamp());
                calendar.add(Calendar.HOUR_OF_DAY, -hoursBefore);
                calendar.add(Calendar.MINUTE, -minutesBefore);
                calendar.add(Calendar.SECOND, -secondsfore);
                Date startDate = calendar.getTime();

                calendar.setTime(vaultEntry.getTimestamp());
                calendar.add(Calendar.HOUR_OF_DAY, hoursAfter);
                calendar.add(Calendar.MINUTE, minutesAfter);
                calendar.add(Calendar.SECOND, secondsAfter);
                Date endDate = calendar.getTime();

                DateTimeSpanFilter dateTimeSpanFilter = new DateTimeSpanFilter(new DateTimeSpanFilterOption(startDate, endDate));

                filterResultsForSplit.add(dateTimeSpanFilter.filter(importedData));

            }

        }

        if (filterResultsForSplit != null && filterResultsForSplit.size() > 0) {
            filterResultPositionForSplit = 0;
            populateChart(filterResultsForSplit.get(filterResultPositionForSplit));
        } else {
            String message = "Das Splitten ist mit den gegebenen Parametern nicht möglich";

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Splitten Fehlgeschlagen");
            alert.setHeaderText("Splitten Fehlgeschlagen:");
            alert.setContentText(message);

            alert.showAndWait();
        }

    }

    @FXML
    private void getNextFilterSplit(ActionEvent event) {
        if (filterResultPositionForSplit >= 0 && filterResultPositionForSplit < filterResultsForSplit.size()) {
            filterResultPositionForSplit++;
            populateChart(filterResultsForSplit.get(filterResultPositionForSplit));
        }
    }

    @FXML
    private void getPreviousFilterSplit(ActionEvent event) {
        if (filterResultPositionForSplit > 0) {
            filterResultPositionForSplit--;
            populateChart(filterResultsForSplit.get(filterResultPositionForSplit));
        }
    }

    @FXML
    private void doFilter(ActionEvent event) {

        boolean splitFilter = checkboxforsplit.isSelected();

        List<Filter> filters = getFiltersFromCurrentState();
        if (filters != null && !splitFilter) {
            //null entfernen normalerweise keine drinnen
            while (filters.remove(null));

            FilterResult filterResult = filterManagementUtil.sliceVaultEntries(filters, importedData);

            if (filterResult != null) {

                importedData = filterResult.filteredData;

                //For JavaFx
                populateChart(filterResult);

                //Plotteria
                generateGraphs(filterResult);
            }
        } else if (splitFilter && filterResultsForSplit != null && filterResultsForSplit.size() > 0) {

            List<FilterResult> tempFilterResults = new ArrayList<>();

            for (FilterResult filterResult : filterResultsForSplit) {

                FilterResult tempFilterResult = filterManagementUtil.sliceVaultEntries(filters, filterResult.filteredData);

                if (tempFilterResult != null && tempFilterResult.filteredData.size() > 0) {
                    tempFilterResults.add(tempFilterResult);
                }

            }

            filterResultsForSplit.clear();
            filterResultsForSplit.addAll(tempFilterResults);
            filterResultPositionForSplit = 0;
            populateChart(filterResultsForSplit.get(filterResultPositionForSplit));

        }
    }

    @FXML
    private void doReset(ActionEvent event) {

        filterCombinationHbox.getChildren().removeAll(filterCombinationHbox.getChildren());
        vboxChoiceBoxFilterNodesContainers.clear();
        addNewChoiceBoxAndSeperator();

        importedData = vaultDao.queryAllVaultEntries();
        FilterResult filterResult = filterManagementUtil.getLastDay(importedData);
        populateChart(filterResult);
        generateGraphs(filterResult);
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
        List<List<FilterNode>> columnFilterNodes = new ArrayList<>();

        for (VBoxChoiceBoxFilterNodesContainer vboxChoiceBoxFilterNodesContainer : vboxChoiceBoxFilterNodesContainers) {
            columnFilterNodes.add(vboxChoiceBoxFilterNodesContainer.getFilterNodes());
        }

        List<Filter> filters = filterManagementUtil.combineFilters(getCurrentCombineFilters(), columnFilterNodes);
        return filters;
    }

    private List<String> getCurrentCombineFilters() {
        List<String> combineFilters = new ArrayList<>();
        for (VBoxChoiceBoxFilterNodesContainer vBoxChoiceBoxFilterNodesContainer : vboxChoiceBoxFilterNodesContainers) {
            combineFilters.add(vBoxChoiceBoxFilterNodesContainer.getChoicebox().getSelectionModel().getSelectedItem().toString());
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

        //Choiceboxen füllen
        itemsForChocieBox = FXCollections.observableArrayList(filterManagementUtil.getCombineFilter());

        addNewChoiceBoxAndSeperator();

        ObservableList itemsForTmpChocieBox = FXCollections.observableArrayList(new VaultEntryTypeFilterOption(null).getDropDownEntries().keySet());
        itemsForTmpChocieBox.add(NO_SELECTION);
        filtertypechoiceboxforsplit.setItems(itemsForTmpChocieBox);
        filtertypechoiceboxforsplit.getSelectionModel().selectLast();

        filteroptiontypehbox.getChildren().add(filtertypechoiceboxforsplit);

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

    }

    public void onDragOverFilter(DragEvent event) {
        if (event.getGestureSource() != filtercombinationfield
                && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.ANY);
            mousePositionX = event.getX();
            mousePositionY = event.getY();

            //filterbutton.setText("X: " + mousePositionX + " Y: " + mousePositionY);
        }

        event.consume();
    }

    public void onDragDroppedFilter(DragEvent event, Node node, List<FilterNode> filterNodes) {

        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            success = onDragDropFilter(db.getString(), node, filterNodes);
        }

        event.setDropCompleted(success);

        event.consume();
    }

    private boolean onDragDropFilter(String name, Node node, List<FilterNode> filterNodes) {

        VBox tmpInputVBox = new VBox();
        tmpInputVBox.setSpacing(10);

        FilterNode tmpNode;
        Map<String, Class> parameterClasses = filterManagementUtil.getParametersFromName(name);
        Iterator iterator = iterator = parameterClasses.entrySet().iterator();

        if (importedFilterNodes.get(name) != null) {
            tmpNode = importedFilterNodes.get(name);
        } else {
            tmpNode = new FilterNode(name);
        }

        //Input für Values                    
        Label label = new Label();
        label.setText(name);

        tmpInputVBox.setStyle("-fx-border-color:grey; -fx-background-radius: 10; -fx-border-radius: 10; -fx-box-shadow: 2 3 #888888;");

        tmpInputVBox.getChildren().add(label);

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            final String simpleName = (String) pair.getKey();
            final Class typeClass = (Class) pair.getValue();

            HBox tmpHBox = new HBox();
            tmpHBox.setAlignment(Pos.CENTER);
            //tmpHBox.setMaxWidth(200);

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
            } else if (typeClass.getSimpleName().toLowerCase().contains("data")) {
                tmpNode.setData(importedData);
                tmpHBox.getChildren().remove(tmpHBox.getChildren().get(tmpHBox.getChildren().size() - 1));
            } else if (typeClass.getSimpleName().toLowerCase().contains("filter")) {
                VBox filterVBox = new VBox();
                filterVBox.setStyle("-fx-border-color:grey; -fx-background-radius: 10; -fx-border-radius: 10; -fx-box-shadow: 2 3 #888888;");
                filterVBox.setMinHeight(50);
                filterVBox.setMinWidth(50);

                tmpNode.setParameterAndFilterNodes(simpleName, new ArrayList<FilterNode>());

                //DragOver for vBox
                filterVBox.setOnDragOver(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        onDragOverFilter(event);
                    }
                });
                //Drag loslassen
                filterVBox.setOnDragDropped(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        onDragDroppedFilter(event, filterVBox, tmpNode.getParameterAndFilterNodesFromName(simpleName));

                    }
                });

                tmpHBox.getChildren().add(filterVBox);

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
                } else if (typeClass.getSimpleName().equals("double") || typeClass.getSimpleName().equals("float")) {
                    tmpTextField.setPromptText("0.0");
                    TextFormatter textFormatter = new TextFormatter(new NumberStringConverter());
                    tmpTextField.setTextFormatter(textFormatter);
                    tmpNode.addParam(simpleName, "0.0");
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

        filterNodes.add(tmpNode);

        //LoeschButton
        Button deleteButton = new Button();
        deleteButton.setText("Entfernen");
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (node instanceof VBox) {
                    ((VBox) node).getChildren().remove(tmpInputVBox);
                }

                if (node instanceof Pane) {
                    ((Pane) node).getChildren().remove(tmpInputVBox);
                }

                filterNodes.clear();

            }
        });

        tmpInputVBox.getChildren().add(deleteButton);

        if (node instanceof VBox) {
            ((VBox) node).getChildren().add(tmpInputVBox);
        }

        if (node instanceof Pane) {
            ((Pane) node).getChildren().add(tmpInputVBox);
        }

        return true;
    }

    private void addNewChoiceBoxAndSeperator() {

        //Separator hinzufügen
        Separator separator = new Separator(Orientation.HORIZONTAL);
        filterCombinationHbox.getChildren().add(separator);

        //Vbox für Filter und ChoiceBox
        VBox vBox = new VBox();
        vBox.setSpacing(5);

        ChoiceBox choiceBox = new ChoiceBox(itemsForChocieBox);
        choiceBox.getSelectionModel().selectFirst();

        vBox.getChildren().add(choiceBox);

        filterCombinationHbox.getChildren().add(vBox);

        VBoxChoiceBoxFilterNodesContainer vboxChoiceBoxFilterNodesContainer = new VBoxChoiceBoxFilterNodesContainer(vBox, choiceBox, new ArrayList<>());

        vboxChoiceBoxFilterNodesContainers.add(vboxChoiceBoxFilterNodesContainer);

        //DragOver for vBox
        vBox.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                onDragOverFilter(event);
            }
        });
        //Drag loslassen
        vBox.setOnDragDropped(new EventHandler<DragEvent>() {
            boolean newvBox = true;

            public void handle(DragEvent event) {
                if (newvBox) {
                    addNewChoiceBoxAndSeperator();
                    newvBox = false;
                }
                onDragDroppedFilter(event, vboxChoiceBoxFilterNodesContainer.getVBox(), vboxChoiceBoxFilterNodesContainer.getFilterNodes());

            }
        });

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
        //List<String> filterNames = filterManagementUtil.getAllNotCombineFilters();
        List<String> filterNames = filterManagementUtil.getAllFilters();

        for (String filterName : filterNames) {
            HBox hBox = new HBox();
            ImageView imageView = new ImageView();
            imageView.setFitHeight(15);
            imageView.setFitWidth(15);

            if (filterName.toLowerCase().contains("date")) {
                File file = new File("src/opendiabetesvaultgui/shapes/calendar.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else if (filterName.toLowerCase().contains("time")) {
                File file = new File("src/opendiabetesvaultgui/shapes/time.png");
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
