package top.jalva.jalvafx.node;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final Logger log = LoggerFactory.getLogger(ComboBoxCustomizer.class.getName());

	static final int ITEMS_SIZE_TO_CUT = 100;
	static final String EMPHASIZED_CSS_STYLE = top.jalva.jalvafx.style.CssStyle.TEXT_BOLD;
	static final String DE_EMPHASIZED_CSS_STYLE = CssStyle.TEXT_FILL_SHY_LIGHT;

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
	int delayBeforeSearch_millis = 0;
	LocalTime lastSearch = LocalTime.now();

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

	public ComboBoxCustomizer<T> delayBeforeSearch(int milliseconds){
		this.delayBeforeSearch_millis = milliseconds;
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
			setExtraColumnsCellFactory();
		} else if (toString != defaultToString || glyphStyleFunction != null || emphasizedPredicate != null
				|| deemphasizedPredicate != null) {
			setCellFactory();
		}
	}

	private void setCellFactory() {
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
							setEmphasizedDeemphasizedStyleIfNecessary(this, item);
							setGlyphIfNecessary(this, item);
						}
					}
				};
			}
		});
	}

	private void setEmphasizedDeemphasizedStyleIfNecessary(Labeled labeled, T item) {
		if (emphasizedPredicate != null && emphasizedPredicate.test(item)) {
			labeled.setStyle(EMPHASIZED_CSS_STYLE);
			labeled.setGraphic(Glyphs.createGlyph(emphasizedGlyph, emphasizedGlyphCssStyle));
		} else if (deemphasizedPredicate != null && deemphasizedPredicate.test(item))
			labeled.setStyle(DE_EMPHASIZED_CSS_STYLE);
	}

	private void setExtraColumnsCellFactory() {
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

							setEmphasizedDeemphasizedStyleIfNecessary(toStringLabel, item);
							setGlyphIfNecessary(toStringLabel, item);

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
	}

	private void setGlyphIfNecessary(Labeled labeled, T item) {
		if (glyphStyleFunction != null) {
			Optional<Pair<Glyph, String>> glyphAndStyle = glyphStyleFunction.apply(item);

			if (glyphAndStyle.isPresent())
				labeled.setGraphic(Glyphs.createGlyph(glyphAndStyle.get().getKey(), glyphAndStyle.get().getValue()));
		}
	}

	private void fillComboBoxAsAutocompleted() {

		if (items != null) {

			final Predicate<T> emptyTextPredicate;

			if (items.size() > ITEMS_SIZE_TO_CUT) {
				emptyTextPredicate = o -> false;
			} else {
				emptyTextPredicate = o -> false; // o -> true;
			}

			comboBox.setEditable(true);

			ObservableList<T> initialItems = null;
			Stream<T> initialItemsStream = items.parallelStream();
			if (emptyTextPredicate != null) {
				initialItemsStream = initialItemsStream.filter(emptyTextPredicate);
			}

			initialItems = FXCollections.observableArrayList(initialItemsStream.collect(Collectors.toList()));
			comboBox.setItems(initialItems);

			if (comboBox.getPromptText() != null && !comboBox.getPromptText().isEmpty()
					&& comboBox.getTooltip() == null) {
				comboBox.setTooltip(new Tooltip(comboBox.getPromptText()));
			}

			comboBox.getEditor().textProperty().addListener((ov, old_v, new_v) -> {
				if(timeToSearch()) search(emptyTextPredicate, new_v);
			});
		}
	}

	private boolean timeToSearch(){	
		boolean result = false;

		if(delayBeforeSearch_millis == 0) result = true;
		else{
			LocalTime currenSearch = LocalTime.now();
			lastSearch = currenSearch;
			try {
				TimeUnit.MILLISECONDS.sleep(delayBeforeSearch_millis);
			} catch (InterruptedException e) {
				log.error("Error occured during delay before search", e);
			}

			if(lastSearch == currenSearch){
				result = true;
			}		
		}

		return result;
	}

	private void search(final Predicate<T> emptyTextPredicate, String newText) {
		comboBox.hide();

		if(comboBox.getValue() != null) {
			return;
		}

		Predicate<T> predicate = null;
		Comparator<? super T> comparator = null;

		if (StringUtils.isBlank(newText)) {
			comboBox.setValue(null);
			predicate = emptyTextPredicate;
		} else {

			final String newValueLowerCase = newText.toLowerCase();
			final String newValueCyrrilicLowerCase = convertToCyrrylicLowerCaseIfAccessible(newValueLowerCase);

			predicate = generatePredicate(newValueLowerCase, newValueCyrrilicLowerCase);
			comparator = generateComparator(newValueLowerCase, newValueCyrrilicLowerCase);

			if (extraOrFilterFunction != null) {
				predicate = predicate.or(o -> extraOrFilterFunction.apply(o, newValueLowerCase));
			}

		}

		List<T> filteredItems = getFilteredItems(predicate, comparator);

		comboBox.getItems().clear();
		comboBox.getItems().addAll(filteredItems);

		if (comboBox.getValue() == null) {
			comboBox.show();
			comboBox.autosize();
		}

		log.trace("ComboBoxCustomizer items filtered by entered text '{}' ", newText);
	}

	private List<T> getFilteredItems(Predicate<T> predicate, Comparator<? super T> comparator) {
		List<T> filteredItems;

		Stream<T> stream = items.parallelStream();
		if (predicate != null)
			stream = stream.filter(predicate);
		if (comparator != null)
			stream = stream.sorted(comparator);
		filteredItems = stream.collect(Collectors.toList());

		if (filteredItems.size() > ITEMS_SIZE_TO_CUT)
			filteredItems.subList(ITEMS_SIZE_TO_CUT, filteredItems.size()).clear();
		return filteredItems;
	}

	private Comparator<? super T> generateComparator(final String newValueLowerCase,
			final String newValueCyrrilicLowerCase) {
		Comparator<? super T> comparator;
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
		return comparator;
	}

	private Predicate<T> generatePredicate(final String newValueLowerCase, final String newValueCyrrilicLowerCase) {
		Predicate<T> predicate;

		if (StringUtils.isNotBlank(newValueCyrrilicLowerCase)) {
			predicate = o -> toString.apply(o).toLowerCase().contains(newValueLowerCase)
					|| toString.apply(o).toLowerCase().contains(newValueCyrrilicLowerCase);
		}
		else {
			predicate = o -> toString.apply(o).toLowerCase().contains(newValueLowerCase);
		}

		return predicate;
	}

	private String convertToCyrrylicLowerCaseIfAccessible(final String text) {
		final String newValueCyrrilicLowerCase;

		final String firstLetter = text.trim().substring(0, 1);
		if (!StringUtils.isCyryllicLetter(firstLetter)) {
			newValueCyrrilicLowerCase = StringUtils.convertKeyboardLayout(text,
					KeyboardLayoutConvertationType.FROM_LATIN_TO_RU).toLowerCase();
		} else
			newValueCyrrilicLowerCase = null;
		return newValueCyrrilicLowerCase;
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
