package top.jalva.jalvafx.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class NumberUtils {

	/*
	 * because of Value-added tax (20%). [Price with tax] 120 $ => [price
	 * without tax] 100 $. If [price with tax] is not multiple of 6, [price
	 * without tax] can has infinite continued fraction. (example: 100 $ =>
	 * 83.33333333333... $)
	 */
	final static int PRICE_MULTIPLICITY = 6;

	public static BigDecimal roundPrice(BigDecimal price) {
		long cents = (long) (price.doubleValue() * 100);
		long roundedCents = (cents / PRICE_MULTIPLICITY) * PRICE_MULTIPLICITY;
		if (cents > roundedCents)
			roundedCents += PRICE_MULTIPLICITY;

		return new BigDecimal(BigInteger.valueOf(roundedCents), 2).setScale(2, RoundingMode.HALF_UP);
	}

	public static BigDecimal createBigDecimalPrice(double price) {
		int cents = (int) Math.abs((price - 0) * 100);

		if (cents == 0)
			return BigDecimal.ZERO;
		return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
	}

	public static boolean fractionIsEmpty(Double number) {
		return number != null && Math.abs(number.intValue() - number) == 0;
	}
}
