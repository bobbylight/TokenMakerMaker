package org.fife.tmm;

/**
 * Format for integer literals.
 */
public enum IntLiteralFormat implements NumberFormat {

	FORMAT1("{Digit}+", "512"),
	FORMAT2("{Digit}+[lL]?", "512, 512L"),
	FORMAT3("({Digit}|\"_\")+", "512, 1_000"),
	FORMAT4("({Digit}|\"_\")+[lL]?", "512, 512L, 1_000, 1_000L");

	private String format;
	private String sample;


	/**
	 * Constructor.
	 *
	 * @param format The format.
	 * @param sample A sample of text that this format would match.
	 */
	IntLiteralFormat(String format, String sample) {
		this.format = format;
		this.sample = sample;
	}


	public static IntLiteralFormat getByFormat(String format) {
		for (IntLiteralFormat ilf : values()) {
			if (ilf.getFormat().equals(format)) {
				return ilf;
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


	@Override
	public String toString() {
		return format;
	}


}
