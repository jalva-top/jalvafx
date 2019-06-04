package top.jalva.jalvafx.node;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;

import com.panemu.tiwulfx.form.TypeAheadControl;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import top.jalva.jalvafx.style.CssStyle;
import top.jalva.jalvafx.style.CssStyleClass;
import top.jalva.jalvafx.util.ApplicationFormatter;
import top.jalva.jalvafx.util.DateUtils;
import top.jalva.jalvafx.util.NumberUtils;
import top.jalva.jalvafx.util.StringUtils;

public class PopOvers {

	static List<PopOver> popOvers = new ArrayList<>();

	static PopOver textEditPopOver = null;
	static PopOver copyToClipboardPopOver = null;

	/**
	 * All application {@link PopOver}s have to be created through this Factory
	 * method to avoid ControlsFX library bugs
	 */
	static PopOver createPopOver(boolean allowedFromPool) {
		PopOver popOver = null;

		if (allowedFromPool)
			popOver = getFromPool().orElse(new PopOver());

		if (popOver == null)
			popOver = new PopOver();
		else {
			popOver.setContentNode(null);
			popOver.setArrowLocation(ArrowLocation.LEFT_TOP);
		}

		popOver.setDetachable(false);

		String style = CssStyle.TEXT_FILL_BLACK + CssStyle.FILL_BLACK + CssStyle.TEXT_NOT_BOLD
				+ CssStyle.FONT_SIZE_NORMAL + CssStyle.TEXT_NOT_ITALIC;
		if (!popOver.getStyle().contains(style))
			popOver.setStyle(style);

		if (!popOvers.contains(popOver))
			popOvers.add(popOver);
		return popOver;
	}

	/**
	 * All application {@link PopOver}s have to be created through this Factory
	 * method to avoid ControlsFX library bugs
	 */
	public static PopOver createPopOver() {
		return createPopOver(true);
	}

	/**
	 * Have to use when closing Stage contains {@link PopOver}s to avoid
	 * ControlsFX library bugs
	 */
	public static void hidePopOvers() {
		if (PopOvers.popOvers != null && !PopOvers.popOvers.isEmpty()) {
			for (PopOver popOver : popOvers) {
				if (popOver != null && popOver.isShowing()) {
					try {
						if (popOver.getOwnerNode() != null)
							popOver.hide(Duration.millis(0));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static Optional<PopOver> getFromPool() {
		return popOvers.stream().filter(o -> !o.isShowing() && o != textEditPopOver && o != copyToClipboardPopOver)
				.findAny();
	}

	/**
	 * @param owner
	 *            is Node PopOver arrow points
	 * @param header
	 *            is a description of textToEdit
	 * @param textToEdit
	 *            current value of text is supposed to be edited
	 * @param maxLength
	 *            is max allowed length of textToEdit string
	 * @param arrowLocation
	 *            is nullable. If it is null then it has default value
	 * 
	 * @return ObjectProperty<String> to listen textToEdit changes
	 */
	public static ObjectProperty<String> showTextEditPopOver(Node owner, String header, String textToEdit,
			Integer maxLength, ArrowLocation arrowLocation) {

		ObjectProperty<String> prop = new SimpleObjectProperty<>();
		VBox parent = new VBox();
		parent.setSpacing(10.0);
		parent.setPadding(new Insets(10.0));

		if (!header.trim().endsWith(":"))
			header = header.trim() + ":";
		Label headerLabel = Controls.createHeaderLabel(header);

		TextArea textToEditArea = new TextArea();

		final double LINE_HEIGHT = 20.0;
		final double CHARS_PER_LINE = 70.0;
		final int MAX_LINE_NUMBER = 8;

		long lineNumberExpectation = Math.min(Math.round(maxLength / CHARS_PER_LINE) + 1, MAX_LINE_NUMBER);
		double prefWidth = LINE_HEIGHT * (lineNumberExpectation);

		textToEditArea.setPrefHeight(prefWidth);
		textToEditArea.setWrapText(true);

		Button save = new Button("Сохранить");
		save.getStyleClass().add(CssStyleClass.SUCCESS);
		save.setStyle(CssStyle.BUTTON_HEIGHT_24);
		save.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.SAVE));

		Button cancel = new Button("Отмена");
		cancel.setStyle(CssStyle.BUTTON_HEIGHT_24);

		HBox buttons = Controls.createHBox(10.0, Pos.CENTER_LEFT);
		buttons.getChildren().addAll(save, cancel);

		AnchorPane countParent = new AnchorPane();
		countParent.setPrefHeight(20.0);

		Label count = new Label();
		count.setStyle(CssStyle.TEXT_FILL_SHY);
		AnchorPane.setLeftAnchor(count, 0.0);

		Label maxCount = new Label("Максимальная длина строки: " + maxLength);
		maxCount.setStyle(CssStyle.TEXT_FILL_DANGER);
		AnchorPane.setRightAnchor(maxCount, 0.0);

		countParent.getChildren().addAll(count, maxCount);

		parent.getChildren().addAll(headerLabel, textToEditArea, countParent, new Separator(), buttons);

		if (textEditPopOver == null)
			textEditPopOver = createPopOver(false);
		textEditPopOver.setContentNode(parent);

		if (arrowLocation != null)
			textEditPopOver.setArrowLocation(arrowLocation);
		else
			textEditPopOver.setArrowLocation(ArrowLocation.LEFT_TOP);

		if (owner != null)
			textEditPopOver.show(owner);

		textToEditArea.textProperty().addListener((ov, old_v, new_v) -> {
			if (new_v != null)
				count.setText("Введено символов: " + new_v.length());
			else
				count.setText("Введено символов: " + 0);
		});
		textToEditArea.setText(textToEdit);

		save.setOnAction(e -> {
			String newTextValue = textToEditArea.getText();
			if (newTextValue == null)
				newTextValue = "";

			if (newTextValue.length() <= maxLength) {
				prop.set(newTextValue);
				if (owner != null)
					textEditPopOver.hide();
			} else
				AppNotifications.create(parent).title("Редактирование текста")
						.text("Максимальная допустимая длина строки: " + maxLength).showWarning();
		});

		cancel.setOnAction(e -> {
			if (owner != null)
				textEditPopOver.hide();
		});

		return prop;

	}

	/**
	 * @param owner
	 *            is Node PopOver arrow points
	 * @param header
	 *            is a description of textToEdit
	 * @param textToEdit
	 *            current value of text is supposed to be edited
	 * @param maxLength
	 *            is max allowed length of textToEdit string
	 * 
	 * @return ObjectProperty<String> to listen textToEdit changes
	 */
	public static ObjectProperty<String> showTextEditPopOver(Node owner, String header, String textToEdit,
			Integer maxLength) {
		return showTextEditPopOver(owner, header, textToEdit, maxLength, null);
	}

	/**
	 * @param owner
	 *            is Node PopOver arrow points
	 * @param header
	 *            is a description of textToEdit
	 * @param textToEdit
	 *            current value of text is supposed to be edited
	 * 
	 * @return ObjectProperty<String> to listen textToEdit changes
	 */
	public static ObjectProperty<String> showTextEditPopOver(Node owner, String header, String textToEdit) {
		return showTextEditPopOver(owner, header, textToEdit, 255);
	}

	public static ObjectProperty<LocalDate> showLocalDateEditPopOver(Node owner, String header, LocalDate dateToEdit,
			ArrowLocation arrowLocation) {
		ObjectProperty<LocalDate> prop = new SimpleObjectProperty<>();

		VBox parent = new VBox();
		parent.setSpacing(10.0);
		parent.setPadding(new Insets(10.0));

		if (!header.trim().endsWith(":"))
			header = header.trim() + ":";
		Label headerLabel = Controls.createHeaderLabel(header);

		DatePicker datePicker = new DatePicker();
		datePicker.setValue(dateToEdit);

		parent.getChildren().addAll(headerLabel, datePicker);

		PopOver popOver = createPopOver();
		popOver.setContentNode(parent);
		if (arrowLocation != null)
			popOver.setArrowLocation(arrowLocation);

		if (owner != null)
			popOver.show(owner);

		datePicker.valueProperty().addListener((ov, old_v, new_v) -> {
			if (new_v != null && !new_v.equals(dateToEdit)) {
				prop.setValue(new_v);
				popOver.hide();
			}
		});

		return prop;
	}

	/**
	 * @param warningMessage
	 *            if is null then will be generated based on
	 *            <b>minAllowedValue</b> and <b>maxAllowedValue</b>
	 * @param arrowLocation
	 *            is nullable. If is null then it has default value
	 * @return ObjectProperty<Double> to listen numberToEdit changes
	 */
	public static <N extends Number> ObjectProperty<Double> showNumberEditPopOver(Node owner, String header,
			String warningMessage, N numberToEdit, Double minAllowedValue, Double maxAllowedValue,
			ArrowLocation arrowLocation) {
		ObjectProperty<Double> prop = new SimpleObjectProperty<>();
		VBox parent = new VBox();
		parent.setSpacing(10.0);
		parent.setPadding(new Insets(10.0));

		if (!header.trim().endsWith(":"))
			header = header.trim() + ":";
		Label headerLabel = Controls.createHeaderLabel(header);

		TextField numberToEditTF = new TextField();
		numberToEditTF.setMaxWidth(80.0);

		if (numberToEdit instanceof Integer || numberToEdit instanceof Long)
			Controls.setAsDigitTextField(numberToEditTF, true);
		else
			Controls.setAsDoubleTextField(numberToEditTF);

		Button save = new Button("Сохранить");
		Button cancel = new Button("Отмена");
		HBox buttons = Controls.createSaveCancelButtonHBox(save, cancel);

		if (StringUtils.isBlank(warningMessage)) {
			if (minAllowedValue != null && maxAllowedValue != null)
				warningMessage = "Допускается значение"
						+ (minAllowedValue != null ? " не менее " + ApplicationFormatter.formatPrice(minAllowedValue)
								: "")
						+ (maxAllowedValue != null ? " не более " + ApplicationFormatter.formatPrice(maxAllowedValue)
								: "");
		}

		parent.getChildren().addAll(headerLabel, numberToEditTF);

		if (StringUtils.isNotBlank(warningMessage)) {
			Label allowedValuesTextLabel = new Label(warningMessage);
			allowedValuesTextLabel.setStyle(CssStyle.TEXT_FILL_DANGER);
			parent.getChildren().add(allowedValuesTextLabel);
		}

		parent.getChildren().addAll(new Separator(), buttons);

		PopOver popOver = createPopOver();
		popOver.setContentNode(parent);
		if (arrowLocation != null)
			popOver.setArrowLocation(arrowLocation);

		if (owner != null)
			popOver.show(owner);
		if (numberToEdit != null)
			numberToEditTF.setText(numberToEdit.toString());
		else
			numberToEditTF.clear();

		save.setOnAction(e -> {
			Double newDoubleValue = null;

			try {
				newDoubleValue = StringUtils.parseDouble(numberToEditTF.getText());
			} catch (Exception e1) {
			}

			String title = "Редактирование числа".intern();
			String incorrectValueMessage = "Введено некорректное значение".intern();

			if (newDoubleValue != null && (minAllowedValue == null || newDoubleValue.compareTo(minAllowedValue) >= 0)
					&& (maxAllowedValue == null || newDoubleValue.compareTo(maxAllowedValue) <= 0)) {

				if (numberToEdit instanceof Integer && !NumberUtils.fractionIsEmpty(newDoubleValue))
					AppNotifications.create(parent).title(title).text(incorrectValueMessage).showWarning();
				else {
					prop.set(newDoubleValue);
					if (owner != null)
						popOver.hide();
				}
			} else
				AppNotifications.create(parent).title(title).text(incorrectValueMessage).showWarning();
		});

		cancel.setOnAction(e -> {
			if (owner != null)
				popOver.hide();
		});

		numberToEditTF.setOnAction(e -> save.fire());

		return prop;

	}

	/**
	 * @param warningMessage
	 *            if is null then will be generated based on
	 *            <b>minAllowedValue</b> and <b>maxAllowedValue</b>
	 * @return ObjectProperty<Double> to listen numberToEdit changes
	 */
	public static <N extends Number> ObjectProperty<Double> showNumberEditPopOver(Node owner, String header,
			String warningMessage, N numberToEdit, Double minAllowedValue, Double maxAllowedValue) {
		return showNumberEditPopOver(owner, header, warningMessage, numberToEdit, minAllowedValue, maxAllowedValue,
				null);

	}

	public static void showPopOverWithContent(Node owner, Node content, ArrowLocation arrowlocation) {
		HBox parent = Controls.createHBox(10.0, Pos.CENTER);
		((Region) content).setPadding(new Insets(10.0));
		parent.setPadding(new Insets(10.0));

		parent.getChildren().add(content);

		PopOver popOver = createPopOver();
		popOver.setContentNode(content);
		if (arrowlocation != null)
			popOver.setArrowLocation(arrowlocation);
		popOver.show(owner);
	}

	public static <T> ObjectProperty<T> showObjectChoosePopOver(Node owner, Collection<T> items, String header,
			ArrowLocation location) {

		int LIST_VIEW_ROW_HEIGHT = 25;
		int MAX_ALLOWED_WIDTH = 800;
		int MAX_ALLOWED_HEIGHT = 400;
		int HEADER_HEIGHT = 50;
		int SYMBOL_WIDTH = 8;

		ObjectProperty<T> prop = new SimpleObjectProperty<>();

		if (items != null && !items.isEmpty()) {
			PopOver popOver = createPopOver();

			VBox parent = new VBox(10.0);
			parent.setPadding(new Insets(10.0));

			Label headerLabel = Controls.createHeaderLabel(header);
			parent.getChildren().addAll(headerLabel, new Separator());

			String new_line_symbol = "\n";

			int maxStringLength = items.stream().filter(o -> o.toString() != null)
					.mapToInt(o -> o.toString().contains(new_line_symbol)
							? Stream.of(o.toString().split(new_line_symbol)).mapToInt(s -> s.length()).max().orElse(0)
							: o.toString().length())
					.max().orElse(0);

			int nRows = items.stream().mapToInt(o -> o.toString().contains(new_line_symbol)
					? (int) Stream.of(o.toString(), new_line_symbol).count() : 1).sum();

			ListView<T> view = new ListView<>();
			view.setItems(FXCollections.observableArrayList(items));
			view.setPrefWidth(Math.min(maxStringLength * SYMBOL_WIDTH, MAX_ALLOWED_WIDTH));
			view.setPrefHeight(Math.min((nRows * LIST_VIEW_ROW_HEIGHT + HEADER_HEIGHT), MAX_ALLOWED_HEIGHT));
			parent.getChildren().add(view);

			view.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					prop.setValue(view.getSelectionModel().getSelectedItem());
					popOver.hide();
				}
			});

			if (location != null)
				popOver.setArrowLocation(location);
			popOver.setContentNode(parent);
			popOver.show(owner);
		} else
			showPopOverWithContent(owner, Controls.createDefaultPlaceholder());

		return prop;
	}

	public static void showPopOverWithContent(Node owner, Node content) {
		showPopOverWithContent(owner, content, null);
	}

	public static <T> ObjectProperty<T> showTypeAheadObjectEditPopOver(Node owner, String header, List<T> items,
			T currentValue) {
		final int MAX_WIDTH = 300;
		final int MIN_WIDTH = 100;
		final int FREE_SPACE = 50;

		ObjectProperty<T> prop = new SimpleObjectProperty<>(currentValue);

		int maxToStringLength = items.stream().mapToInt(o -> o.toString().length()).max().orElse(20);
		int prefWidth = Math.max(Math.min(maxToStringLength * Controls.SYMBOL_WIDTH + FREE_SPACE, MAX_WIDTH),
				MIN_WIDTH);

		PopOver popOver = createPopOver();
		VBox parent = new VBox(10.0);
		parent.setPadding(new Insets(10.0));

		Label headerLabel = Controls.createHeaderLabel(header);

		TypeAheadControl<T> typeAheadControl = new TypeAheadControl<>();
		typeAheadControl.getItems().addAll(items);
		typeAheadControl.setValue(currentValue);
		typeAheadControl.setPrefWidth(prefWidth);

		Button save = new Button();
		Button cancel = new Button();
		save.setOnAction(e -> {
			prop.setValue(typeAheadControl.getValue());
			popOver.hide();
		});
		save.disableProperty().bind(typeAheadControl.valueProperty().isNull());
		cancel.setOnAction(e -> popOver.hide());

		HBox buttons = Controls.createSaveCancelButtonHBox(save, cancel);

		parent.getChildren().addAll(headerLabel, typeAheadControl, new Separator(), buttons);

		popOver.setContentNode(parent);
		popOver.show(owner);

		return prop;
	}

	public static void showYesNoDialogPopOver(Node owner, String question, Button yes, Button no,
			ArrowLocation location) {
		PopOver popOver = createPopOver();
		if (location != null)
			popOver.setArrowLocation(location);

		no.setOnAction(e -> popOver.hide());
		yes.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> popOver.hide());

		ButtonBar buttons = Controls.createYesNoButtonHBox(yes, no);

		Label questionLabel = new Label(question);
		questionLabel.setStyle(CssStyle.TEXT_FILL_PRIMARY);

		VBox parent = new VBox(10);
		parent.setPadding(new Insets(10));

		parent.getChildren().addAll(questionLabel, new Separator(), buttons);
		popOver.setContentNode(parent);
		popOver.show(owner);
		no.requestFocus();
	}

	public static ObjectProperty<LocalTime> showTimePickerPopOver(Node owner, String header, LocalTime defaultValue,
			LocalTime minAllowedTime, LocalTime maxAllowedTime) {
		final LocalTime minTimeValue;
		final LocalTime maxTimeValue;

		if (minAllowedTime == null)
			minTimeValue = LocalTime.MIN;
		else
			minTimeValue = minAllowedTime;

		if (maxAllowedTime == null)
			maxTimeValue = LocalTime.MAX;
		else
			maxTimeValue = maxAllowedTime;

		ObjectProperty<LocalTime> result = new SimpleObjectProperty<>(defaultValue);
		PopOver popOver = createPopOver();

		VBox parent = new VBox(10.0);
		parent.setPadding(new Insets(10.0));
		Label headerLabel = Controls.createHeaderLabel(header);

		ComboBox<Integer> hour = new ComboBox<>();
		ComboBoxCustomizer.create(hour).overrideToString(o -> o.intValue() < 10 ? "0" + o : o.toString()).customize();
		hour.setPromptText("Часы");
		int minHour = minAllowedTime.getHour();
		int maxHour = maxAllowedTime.getHour();
		for (Integer hourValue = minHour; hourValue <= maxHour; hourValue++)
			hour.getItems().add(hourValue);
		if (defaultValue != null)
			hour.setValue(defaultValue.getHour());

		Label delimeter = new Label(":");

		ComboBox<Integer> minute = new ComboBox<>();
		ComboBoxCustomizer.create(minute).overrideToString(o -> o.intValue() < 10 ? "0" + o : o.toString()).customize();
		minute.setPromptText("Минуты");
		minute.getItems().addAll(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
		if (defaultValue != null)
			minute.setValue(defaultValue.getMinute());

		HBox time = Controls.createHBox(5.0, Pos.BASELINE_LEFT);
		time.getChildren().addAll(hour, delimeter, minute);

		Button save = new Button();
		Button cancel = new Button();

		cancel.setOnAction(e -> popOver.hide());
		save.setOnAction(e -> {
			try {
				LocalTime newTime = LocalTime.of(hour.getValue(), minute.getValue());

				if (newTime.isBefore(minTimeValue) || newTime.isAfter(maxTimeValue)) {
					AppNotifications.create(popOver)
							.text("Некорректно задано время. Значение может быть в диапазоне между "
									+ ApplicationFormatter.formatTime(minAllowedTime) + " и "
									+ ApplicationFormatter.formatTime(maxAllowedTime))
							.showWarning();
					;
				} else {
					result.setValue(newTime);
					popOver.hide();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		});
		save.disableProperty().bind(hour.valueProperty().isNull().or(minute.valueProperty().isNull()));

		HBox buttons = Controls.createSaveCancelButtonHBox(save, cancel);
		parent.getChildren().addAll(headerLabel, new Separator(), time, new Separator(), buttons);

		popOver.setContentNode(parent);
		popOver.show(owner);

		return result;
	}

	public static ObjectProperty<LocalDate> showDatePickerPopOver(Node owner, String header, LocalDate defaultValue,
			LocalDate minAllowedDate, LocalDate maxAllowedDate) {
		ObjectProperty<LocalDate> result = new SimpleObjectProperty<>(defaultValue);

		PopOver popOver = createPopOver();

		VBox parent = new VBox(10.0);
		parent.setPadding(new Insets(10.0));
		Label headerLabel = Controls.createHeaderLabel(header);

		DatePicker datePicker = new DatePicker(defaultValue);
		Controls.addContextMenu(datePicker);

		Button save = new Button();
		Button cancel = new Button();

		cancel.setOnAction(e -> popOver.hide());
		save.setOnAction(e -> {
			boolean error = false;
			LocalDate value = datePicker.getValue();
			if (minAllowedDate != null) {
				if (value.isBefore(minAllowedDate)) {
					error = true;
					AppNotifications.create(popOver)
							.text("Выберите дату не ранее " + DateUtils.asShortString(minAllowedDate)).showError();
				}
			}

			if (maxAllowedDate != null) {
				if (value.isBefore(maxAllowedDate)) {
					error = true;
					AppNotifications.create(popOver)
							.text("Выберите дату не позднее " + DateUtils.asShortString(maxAllowedDate)).showError();
				}
			}

			if (!error) {
				result.set(value);
				popOver.hide();
			}
		});
		save.disableProperty().bind(datePicker.valueProperty().isNull());

		HBox buttons = Controls.createSaveCancelButtonHBox(save, cancel);
		parent.getChildren().addAll(headerLabel, new Separator(), datePicker, new Separator(), buttons);

		popOver.setContentNode(parent);
		popOver.show(owner);

		return result;
	}

}
