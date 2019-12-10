package top.jalva.jalvafx.style;

import javafx.scene.control.Control;

public class CssStyle {

	private static final String EXTRA_TINY_SIZE = "70%";
	private static final String TINY_SIZE = "80%";
	private static final String SMALL_SIZE = "90%";
	private static final String NORMAL_SIZE = "100%";
	private static final String OVER_NORMAL_SIZE = "120%";
	private static final String BIG_SIZE = "150%";
	private static final String HUGE_SIZE = "200%";

	public static final String TEXT_BOLD = "-fx-font-weight:bold;";
	public static final String TEXT_NOT_BOLD = "-fx-font-weight:normal;";
	public static final String TEXT_ITALIC = "-fx-font-style:italic;";
	public static final String TEXT_NOT_ITALIC = "-fx-font-style:normal;";

	public static final String FONT_SIZE_12px = getFontSize("12");
	public static final String FONT_SIZE_EXTRA_TINY = getFontSize(EXTRA_TINY_SIZE);
	public static final String FONT_SIZE_TINY = getFontSize(TINY_SIZE);
	public static final String FONT_SIZE_SMALL = getFontSize(SMALL_SIZE);
	/** 100% */
	public static final String FONT_SIZE_NORMAL = getFontSize(NORMAL_SIZE);
	/** 120% */
	public static final String FONT_SIZE_OVER_NORMAL = getFontSize(OVER_NORMAL_SIZE);
	/** 150% */
	public static final String FONT_SIZE_BIG = getFontSize(BIG_SIZE);
	/** 200% */
	public static final String FONT_SIZE_HUGE = getFontSize(HUGE_SIZE);

	public static final String TEXT_FILL_BLACK = getTextFill("black");
	public static final String TEXT_FILL_WHITE = getTextFill("white");
	public static final String TEXT_FILL_DANGER = getTextFill(Color.DANGER);
	public static final String TEXT_FILL_WARNING = getTextFill(Color.WARNING);
	public static final String TEXT_FILL_SUCCESS = getTextFill(Color.SUCCESS);
	public static final String TEXT_FILL_SUCCESS_LIGHT = getTextFill(Color.SUCCESS_LIGHT);
	public static final String TEXT_FILL_PRIMARY = getTextFill(Color.PRIMARY);
	public static final String TEXT_FILL_INFO = getTextFill(Color.INFO);
	public static final String TEXT_FILL_SHY = getTextFill(Color.SHY);
	public static final String TEXT_FILL_SHY_LIGHT = getTextFill(Color.SHY_LIGHT);
	public static final String TEXT_FILL_POSTED = getTextFill(Color.POSTED);
	public static final String TEXT_FILL_RESERVED = getTextFill(Color.RESERVED);

	public static final String FILL_BLACK = getFill("black");
	public static final String FILL_DANGER = getFill(Color.DANGER);
	public static final String FILL_WARNING = getFill(Color.WARNING);
	public static final String FILL_SUCCESS = getFill(Color.SUCCESS);
	public static final String FILL_PRIMARY = getFill(Color.PRIMARY);
	public static final String FILL_INFO = getFill(Color.INFO);
	public static final String FILL_SHY_LIGHT = getFill(Color.SHY_LIGHT);
	public static final String FILL_POSTED = getFill(Color.POSTED);
	public static final String FILL_RESERVED = getFill(Color.RESERVED);

	public static final String BACKGROUND_COLOR_DANGER_LIGHT = getBackgroundColor(Color.DANGER_LIGHT);
	public static final String BACKGROUND_COLOR_WARNING_LIGHT = getBackgroundColor(Color.WARNING_LIGHT);
	public static final String BACKGROUND_COLOR_SUCCESS_LIGHT = getBackgroundColor(Color.SUCCESS_LIGHT);
	public static final String BACKGROUND_COLOR_PRIMARY_LIGHT = getBackgroundColor(Color.PRIMARY_LIGHT);
	public static final String BACKGROUND_COLOR_INFO_LIGHT = getBackgroundColor(Color.INFO_LIGHT);
	public static final String BACKGROUND_COLOR_WHITE = getBackgroundColor("white");

	public static final String BACKGROUND_COLOR_DANGER = getBackgroundColor(Color.DANGER);
	public static final String BACKGROUND_COLOR_INFO = getBackgroundColor(Color.INFO);
	public static final String BACKGROUND_COLOR_PRIMARY = getBackgroundColor(Color.PRIMARY);
	public static final String BACKGROUND_COLOR_WARNING = getBackgroundColor(Color.WARNING);
	public static final String BACKGROUND_COLOR_SHY = getBackgroundColor(Color.SHY);
	public static final String BACKGROUND_COLOR_SHY_LIGHT = getBackgroundColor(Color.SHY_LIGHT);

	public static final String ALIGNMENT_BASELINE_CENTER = getAlignment("baseline-center");

	public static final String FONT_FAMILY_FONTAWESOME = "-fx-font-family: FontAwesome;";

	public static final String BUTTON_HEIGHT_24 = "-fx-min-height: 24;" + "-fx-max-height: 24;" + "-fx-font-size: 12;"
			+ "-fx-padding: 0 6 0 6;";

	public static final String TABLE_VIEW_POSTED_STATE_CELL_STYLE = getTableViewStateCellStyle(Color.POSTED);
	public static final String TABLE_VIEW_RESERVED_STATE_CELL_STYLE = getTableViewStateCellStyle(Color.RESERVED);

	public static String getFill(String color) {
		return "-fx-fill: " + color + ";";
	}

	public static String getTextFill(String color) {
		return "-fx-text-fill: " + color + ";";
	}

	public static String getBackgroundColor(String color) {
		return "-fx-background-color: " + color + ";";
	}

	public static String getFontSize(String size) {
		return "-fx-font-size: " + size + ";";
	}

	public static String getBorderColor(String colorBottom, String colorLeft, String colorTop, String colorRight) {
		return "-fx-border-color: " + colorBottom + " " + colorLeft + " " + colorTop + " " + colorRight + ";";
	}

	public static String getTableViewStateCellStyle(String stateColor) {
		return getBorderColor(Color.SHY_LIGHT, stateColor, Color.SHY_LIGHT, Color.SHY_LIGHT)
				+ "-fx-border-width: 0.5 8 0.5 0.5;" + getTextFill(stateColor) + TEXT_BOLD;
	}

	public static String getBorderColor(String color) {
		return getBorderColor(color, color, color, color);
	}

	public static String getBorderWidth(double width) {
		return "-fx-border-width: " + width + " " + width + " " + width + " " + width + ";";
	}

	public static String getAlignment(String alignment) {
		return "-fx-alignment: " + alignment + ";";
	}

	public static void removeIfContains(Control controls, String styleToRemove) {
		String style = controls.getStyle();

		if (style != null && style.contains(styleToRemove)) {
			style = style.replace(styleToRemove, "");

			controls.setStyle(style);
		}
	}

	public static void addIfNotContains(Control control, String styleToAdd) {
		if (!control.getStyle().contains(styleToAdd)) {
			String style = control.getStyle();
			if(style != null) {
				styleToAdd += style;
			}
			
			control.setStyle(styleToAdd);
		}
	}

}
