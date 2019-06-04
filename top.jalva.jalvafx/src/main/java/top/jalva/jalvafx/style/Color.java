package top.jalva.jalvafx.style;

public class Color {

	// Main colors->

	public static final String PRIMARY = "#337ab7";
	public static final String INFO = "#5bc0de";
	public static final String SUCCESS = "#5cb85c";
	public static final String WARNING = "#f0ad4e";
	public static final String DANGER = "#d9534f";

	public static final String SHY = "gray";
	public static final String POSTED = Color.derive(Color.POSTED_LIGHT, -30);

	public static final String POSTED_BACK = Color.WARNING;
	public static final String RESERVED = Color.SUCCESS;

	// Light colors->

	public static final String PRIMARY_LIGHT = Color.derive(Color.PRIMARY, 90);
	public static final String INFO_LIGHT = Color.derive(Color.INFO, 90);
	public static final String SUCCESS_LIGHT = Color.derive(Color.SUCCESS, 90);
	public static final String WARNING_LIGHT = Color.derive(Color.WARNING, 80);
	public static final String DANGER_LIGHT = Color.derive(Color.DANGER, 90);

	public static final String SHY_LIGHT = "lightgray";
	public static final String POSTED_LIGHT = "violet";

	// Methods->

	public static String derive(String color, int percentage) {
		return "derive(" + color + ", " + percentage + "%)";
	}

}
