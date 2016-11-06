package view;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by minas on 06/11/2016.
 */
public class LineChartDoubleDouble {

    private final LineChart<Double, Double> chart;

    private List<XYChart.Series<Double, Double>> series;

    public LineChartDoubleDouble(LineChart<Double, Double> chart) {

        this.chart = chart;
    }

    public void importData(List<String[]> data) {

        series = generateSeries(data);
        fillSeries(series);
    }

    private List<XYChart.Series<Double, Double>> generateSeries(List<String[]> data) {

        Map<Integer, XYChart.Series<Double, Double>> seriesById = new TreeMap<>();

        for(String[] line : data) {

            if (line.length == 0)
                continue;

            String first = line[0];

            for(int i = 1; i < line.length; ++i) {

                final int j = i;

                if (!line[j].equals("")) {
                    seriesById.compute(i, (k, v) -> {

                        v = v == null ? new XYChart.Series() : v;
                        v.getData().add(new XYChart.Data<>(Double.parseDouble(first), Double.parseDouble(line[j])));
                        return v;
                    });
                }
            }
        }

        List<XYChart.Series<Double, Double>> series = new ArrayList<>(seriesById.size());
        seriesById.forEach((i, s) -> series.add(s));

        return series;
    }

    private void fillSeries(List<XYChart.Series<Double, Double>> series) {

        series.forEach(chart.getData()::add);
    }

    public void createSeriesPropertyLabels(ListView<HBox> seriesProperties) {

        for (int i = 0; i < series.size(); ++i) {

            XYChart.Series<?, ?> serie = series.get(i);

            HBox hbox = new HBox();
            seriesProperties.getItems().add(hbox);

            TextField seriesName = new TextField();
            hbox.getChildren().add(seriesName);
            serie.nameProperty().bindBidirectional(seriesName.textProperty());
            seriesName.setText("Series " + i);
        }
    }
}
