package top.jalva.jalvafx.node;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import top.jalva.jalvafx.style.CssStyle;
import top.jalva.jalvafx.util.ApplicationFormatter;
import top.jalva.jalvafx.util.Clipboard;
import top.jalva.jalvafx.util.DateUtils;
import top.jalva.jalvafx.util.ListUtils;
import top.jalva.jalvafx.util.NumberUtils;
import top.jalva.jalvafx.util.StringUtils;

public class TableViewUtils {

	public static final double ROW_HEIGHT = 30.0;
	private static double MONITOR_WIDTH = 0;

	private static <K> void addCopyToClipboardOnClick(Label label, K cellItemValue) {
		if (cellItemValue != null && (cellItemValue instanceof Double || cellItemValue instanceof BigDecimal))
			Controls.addCopyToClipboardOnClick(label, true);
		else
			Controls.addCopyToClipboardOnClick(label, false);
	}

	public static <T, K> void setCellFactoryWithEditButton(TableColumn<T, K> cell, String labelCssStyle,
			BiConsumer<T, Button> buttonActionHandler) {

		cell.setCellFactory(column -> {
			return new TableCell<T, K>() {
				@Override
				protected void updateItem(K item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle("");

					if (!empty && item != null) {
						AnchorPane parent = new AnchorPane();

						Label label = new Label(item.toString());
						if (StringUtils.isNotBlank(labelCssStyle))
							label.setStyle(labelCssStyle);
						AnchorPane.setLeftAnchor(label, 0.0);
						AnchorPane.setTopAnchor(label, 0.0);

						Button button = Controls.createSmallEditButton();
						AnchorPane.setRightAnchor(button, 0.0);
						AnchorPane.setTopAnchor(button, 0.0);

						T currentT = getTableView().getItems().get(getIndex());
						button.setOnAction(e -> buttonActionHandler.accept(currentT, button));

						addCopyToClipboardOnClick(label, item);

						parent.getChildren().addAll(label, button);

						setGraphic(parent);
					}
				}
			};
		});

	}

	public static <T, K> void setCellFactoryWithEditButton(TableColumn<T, K> cell,
			BiConsumer<T, Button> buttonActionHandler) {
		setCellFactoryWithEditButton(cell, null, buttonActionHandler);
	}

	public static <T, K> void setStyledToStringCellFactory(TableColumn<T, K> cell, String cssStyle,
			Boolean addCopyOnClick) {
		setStyledToStringCellFactory(cell, cssStyle, addCopyOnClick, Pos.BASELINE_CENTER);

	}

	public static <T, K> void setStyledToStringCellFactory(TableColumn<T, K> cell, Function<K, String> styleFunction) {

		cell.setCellFactory(column -> {
			return new TableCell<T, K>() {
				@Override
				protected void updateItem(K item, boolean empty) {
					super.updateItem(item, empty);

					setText(empty ? "" : item.toString());
					setGraphic(null);
					setStyle("");

					if (!empty && item != null) {
						String style = styleFunction.apply(item);
						if (StringUtils.isNotBlank(style))
							setStyle(style);
					}
				}
			};
		});
	}

	public static <T, K> void setStyledToStringCellFactory(TableColumn<T, K> cell, String cssStyle,
			Boolean addCopyOnClick, Pos textAlignment) {

		cell.setCellFactory(column -> {
			return new TableCell<T, K>() {
				@Override
				protected void updateItem(K item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle("");

					if (!empty && item != null) {
						HBox parent = Controls.createHBox(10.0, textAlignment);
						Label label = new Label(item.toString());
						label.setTooltip(new Tooltip(item.toString()));
						label.setStyle(cssStyle);
						if (addCopyOnClick)
							addCopyToClipboardOnClick(label, item);
						parent.getChildren().add(label);

						setGraphic(parent);
					}

				}
			};
		});
	}

	public static <T, K extends Number> void setGrayRedCellFactory(TableColumn<T, K> cell, double maxGrayValue,
			double minRedValue) {
		setGrayHighlightedCellFactory(cell, maxGrayValue, minRedValue, CssStyle.TEXT_FILL_DANGER + CssStyle.TEXT_BOLD);
	}

	public static <T, K extends Number> void setGrayHighlightedCellFactory(TableColumn<T, K> cell, double maxGrayValue,
			double minHighlightedValue, String highlightedCssStyle, String containerHighlightedCssStyle) {

		cell.setCellFactory(column -> {
			return new TableCell<T, K>() {
				@Override
				protected void updateItem(K item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle("");

					if (item != null && !empty) {
						HBox parent = Controls.createHBox(10.0, Pos.BASELINE_CENTER);
						Label label = new Label();

						if (item instanceof BigDecimal) {
							Controls.addCopyToClipboardOnClick(label, true);
							label.setText(ApplicationFormatter.format((BigDecimal) item));
						} else
							label.setText(item.toString());

						if (item.doubleValue() <= maxGrayValue)
							label.setStyle(CssStyle.TEXT_FILL_SHY_LIGHT);
						if (item.doubleValue() >= minHighlightedValue) {
							label.setStyle(highlightedCssStyle);
							if (StringUtils.isNotBlank(containerHighlightedCssStyle))
								setStyle(containerHighlightedCssStyle);
						}

						parent.getChildren().add(label);

						setGraphic(parent);
					}

				}
			};
		});
	}

	@SuppressWarnings("unchecked")
	public static <T, K> void setHighlightedRowCellFactory(TableColumn<T, K> cell, String cellLabelCssStyle,
			String highlightedRowCssStyle, Function<T, Boolean> isHighlightedCell, Pos cellLabelAlignment,
			boolean addCopyOnCellClick) {
		cell.setCellFactory(column -> {
			return new TableCell<T, K>() {

				@Override
				protected void updateItem(K item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle("");
					TableRow<T> tableRow = getTableRow();

					if (tableRow != null) {
						if (StringUtils.isNotBlank(highlightedRowCssStyle)
								&& tableRow.getStyle().contains(highlightedRowCssStyle))
							tableRow.setStyle(tableRow.getStyle().replace(highlightedRowCssStyle, ""));
					}

					if (item != null && !empty) {
						HBox parent = Controls.createHBox(10.0, cellLabelAlignment);
						Label label = new Label(item.toString());
						if (addCopyOnCellClick)
							addCopyToClipboardOnClick(label, item);
						label.setStyle(cellLabelCssStyle);
						parent.getChildren().add(label);
						setGraphic(parent);

						if (tableRow != null) {
							T currentRowItem = tableRow.getTableView().getItems().get(getIndex());
							if (currentRowItem != null && isHighlightedCell.apply(currentRowItem)) {
								if (StringUtils.isNotBlank(highlightedRowCssStyle))
									tableRow.setStyle(highlightedRowCssStyle);
							}
						}
					}

				}
			};
		});
	}

	public static <T, K extends Number> void setGrayHighlightedCellFactory(TableColumn<T, K> cell, double maxGrayValue,
			double minHighlightedValue, String highlightedCssStyle) {
		setGrayHighlightedCellFactory(cell, maxGrayValue, minHighlightedValue, highlightedCssStyle, "");
	}

	public static <T> void setFormattedBigDecimalCellFactory(TableColumn<T, BigDecimal> cell, String labelCssStyle,
			boolean showZeroFraction) {

		cell.setCellFactory(column -> {
			return new TableCell<T, BigDecimal>() {
				@Override
				protected void updateItem(BigDecimal item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle("");

					if (item != null && !empty) {
						HBox parent = Controls.createHBox(10.0, Pos.BASELINE_CENTER);
						Label label = new Label(ApplicationFormatter.format(item, showZeroFraction));

						if (StringUtils.isNotBlank(labelCssStyle))
							label.setStyle(labelCssStyle);

						Controls.addCopyToClipboardOnClick(label, true);

						parent.getChildren().add(label);

						setGraphic(parent);
					}

				}
			};
		});
	}

	public static <T> void setFormattedBigDecimalCellFactory(TableColumn<T, BigDecimal> cell, String labelCssStyle) {
		setFormattedBigDecimalCellFactory(cell, labelCssStyle, true);
	}

	public static <T> void setFormattedBigDecimalCellFactory(TableColumn<T, BigDecimal> cell,
			boolean showZeroFraction) {
		setFormattedBigDecimalCellFactory(cell, "", showZeroFraction);
	}

	public static <T> void setFormattedBigDecimalCellFactory(TableColumn<T, BigDecimal> cell) {
		setFormattedBigDecimalCellFactory(cell, true);
	}

	public static <T> void setLocalDateTimeCellFactory(TableColumn<T, LocalDateTime> cell) {

		cell.setCellFactory(column -> {
			return new TableCell<T, LocalDateTime>() {
				@Override
				protected void updateItem(LocalDateTime item, boolean empty) {
					super.updateItem(item, empty);

					setText((empty || item == null) ? "" : DateUtils.AsShortString(item));
					setGraphic(null);
					setStyle("");

					if (!empty && item != null) {
						setTooltip(new Tooltip(getText()));
						Controls.addCopyToClipboardOnClick(this);
					}

				}
			};
		});
	}

	public static <T> void setLocalDateCellFactory(TableColumn<T, LocalDate> cell) {

		cell.setCellFactory(column -> {
			return new TableCell<T, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);

					setText(empty ? "" : DateUtils.asShortString(item));
					setGraphic(null);
					setStyle("");

				}
			};
		});
	}

	public static <T> void setLocalDateCellFactory(TableColumn<T, LocalDate> cell, LocalDate minDateToNotMarkAsDanger) {

		cell.setCellFactory(column -> {
			return new TableCell<T, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);

					setText(empty ? "" : DateUtils.asShortString(item));
					setGraphic(null);

					if (item != null && item.compareTo(minDateToNotMarkAsDanger) < 0)
						setStyle(CssStyle.TEXT_BOLD + CssStyle.TEXT_FILL_DANGER);
					else
						setStyle(null);

				}
			};
		});
	}

	public static <T> void setBooleanGlyphCellFactory(TableColumn<T, Boolean> cell) {
		cell.setCellFactory(column -> {
			return new TableCell<T, Boolean>() {
				@Override
				protected void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle(null);

					if (item != null && !empty && item) {
						setGraphic(Glyphs.createCheckInfoGlyph());
						setStyle(CssStyle.ALIGNMENT_BASELINE_CENTER);
					}
				}
			};
		});
	}

	public static <T> void createColumnsAndFillTableView(TableView<T> tableView, Collection<T> items,
			List<Function<T, Object>> columnValueFunctions, List<String> headers, StringProperty filterProperty) {
		createColumnsAndFillTableView(tableView, items, columnValueFunctions, headers, true, null);
	}

	public static <T> void createColumnsAndFillTableView(TableView<T> tableView, Collection<T> items,
			List<Function<T, Object>> columnValueFunctions, List<String> headers, boolean calculatePrefHeight,
			StringProperty filterProperty) {

		if (items != null) {

			FilteredList<T> filteredItems = FXCollections.observableArrayList(items).filtered(null);
			SortedList<T> sortedItems = filteredItems.sorted();

			tableView.setItems(sortedItems);
			makeFiltered(tableView, filterProperty, filteredItems, columnValueFunctions);
			sortedItems.comparatorProperty().bind(tableView.comparatorProperty());

			tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
			tableView.setPlaceholder(Controls.createDefaultPlaceholder());

			double tableWidth = 6.0; // extra width to hide table view scroll
			if (tableView.getPadding() != null && tableView.getPadding() != Insets.EMPTY) {
				tableWidth += tableView.getPadding().getRight() + tableView.getPadding().getLeft();
			}

			int i = 0;
			for (Function<T, Object> columnValueFunction : columnValueFunctions) {
				String header = "";
				if (headers.size() >= i + 1) {
					header = headers.get(i++);
				}
				TableColumn<T, Object> column = createColumn(columnValueFunction, header);

				int maxValueLength = items.parallelStream().filter(o -> columnValueFunction.apply(o) != null)
						.mapToInt(o -> columnValueFunction.apply(o).toString().length()).max().orElse(0);
				double width = calculateColumnWidht(maxValueLength, header.length());
				column.setPrefWidth(width);

				tableWidth += width;

				tableView.getColumns().add(column);
			}

			if (calculatePrefHeight) {
				double height = calculateTableViewHeight(items.size());
				tableView.setPrefHeight(height);
			}

			tableView.setPrefWidth(calculateTableViewWidth(tableWidth));
		}
	}

	private static <T> void makeFiltered(TableView<T> tableView, StringProperty filterProperty,
			FilteredList<T> filteredItems, List<Function<T, Object>> columnValueFunctions) {
		if (filterProperty != null) {
			filterProperty.addListener((ov, old_v, new_v) -> {

				if (StringUtils.isNotBlank(new_v)) {
					String filter = new_v.toLowerCase();

					filteredItems.setPredicate(o -> {
						boolean match = false;
						for (Function<T, Object> f : columnValueFunctions) {
							Object columnValue = f.apply(o);

							if (columnValue != null && (columnValueToString(columnValue).toLowerCase().contains(filter)
									|| columnValue.toString().toLowerCase().contains(filter))) {
								match = true;
								break;
							}
						}

						return match;
					});
				} else
					filteredItems.setPredicate(null);

				tableView.refresh();
			});
		}
	}

	public static void createColumnsAndFillTableView(TableView<List<Object>> tableView, ResultSet resultSet,
			boolean calculatePrefHeight, StringProperty filterProperty) throws SQLException {
		List<List<Object>> data = new ArrayList<>();

		List<String> headers = new ArrayList<>();
		List<Function<List<Object>, Object>> columnValueFunctions = new ArrayList<>();

		int iteration = 0;
		resultSet.beforeFirst();
		while (resultSet.next()) {
			List<Object> row = new ArrayList<>();
			data.add(row);
			for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
				row.add(resultSet.getObject(i + 1));

				int index = i;
				if (iteration == 0) {
					columnValueFunctions.add(list -> list.get(index));

					String columnName = resultSet.getMetaData().getColumnLabel(index + 1);
					if (StringUtils.isBlank(columnName))
						columnName = resultSet.getMetaData().getColumnName(index + 1);
					headers.add(columnName);
				}
			}
			iteration++;
		}

		createColumnsAndFillTableView(tableView, data, columnValueFunctions, headers, calculatePrefHeight,
				filterProperty);
	}

	public static <T> TableColumn<T, Object> createColumn(Function<T, Object> columnValueFunction, String header) {

		boolean isHighlightedColumn = header != null && header.toLowerCase().equals("highlighted_row");

		TableColumn<T, Object> result = new TableColumn<>(header);

		result.setCellValueFactory(data -> new SimpleObjectProperty<>(columnValueFunction.apply(data.getValue())));
		result.setCellFactory(column -> {
			return new TableCell<T, Object>() {
				@Override
				protected void updateItem(Object item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle(null);

					if (!empty && item != null) {
						if (item instanceof LocalDate || item instanceof LocalDateTime || item instanceof Timestamp
								|| item instanceof Date)
							setText(columnValueToString(item));
						else {

							if (isHighlightedColumn
									&& (item.toString().equals("1") || item.toString().toLowerCase().equals("true"))) {

								setStyle(CssStyle.BACKGROUND_COLOR_WARNING_LIGHT);
							}

							HBox parent = new HBox();
							if (item instanceof Number)
								parent.setAlignment(Pos.BASELINE_CENTER);
							Label value = new Label();

							if (item instanceof BigDecimal)
								value.setText(ApplicationFormatter.format((BigDecimal) item));
							else
								value.setText(item.toString());

							value.setTooltip(new Tooltip(value.getText()));
							addCopyToClipboardOnClick(value, item);

							parent.getChildren().add(value);
							setGraphic(parent);
						}
					}

				}
			};
		});

		return result;
	}

	private static String columnValueToString(Object item) {
		String res = "";

		if (item != null) {
			if (item instanceof LocalDate)
				res = DateUtils.asShortString((LocalDate) item);
			else if (item instanceof LocalDateTime)
				res = DateUtils.AsShortString((LocalDateTime) item);
			else if (item instanceof Timestamp)
				res = DateUtils.AsShortString((Timestamp) item);
			else if (item instanceof Date)
				res = DateUtils.asShortString((Date) item);
			else if (item instanceof BigDecimal)
				res = ApplicationFormatter.format((BigDecimal) item);
			else
				res = item.toString();
		}

		return res;
	}

	public static double calculateTableViewHeight(int numberOfElements) {
		final int MAX_VISIBLE_ROWS_NUMBER = 20;
		final int MIN_VISIBLE_ROWS_NUMBER = 3;
		final double HEADER_HEIGHT = ROW_HEIGHT;
		final int EMPTY_ROWS_NUMBER = 2;

		double tableMaxHeight = ROW_HEIGHT * MAX_VISIBLE_ROWS_NUMBER;
		double tableMinHeight = ROW_HEIGHT * MIN_VISIBLE_ROWS_NUMBER;
		double tableFullHeight = ROW_HEIGHT * (numberOfElements + EMPTY_ROWS_NUMBER);

		double height = Math.min(tableMaxHeight, tableFullHeight);
		height = Math.max(height, tableMinHeight);
		height += HEADER_HEIGHT;

		return height;
	}

	private static double calculateTableViewWidth(double columnsSumWidth) {

		if (MONITOR_WIDTH == 0)
			MONITOR_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double MAX_TABLE_VIEW_WIDTH = MONITOR_WIDTH * 0.8;

		double width = columnsSumWidth;
		width = Math.min(width, MAX_TABLE_VIEW_WIDTH);

		return width;
	}

	private static <T> double calculateColumnWidht(int maxValueLength, int headerLength) {

		final double SYMBOL_WIDTH = 8;
		final double MAX_WIDTH = 400.0;
		final double MIN_WIDTH = 50.0;
		final int SHORT_VALUE_LENGTH = 12;
		final int EMTY_SPACE_SYMBOLS_NUMBER = 2;

		if (maxValueLength <= SHORT_VALUE_LENGTH)
			maxValueLength += EMTY_SPACE_SYMBOLS_NUMBER; // to add empty space
		if (headerLength <= SHORT_VALUE_LENGTH)
			headerLength += EMTY_SPACE_SYMBOLS_NUMBER; // to add empty space

		double width = MIN_WIDTH;
		width = Math.max(width, maxValueLength * SYMBOL_WIDTH);
		width = Math.max(width, headerLength * SYMBOL_WIDTH);
		width = Math.min(width, MAX_WIDTH);

		return width;
	}

	/** No need to assign value of cell, ROW_NUMBER = getIndex() + 1 */
	public static <T> void setRowNumberCellFactory(TableColumn<T, Integer> cell) {
		cell.setCellFactory(column -> {
			return new TableCell<T, Integer>() {
				@Override
				protected void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setGraphic(null);
					setStyle(CssStyle.TEXT_FILL_SHY_LIGHT);

					if (!isEmpty()) {
						setText((getIndex() + 1) + ".");
					}
				}
			};
		});
	}

	public static <T extends Object, K extends Number> void setEditNumberCellFactory(TableColumn<T, K> cell,
			final BiConsumer<T, Double> changeHandler, String editControlHeader, Double minAllowedValue,
			Double maxAllowedValue, Function<T, Boolean> ignoreEdit) {

		setEditNumberCellFactory(cell, changeHandler, editControlHeader, minAllowedValue, maxAllowedValue, ignoreEdit,
				true);

	}

	public static <T extends Object, K extends BigDecimal> void setEditBigDecimalCellFactory(TableColumn<T, K> cell,
			final BiConsumer<T, Double> changeHandler, String editControlHeader, Double minAllowedValue,
			Double maxAllowedValue, Function<T, Boolean> ignoreEdit, boolean showZeroFraction) {

		setEditNumberCellFactory(cell, changeHandler, editControlHeader, minAllowedValue, maxAllowedValue, ignoreEdit,
				showZeroFraction);

	}

	private static <T extends Object, K extends Number> void setEditNumberCellFactory(TableColumn<T, K> cell,
			final BiConsumer<T, Double> changeHandler, String editControlHeader, Double minAllowedValue,
			Double maxAllowedValue, Function<T, Boolean> ignoreEdit, boolean showZeroFraction) {
		setEditNumberCellFactory(cell, null, changeHandler, editControlHeader, minAllowedValue, maxAllowedValue,
				ignoreEdit, showZeroFraction);
	}

	public static <T extends Object, K extends Number> void setEditNumberCellFactory(TableColumn<T, K> cell,
			Function<K, String> labelStyleFunction, final BiConsumer<T, Double> changeHandler, String editControlHeader,
			Double minAllowedValue, Double maxAllowedValue, Function<T, Boolean> ignoreEdit, boolean showZeroFraction) {

		final Function<T, Boolean> finalIgnoreEdit;

		if (ignoreEdit == null)
			finalIgnoreEdit = o -> false;
		else
			finalIgnoreEdit = ignoreEdit;

		cell.setCellFactory(column -> {
			return new TableCell<T, K>() {
				@Override
				protected void updateItem(K item, boolean empty) {
					super.updateItem(item, empty);

					setText("");
					setStyle(null);
					setGraphic(null);

					if (!empty) {

						TableView<T> table = getTableView();
						T currentT = table.getItems().get(getIndex());

						String labelText = item.toString();
						if (item instanceof BigDecimal)
							labelText = ApplicationFormatter.format((BigDecimal) item, showZeroFraction);

						HBox hbox = Controls.createHBox(10.0, Pos.BASELINE_CENTER);
						Label label = new Label(labelText);

						if (labelStyleFunction != null)
							label.setStyle(labelStyleFunction.apply(item));
						;
						setStyle(CssStyle.ALIGNMENT_BASELINE_CENTER);

						if (finalIgnoreEdit.apply(currentT))
							addCopyToClipboardOnClick(label, item);
						else
							label.setTooltip(new Tooltip("Для редактирования ячейки кликните на ее значении"));

						hbox.getChildren().add(label);
						setGraphic(hbox);

						hbox.setOnMouseClicked(e -> {
							boolean notIgnored = !finalIgnoreEdit.apply(currentT);

							if (notIgnored) {
								table.getSelectionModel().select(currentT);

								ObjectProperty<Double> prop = PopOvers.showNumberEditPopOver(label, editControlHeader,
										null, item, minAllowedValue, maxAllowedValue);

								prop.addListener((ov, old_v, new_v) -> {
									changeHandler.accept(currentT, new_v);
								});
							}
						});
					}
				}
			};
		});
	}

	/** One level of column nesting is supported */
	public static <T> void copyToClipboard(TableView<T> table, ObservableList<T> rows) {

		StringBuilder body = new StringBuilder();
		StringBuilder headers = new StringBuilder();

		List<TableColumn<T, ?>> columns = new ArrayList<>();

		int i = 0;
		for (TableColumn<T, ?> column : table.getColumns()) {
			if (column.isVisible()) {
				if (i != 0)
					headers.append('\t');

				if (column.getColumns() == null || column.getColumns().isEmpty()) {
					columns.add(column);
					headers.append(column.getText());
				} else {
					columns.addAll(column.getColumns());

					int ni = 0;
					for (TableColumn<T, ?> nestedColumn : column.getColumns()) {
						if (nestedColumn.isVisible()) {
							if (ni != 0)
								headers.append('\t');
							headers.append(column.getText() + " " + nestedColumn.getText());
							ni++;
						}
					}
				}
				i++;
			}
		}

		for (T row : rows) {
			body.append('\n');
			int c = 0;
			for (TableColumn<T, ?> column : columns) {
				if (column.isVisible()) {
					if (c != 0)
						body.append('\t');

					Object value = null;
					ObservableValue<?> observableValue = column.getCellObservableValue(row);
					if (observableValue != null)
						value = observableValue.getValue();
					String valueString = "";

					if (value != null) {
						if (value instanceof Number)
							valueString = value.toString().replace(".", ",");
						else if (value instanceof LocalDateTime)
							valueString = ApplicationFormatter.formatDateTime((LocalDateTime) value);
						else
							valueString = value.toString().replace("\t", " ").replace("\n", " ");
					}

					body.append(valueString);
					c++;
				}
			}
		}

		Clipboard.set(headers.toString() + body.toString());
	}

	/** One level of column nesting is supported */
	public static <T> void addCopyToClipboardMenuItem(TableView<T> table) {

		MenuItem copyTable = new MenuItem("Копировать таблицу".intern());
		copyTable.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.COPY, CssStyle.TEXT_FILL_SUCCESS));

		copyTable.setOnAction(e -> copyToClipboard(table, table.getItems()));

		MenuItem copySelection = new MenuItem("Копировать строку".intern());
		copySelection.setGraphic(Glyphs.createGlyph(FontAwesome.Glyph.COPY, CssStyle.TEXT_FILL_SUCCESS));
		copySelection.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

		copySelection.setOnAction(e -> copyToClipboard(table,
				FXCollections.observableArrayList(Arrays.asList(table.getSelectionModel().getSelectedItem()))));

		Controls.addMenuItemToContextMenuOnTop(table, true, copyTable, copySelection);
	}

	public static <C extends TableColumn<?, ?>> Optional<Object> getCellValue(ObservableList<C> columns,
			String columnText, int rowIndex) {
		Optional<Object> value = Optional.empty();

		for (TableColumn<?, ?> column : columns) {

			if (column.getText().equals(columnText))
				value = getCellValue(column, rowIndex);
			else if (column.getColumns() != null && !column.getColumns().isEmpty()) {
				value = getCellValue(column.getColumns(), columnText, rowIndex);
			}
		}

		return value;
	}

	public static Optional<Object> getCellValue(TableView<?> tableView, String columnText, int rowIndex) {
		return getCellValue(tableView.getColumns(), columnText, rowIndex);
	}

	public static Optional<Object> getCellValue(TableColumn<?, ?> column, int rowIndex) {
		Object value = null;

		ObservableValue<?> observableValue = column.getCellObservableValue(rowIndex);
		if (observableValue != null)
			value = observableValue.getValue();

		return Optional.ofNullable(value);
	}

	public static Optional<Object> getFirstNotNullCellValue(TableView<?> tableView, String columnText) {
		Optional<Object> value = Optional.empty();

		int rowIndex = 0;
		for (@SuppressWarnings("unused")
		Object item : tableView.getItems()) {
			value = getCellValue(tableView, columnText, rowIndex);
			rowIndex++;
			if (value.isPresent())
				break;
		}

		return value;
	}

	public static Optional<Object> getFirstNotNullCellValue(TableColumn<?, ?> column) {
		Optional<Object> value = Optional.empty();

		TableView<?> tableView = column.getTableView();

		if (tableView != null) {

			int rowIndex = 0;
			for (@SuppressWarnings("unused")
			Object item : tableView.getItems()) {
				value = getCellValue(column, rowIndex);
				rowIndex++;
				if (value.isPresent())
					break;
			}
		}

		return value;
	}

	public static <T> Optional<Integer> getColumnIndex(TableView<T> table, String columnText) {
		Integer index = null;

		int i = 0;
		for (TableColumn<T, ?> column : table.getColumns()) {
			if (column.getText().toLowerCase().equals(columnText.toLowerCase())) {
				index = i;
				break;
			}
			i++;
		}

		return Optional.ofNullable(index);
	}

	public static <T> Optional<TableColumn<T, ?>> getColumn(TableView<T> table, String columnText) {

		TableColumn<T, ?> result = null;

		for (TableColumn<T, ?> column : table.getColumns()) {
			if (column.getText().toLowerCase().equals(columnText.toLowerCase())) {
				result = column;
				break;
			}
		}

		return Optional.ofNullable(result);
	}

	@SuppressWarnings("unchecked")
	private static <O extends Object, COLUMN extends TableColumn<O, ?>> List<COLUMN> getNumberColumns(
			List<COLUMN> columns, boolean onlyNumbersWithFloatingPoint) {
		Predicate<Optional<Object>> isTargetColumn;

		if (onlyNumbersWithFloatingPoint)
			isTargetColumn = cellValue -> cellValue.isPresent()
					&& (cellValue.get() instanceof BigDecimal || cellValue.get() instanceof Double);
		else {
			isTargetColumn = cellValue -> cellValue.isPresent() && cellValue.get() instanceof Number;
		}

		List<COLUMN> columnsToSum = new ArrayList<>();

		for (COLUMN column : columns) {

			if (column.getColumns().isEmpty()) {
				Optional<Object> cellValue = getFirstNotNullCellValue(column);

				if (isTargetColumn.test(cellValue)) {
					columnsToSum.add(column);
				}
			} else {
				List<TableColumn<O, ?>> cols = getNumberColumns(column.getColumns(), onlyNumbersWithFloatingPoint);

				columnsToSum.addAll((Collection<? extends COLUMN>) cols);

			}
		}

		return columnsToSum;
	}

	public static <O extends Object, T extends Number> Optional<HBox> createNumberColumnSumNode(TableView<O> table,
			List<TableColumn<O, T>> columns, boolean onlyNumbersWithFloatingPoint) {

		HBox parent = null;
		if (!table.getItems().isEmpty()) {

			List<TableColumn<O, ? extends Object>> columnsToSum;
			if (columns.isEmpty())
				columnsToSum = getNumberColumns(table.getColumns(), onlyNumbersWithFloatingPoint);
			else {
				columnsToSum = new ArrayList<>();
				columns.forEach(o -> columnsToSum.add(o));
			}

			if (!columnsToSum.isEmpty()) {
				parent = Controls.createHBox(10.0, Pos.BASELINE_LEFT);

				ComboBox<TableColumn<O, ?>> columnsComboBox = new ComboBox<>();
				ComboBoxCustomizer.create(columnsComboBox).overrideToString(o -> o.getText()).customize();
				columnsComboBox.setPromptText("Сумма значений для столбца");

				columnsComboBox.getItems().addAll(columnsToSum);

				columnsComboBox.valueProperty().addListener((ov, old_v, new_v) -> {

					if (new_v != null) {
						if (new_v.isVisible()) {
							double sum = 0;
							for (int i = 0; i < table.getItems().size(); i++) {
								Optional<Object> value = getCellValue(new_v, i);

								if (value.isPresent())
									sum += ((Number) value.get()).doubleValue();
							}

							HBox resultNode = Controls.createCaptionWithValueNode(
									"Сумма всех ячеек [" + new_v.getText() + "]:",
									ApplicationFormatter.format(NumberUtils.createBigDecimalPrice(sum)));

							if (new_v.getGraphic() == null) {
								new_v.setGraphic(new Label());
							}

							Node popOverOwner = new_v.getGraphic();

							PopOvers.showPopOverWithContent(popOverOwner, resultNode, ArrowLocation.BOTTOM_LEFT);
						} else
							AppNotifications.create().text("Выбранный столбец не активен").showError();

						columnsComboBox.setValue(null);
					}
				});

				parent.getChildren().add(columnsComboBox);
			}
		}

		return Optional.ofNullable(parent);
	}

	public static <O extends Object, T extends Number> void addNumberColumnSumMenuItemIfNotExist(TableView<O> table,
			boolean onlyNumbersWithFloatingPoint) {
		addNumberColumnSumMenuItemIfNotExist(table, Collections.emptyList(), onlyNumbersWithFloatingPoint);
	}

	public static <O extends Object, T extends Number> void addNumberColumnSumMenuItemIfNotExist(TableView<O> table,
			List<TableColumn<O, T>> columns) {
		addNumberColumnSumMenuItemIfNotExist(table, columns, false);
	}

	private static <O extends Object, T extends Number> void addNumberColumnSumMenuItemIfNotExist(TableView<O> table,
			List<TableColumn<O, T>> columns, boolean onlyNumbersWithFloatingPoint) {
		String menuItemId = "addNumberColumnSumMenuItemIfNotExist" + table.getId();

		if (!Controls.alreadyHasMenuItem(table.getContextMenu(), menuItemId)) {
			Optional<HBox> node = createNumberColumnSumNode(table, columns, onlyNumbersWithFloatingPoint);

			if (node.isPresent()) {
				CustomMenuItem menuItem = new CustomMenuItem(node.get());
				menuItem.setUserData(menuItemId);

				Controls.addMenuItemsToContextMenu(table, true, Arrays.asList(menuItem));
			}
		}
	}

	public static <T extends Object> void refreshItem(TableView<T> tableView, T item) {
		if (item != null) {
			ObservableList<T> list = tableView.getItems();
			ObservableList<T> items = ListUtils.getNewListChangingItem(list, item);
			tableView.setItems(items);
			tableView.refresh();
		}
	}

	public static <T extends Object> void refreshItems(TableView<T> tableView, List<T> itemsToRefresh) {
		if (!itemsToRefresh.isEmpty()) {
			ObservableList<T> list = tableView.getItems();
			ObservableList<T> items = ListUtils.getNewListChangingItems(list, itemsToRefresh);
			tableView.setItems(items);
			tableView.refresh();
		}
	}

	public static <T> void setItems(TableView<T> table, List<T> items, T itemToSelect) {
		if (itemToSelect == null)
			itemToSelect = table.getSelectionModel().getSelectedItem();
		table.getItems().clear();

		if (items != null) {
			table.getItems().addAll(items);

			if (itemToSelect != null && items.contains(itemToSelect))
				table.getSelectionModel().select(itemToSelect);
			else if (!table.getItems().isEmpty())
				table.getSelectionModel().select(0);
		}

		table.setPlaceholder(Controls.createDefaultPlaceholder());
	}

	public static <T> void setItemsWithAutoselect(TableView<T> table, List<T> items) {
		setItems(table, items, null);
	}
}
