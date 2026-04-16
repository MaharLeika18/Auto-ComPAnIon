package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import raven.chart.line.LineChart;
import java.awt.Font;
import java.text.DecimalFormat;

public class CustomLineChart extends LineChart {
    public CustomLineChart() {
        super();
        this.valuesFormat = new DecimalFormat("K#,##0.##");

        // Ensure the font is set initially
        setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
    }

    @Override
    public void setFont(Font font) {
        // Force FlatRoboto and ignore any external font changes
        super.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
    }
}

