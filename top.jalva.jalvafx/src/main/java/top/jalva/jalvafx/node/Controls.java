package top.jalva.jalvafx.node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import top.jalva.jalvafx.style.CssStyle;
import top.jalva.jalvafx.style.CssStyleClass;
import top.jalva.jalvafx.util.ApplicationFormatter;
import top.jalva.jalvafx.util.Clipboard;
import top.jalva.jalvafx.util.DateUtils;
import top.jalva.jalvafx.util.StringUtils;

public class Controls {

	final static int SYMBOL_WIDTH = 12;
	final static int ROW_HEIGHT = 30;
	private static final int MAX_ROW_INDEX = 20;

	static Map<String, Deque<String>> customTextFieldHistoryMap = new HashMap<>();

	public static void addRequiredFieldMarker(Labeled... labels) {

		for (Labeled label : labels) {
			Label marker = new Label("*");
			marker.setStyle(CssStyle.TEXT_BOLD + CssStyle.TEXT_FILL_DANGER + CssStyle.FONT_SIZE_OVER_NORMAL);

			label.setGraphic(marker);
			label.setContentDisplay(ContentDisplay.RIGHT);
		}
	}

	public static Button createSmallEditButton(String tooltipText) {
		Button button = new Button();
		button.getStyleClass().add(CssStyleClass.BUTTON_SMALL);

		Glyph glyph = Glyphs.createGlyph(FontAwesome.Glyph.EDIT);
		glyph.setStyle(CssStyle.TEXT_FILL_SHY);
		button.setGraphic(glyph);

		if (tooltipText != null && !tooltipText.trim().isEmpty())
			button.setTooltip(new Tooltip(tooltipText));

		return button;
	}

	public static Button createSmallEditButton() {
		return createSmallEditButton("");
	}

	public static Button createSmallRoundButton(String tooltipText) {
		Button button = new Button();
		button.getStyleClass().add(CssStyleClass.BUTTON_SMALL_ROUND);

		if (tooltipText != null && !tooltipText.trim().isEmpty())
			button.setTooltip(new Tooltip(tooltipText));

		return button;
	}

	public static Button createSmallInfoButton(String tooltipText) {
		Button button = new Button();
		button.getStyleClass().addAll(CssStyleClass.BUTTON_SMALL_ROUND);
		button.setStyle(CssStyle.BUTTON_HEIGHT_24);
		button.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.INFO, CssStyle.TEXT_FILL_INFO));

		if (tooltipText != null && !tooltipText.trim().isEmpty())
			button.setTooltip(new Tooltip(tooltipText));

		return button;
	}

	public static Label createLabelWithTooltip(String labelText, String tooltipText) {
		Label label = new Label(labelText);
		label.setTooltip(new Tooltip(tooltipText));

		return label;
	}

	public static Label createLabelWithGlyph(String labelText, FontAwesome.Glyph glyph, String glyphCssStyle) {

		Label label = new Label(labelText);
		label.setGraphic(Glyphs.createGlyph(glyph, glyphCssStyle));

		return label;
	}

	public static HBox createHBox(Double spacing, Pos alignment) {
		HBox hbox = new HBox();
		hbox.setSpacing(spacing);
		hbox.setAlignment(alignment);

		return hbox;
	}

	public static String getDefaultPlaceholderText() {
		return "Ничего не найдено".intern();
	}

	public static Node createDefaultPlaceholder() {
		Label label = new Label(getDefaultPlaceholderText());
		label.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.SEARCH_MINUS, CssStyle.TEXT_FILL_DANGER));
		label.setPadding(new Insets(10.0));

		return label;
	}

	public static String getPlaceholderWhenUpdateDataText() {
		return "Получение данных...".intern();
	}

	public static Node createPlaceholderWhenUpdateData() {
		Label label = new Label(getPlaceholderWhenUpdateDataText());
		label.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.DATABASE, CssStyle.TEXT_FILL_PRIMARY));
		label.setPadding(new Insets(10.0));

		return label;
	}

	public static Node createPlaceholderWhenError(Exception e) {
		Label label = new Label("ПРОИЗОШЛА ОШИБКА".intern());
		if (e != null && e.getMessage() != null && !e.getMessage().isEmpty())
			label.setTooltip(new Tooltip(e.getMessage()));
		label.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.WARNING, CssStyle.TEXT_FILL_DANGER));
		label.setPadding(new Insets(10.0));

		return label;
	}

	public static Boolean alreadyHasMenuItem(ContextMenu contextMenu, String userDataText) {
		return contextMenu != null && contextMenu.getItems() != null && contextMenu.getItems().stream()
				.filter(o -> o.getUserData() != null && o.getUserData().equals(userDataText)).findAny().isPresent();
	}

	public static void addCopyPasteMenuItem(TextInputControl textInputControl) {

		List<MenuItem> menuItems = new ArrayList<>();

		String copyMenuItemId = "copyMenuItem " + textInputControl.getId();
		String pasteMenuItemId = "pasteMenuItem " + textInputControl.getId();

		if (!alreadyHasMenuItem(textInputControl.getContextMenu(), pasteMenuItemId)) {
			MenuItem copy = createCopyMenuItem(textInputControl, copyMenuItemId);
			menuItems.add(copy);
		}

		if (!alreadyHasMenuItem(textInputControl.getContextMenu(), pasteMenuItemId)) {
			MenuItem paste = createPasteMenuItem(textInputControl, pasteMenuItemId);
			menuItems.add(paste);
		}

		addMenuItemToContextMenuOnTop(textInputControl, true, menuItems.toArray(new MenuItem[menuItems.size()]));
	}

	private static ContextMenu getContextMenuCreateIfNull(Control control) {
		ContextMenu cm = control.getContextMenu();
		if (cm == null) {
			cm = new ContextMenu();
			control.setContextMenu(cm);
		}

		return cm;
	}

	private static MenuItem createCopyMenuItem(TextInputControl inputControl, String menuItemId) {
		MenuItem copy = new MenuItem("Копировать".intern());
		copy.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.COPY, CssStyle.TEXT_FILL_SUCCESS));

		copy.setOnAction(e -> copyTextToClipboard(inputControl, false));

		ContextMenu cm = getContextMenuCreateIfNull(inputControl);

		cm.showingProperty().addListener((ov, old_v, new_v) -> {
			copy.setDisable(StringUtils.isBlank(inputControl.getText()));
		});

		return copy;
	}

	private static MenuItem createPasteMenuItem(TextInputControl inputControl, String menuItemId) {
		HBox parent = createHBox(10.0, Pos.BASELINE_LEFT);

		Label label = new Label("Вставить:");
		label.setStyle(CssStyle.TEXT_FILL_BLACK);
		Glyph glyph = Glyphs.createPasteGlyph();
		label.setGraphic(glyph);

		Label contentLabel = new Label();
		contentLabel.setStyle(CssStyle.TEXT_FILL_SHY + CssStyle.TEXT_ITALIC + CssStyle.FONT_SIZE_SMALL);

		Tooltip contentLabelTooltip = new Tooltip();
		contentLabel.setTooltip(contentLabelTooltip);

		parent.getChildren().addAll(label, contentLabel);

		ContextMenu cm = getContextMenuCreateIfNull(inputControl);

		MenuItem paste = new CustomMenuItem(parent);
		paste.setUserData(menuItemId);

		paste.setOnAction(e -> {
			Optional<String> content = Clipboard.getTextContent();

			if (content.isPresent()) {
				inputControl.setText(content.get());
			}
		});

		cm.showingProperty().addListener((ov, old_v, new_v) -> {
			Optional<String> content = Clipboard.getTextContent();
			paste.setDisable(!content.isPresent());

			if (content.isPresent()) {
				int maxTooltipLength = 20;
				String contentLabelText;

				if (content.get().length() > maxTooltipLength)
					contentLabelText = content.get().substring(0, maxTooltipLength) + "...";
				else
					contentLabelText = content.get();

				contentLabel.setText(contentLabelText);
				contentLabelTooltip.setText(content.get());
			} else {
				contentLabelTooltip.setText("");
				contentLabel.setText("");
			}

		});

		return paste;
	}

	private static void addClearButton(CustomTextField textField, Node rightNode, Node leftNode) {
		Label clearLabel;

		Glyph glyph = Glyphs.createGlyph(FontAwesome.Glyph.CLOSE, CssStyle.TEXT_FILL_SHY_LIGHT);
		clearLabel = createLabelWithGlyphToUseAsButton(glyph, CssStyle.TEXT_FILL_DANGER, CssStyle.TEXT_FILL_SHY_LIGHT);
		clearLabel.getGraphic().setVisible(false);

		Tooltip tooltip = new Tooltip("Очистить".intern());
		clearLabel.setTooltip(tooltip);

		if (textField.getPromptText() != null && !textField.getPromptText().trim().isEmpty()) {
			Tooltip textFieldTooltip = new Tooltip(textField.getPromptText());
			textField.setTooltip(textFieldTooltip);
		}

		clearLabel.setOnMouseClicked(e -> {
			textField.clear();
			clearLabel.setText("");
			textField.fireEvent(new ActionEvent());

		});

		clearLabel.getGraphic().setVisible(textField.getText() != null && !textField.getText().isEmpty());

		textField.textProperty().addListener((ov, o, n) -> {
			if (n == null || n.equals("")) {
				clearLabel.getGraphic().setVisible(false);
				glyph.setStyle(CssStyle.TEXT_FILL_SHY_LIGHT);
			}
			if ((o == null || o.equals("")) && (n != null && !n.equals("")))
				clearLabel.getGraphic().setVisible(true);
		});

		if (rightNode == null)
			textField.setRight(clearLabel);
		else {
			HBox parent = Controls.createHBox(3.0, Pos.CENTER_RIGHT);

			parent.getChildren().addAll(clearLabel, new Separator(Orientation.VERTICAL), rightNode);
			HBox.setMargin(rightNode, new Insets(3, 4, 3, 0));

			textField.setRight(parent);
		}

		if (leftNode != null) {
			textField.setLeft(leftNode);
		}

		addCopyPasteMenuItem(textField);
	}

	public static Label createLabelWithGlyphToUseAsButton(Glyph glyph, String cssStyleHovered,
			String cssStyleNotHovered) {
		Label clearLabel;
		clearLabel = new Label();
		clearLabel.setGraphic(glyph);
		clearLabel.setAlignment(Pos.CENTER_RIGHT);

		clearLabel.setStyle("-fx-border-color: transparent;\r\n" + " -fx-border-width: 0;\r\n"
				+ " -fx-background-radius: 0;\r\n" + " -fx-background-color: transparent;".intern());

		clearLabel.hoverProperty().addListener((ov, o, n) -> {
			if (!n.equals(o)) {
				if (n.equals(true))
					glyph.setStyle("-fx-cursor: arrow;" + cssStyleHovered);
				else
					glyph.setStyle(cssStyleNotHovered);
			}
		});

		return clearLabel;
	}

	public static void addClearButton(CustomTextField customTextField, Node rightNode) {
		addClearButton(customTextField, rightNode, null);
	}

	/**
	 * Method set OnKeyPressed handler and collect customTextField text when
	 * ENTER key pressed
	 */
	public static void addClearButtonAndHistoryButton(CustomTextField customTextField, String historyKey,
			Node rightNode) {
		Deque<String> searchHistory;

		if (historyKey != null && !historyKey.isEmpty() && customTextFieldHistoryMap.containsKey(historyKey))
			searchHistory = customTextFieldHistoryMap.get(historyKey);
		else {
			searchHistory = new LinkedList<>();
			if (historyKey != null && !historyKey.isEmpty())
				customTextFieldHistoryMap.put(historyKey, searchHistory);
		}

		Glyph glyph = Glyphs.createGlyph(FontAwesome.Glyph.HISTORY, CssStyle.TEXT_FILL_SHY_LIGHT);
		Label historyLabel = createLabelWithGlyphToUseAsButton(glyph, CssStyle.TEXT_FILL_PRIMARY,
				CssStyle.TEXT_FILL_SHY_LIGHT);
		historyLabel.setVisible(!searchHistory.isEmpty());

		historyLabel.setOnMouseClicked(e -> {
			if (searchHistory != null && !searchHistory.isEmpty()) {

				ObjectProperty<String> prop = PopOvers.showObjectChoosePopOver(historyLabel, searchHistory,
						"История поиска", ArrowLocation.TOP_RIGHT);

				prop.addListener((ov, old_v, new_v) -> {
					customTextField.setText(new_v);
					customTextField.fireEvent(new ActionEvent());
				});
			}
		});

		customTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				String text = customTextField.getText();

				if (text != null && !text.isEmpty()) {
					if (searchHistory.contains(text))
						searchHistory.remove(text);
					searchHistory.addFirst(text);
					historyLabel.setVisible(!searchHistory.isEmpty());

					int MAX_HISTORY_SIZE = 20;
					if (searchHistory.size() > MAX_HISTORY_SIZE) {
						List<String> toRemove = new ArrayList<>(searchHistory).subList(MAX_HISTORY_SIZE,
								searchHistory.size());
						searchHistory.removeAll(toRemove);
					}
				}
			}
		});

		if (rightNode != null) {
			HBox parent = Controls.createHBox(3.0, Pos.CENTER_RIGHT);
			parent.getChildren().addAll(historyLabel, rightNode);
			addClearButton(customTextField, parent);
		} else
			addClearButton(customTextField, historyLabel);
	}

	public static void addClearButton(CustomTextField customTextField) {
		addClearButton(customTextField, null, null);
	}

	public static void setSearchCustomFieldDesign(CustomTextField customTextField) {
		addClearButton(customTextField, Glyphs.createSearchGrayGlyph());
	}

	public static void addProductQuantityContextMenu(TextField textField, Integer numberInPackage) {

		ContextMenu currentMenu = textField.getContextMenu();
		Boolean leftExisting = false;

		if (currentMenu != null) {
			Object userData = currentMenu.getUserData();

			if (userData != null && userData instanceof Integer && ((Integer) userData).equals(numberInPackage))
				leftExisting = true;
		}

		if (!leftExisting) {

			ContextMenu menu = new ContextMenu();
			menu.setUserData(numberInPackage);

			List<Integer> quantityList = new ArrayList<>();
			List<Integer> packageList = new ArrayList<>();

			quantityList.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));

			if (numberInPackage == null || numberInPackage == 0 || numberInPackage == 1)
				quantityList.addAll(Arrays.asList(7, 8, 9, 10, 11, 12));
			else {
				packageList.addAll(Arrays.asList(1, 2, 3, 4, 5));
			}

			addMenuHeader(menu, "Штуки:");
			for (Integer item : quantityList) {

				MenuItem menuItem;

				if (item == null)
					menuItem = new SeparatorMenuItem();
				else {
					menuItem = new MenuItem("\t" + item.toString());

					menuItem.setOnAction(e -> {
						textField.setText(item.toString());
					});
				}

				menu.getItems().add(menuItem);
			}

			if (!packageList.isEmpty())
				addMenuHeader(menu, "Упаковки:");

			for (Integer item : packageList) {

				MenuItem menuItem;

				if (item == null)
					menuItem = new SeparatorMenuItem();
				else {
					menuItem = new MenuItem("\t" + item.toString());

					menuItem.setOnAction(e -> {
						textField.setText((item * numberInPackage) + "");
					});
				}

				menu.getItems().add(menuItem);
			}

			textField.setContextMenu(menu);
		}
	}

	/**
	 * To textInputControl will be added ContextMenu with toString()
	 * representation of items. When item is opted -
	 * textInputControl.setText(item.toString())
	 */
	public static <T extends TextInputControl, O extends Object> void addStringAutofillContextMenu(T textInputControl,
			Collection<O> items, String menuHeader) {

		List<MenuItem> menuItems = new ArrayList<>();

		if (items != null && !items.isEmpty()) {

			if (menuHeader != null)
				menuItems.add(createContextMenuHeader(menuHeader));

			items = items.stream().filter(o -> o != null).distinct()
					.sorted((o1, o2) -> o1.toString().compareTo(o2.toString())).collect(Collectors.toList());

			for (O item : items) {
				MenuItem menuItem = new MenuItem(item.toString());

				menuItem.setOnAction(e -> {
					textInputControl.setText(item.toString());
					if (!(item instanceof String))
						textInputControl.setUserData(item); // Hesitate if it
					// necessary
				});

				menuItems.add(menuItem);
			}

			ContextMenu cm = new ContextMenu();
			cm.getItems().addAll(menuItems);

			textInputControl.setContextMenu(cm);
			addCopyPasteMenuItem(textInputControl);

		}

	}

	/**
	 * To textInputControl will be added ContextMenu with toString()
	 * representation of items. When item is opted -
	 * textInputControl.setText(item.toString())
	 */
	public static <T extends TextInputControl, O extends Object> void addStringAutofillContextMenu(T textInputControl,
			Collection<O> items) {
		addStringAutofillContextMenu(textInputControl, items, null);
	}

	public static void setClearOnDoubleClick(ComboBox<?> comboBox) {

		String tooltipText = "Очистить - двойной клик".intern();
		addInfoRowTextToTooltip(comboBox, tooltipText);

		if (comboBox.isEditable()) {
			comboBox.getEditor().setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					comboBox.getEditor().clear();
				}
			});
		} else {
			comboBox.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2)
					comboBox.setValue(null);
			});
		}
	}

	private static MenuItem createContextMenuHeader(String text) {
		MenuItem packageHeader = new MenuItem(text);
		packageHeader.setStyle(CssStyle.FONT_SIZE_OVER_NORMAL + CssStyle.BACKGROUND_COLOR_SHY_LIGHT);

		return packageHeader;
	}

	private static void addMenuHeader(ContextMenu menu, String text) {
		MenuItem packageHeader = createContextMenuHeader(text);
		menu.getItems().add(packageHeader);
	}

	public static void createWarningButtonWithPopOver(Button button, String description, List<String> contentList) {
		button.setStyle(CssStyle.BUTTON_HEIGHT_24);
		button.getStyleClass().add(CssStyleClass.DANGER);

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(15.0));

		Label title = new Label("ВНИМАНИЕ");
		title.setStyle(CssStyle.TEXT_FILL_DANGER);
		title.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.WARNING, CssStyle.TEXT_FILL_DANGER));

		Label descriptionLabel = new Label(description);

		descriptionLabel.setStyle(CssStyle.TEXT_BOLD);
		descriptionLabel.setWrapText(true);
		vbox.getChildren().addAll(title, new Separator(), descriptionLabel, new Separator());

		int i = 0;
		for (String item : contentList) {
			HBox parent = Controls.createHBox(10.0, Pos.CENTER_LEFT);

			Label label = new Label(item);
			if (i == MAX_ROW_INDEX)
				label.setText("...");

			Controls.addCopyToClipboardOnClick(label);

			parent.getChildren().add(label);

			vbox.getChildren().add(parent);
			if (i == MAX_ROW_INDEX)
				break;
			i++;
		}

		PopOver popOver = new PopOver(vbox);

		button.setOnAction(e -> popOver.show(button));
	}

	public static void addInfoRowTextToTooltip(Control control, String text) {

		Tooltip tooltip = control.getTooltip();
		if (tooltip == null)
			tooltip = new Tooltip();

		Label label = new Label(text);
		label.setStyle(CssStyle.TEXT_FILL_INFO);
		tooltip.setGraphic(label);
		tooltip.setWrapText(true);
		tooltip.setContentDisplay(ContentDisplay.BOTTOM);

		control.setTooltip(tooltip);

	}

	public static Label createHeaderLabel(String header) {
		if (header != null && !header.trim().endsWith(":"))
			header = header.trim() + ":";

		Label headerLabel = new Label(header);
		headerLabel.setStyle(CssStyle.TEXT_FILL_PRIMARY + CssStyle.FONT_SIZE_OVER_NORMAL);
		return headerLabel;
	}

	/** Use for PopOver */
	public static HBox createSaveCancelButtonHBox(Button save, Button cancel) {
		save.setText("Сохранить".intern());
		save.getStyleClass().add(CssStyleClass.SUCCESS);
		save.setStyle(CssStyle.BUTTON_HEIGHT_24 + CssStyle.TEXT_NOT_ITALIC + CssStyle.TEXT_NOT_BOLD);
		save.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.SAVE));

		cancel.setText("Отмена".intern());
		cancel.setStyle(CssStyle.BUTTON_HEIGHT_24 + CssStyle.TEXT_NOT_ITALIC + CssStyle.TEXT_NOT_BOLD);

		HBox buttons = Controls.createHBox(10.0, Pos.CENTER_LEFT);
		buttons.getChildren().addAll(save, cancel);

		return buttons;
	}

	/** Use for PopOver */
	public static ButtonBar createYesNoButtonHBox(Button yes, Button no) {
		yes.setText("Да".intern());
		yes.getStyleClass().add(CssStyleClass.SUCCESS);
		yes.setStyle(CssStyle.BUTTON_HEIGHT_24 + CssStyle.TEXT_NOT_ITALIC + CssStyle.TEXT_NOT_BOLD);
		yes.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.CHECK));

		no.setText("Нет".intern());
		no.setStyle(CssStyle.BUTTON_HEIGHT_24 + CssStyle.TEXT_NOT_ITALIC + CssStyle.TEXT_NOT_BOLD);

		ButtonBar buttons = new ButtonBar();
		buttons.setButtonMinWidth(60.0);
		buttons.getButtons().addAll(yes, no);

		return buttons;
	}

	public static Label createPropertyNameLabel(String propertyName) {
		return createPropertyNameLabel(propertyName, false);
	}

	public static Label createPropertyNameLabel(String propertyName, boolean requiredField) {
		Label label = new Label();
		if (requiredField)
			Controls.addRequiredFieldMarker(label);
		return decoratePropertyNameLabel(label, propertyName);
	}

	private static Label decoratePropertyNameLabel(Label label, String propertyName) {
		label.setText(propertyName + ":");
		label.setPrefWidth(120.0);
		label.setStyle(
				CssStyle.TEXT_FILL_SHY + CssStyle.TEXT_NOT_ITALIC + CssStyle.TEXT_NOT_BOLD + CssStyle.FONT_SIZE_12px);
		return label;
	}

	public static HBox getStringPropertyRow(String propertyName, String propertyValue, CustomTextField textField,
			boolean requiredField) {
		return createStringPropertyRow(propertyName, createPropertyNameLabel(propertyName, requiredField),
				propertyValue, textField);
	}

	public static HBox createStringPropertyRow(String propertyName, Label propertyNameLabel, String propertyValue,
			CustomTextField textField) {
		HBox hbox = Controls.createHBox(10.0, Pos.CENTER_LEFT);
		decoratePropertyNameLabel(propertyNameLabel, propertyName);
		textField.setText(propertyValue);

		if (textField.getTooltip() == null || textField.getTooltip().getText() == null
				|| textField.getTooltip().getText().trim().isEmpty()) {
			textField.setTooltip(new Tooltip(propertyName));
		}

		Controls.addClearButton(textField);

		hbox.getChildren().addAll(propertyNameLabel, textField);

		return hbox;
	}

	public static <T> void addClearContextMenu(ComboBox<T> comboBox) {
		ContextMenu menu = comboBox.getContextMenu();
		if (menu == null)
			menu = new ContextMenu();

		MenuItem menuItem = new MenuItem("Очистить".intern());
		Glyph glyph = Glyphs.createGlyph(FontAwesome.Glyph.CLOSE, CssStyle.TEXT_FILL_DANGER);
		menuItem.setGraphic(glyph);

		menuItem.setOnAction(e -> {
			if (comboBox.isEditable())
				comboBox.getEditor().clear();
			comboBox.setValue(null);

		});

		menu.getItems().add(menuItem);
		comboBox.getEditor().setContextMenu(menu);
	}

	public static void addContextMenu(DatePicker datePicker) {
		ContextMenu menu = datePicker.getEditor().getContextMenu();

		if (menu == null)
			menu = new ContextMenu();

		addMenuHeader(menu, "Установить:");

		MenuItem today = new MenuItem("Сегодня");
		today.setOnAction(e -> datePicker.setValue(LocalDate.now()));

		MenuItem yesterday = new MenuItem("Вчера");
		yesterday.setOnAction(e -> datePicker.setValue(LocalDate.now().minusDays(1)));

		MenuItem tomorrow = new MenuItem("Завтра");
		tomorrow.setOnAction(e -> datePicker.setValue(LocalDate.now().plusDays(1)));

		MenuItem curMonthBegin = new MenuItem("Начало текущего месяца");
		curMonthBegin.setOnAction(e -> datePicker.setValue(DateUtils.getMonthBegin(LocalDate.now())));

		MenuItem curMonthEnd = new MenuItem("Конец текущего месяца");
		curMonthEnd.setOnAction(e -> datePicker.setValue(DateUtils.getMonthEnd(LocalDate.now())));

		MenuItem prevMonthBegin = new MenuItem("Начало предыдущего месяца");
		prevMonthBegin.setOnAction(e -> datePicker.setValue(DateUtils.getMonthBegin(LocalDate.now().minusMonths(1))));

		MenuItem prevMonthEnd = new MenuItem("Конец предыдущего месяца");
		prevMonthEnd.setOnAction(e -> datePicker.setValue(DateUtils.getMonthEnd(LocalDate.now().minusMonths(1))));

		menu.getItems().addAll(today, yesterday, tomorrow, new SeparatorMenuItem(), curMonthBegin, curMonthEnd,
				new SeparatorMenuItem(), prevMonthBegin, prevMonthEnd);

		datePicker.getEditor().setContextMenu(menu);
	}

	public static void addCopyToClipboardOnClick(Labeled label, boolean copyAsComaSeparatedDecimal) {
		HBox hbox = createHBox(10.0, Pos.CENTER);
		hbox.setPadding(new Insets(10.0));
		Label successMessage = new Label("Скопировано в буфер обмена");
		successMessage.setStyle(
				CssStyle.TEXT_FILL_BLACK + CssStyle.TEXT_NOT_BOLD + CssStyle.TEXT_NOT_ITALIC + CssStyle.FONT_SIZE_12px);
		hbox.getChildren().addAll(Glyphs.createGlyph(FontAwesome.Glyph.COPY, CssStyle.TEXT_FILL_SUCCESS),
				successMessage);

		if (PopOvers.copyToClipboardPopOver == null)
			PopOvers.copyToClipboardPopOver = PopOvers.createPopOver(false);
		PopOvers.copyToClipboardPopOver.setContentNode(hbox);

		String tooltipText = "Нажмите, чтобы скопировать в буфер обмена";
		addInfoRowTextToTooltip(label, tooltipText);

		label.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY)
				copyTextToClipboard(label, copyAsComaSeparatedDecimal);
		});
	}

	private static void copyTextToClipboard(TextInputControl textInputControl, Boolean copyAsComaSeparatedDecimal) {
		copyTextToClipboard(textInputControl, () -> textInputControl.getText(), copyAsComaSeparatedDecimal);
	}

	private static void copyTextToClipboard(Labeled labeled, Boolean copyAsComaSeparatedDecimal) {
		copyTextToClipboard(labeled, () -> labeled.getText(), copyAsComaSeparatedDecimal);
	}

	private static void copyTextToClipboard(Control control, Supplier<String> valueToCopySupplier,
			Boolean copyAsComaSeparatedDecimal) {
		String valueToCopy = valueToCopySupplier.get();
		if (valueToCopy == null)
			valueToCopy = "";

		if (copyAsComaSeparatedDecimal)
			valueToCopy = valueToCopy.replace(".", ",").replace(" ", "").replace("\u00A0", ""); // &#160
		// symbol
		// -
		// delimeter
		// in
		// BigDecimal

		Clipboard.set(valueToCopy);
		PopOvers.copyToClipboardPopOver.show(control);
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(500);
				Platform.runLater(() -> {
					try {
						PopOvers.copyToClipboardPopOver.hide();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		});

		try {
			thread.join();
			thread.start();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void addCopyToClipboardOnClick(Labeled label) {
		addCopyToClipboardOnClick(label, false);
	}

	public static <TF extends TextField> void setAsDoubleTextField(TF tf) {
		tf.textProperty().addListener((ov, old_v, new_v) -> {
			if (StringUtils.isNotBlank(new_v) && !"-".equals(new_v)) {
				try {
					StringUtils.parseDouble(new_v);
				} catch (NumberFormatException e) {
					Platform.runLater(() -> {
						tf.setText(old_v.trim());
						tf.positionCaret(old_v.length());
					});
				}
			}
		});
	}

	/** if maxLength == null -> any length is allowed */
	public static <TF extends TextField> void setAsDigitTextField(TF tf, Integer maxLength, boolean allowNegative) {
		String pattern;
		if (maxLength != null && maxLength.compareTo(0) > 0)
			pattern = "[0-9]{0,{maxLength}}".replace("{maxLength}", maxLength.toString());
		else
			pattern = "[0-9]*";

		if (allowNegative)
			pattern = "\\-{0,1}" + pattern;

		String finalPattern = pattern;

		tf.textProperty().addListener((ov, old_v, new_v) -> {
			if (StringUtils.isNotBlank(new_v)) {
				if (!new_v.matches(finalPattern)) {
					Platform.runLater(() -> {
						tf.setText(old_v.trim());
						tf.positionCaret(old_v.length());
					});
				}
			}
		});
	}

	/** if maxLength == null -> any length is allowed */
	public static <TF extends TextField> void setAsDigitTextField(TF tf, boolean allowNegative) {
		setAsDigitTextField(tf, null, allowNegative);
	}

	/** if maxLength == null -> any length is allowed */
	public static <TF extends TextField> void setAsDigitTextField(TF tf, Integer maxLength) {
		setAsDigitTextField(tf, maxLength, false);
	}

	public static <TF extends TextField> void setAsDigitTextField(TF tf) {
		setAsDigitTextField(tf, null);
	}

	public static <TF extends TextField> void setAsPhoneTextField(TF tf) {
		tf.textProperty().addListener((ov, old_v, new_v) -> {
			if (StringUtils.isNotBlank(new_v)) {
				if (!new_v.matches("[0-9]{0,10}")) {
					Platform.runLater(() -> {
						tf.setText(old_v.trim());
						tf.positionCaret(old_v.length());
					});
				}
			}
		});
	}

	public static <TF extends TextField> void setTextFieldValueValidator(TF tf,
			Function<String, Boolean> isValidValue) {

		tf.textProperty().addListener((ov, old_v, new_v) -> {
			if (isValidValue.apply(new_v))
				CssStyleClass.remove(tf, CssStyleClass.ERROR);
		});

		tf.focusedProperty().addListener((ov, old_v, new_v) -> {
			if (!new_v) {
				if (isValidValue.apply(tf.getText()))
					CssStyleClass.remove(tf, CssStyleClass.ERROR);
				else
					CssStyleClass.add(tf, CssStyleClass.ERROR);
			}
		});
	}

	public static <T, CB extends ComboBoxBase<T>> void setTextFieldValueValidator(CB cb,
			Function<T, Boolean> isValidValue) {

		cb.valueProperty().addListener((ov, old_v, new_v) -> {
			if (isValidValue.apply(new_v))
				CssStyleClass.remove(cb, CssStyleClass.ERROR);
		});

		cb.focusedProperty().addListener((ov, old_v, new_v) -> {
			if (!new_v) {
				if (isValidValue.apply(cb.getValue()))
					CssStyleClass.remove(cb, CssStyleClass.ERROR);
				else
					CssStyleClass.add(cb, CssStyleClass.ERROR);
			}
		});
	}

	public static void addMenuItemToContextMenuOnTop(Control control, boolean addSeparatorAfterIfItemsBelow,
			MenuItem... menuItems) {
		addMenuItemsToContextMenu(control, addSeparatorAfterIfItemsBelow, Arrays.asList(menuItems), true);
	}

	public static void addMenuItemsToContextMenu(Control control, boolean separateFromExistedBefore,
			List<MenuItem> menuItems) {
		addMenuItemsToContextMenu(control, separateFromExistedBefore, menuItems, false);
	}

	public static void addMenuItemsToContextMenu(Control control, boolean separateFromExistedBefore,
			List<MenuItem> menuItems, boolean onContextMenuTop) {
		ContextMenu contextMenu = getContextMenuCreateIfNull(control);

		List<MenuItem> items = new ArrayList<>();

		if (onContextMenuTop)
			items.addAll(menuItems);

		if (!contextMenu.getItems().isEmpty()) {
			if (separateFromExistedBefore && onContextMenuTop)
				items.add(new SeparatorMenuItem());
			items.addAll(contextMenu.getItems());
		}

		if (!onContextMenuTop) {
			if (!contextMenu.getItems().isEmpty() && separateFromExistedBefore)
				items.add(new SeparatorMenuItem());
			items.addAll(menuItems);
		}

		contextMenu.getItems().clear();
		contextMenu.getItems().addAll(items);
		control.setContextMenu(contextMenu);
	}

	public static void addRequiredTextMarker(TextField tf) {
		final String dangerStyle = CssStyle.BACKGROUND_COLOR_WARNING_LIGHT;

		if (StringUtils.isBlank(tf.getText()) && !tf.getStyle().contains(dangerStyle))
			tf.setStyle(dangerStyle);

		tf.textProperty().addListener((ov, old_v, new_v) -> {
			if (StringUtils.isBlank(new_v)) {
				if (!tf.getStyle().contains(dangerStyle))
					tf.setStyle(dangerStyle);
			} else if (tf.getStyle().contains(dangerStyle))
				tf.setStyle(tf.getStyle().replace(dangerStyle, ""));
		});
	}

	public static void addNotBlankTextMarker(TextField tf) {
		final String notBlankStyle = CssStyle.BACKGROUND_COLOR_INFO_LIGHT;

		if (StringUtils.isNotBlank(tf.getText()) && !tf.getStyle().contains(notBlankStyle))
			tf.setStyle(notBlankStyle);

		tf.textProperty().addListener((ov, old_v, new_v) -> {
			if (StringUtils.isNotBlank(new_v)) {
				if (!tf.getStyle().contains(notBlankStyle))
					tf.setStyle(notBlankStyle);
			} else if (tf.getStyle().contains(notBlankStyle))
				tf.setStyle(tf.getStyle().replace(notBlankStyle, ""));
		});
	}

	public static void customizeCheckComboBox(CheckComboBox<?> checkComboBox) {
		MenuItem checkAll = new MenuItem("Выбрать все");
		MenuItem uncheckAll = new MenuItem("Убрать все");

		checkAll.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.CHECK, CssStyle.TEXT_FILL_SUCCESS));
		uncheckAll.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.CLOSE, CssStyle.TEXT_FILL_DANGER));

		checkAll.setOnAction(e -> checkAllNotNull(checkComboBox));
		uncheckAll.setOnAction(e -> checkComboBox.getCheckModel().clearChecks());

		addMenuItemToContextMenuOnTop(checkComboBox, true, checkAll, uncheckAll);
	}

	public static <T> void checkAllNotNull(CheckComboBox<T> checkComboBox) {
		checkComboBox.getCheckModel().clearChecks();

		for (T item : checkComboBox.getItems()) {
			if (item != null)
				checkComboBox.getCheckModel().check(item);
			else
				checkComboBox.getCheckModel().clearCheck(item);
		}
	}

	public static HBox createCaptionWithValueNode(String caption, BigDecimal value) {
		return createCaptionWithValueNode(caption, ApplicationFormatter.format(value), true);
	}

	public static HBox createCaptionWithValueNode(String caption, String value) {
		return createCaptionWithValueNode(caption, value, false);
	}

	public static HBox createCaptionWithValueNode(String caption, String value, boolean copyAsComaSeparated) {
		HBox parent = Controls.createHBox(5.0, Pos.CENTER_LEFT);

		Label captionLabel = new Label(caption.trim());
		captionLabel.setStyle(CssStyle.TEXT_FILL_SHY);
		Label valueLabel = new Label(value.trim());
		Controls.addCopyToClipboardOnClick(valueLabel, copyAsComaSeparated);

		parent.getChildren().addAll(captionLabel, valueLabel);

		return parent;
	}

}
