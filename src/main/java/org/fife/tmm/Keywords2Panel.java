package org.fife.tmm;


/**
 * A panel for editing the language's second set of keywords (if any).
 *
 * @author Robert Futrell
 * @version 1.0
 */
class Keywords2Panel extends KeywordsPanel {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public Keywords2Panel(TokenMakerMaker app) {
		super(app);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTokenMakerInfo(TokenMakerInfo info) {
		info.setKeywords2(keywordsTable.getWords());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(TokenMakerInfo info) {
		keywordsTable.setWords(info.getKeywords2());
	}


}