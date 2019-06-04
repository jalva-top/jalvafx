package top.jalva.jalvafx.node;

import java.math.BigDecimal;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import top.jalva.jalvafx.style.CssStyle;
import top.jalva.jalvafx.util.StringUtils;

public class ChartUtils {

	public static HBox createChartLegend(String color, String seriesName) {
		HBox legend = Controls.createHBox(10.0, Pos.CENTER_LEFT);
		Rectangle marker = new Rectangle(16, 16);
		marker.setStyle(CssStyle.getFill(color));

		Label label = new Label(seriesName);

		legend.getChildren().addAll(marker, label);

		return legend;
	}

	public static <X, Y extends BigDecimal> void customize(Data<X, Y> data, String color, String xValue) {
		data.nodeProperty().addListener((ov, oldNode, node) -> {
			if (node != null) {
				if (!StringUtils.isBlank(color)) {
					String style = "-fx-bar-fill: " + color;
					node.setStyle(style);
				}
				showDetails(xValue, data.getYValue(), node);
			}
		});
	}

	private static void showDetails(String xValue, BigDecimal yValue, Node node) {
		node.setOnMouseClicked(e -> {
			HBox parent = Controls.createCaptionWithValueNode(xValue, yValue);
			PopOvers.showPopOverWithContent(node, parent);
		});
	}

	// **Use when lineChart.getData()!=null && !lineChart.getData().isEmpty()*/
	public static void createLineChartValueTooltips(LineChart<String, BigDecimal> lineChart) {
		for (Series<String, BigDecimal> series : lineChart.getData()) {
			for (Data<String, BigDecimal> data : series.getData()) {
				showDetails(series.getName() + " " + data.getXValue() + ":", data.getYValue(), data.getNode());
			}
		}
	}
}
