package de.lbmaster.dayztoolbox.utils;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

public class NumberFormatterOverride extends NumberFormatter {

	private static final long serialVersionUID = 1L;

	public NumberFormatterOverride(NumberFormat format) {
		super(format);
	}

	@Override
	public Object stringToValue(String text) throws ParseException {
		if (text.length() <= 0)
			return null;
		return super.stringToValue(text);
	}

}
