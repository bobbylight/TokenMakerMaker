package org.fife.tmm;


public enum HexLiteralFormat implements NumberFormat {

	FORMAT1("0x{HexDigit}+", "0x0af"),
	FORMAT2("0x{HexDigit}+[lL]?", "0x0af, 0x0afL");

	private String format;
	private String sample;


	/**
	 * Constructor.
	 *
	 * @param format The format.
	 * @param sample A sample of text that this format would match.
	 */
	private HexLiteralFormat(String format, String sample) {
		this.format = format;
		this.sample = sample;
	}


	public static HexLiteralFormat getByFormat(String format) {
		for (HexLiteralFormat hlf : values()) {
			if (hlf.getFormat().equals(format)) {
				return hlf;
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