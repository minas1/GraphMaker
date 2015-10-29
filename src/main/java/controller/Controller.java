package controller;

import au.com.bytecode.opencsv.CSVReader;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import model.SymbolType;
import utils.Colors;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

import static utils.Utils.*;

/*
 * CSS Styles for charts: http://docs.oracle.com/javafx/2/charts/css-styles.htm
 * TODO Set the background color to white transparent
 */

/**
 * Created by minas on 10/10/2015.
 */
public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for(SymbolType symbolType : SymbolType.values())
            _symbolType.getItems().add(symbolType);
        _symbolType.getSelectionModel().select(0);

        _symbolType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setSymbolType(_chart, newValue));

        _fontSize.setText(Integer.toString(DEFAULT_FONT_SIZE));
        _fontSize.setPromptText(Integer.toString(DEFAULT_FONT_SIZE));
        _fontSize.textProperty().addListener((observable, oldValue, newValue) -> setFontSize(_chart, newValue));
    }

    @FXML
    public void importData() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import data");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV file", "csv"));
        File file = fileChooser.showOpenDialog(getStage(_chartPane));

        if (file != null) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {

                List<String[]> entries = reader.readAll();

                List<String> xAxisValues = new ArrayList<>();
                List<String> yAxisValues = new ArrayList<>();

                for (String[] values : entries) {

                    if (values.length == 0)
                        continue;

                    xAxisValues.add(values[0]);
                    for (int i = 1; i < values.length; ++i)
                        if (!values[i].equals(""))
                            yAxisValues.add(values[i]);
                }

                _xAxisType = getBestCommonType(xAxisValues);
                _yAxisType = getBestCommonType(yAxisValues);
                _chart = createLineChart(_xAxisType, _yAxisType);
                setupChart();

                _chartPane.getChildren().clear();
                _seriesProperties.getItems().clear();

                _chartPane.getChildren().add(_chart);

                populateChart(entries);
            }
            catch (Throwable e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error while importing data");
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void setupChart() {

        // keep the size equal to the parent's
        _chart.minWidthProperty().bind(_chartPane.widthProperty());
        _chart.minHeightProperty().bind(_chartPane.heightProperty());

        _chart.titleProperty().bindBidirectional(_titleTextField.textProperty());
        _chart.getXAxis().labelProperty().bind(_xAxisLabel.textProperty());
        _chart.getYAxis().labelProperty().bind(_yAxisLabel.textProperty());

        _xAxisAutoBounds.setDisable(false);
        _yAxisAutoBounds.setDisable(false);

        _xAxisAutoBounds.selectedProperty().addListener((observable, oldValue, newValue) -> {

            NumberAxis axis = (NumberAxis) _chart.getXAxis();

            if (newValue)
                enableAutoRanging(axis);
            else {
                disableAutoRanging(axis, _xAxisLowerBound.getText(), _xAxisUpperBound.getText());
                setAxisTickUnit(axis, _xAxisTickUnit.getText());
            }
        });

        _yAxisAutoBounds.selectedProperty().addListener((observable, oldValue, newValue) -> {

            NumberAxis axis = (NumberAxis) _chart.getYAxis();

            if (newValue)
                enableAutoRanging(axis);
            else {
                disableAutoRanging(axis, _yAxisLowerBound.getText(), _yAxisUpperBound.getText());
                setAxisTickUnit(axis, _yAxisTickUnit.getText());
            }
        });

        _xAxisLowerBound.disableProperty().bindBidirectional(_xAxisAutoBounds.selectedProperty());
        _xAxisUpperBound.disableProperty().bindBidirectional(_xAxisAutoBounds.selectedProperty());
        _yAxisLowerBound.disableProperty().bindBidirectional(_yAxisAutoBounds.selectedProperty());
        _yAxisUpperBound.disableProperty().bindBidirectional(_yAxisAutoBounds.selectedProperty());
        _xAxisTickUnit.disableProperty().bind(_xAxisAutoBounds.selectedProperty());
        _yAxisTickUnit.disableProperty().bind(_yAxisAutoBounds.selectedProperty());

        _xAxisLowerBoundLabel.disableProperty().bind(_xAxisAutoBounds.selectedProperty());
        _xAxisUpperBoundLabel.disableProperty().bind(_xAxisAutoBounds.selectedProperty());
        _yAxisLowerBoundLabel.disableProperty().bind(_yAxisAutoBounds.selectedProperty());
        _yAxisUpperBoundLabel.disableProperty().bind(_yAxisAutoBounds.selectedProperty());
        _xAxisTickUnitLabel.disableProperty().bind(_xAxisAutoBounds.selectedProperty());
        _yAxisTickUnitLabel.disableProperty().bind(_yAxisAutoBounds.selectedProperty());

        _chart.legendVisibleProperty().bindBidirectional(_showLegend.selectedProperty());

        BiConsumer<NumberAxis, TextField> bindLowerBound = (axis, textField) -> {

            textField.textProperty().addListener((observable, oldValue, newValue) -> setAxisLowerBound(axis, newValue));
        };

        BiConsumer<NumberAxis, TextField> bindUpperBound = (axis, textField) -> {

            textField.textProperty().addListener((observable, oldValue, newValue) -> setAxisUpperBound(axis, newValue));
        };

        BiConsumer<NumberAxis, TextField> setStep = (axis, textField) -> {


            textField.textProperty().addListener((observable, oldValue, newValue) -> setAxisTickUnit(axis, newValue));
        };

        if (_xAxisType.equals(Number.class)) {

            final NumberAxis axis = (NumberAxis) _chart.getXAxis();
            bindLowerBound.accept(axis, _xAxisLowerBound);
            bindUpperBound.accept(axis, _xAxisUpperBound);
            setStep.accept(axis, _xAxisTickUnit);
        }
        // else { TODO add the other case as well (categoryAxis)

        if (_yAxisType.equals(Number.class)) {

            final NumberAxis axis = (NumberAxis) _chart.getYAxis();
            bindLowerBound.accept(axis, _yAxisLowerBound);
            bindUpperBound.accept(axis, _yAxisUpperBound);
            setStep.accept(axis, _yAxisTickUnit);
        }

    }

    private static void setSymbolType(LineChart chart, SymbolType type) {

        if (chart == null)
            return;

        for (int i = 0; i < chart.getData().size(); ++i) {

            XYChart.Series series = (XYChart.Series) chart.getData().get(i);

            for (Object o2 : series.getData()) {

                Node node = ((XYChart.Data) o2).getNode();

                switch (type) {

                    case FILLED_CIRCLE:
                        node.setStyle(String.format("-fx-background-color: %s, %s;", Colors.get(i), Colors.get(i)));
                        break;

                    case EMPTY_CIRCLE:
                        node.setStyle(String.format("-fx-background-color: %s, white;", Colors.get(i)));
                        break;

                    case NO_SYMBOL:
                        node.setStyle(String.format("-fx-background-color: null, null;"));
                        break;

                    default:
                        throw new RuntimeException("Unhandled case " + type.toString());
                }

            }
        }
    }

    private static void setFontSize(LineChart chart, String text) {

        if (chart == null)
            return;

        double fontSize;

        try {
            fontSize = Double.parseDouble(text);
        }
        catch (NumberFormatException ex) {
            fontSize = DEFAULT_FONT_SIZE;
        }

        chart.getXAxis().setStyle("-fx-font-size: "+ fontSize + " pt;");
        chart.getXAxis().setTickLabelFont(Font.font(fontSize));

        chart.getYAxis().setStyle("-fx-font-size: "+ fontSize + " pt;");
        chart.getYAxis().setTickLabelFont(Font.font(fontSize));
    }

    private static void setAxisLowerBound(NumberAxis axis, String str) {

        try {
            double val = Double.parseDouble(str);
            axis.setLowerBound(val);
        }
        catch (NumberFormatException ex) {
            axis.setLowerBound(0.0);
        }
    }

    private static void setAxisUpperBound(NumberAxis axis, String str) {

        try {
            double val = Double.parseDouble(str);
            axis.setUpperBound(val);
        }
        catch (NumberFormatException ex) {
            axis.setUpperBound(0.0);
        }
    }

    private static void setAxisTickUnit(NumberAxis axis, String str) {

        try {
            double val = Double.parseDouble(str);
            axis.setTickUnit(val);
        }
        catch (NumberFormatException ex) {
            axis.setTickUnit(0.0);
        }
    }

    private static void enableAutoRanging(NumberAxis axis) {

        axis.setAutoRanging(true);
    }

    private static void disableAutoRanging(NumberAxis axis, String min, String max) {

        // must be set to false to change to lower/upper bounds otherwise they won't be updated
        axis.setAutoRanging(false);

        setAxisLowerBound(axis, min);
        setAxisUpperBound(axis, max);
    }

    @SuppressWarnings("unchecked")
    private void populateChart(List<String[]> data) {

        Map<Integer, XYChart.Series> seriesById = new TreeMap<>();

        for(String[] line : data) {

            if (line.length == 0)
                continue;

            String first = line[0];

            for(int i = 1; i < line.length; ++i) {

                final int j = i;

                if (!line[j].equals("")) {
                    seriesById.compute(i, (k, v) -> {

                        v = v == null ? new XYChart.Series() : v;
                        v.getData().add(new XYChart.Data(parse(first, _xAxisType), parse(line[j], _yAxisType)));
                        return v;
                    });
                }
            }
        }

        seriesById.forEach((i, series) -> {
            _chart.getData().add(series);

            HBox hbox = new HBox();
            _seriesProperties.getItems().add(hbox);

            TextField seriesName = new TextField();
            hbox.getChildren().add(seriesName);
            series.nameProperty().bindBidirectional(seriesName.textProperty());
            seriesName.setText("Series " + i);

            //v.getNode().setStyle(String.format("-fx-stroke: %s;", color.toString())); // set line color
            //v.getNode().setStyle("-fx-stroke-width: 1px;");*/
        });

        final NumberAxis xAxis = (NumberAxis) _chart.getXAxis();
        if (_xAxisAutoBounds.isSelected())
            enableAutoRanging(xAxis);
        else
            disableAutoRanging(xAxis, _xAxisLowerBound.getText(), _xAxisUpperBound.getText());

        final NumberAxis yAxis = (NumberAxis) _chart.getYAxis();
        if (_yAxisAutoBounds.isSelected())
            enableAutoRanging(yAxis);
        else
            disableAutoRanging(yAxis, _yAxisLowerBound.getText(), _yAxisUpperBound.getText());

        _exportButton.setDisable(false);
        setSymbolType(_chart, _symbolType.getValue());
        setFontSize(_chart, _fontSize.getText());

        _chart.setLegendVisible(_showLegend.isSelected());
    }

    @FXML
    public void about() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Graph Maker");
        alert.setHeaderText("Yet another tool for making graphs.");
        alert.setContentText(
                "This is free software (MIT license).\n" +
                        "Programmed by: Minas Mina\n\n" +
                        "For comments and/or suggestions, send\n" +
                        "me an email at minasm1990 [at] gmail.com");
        alert.showAndWait();
    }

    @FXML
    public void export() {

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG image", "*.png"));
            fileChooser.setInitialFileName(_titleTextField.getText());

            File file = fileChooser.showSaveDialog(getStage(_chartPane));

            if (file != null) {

                if (!file.getName().endsWith(".png") && !file.getName().endsWith(".PNG"))
                    file = new File(file.getAbsolutePath() + ".png");

                _chart.setScaleX(2.0);
                _chart.setScaleY(2.0);
                WritableImage snapshot = _chart.snapshot(new SnapshotParameters(), null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                } finally {
                    _chart.setScaleX(1.0);
                    _chart.setScaleY(1.0);
                }
            }
        }
        catch (IOException e) {

            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not export image");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    private LineChart _chart;
    private Class<?> _xAxisType;
    private Class<?> _yAxisType;

    @FXML private Pane _chartPane;
    @FXML private TextField _titleTextField;
    @FXML private ListView<HBox> _seriesProperties;

    @FXML private TextField _xAxisLabel;
    @FXML private TextField _yAxisLabel;

    @FXML private Label _xAxisLowerBoundLabel;
    @FXML private Label _xAxisUpperBoundLabel;
    @FXML private Label _yAxisLowerBoundLabel;
    @FXML private Label _yAxisUpperBoundLabel;

    @FXML private TextField _xAxisLowerBound;
    @FXML private TextField _xAxisUpperBound;
    @FXML private TextField _yAxisLowerBound;
    @FXML private TextField _yAxisUpperBound;

    @FXML private CheckBox _xAxisAutoBounds;
    @FXML private CheckBox _yAxisAutoBounds;

    @FXML private ComboBox<SymbolType> _symbolType;

    private static final int DEFAULT_FONT_SIZE = 18;
    @FXML private TextField _fontSize;


    @FXML private TextField _xAxisTickUnit;
    @FXML private Label _xAxisTickUnitLabel;
    @FXML private TextField _yAxisTickUnit;
    @FXML private Label _yAxisTickUnitLabel;

    @FXML private Button _exportButton;
    @FXML private CheckBox _showLegend;
}
