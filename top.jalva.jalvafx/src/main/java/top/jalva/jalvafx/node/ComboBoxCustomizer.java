package top.jalva.jalvafx.node;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;
import top.jalva.jalvafx.style.CssStyle;
import top.jalva.jalvafx.util.Constant;
import top.jalva.jalvafx.util.StringUtils;
import top.jalva.jalvafx.util.StringUtils.KeyboardLayoutConvertationType;

public class ComboBoxCustomizer<T> {
	final static int ITEMS_SIZE_TO_CUT = 100;
	final static String EMPHASIZED_CSS_STYLE = top.jalva.jalvafx.style.CssStyle.TEXT_BOLD;
	final static String DE_EMPHASIZED_CSS_STYLE = CssStyle.TEXT_FILL_SHY_LIGHT;

	Glyph emphasizedGlyph = FontAwesome.Glyph.CARET_RIGHT;
	String emphasizedGlyphCssStyle = CssStyle.TEXT_FILL_SHY_LIGHT;

	ComboBox<T> comboBox;
	ObservableList<T> items;

	Function<T, List<Object>> extraColumnsFunction = null;
	BiFunction<T, String, Boolean> extraOrFilterFunction;

	Function<T, String> defaultToString = (o) -> o == null ? "" : o.toString();
	Function<T, String> toString = defaultToString;

	Predicate<T> deemphasizedPredicate = null;
	Predicate<T> emphasizedPredicate = null;

	Function<T, Optional<Pair<FontAwesome.Glyph, String>>> glyphStyleFunction = null;

	boolean hideClearButton = false;

	// ************** PUBLIC

	public static <T> ComboBoxCustomizer<T> create(ComboBox<T> comboBox) {
		return new ComboBoxCustomizer<T>(comboBox);
	}

	private ComboBoxCustomizer(ComboBox<T> comboBox) {
		super();
		this.comboBox = comboBox;
	}

	public ComboBoxCustomizer<T> multyColumn(Function<T, List<Object>> extraColumnsFunction) {
		this.extraColumnsFunction = extraColumnsFunction;
		return this;
	}

	public ComboBoxCustomizer<T> autocompleted(ObservableList<T> items) {
		this.items = items;
		return this;
	}

	public ComboBoxCustomizer<T> autocompleted(Collection<T> items) {
		this.items = FXCollections.observableArrayList(items);
		return this;
	}

	public ComboBoxCustomizer<T> overrideToString(Function<T, String> toString) {
		this.toString = toString;
		return this;
	}

	public ComboBoxCustomizer<T> extraSearch(BiFunction<T, String, Boolean> extraSearchFunction) {
		this.extraOrFilterFunction = extraSearchFunction;
		return this;
	}

	public ComboBoxCustomizer<T> emphasized(Predicate<T> filter) {
		this.emphasizedPredicate = filter;
		return this;
	}

	public ComboBoxCustomizer<T> deemphasized(Predicate<T> filter) {
		this.deemphasizedPredicate = filter;
		return this;
	}

	public ComboBoxCustomizer<T> glyph(FontAwesome.Glyph glyph, String cssStyle, Predicate<T> itemWithGlyphFilter) {
		if (glyph != null && itemWithGlyphFilter != null) {
			glyph(t -> itemWithGlyphFilter.test(t) ? Optional.of(new Pair<>(glyph, cssStyle)) : Optional.empty());

		}
		return this;
	}

	public ComboBoxCustomizer<T> glyph(String cssStyle, Predicate<T> itemWithGlyphFilter) {
		return glyph(this.emphasizedGlyph, cssStyle, itemWithGlyphFilter);
	}

	public ComboBoxCustomizer<T> glyph(Function<T, Optional<Pair<FontAwesome.Glyph, String>>> glyphStyleFunction) {
		this.glyphStyleFunction = glyphStyleFunction;
		return this;
	}

	public ComboBoxCustomizer<T> hideClearButton() {
		this.hideClearButton = true;
		return this;
	}

	public void customize() {
		initCellFactory();
		fillComboBoxAsAutocompleted();
		if (toString != defaultToString || glyphStyleFunction != null || items != null)
			initStringConverter();
		if (!hideClearButton)
			switchClearOnDoubleClick();
	}

	// ************** PRIVATE

	private void initStringConverter() {
		StringConverter<T> stringConverter = new StringConverter<T>() {
			@Override
			public T fromString(String string) {

				if (string == null || string.trim().isEmpty() || comboBox.getItems() == null)
					return null;
				else {
					Stream<T> stream;

					if (items == null)
						stream = comboBox.getItems().parallelStream();
					else
						stream = items.parallelStream();

					return stream.filter(o -> toString(o).equals(string)).findAny().orElse(null);
				}
			}

			@Override
			public String toString(T o) {
				if (o == null)
					return "";
				else
					return toString.apply(o);
			}
		};

		comboBox.setConverter(stringConverter);
	}

	private void initCellFactory() {
		if (extraColumnsFunction != null) {
			comboBox.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {

				@Override
				public ListCell<T> call(ListView<T> arg0) {
					return new ListCell<T>() {

						@Override
						protected void updateItem(T item, boolean empty) {
							super.updateItem(item, empty);

							setText("");
							setGraphic(null);
							setStyle(null);

							if (!empty) {
								HBox parent = Controls.createHBox(10.0, Pos.CENTER_LEFT);

								Label toStringLabel = new Label(toString.apply(item));

								if (emphasizedPredicate != null && emphasizedPredicate.test(item)) {
									toStringLabel.setStyle(EMPHASIZED_CSS_STYLE);
									toStringLabel
											.setGraphic(Glyphs.createGlyph(emphasizedGlyph, emphasizedGlyphCssStyle));
								} else if (deemphasizedPredicate != null && deemphasizedPredicate.test(item))
									toStringLabel.setStyle(DE_EMPHASIZED_CSS_STYLE);

								setGlyphIfNecessary(item, toStringLabel);

								parent.getChildren().add(toStringLabel);

								List<Object> columnValues = extraColumnsFunction.apply(item);

								for (Object columnValue : columnValues) {
									Label columnLabel = new Label();

									if (columnValue != null) {
										columnLabel.setText(columnValue.toString());
										columnLabel.setStyle(CssStyle.TEXT_FILL_SHY + CssStyle.BACKGROUND_COLOR_WHITE);
									}

									parent.getChildren().add(columnLabel);
								}

								setGraphic(parent);
							}

						}
					};
				}
			});
		} else if (toString != defaultToString || glyphStyleFunction != null || emphasizedPredicate != null
				|| deemphasizedPredicate != null) {
			comboBox.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {

				@Override
				public ListCell<T> call(ListView<T> arg0) {
					return new ListCell<T>() {

						@Override
						protected void updateItem(T item, boolean empty) {
							super.updateItem(item, empty);

							setText(empty || item == null ? "" : toString.apply(item));
							setGraphic(null);
							setStyle(null);

							if (!empty && item != null) {
								if (emphasizedPredicate != null && emphasizedPredicate.test(item)) {
									setStyle(EMPHASIZED_CSS_STYLE);
									setGraphic(Glyphs.createGlyph(emphasizedGlyph, emphasizedGlyphCssStyle));
								} else if (deemphasizedPredicate != null && deemphasizedPredicate.test(item))
									setStyle(DE_EMPHASIZED_CSS_STYLE);

								setGlyphIfNecessary(item, this);
							}
						}
					};
				}
			});
		}
	}

	private void setGlyphIfNecessary(T item, Labeled labeled) {
		if (glyphStyleFunction != null) {
			Optional<Pair<Glyph, String>> glyphAndStyle = glyphStyleFunction.apply(item);

			if (glyphAndStyle.isPresent())
				labeled.setGraphic(Glyphs.createGlyph(glyphAndStyle.get().getKey(), glyphAndStyle.get().getValue()));
		}
	}

	private void fillComboBoxAsAutocompleted() {

		if (items != null) {

			final Predicate<T> emptyTextPredicate;
			if (items.size() > ITEMS_SIZE_TO_CUT)
				emptyTextPredicate = o -> false;
			else
				emptyTextPredicate = o -> false; // o -> true;

			comboBox.setEditable(true);

			ObservableList<T> initialItems = null;
			Stream<T> initialItemsStream = items.parallelStream();
			if (emptyTextPredicate != null)
				initialItemsStream = initialItemsStream.filter(emptyTextPredicate);
			initialItems = FXCollections.observableArrayList(initialItemsStream.collect(Collectors.toList()));
			comboBox.setItems(initialItems);

			if (comboBox.getPromptText() != null && !comboBox.getPromptText().isEmpty()
					&& comboBox.getTooltip() == null) {
				comboBox.setTooltip(new Tooltip(comboBox.getPromptText()));
			}

			comboBox.getEditor().textProperty().addListener((ov, old_v, new_v) -> {
				comboBox.hide();

				Predicate<T> predicate = null;
				Comparator<? super T> comparator = null;

				if (StringUtils.isBlank(new_v)) {
					comboBox.setValue(null);
					predicate = emptyTextPredicate;
				} else {

					final String newValueLowerCase = new_v.toLowerCase();
					final String newValueCyrrilicLowerCase;

					final String firstLetter = newValueLowerCase.trim().substring(0, 1);
					if (!StringUtils.isCyryllicLetter(firstLetter)) {
						newValueCyrrilicLowerCase = StringUtils.convertKeyboardLayout(newValueLowerCase,
								KeyboardLayoutConvertationType.FROM_LATIN_TO_RU).toLowerCase();
					} else
						newValueCyrrilicLowerCase = null;

					if (StringUtils.isNotBlank(newValueCyrrilicLowerCase))
						predicate = o -> toString.apply(o).toLowerCase().contains(newValueLowerCase)
								|| toString.apply(o).toLowerCase().contains(newValueCyrrilicLowerCase);
					else
						predicate = o -> toString.apply(o).toLowerCase().contains(new_v.toLowerCase());

					comparator = (o1, o2) -> {
						int index_1 = toString.apply(o1).toLowerCase().indexOf(newValueLowerCase);
						if (index_1 == -1 && StringUtils.isNotBlank(newValueCyrrilicLowerCase))
							index_1 = toString.apply(o1).toLowerCase().indexOf(newValueCyrrilicLowerCase);

						int index_2 = toString.apply(o2).toLowerCase().indexOf(newValueLowerCase);
						if (index_2 == -1 && StringUtils.isNotBlank(newValueCyrrilicLowerCase))
							index_2 = toString.apply(o2).toLowerCase().indexOf(newValueCyrrilicLowerCase);

						int result = Integer.valueOf(index_1 == -1 ? 1000 : index_1)
								.compareTo(Integer.valueOf(index_2 == -1 ? 1000 : index_2));

						if (result == 0)
							result = toString.apply(o1).toLowerCase().compareTo(toString.apply(o2).toLowerCase());
						return result;
					};

					if (extraOrFilterFunction != null)
						predicate = predicate.or(o -> extraOrFilterFunction.apply(o, newValueLowerCase));

				}

				List<T> filteredItems;

				Stream<T> stream = items.parallelStream();
				if (predicate != null)
					stream = stream.filter(predicate);
				if (comparator != null)
					stream = stream.sorted(comparator);
				filteredItems = stream.collect(Collectors.toList());

				if (filteredItems.size() > ITEMS_SIZE_TO_CUT)
					filteredItems.subList(ITEMS_SIZE_TO_CUT, filteredItems.size()).clear();

				try {
					comboBox.getItems().clear();
					comboBox.getItems().addAll(filteredItems);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (comboBox.getValue() == null) {
					comboBox.show();
					comboBox.autosize();
				}

			});
		}
	}

	private void switchClearOnDoubleClick() {

		String tooltipText = Constant.get(Constant.Key.DOUBLE_CLICK_TO_CLEAR);
		Controls.addInfoRowTextToTooltip(comboBox, tooltipText);

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

}
