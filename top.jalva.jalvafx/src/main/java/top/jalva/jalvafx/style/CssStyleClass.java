package top.jalva.jalvafx.style;

import javafx.scene.Node;

public class CssStyleClass {

	private CssStyleClass() {
	}

	public static void add(Node styleOwner, String cssStyleClass) {
		if (!styleOwner.getStyleClass().contains(cssStyleClass)) {
			styleOwner.getStyleClass().add(cssStyleClass);
		}
	}

	public static void remove(Node styleOwner, String cssStyleClass) {
		if (styleOwner.getStyleClass().contains(cssStyleClass)) {
			styleOwner.getStyleClass().remove(cssStyleClass);
		}
	}

	public static final String INFO = "info";
	public static final String WARNING = "warning";
	public static final String DANGER = "danger";
	public static final String PRIMARY = "primary";
	public static final String SUCCESS = "success";

	public static final String BUTTON_SMALL = "button-small";
	public static final String BUTTON_SMALL_ROUND = "button-small-round";
	public static final String BUTTON_SHY = "button-shy";

	public static final String FIRST = "first";
	public static final String LAST = "last";
	public static final String MIDDLE = "middle";
	public static final String ERROR = "error";

}
