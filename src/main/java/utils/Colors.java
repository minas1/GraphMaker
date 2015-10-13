package utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by minas on 12/10/2015.
 */
public final class Colors {

    public static String get(int index) {

        return _colors.get(index);
    }

    private static final List<String> _colors;

    static {

        _colors = Arrays.asList(
                "#f3622d",
                "#fba71b",
                "#57b757",
                "#41a9c9",
                "#4258c9",
                "#9a42c8",
                "#c84164"
        );
    }

    private Colors() {}
}
