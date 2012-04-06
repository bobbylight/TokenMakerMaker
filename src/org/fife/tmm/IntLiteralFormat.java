package org.fife.tmm;


public enum IntLiteralFormat implements NumberFormat {

	FORMAT1("{Digit}+", "512"),
	FORMAT2("{Digit}+[lL]?", "512, 512L");

	private String format;
	private String sample;


	/**
	 * Constructor.
	 *
	 * @param format The format.
	 * @param sample A sample of text that this format would match.
	 */
	private IntLiteralFormat(String format, String sample) {
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


	public String toString() {
		return format;
	}


}