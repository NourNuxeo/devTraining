package org.nuxeo.uni.util;

import java.text.NumberFormat;

import org.nuxeo.uni.behavior.Valuable;

public class ProductUtils {
	public static NumberFormat nf = NumberFormat.getCurrencyInstance();
	public static String getFormattedPrice(Valuable v) {
		return nf.format(v.getPrice());
	}
}
