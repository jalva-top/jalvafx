package top.jalva.jalvafx.util;

import java.util.ResourceBundle;

public class Constant {

	private static String get(String key) {
		return ResourceBundle.getBundle("jalvafxConstants").getString(key);
	}

	public static String get(Key key) {
		return get(key.getFullKey());
	}

	public static String getUpper(Key key) {
		return get(key).toUpperCase();
	}

	public static String getLower(Key key) {
		return get(key).toLowerCase();
	}

	public static enum Key {

		TODAY("word.today"), 
		
		TOMORROW("word.tomorrow"), 
		
		YESTERDAY("word.yesterday"), 
		
		NOTHING_FOUND("nothingFound"), 
		
		DATA_LOAD("dataLoad"), 
		
		ERROR_HAPPENED("errorHappened"), 
		
		COPY("word.copy"), 
		
		PASTE("word.paste"), 
		
		CLEAR("word.clear"), 
		
		SEARCH_HISTORY("searchHistory"), 
		
		ITEMS("word.items"), 
		
		BOXES("word.boxes"), 
		
		DOUBLE_CLICK_TO_CLEAR("doubleClickToClear"), 
		
		WARNING("word.warning"), COPY_TO_CLIPBOARD("copyToClipboard"), 
		
		SAVE("word.save"), CANCEL("word.cancel"), 
		
		YES("word.yes"), 
		
		NO("word.no"), 
		
		SET("word.set"), 
		
		CURRENT_MOTH_BEGIN("currentMonthBegin"), 
		
		CURRENT_MOTH_END("currentMonthEnd"), 
		
		PREVIOUS_MOTH_BEGIN("previousMonthBegin"), 
		
		PREVIOUS_MOTH_END("previousMonthEnd"), 
		
		CLICK_TO_COPY_TO_CLIPBOARD("clickToCopyToClipboard"), 
		
		CHECK_ALL("checkAll"), UNCHECK_ALL("uncheckAll"), 
		
		MAX_ALLOWED_TEXT_LENGTH("maxAllowedTextLength"), 
		
		CURRENT_TEXT_LENGTH("currentTextLength"), 
		
		TEXT_EDIT("textEdit"), 
		
		VALUE_IS_ALLOWED_TO_BE("valueIsAllowedToBe"), 
		
		NO_LESS_THAN("noLessThan"), 
		
		NO_GREATER_THAN("noGreaterThan"), 
		
		NUMBER_EDIT("numberEdit"), 
		
		ENTERED_VALUE_IS_INVALID("enteredValueIsInvalid"),
		
		HOURS("word.hours"), 
		
		MINUTES("word.minutes"), 
		
		AND("word.and"), 
		
		INCORRECT_TIME_VALUE_VALUE_SHOULD_BE_BETWEEN("incorrectTimeValue.valueShouldBeBetween"), 
		
		OPT_FOR_A_DATE_NO_EARLIER_THAN("optForADateNoEarlierThan"), 
		
		OPT_FOR_A_DATE_NO_LATER_THAN("optForADateNoLaterThan"), 
		
		CLICK_ON_VALUE_TO_EDIT_CELL("clickOnValueToEditCell"), 
		
		COPY_TABLE("copyTable"), 
		
		COPY_ROW("copyRow"), 
		
		COLUMN_VALUE_SUM("columnValueSum"), 
		
		ALL_CELLS_SUM("allCellsSum"), 
		
		CURRENT_COLUMN_IS_NOT_VISIBLE("currentColumnIsNotVisible"), 
		
		ERROR("word.error"), 
		
		EXCEPTION_DESCRIPTION("exceptionDescription"), 
		
		AN_ERROR_OCCURED_DURING_THE_LAST_OPERATION("anErrorOccuredDuringTheLastOperation"), 
		
		INFORMATION("word.information");

		String fullKey;

		private Key(String fullKey) {
			this.fullKey = fullKey;
		}

		public String getFullKey() {
			return fullKey;
		}
	}

}
