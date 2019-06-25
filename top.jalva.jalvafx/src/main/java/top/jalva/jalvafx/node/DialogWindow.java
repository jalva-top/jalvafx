package top.jalva.jalvafx.node;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import top.jalva.jalvafx.util.Constant;

public class DialogWindow {

	public static void show(AlertType type, String message) {
		String header = "";
		String title = "";

		switch (type) {
		case INFORMATION:
			header = Constant.get(Constant.Key.INFORMATION);
			break;

		case WARNING:
			header = Constant.get(Constant.Key.WARNING);
			break;

		case ERROR:
			header = Constant.get(Constant.Key.ERROR);
			break;

		default:
			break;

		}

		Alert alert = new Alert(type);
		alert.initOwner(null);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public static void showWarning(String message) {
		show(AlertType.WARNING, message);
	}

	public static void showError(String message) {
		show(AlertType.ERROR, message);
	}

	public static void showInformation(String message) {
		show(AlertType.INFORMATION, message);
	}

	public static void showStackTrace(Exception e) {
		showStackTrace(e, Constant.get(Constant.Key.AN_ERROR_OCCURED_DURING_THE_LAST_OPERATION));
	}

	public static void showStackTrace(Exception e, String headerText) {

		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(null);
		alert.setTitle(Constant.getUpper(Constant.Key.ERROR));
		alert.setHeaderText(headerText);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label(Constant.getUpper(Constant.Key.EXCEPTION_DESCRIPTION));

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
}
