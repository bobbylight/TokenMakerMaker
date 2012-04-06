package org.fife.tmm;


public enum FloatLiteralFormat implements NumberFormat {

	FORMAT1("({Digit}+)(\".\"{Digit}+)?(e[+-]?{Digit}+)? | ({Digit}+)?(\".\"{Digit}+)(e[+-]?{Digit}+)?",
			"3475, 3475e12, .297, .297e-3, 5.2, 5.2e+8"),
	FORMAT2("({Digit}+)(\".\"{Digit}+)?(e[+-]?{Digit}+)?[fd]? | ({Digit}+)?(\".\"{Digit}+)(e[+-]?{Digit}+)?[fd]? | {Digit}+[fd]",
			"3475, 3475e12, .297, .297e-3, 5.2, 5.2e+8, 7234.19f, 7324.19e4d");

	private String format;
	private String sample;


	/**
	 * Constructor.
	 *
	 * @param format The format.
	 * @param sample A sample of text that this format would match.
	 */
	private FloatLiteralFormat(String format, String sample) {
		this.format = format;
		this.sample = sample;
	}


	public static FloatLiteralFormat getByFormat(String format) {
		for (FloatLiteralFormat flf : values()) {
			if (flf.getFormat().equals(format)) {
				return flf;
			}
		}
		return null;
	}


	public String getFormat() {
		return format;
	}


	public String getSample() {
		return sample;
	}


	public String toString() {
		return format;
	}


}