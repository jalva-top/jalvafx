package top.jalva.jalvafx.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

	public static final String SYSTEM_MESSAGE_BLOCK_BEGIN_SYMBOL = "[";
	public static final String SYSTEM_MESSAGE_BLOCK_END_SYMBOL = "]";

	public static String firstLetterToUpper(String string) {
		if (string.length() > 0) {
			string = string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
		}

		return string;
	}

	public static String firstLetterToUpperOtherToLower(String string) {
		if (string.length() > 0) {
			string = string.substring(0, 1).toUpperCase() + string.substring(1, string.length()).toLowerCase();
		}

		return string;
	}

	public static String firstLetterOfEachWordToUpperOtherToLower(String string) {
		String result = "";

		for (int i = 0; i < string.length(); i++) {
			char curChar = string.charAt(i);
			if (i == 0 && isLetter(curChar))
				result += (curChar + "").toUpperCase();
			else {
				char prevChar = string.charAt(i - 1);
				if (isLetter(curChar) && !isLetterOrApostrophe(prevChar))
					result += (curChar + "").toUpperCase();
				else
					result += (curChar + "").toLowerCase();
			}
		}

		return result;
	}

	public static Boolean isLetter(char ch) {
		return Character.isLetter(ch) || isCyryllicLetter(ch + "");
	}

	public static Boolean isLetterOrApostrophe(char ch) {
		return isLetter(ch) || ch == '\'';
	}

	public static String removeFirstAndLastQuatationMarks(String string) {
		string = string.trim();
		char firstChar = string.charAt(0);
		char lastChar = string.charAt(string.length() - 1);

		if ((firstChar == '\"' && lastChar == '\"') || (firstChar == '«' && lastChar == '»')) {
			string.substring(1, string.length() - 1);
		}

		return string;
	}

	public static String parsePhoneNumber(String string) {
		if (string.length() > 0) {
			string = string.replaceAll("[\\-()\\s]", "").toString();
		}

		return string;
	}

	public static Double parseDouble(String doubleAsString) throws NumberFormatException {
		Double number = null;
		number = Double.parseDouble(doubleAsString.replaceAll(",", ".").trim());
		return number;
	}

	public static Boolean isCyryllicLetter(CharSequence letter) {

		Boolean result = false;
		List<String> cyrillicLetters = ResourceBundleList.get("russianAlphabet.low");

		for (String l : cyrillicLetters) {
			if (l.toUpperCase().equals(letter) || l.toLowerCase().equals(letter)) {

				result = true;
				break;
			}
		}

		return result;
	}

	public static String parseEmailAddressConvertingCyrrilicKeyboardLayout(String email) {
		email = email.replace(" ", "").replace("\t", "").replace("\n", "");
		return convertKeyboardLayout(email, KeyboardLayoutConvertationType.PARCE_EMAIL);
	}

	public static String convertKeyboardLayout(String text, KeyboardLayoutConvertationType convertationType) {
		String res = "";

		List<Character> from = convertationType.getFromCharactersList();
		List<Character> to = convertationType.getToCharactersList();

		for (Character c : text.toCharArray()) {

			if (from.contains(c)) {
				Character tmp;
				tmp = to.get(from.indexOf(Character.toLowerCase(c)));
				if (Character.getType(c) == Character.UPPERCASE_LETTER)
					tmp = Character.toUpperCase(tmp);

				res += tmp;
			} else
				res += c;
		}

		return res;
	}

	public enum KeyboardLayoutConvertationType {

		FROM_LATIN_AND_UKR_TO_RU("qwertyuiopasdfghjklzxcvbnmіє", "йцукенгшщзфывапролдячсмитьыэ"), FROM_LATIN_TO_RU(
				"qwertyuiopasdfghjklzxcvbnm", "йцукенгшщзфывапролдячсмить"), FROM_CYRRILIC_TO_LATIN(
						"йцукенгшщзхъфывапролджэячсмитьбюіє", "qwertyuiop[]asdfghjkl;'zxcvbnm,.s'"), PARCE_EMAIL(
								"йцукенгшщзфывапролдячсмитьюі\"", "qwertyuiopasdfghjklzxcvbnm.s@");

		List<Character> from;
		List<Character> to;

		KeyboardLayoutConvertationType(String fromLetters, String toLetters) {
			from = fromLetters.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
			to = toLetters.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		}

		public List<Character> getFromCharactersList() {
			return from;
		}

		public List<Character> getToCharactersList() {
			return to;
		}

	}

	public static String removeStringBlocks(String string, String blockBeginSymbols, String blockEndSymbols) {

		if (string != null && string.contains(blockBeginSymbols) && string.contains(blockEndSymbols)) {
			int blockBegin = string.indexOf(blockBeginSymbols);
			int blockEnd = string.indexOf(blockEndSymbols);

			String block = string.substring(blockBegin, blockEnd + 1);

			string = string.replace(block, "").trim();
		}

		return string;
	}

	public static BigDecimal parseBigDecimal(String string) {
		return BigDecimal.valueOf(parseDouble(string));
	}

	public static boolean isBlank(String string) {
		return string == null || string.trim().isEmpty();
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	public static String transliterateFromCyrrilicToLatin(String text) {
		List<Character> abcCyr = Arrays.asList('а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м',
				'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б',
				'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х',
				'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я');

		List<String> abcLat = Arrays.asList("a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n",
				"o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B",
				"V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F",
				"H", "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja");

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			Character ch = text.charAt(i);
			int index = abcCyr.indexOf(ch);

			if (index >= 0)
				builder.append(abcLat.get(index));
			else
				builder.append(ch);

		}
		return builder.toString();
	}

}
