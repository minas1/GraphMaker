package utils;

import javafx.scene.Parent;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by minas on 10/10/2015.
 */
public final class Utils {

    public static Stage getStage(final Parent p) {

        return (Stage)p.getScene().getWindow();
    }

    /**
     * Can return Number.class or String.class
     *
     * @param values
     * @return
     */
    public static Class<?> getBestCommonType(List<String> values) {

        Class<?> currentType = Integer.class;

        for(String str : values) {

            if (tryParseNumber(str))
                currentType = Number.class;
            else
                return String.class;
        }

        return currentType;
    }

    public static boolean tryParseNumber(String str) {

        try {
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }

    public static void writeln(Object... args) {

        for(Object arg : args)
            System.out.print(arg);
        System.out.println();
    }

    public static LineChart createLineChart(Class<?> xAxisType, Class<?> yAxisType) {

        Axis xAxis = getAxisBasedOnType(xAxisType);
        Axis yAxis = getAxisBasedOnType(yAxisType);

        return new LineChart<>(xAxis, yAxis);
    }

    private static Axis getAxisBasedOnType(Class<?> type) {

        if (type.equals(Number.class))
            return new NumberAxis();
        else
            return new CategoryAxis();
    }

    public static Object parse(String str, Class<?> clazz) {

        if (clazz.equals(Number.class))
            return Double.parseDouble(str);
        else
            return str;
    }

    private Utils() {}
}
