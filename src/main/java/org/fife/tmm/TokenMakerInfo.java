package org.fife.tmm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.fife.io.UnicodeReader;


/**
 * Information about a token maker.  Can generate a flex source file that
 * represents an <code>RSyntaxTextArea TokenMakerMaker</code> class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TokenMakerInfo {

	private String pkg;
	private String className;
	private String extendedClass;
	private String classDoc;
	private boolean ignoreCase;
	private boolean booleanLiterals;

	private boolean lineCommentsEnabled;
	private String lineCommentStart;
	private boolean mlcsEnabled;
	private String mlcStart;
	private String mlcEnd;
	private boolean docCommentsEnabled;
	private String docCommentStart;
	private String docCommentEnd;

	private IntLiteralFormat intLiteralFormat;
	private HexLiteralFormat hexLiteralFormat;
	private FloatLiteralFormat floatLiteralFormat;

	private List<String> keywords;
	private List<String> keywords2;
	private List<String> dataTypes;
	private List<String> functions;
	private List<String> operators;

	private boolean stringsEnabled;
	private boolean stringsMultiLine;
	private boolean charsEnabled;
	private boolean charsMultiLine;
	private boolean backticksEnabled;

	private Map<String, String> flexValuesMap;

	private final Pattern p = Pattern.compile("@[\\w\\d\\.]+@");
	private static final String FILE_ENCODING		= "UTF-8";

	private static final String toEscapeInCharClasses = "|(){}[]<>\\.*+?^$/\"~!";

	private static final String ATTR_ENABLED			= "enabled";
	private static final String ATTR_END				= "end";
	private static final String ATTR_MULTI_LINE			= "multiLine";
	private static final String ATTR_START				= "start";

	private static final String ELEM_BACKTICKS			= "backtickLiterals";
	private static final String ELEM_CHARS				= "charLiterals";
	private static final String ELEM_CLASS_COMMENT		= "classComment";
	private static final String ELEM_CLASS_NAME			= "className";
	private static final String ELEM_COMMENTS			= "comments";
	private static final String ELEM_DATA_TYPE			= "dataType";
	private static final String ELEM_DATA_TYPES			= "dataTypes";
	private static final String ELEM_DOC_COMMENT		= "docComments";
	private static final String ELEM_EXTENDED_CLASS		= "extendedClass";
	private static final String ELEM_FUNCTION			= "function";
	private static final String ELEM_FUNCTIONS			= "functions";
	private static final String ELEM_GENERAL			= "general";
	private static final String ELEM_IGNORE_CASE		= "ignoreCase";
	private static final String ELEM_BOOLEAN_LITERALS	= "booleanLiterals";
	private static final String ELEM_KEYWORD			= "keyword";
	private static final String ELEM_KEYWORDS			= "keywords";
	private static final String ELEM_KEYWORDS_2			= "keywords2";
	private static final String ELEM_MLC				= "multiLineComments";
	private static final String ELEM_LINE_COMMENT		= "lineComments";
	private static final String ELEM_NUMBERS			= "numbers";
	private static final String ELEM_NUMBERS_FLOAT		= "floatLiteral";
	private static final String ELEM_NUMBERS_INT		= "intLiteral";
	private static final String ELEM_NUMBERS_HEX		= "hexLiteral";
	private static final String ELEM_OPERATOR			= "operator";
	private static final String ELEM_OPERATORS			= "operators";
	private static final String ELEM_PACKAGE			= "package";
	private static final String ELEM_STRINGS			= "stringLiterals";
	private static final String ELEM_TOKEN_MAKER_DEF	= "TokenMakerDefinition";


	public TokenMakerInfo() {
		flexValuesMap = new HashMap<String, String>();
	}


	private static void appendFromModel(StringBuilder sb, List<String> list) {
		for (int i=0; i<list.size(); i++) {
			String text = list.get(i);
			sb.append('\"').append(possiblyEscapeStringChars(text)).append('\"');
			if (i<list.size()-1) {
				sb.append(" |\n");
			}
		}
	}


	public File createFlexFile(File outputDir) throws IOException {

		createFlexValuesMap();

		// Write .flex file into source directory, including package
		String subdir = getPackage();
		if (subdir!=null) {
			subdir = subdir.replace('.', '/');
			outputDir = new File(outputDir, subdir);
			if (!outputDir.isDirectory() && !outputDir.mkdirs()) {
				throw new IOException("Can't create output directory: " +
						outputDir.getAbsolutePath());
			}
		}

		File file = new File(outputDir, getClassName() + ".flex");
		PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);

		InputStream in = getClass().getResourceAsStream("Template.flex");
		BufferedReader r = new BufferedReader(new InputStreamReader(in));

		String line = null;
		while ((line=r.readLine())!=null) {
			line = replaceTokens(line);
			w.println(line);
		}

		r.close();
		w.close();

		return file;

	}


	private void createFlexValuesMap() throws IOException {

		flexValuesMap.clear();
		final String newline = File.separatorChar=='\\' ? "\r\n" : "\n";

		flexValuesMap.put("date", new SimpleDateFormat().format(new Date()));

		flexValuesMap.put("package.line", pkg.length()>0 ? ("package " + getPackage() + ";") : "");

		flexValuesMap.put("class.comment", getClassDoc());
		flexValuesMap.put("class.name", getClassName());
		flexValuesMap.put("extended.class", getExtendedClass());

		flexValuesMap.put("possibly.ignore.case", getIgnoreCase() ?
							"%ignorecase" : "/* Case sensitive */");

		flexValuesMap.put("possible.booleanLiteral.macro",
				getBooleanLiterals() ? "BooleanLiteral				= (\"true\"|\"false\")" : "");
		flexValuesMap.put("possible.booleanLiteral.state",
				getBooleanLiterals() ? "{BooleanLiteral}			{ addToken(Token.LITERAL_BOOLEAN); }" : "");

		if (getMultilineCommentsEnabled()) {
			String mlcStart = getMultilineCommentStart();
			String mlcEnd = getMultilineCommentEnd();
			int mlcStartLen = mlcStart.length();
			int mlcEndLen = mlcEnd.length();
			flexValuesMap.put("possible.mlc.macros",
					"MLCBegin					= \"" + mlcStart + "\"\n" +
					"MLCEnd					= \"" + mlcEnd + "\"\n");
			flexValuesMap.put("possible.mlc.check",
					"{MLCBegin}	{ start = zzMarkedPos-" + mlcStartLen + "; yybegin(MLC); }");
			char ch = mlcEnd.charAt(0);
			String temp = Character.toString(ch);
			if (toEscapeInCharClasses.indexOf(ch)>-1) {
				temp = "\\" + temp;
			}
			flexValuesMap.put("mlc.end.first.char", temp);
			flexValuesMap.put("mlc.start.length", Integer.toString(mlcStartLen));
			flexValuesMap.put("mlc.end.length", Integer.toString(mlcEndLen));
			flexValuesMap.put("possible.mlc.state", loadResourceText("org/fife/tmm/mlc.state.txt"));
			flexValuesMap.put("possible.mlc.state.definition", "%state MLC");
			flexValuesMap.put("possible.mlc.switch.case", loadResourceText("org/fife/tmm/mlc.switch.case.txt"));
		}
		else {
			flexValuesMap.put("possible.mlc.macros", "/* No multi-line comments */");
			flexValuesMap.put("possible.mlc.check", "/* No multi-line comments */");
			flexValuesMap.put("mlc.end.first.char", "*"); // Value doesn't matter
			flexValuesMap.put("mlc.start.length", "0");
			flexValuesMap.put("mlc.end.length", "0");
			flexValuesMap.put("possible.mlc.state", "/* No multi-line comment state */");
			flexValuesMap.put("possible.mlc.state.definition", "/* No MLC state */");
			flexValuesMap.put("possible.mlc.switch.case", "/* No multi-line comments */");
		}

		if (getDocCommentsEnabled()) {
			String docStart = getDocCommentStart();
			String docEnd = getDocCommentEnd();
			int docStartLen = docStart.length();
			int docEndLen = docEnd.length();
			flexValuesMap.put("possible.doc.macros",
					"DocCommentBegin	= \"" + docStart + "\"\n" +
					"DocCommentEnd		= \"" + docEnd + "\"\n");
			flexValuesMap.put("possible.doc.check",
					"{DocCommentBegin}	{ start = zzMarkedPos-" + docStartLen + "; yybegin(DOCCOMMENT); }");
			char ch = docEnd.charAt(0);
			String temp = Character.toString(ch);
			if (toEscapeInCharClasses.indexOf(ch)>-1) {
				temp = "\\" + temp;
			}
			flexValuesMap.put("doc.end.first.char", temp);
			flexValuesMap.put("doc.start.length", Integer.toString(docStartLen));
			flexValuesMap.put("doc.end.length", Integer.toString(docEndLen));
			flexValuesMap.put("possible.doc.state", loadResourceText("org/fife/tmm/doc.state.txt"));
			flexValuesMap.put("possible.doc.state.definition", "%state DOCCOMMENT");
			flexValuesMap.put("possible.doc.switch.case", loadResourceText("org/fife/tmm/doc.switch.case.txt"));
		}
		else {
			flexValuesMap.put("possible.doc.macros", "/* No documentation comments */");
			flexValuesMap.put("possible.doc.check", "/* No documentation comments */");
			flexValuesMap.put("doc.end.first.char", "*"); // Value doesn't matter
			flexValuesMap.put("doc.start.length", "0");
			flexValuesMap.put("doc.end.length", "0");
			flexValuesMap.put("possible.doc.state", "/* No documentation comment state */");
			flexValuesMap.put("possible.doc.state.definition", "/* No documentation comment state */");
			flexValuesMap.put("possible.doc.switch.case", "/* No documentation comments */");
		}

		if (getLineCommentsEnabled()) {
			String lineCommentStart = getLineCommentStart();
			int eolStartLen = lineCommentStart.length();
			flexValuesMap.put("possible.eol.macro", "LineCommentBegin			= \"" + lineCommentStart + "\"");
			flexValuesMap.put("possible.eol.check", "{LineCommentBegin}			{ start = zzMarkedPos-" + eolStartLen + "; yybegin(EOL_COMMENT); }");
			flexValuesMap.put("possible.eol.state", loadResourceText("org/fife/tmm/eol.state.txt"));
			flexValuesMap.put("possible.eol.state.definition", "%state EOL_COMMENT");
			flexValuesMap.put("line.comment.start.end.body", "return new String[] { \"" + lineCommentStart + "\", null };");
		}
		else {
			flexValuesMap.put("possible.eol.macro", "/* No line comments */");
			flexValuesMap.put("possible.eol.check", "/* No line comments */");
			flexValuesMap.put("possible.eol.state", "/* No line comment state */");
			flexValuesMap.put("possible.eol.state.definition", "/* No line comment state */");
			flexValuesMap.put("line.comment.start.end.body", "return null;");
		}

		StringBuilder keywords = new StringBuilder();
		List<String> keywordList = getKeywords();
		if (keywordList!=null && keywordList.size()>0) {
			appendFromModel(keywords, keywordList);
			keywords.append("		{ addToken(Token.RESERVED_WORD); }");
		}
		else {
			keywords.append("/* No keywords */");
		}
		flexValuesMap.put("keywords", keywords.toString());

		StringBuilder keywords2 = new StringBuilder();
		List<String> keyword2List = getKeywords2();
		if (keyword2List!=null && keyword2List.size()>0) {
			appendFromModel(keywords2, keyword2List);
			keywords2.append("		{ addToken(Token.RESERVED_WORD_2); }");
		}
		else {
			keywords2.append("/* No keywords 2 */");
		}
		flexValuesMap.put("keywords2", keywords2.toString());

		StringBuilder dataTypes = new StringBuilder();
		List<String> dtList = getDataTypes();
		if (dtList!=null && dtList.size()>0) {
			appendFromModel(dataTypes, dtList);
			dataTypes.append("		{ addToken(Token.DATA_TYPE); }");
		}
		else {
			dataTypes.append("/* No data types */");
		}
		flexValuesMap.put("data.types", dataTypes.toString());

		StringBuilder functions = new StringBuilder();
		List<String> funcList = getFunctions();
		if (funcList!=null && funcList.size()>0) {
			appendFromModel(functions, funcList);
			functions.append("		{ addToken(Token.FUNCTION); }");
		}
		else {
			functions.append("/* No functions */");
		}
		flexValuesMap.put("functions", functions.toString());

		if (getCharsEnabled()) {
			if (getCharsMultiLine()) {
				flexValuesMap.put("possible.char.macros", "/* No char macros */");
				flexValuesMap.put("possible.char.check",
					"\\'							{ start = zzMarkedPos-1; yybegin(CHAR); }");
				flexValuesMap.put("possible.char.state.definition", "%state CHAR");
				flexValuesMap.put("possible.char.state", loadResourceText("org/fife/tmm/escapableChars.state.txt"));
				flexValuesMap.put("possible.char.switch.case", loadResourceText("org/fife/tmm/char.switch.case.txt"));
			}
			else {
				flexValuesMap.put("possible.char.macros",
					"CharLiteral	= ([\\\\']({AnyCharacterButApostropheOrBackSlash}|{Escape})[\\\\'])" + newline +
					"UnclosedCharLiteral			= ([\\\\'][^\\\\'\\\\n]*)" + newline +
					"ErrorCharLiteral			= ({UnclosedCharLiteral}[\\\\'])");
				flexValuesMap.put("possible.char.check",
					"{CharLiteral}				{ addToken(Token.LITERAL_CHAR); }" + newline +
					"{UnclosedCharLiteral}		{ addToken(Token.ERROR_CHAR); addNullToken(); return firstToken; }" + newline +
					"{ErrorCharLiteral}			{ addToken(Token.ERROR_CHAR); }");
				flexValuesMap.put("possible.char.state.definition", "/* No char state */");
				flexValuesMap.put("possible.char.state", "/* No char state */");
				flexValuesMap.put("possible.char.switch.case", "/* No char state */");
			}
		}
		else {
			flexValuesMap.put("possible.char.macros", "/* No char literals */");
			flexValuesMap.put("possible.char.check", "/* No char literals */");
			flexValuesMap.put("possible.char.state.definition", "/* No char state */");
			flexValuesMap.put("possible.char.state", "/* No char state */");
			flexValuesMap.put("possible.char.switch.case", "/* No char state */");
		}

		if (getStringsEnabled()) {
			if (getStringsMultiLine()) {
				flexValuesMap.put("possible.string.macros", "/* No string macros */");
				flexValuesMap.put("possible.string.check",
					"\\\\\"							{ start = zzMarkedPos-1; yybegin(STRING); }");
				flexValuesMap.put("possible.string.state.definition", "%state STRING");
				flexValuesMap.put("possible.string.state", loadResourceText("org/fife/tmm/escapableStrings.state.txt"));
				flexValuesMap.put("possible.string.switch.case", loadResourceText("org/fife/tmm/string.switch.case.txt"));
			}
			else {
				flexValuesMap.put("possible.string.macros",
					"StringLiteral				= ([\\\\\"]({AnyCharacterButDoubleQuoteOrBackSlash}|{Escape})*[\\\\\"])" + newline +
					"UnclosedStringLiteral		= ([\\\\\"]([\\\\\\\\].|[^\\\\\\\\\\\\\"])*[^\\\\\"]?)" + newline +
					"ErrorStringLiteral			= ({UnclosedStringLiteral}[\\\\\"])");
				flexValuesMap.put("possible.string.check",
					"{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }" + newline +
					"{UnclosedStringLiteral}		{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }" + newline +
					"{ErrorStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); }");
				flexValuesMap.put("possible.string.state.definition", "/* No string state */");
				flexValuesMap.put("possible.string.state", "/* No string state */");
				flexValuesMap.put("possible.string.switch.case", "/* No string state */");
			}
		}
		else {
			flexValuesMap.put("possible.string.macros", "/* No string literals */");
			flexValuesMap.put("possible.string.check", "/* No string literals */");
			flexValuesMap.put("possible.string.state.definition", "/* No string state */");
			flexValuesMap.put("possible.string.state", "/* No string state */");
			flexValuesMap.put("possible.string.switch.case", "/* No string state */");
		}

		if (intLiteralFormat!=null) {
			flexValuesMap.put("possible.int.literal.macro", "IntegerLiteral\t\t\t= (" + intLiteralFormat.getFormat() + ")");
			flexValuesMap.put("possible.int.literals", "{IntegerLiteral}\t\t\t\t{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }");
		}
		else {
			flexValuesMap.put("possible.int.literal.macro", "/* No int literals */");
			flexValuesMap.put("possible.int.literals", "/* No int literals */");
		}
		if (hexLiteralFormat!=null) {
			flexValuesMap.put("possible.hex.literal.macro", "HexLiteral\t\t\t= (" + hexLiteralFormat.getFormat() + ")");
			flexValuesMap.put("possible.hex.literals", "{HexLiteral}\t\t\t\t\t{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }");
		}
		else {
			flexValuesMap.put("possible.hex.literal.macro", "/* No hex literals */");
			flexValuesMap.put("possible.hex.literals", "/* No hex literals */");
		}
		if (floatLiteralFormat!=null) {
			flexValuesMap.put("possible.float.literal.macro", "FloatLiteral\t\t\t= (" + floatLiteralFormat.getFormat() + ")");
			flexValuesMap.put("possible.float.literals", "{FloatLiteral}\t\t\t\t\t{ addToken(Token.LITERAL_NUMBER_FLOAT); }");
		}
		else {
			flexValuesMap.put("possible.float.literal.macro", "/* No float literals */");
			flexValuesMap.put("possible.float.literals", "/* No float literals */");
		}
		StringBuilder sb = new StringBuilder();
		boolean numLiterals = false;
		if (intLiteralFormat!=null) {
			numLiterals = true;
			sb.append("({IntegerLiteral}");
		}
		if (hexLiteralFormat!=null) {
			sb.append(numLiterals ? "|" : "(");
			numLiterals = true;
			sb.append("{HexLiteral}");
		}
		if (floatLiteralFormat!=null) {
			sb.append(numLiterals ? "|" : "(");
			numLiterals = true;
			sb.append("{FloatLiteral}");
		}
		if (numLiterals) {
			sb.append("){NonSeparator}+");
			flexValuesMap.put("possible.number.error.macro", "ErrorNumberFormat\t\t\t= (" + sb.toString() + ")");
			flexValuesMap.put("possible.number.errors", "{ErrorNumberFormat}\t\t\t{ addToken(Token.ERROR_NUMBER_FORMAT); }");
		}
		else {
			flexValuesMap.put("possible.number.error.macro", "/* No number literals, so no error literal for them either */");
			flexValuesMap.put("possible.number.errors", "/* No number error literals */");
		}

		StringBuilder operators = new StringBuilder();
		List<String> opList = getOperators();
		if (opList!=null && opList.size()>0) {
			appendFromModel(operators, opList);
			operators.append("		{ addToken(Token.OPERATOR); }");
		}
		else {
			operators.append("/* No operators */");
		}
		flexValuesMap.put("possible.operators", operators.toString());

	}


	public boolean getBackticksEnabled() {
		return backticksEnabled;
	}


	public boolean getBooleanLiterals() {
		return booleanLiterals;
	}


	public boolean getCharsEnabled() {
		return charsEnabled;
	}


	public boolean getCharsMultiLine() {
		return charsMultiLine;
	}


	private static List<String> getChildElemTexts(Element elem, String childName) {
		NodeList childNodes = elem.getElementsByTagName(childName);
		int childCount = childNodes.getLength();
		List<String> values = new ArrayList<String>(childCount);
		for (int j=0; j<childCount; j++) {
			Element temp = (Element)childNodes.item(j);
			values.add(temp.getTextContent());
		}
		return values;
	}


	private static String getChildElemText(Element elem, String childName) {
		return elem.getElementsByTagName(childName).item(0).getTextContent();
	}


	public String getClassDoc() {
		return classDoc;
	}


	public String getClassName() {
		return className;
	}


	public List<String> getDataTypes() {
		return dataTypes;
	}


	public boolean getDocCommentsEnabled() {
		return docCommentsEnabled;
	}


	public String getDocCommentEnd() {
		return docCommentEnd;
	}


	public String getDocCommentStart() {
		return docCommentStart;
	}


	public String getExtendedClass() {
		return extendedClass;
	}


	public FloatLiteralFormat getFloatLiteralFormat() {
		return floatLiteralFormat;
	}


	public List<String> getFunctions() {
		return functions;
	}


	public HexLiteralFormat getHexLiteralFormat() {
		return hexLiteralFormat;
	}


	public boolean getIgnoreCase() {
		return ignoreCase;
	}


	public IntLiteralFormat getIntLiteralFormat() {
		return intLiteralFormat;
	}


	public List<String> getKeywords() {
		return keywords;
	}


	public List<String> getKeywords2() {
		return keywords2;
	}


	public boolean getLineCommentsEnabled() {
		return lineCommentsEnabled;
	}


	public String getLineCommentStart() {
		return lineCommentStart;
	}


	public boolean getMultilineCommentsEnabled() {
		return mlcsEnabled;
	}


	public String getMultilineCommentStart() {
		return mlcStart;
	}


	public String getMultilineCommentEnd() {
		return mlcEnd;
	}


	public List<String> getOperators() {
		return operators;
	}


	public String getPackage() {
		return pkg;
	}


	public boolean getStringsEnabled() {
		return stringsEnabled;
	}


	public boolean getStringsMultiLine() {
		return stringsMultiLine;
	}


	public static TokenMakerInfo load(File xmlFile) throws IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			//InputSource is = new InputSource(new FileReader(file));
			InputSource is = new InputSource(new UnicodeReader(
								new FileInputStream(xmlFile), FILE_ENCODING));
			is.setEncoding(FILE_ENCODING);
			doc = db.parse(is);//db.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
			String desc = e.getMessage();
			if (desc==null) {
				desc = e.toString();
			}
			throw new IOException("Error parsing XML: " + desc);
		}

		TokenMakerInfo info = new TokenMakerInfo();

		Element root = doc.getDocumentElement();

		NodeList childNodes = root.getChildNodes();
		int count = childNodes.getLength();

		for (int i=0; i<count; i++) {

			Node node = childNodes.item(i);
			int type = node.getNodeType();
			if (type==Node.ELEMENT_NODE) {

				Element elem = (Element)node;
				String elemName = elem.getNodeName();

				if (ELEM_GENERAL.equals(elemName)) {
					String pkg = getChildElemText(elem, ELEM_PACKAGE);
					String className = getChildElemText(elem, ELEM_CLASS_NAME);
					String extended = getChildElemText(elem, ELEM_EXTENDED_CLASS);
					String classDoc = getChildElemText(elem, ELEM_CLASS_COMMENT);
					boolean ignoreCase = Boolean.parseBoolean(getChildElemText(elem, ELEM_IGNORE_CASE));
					boolean booleanLiterals = Boolean.parseBoolean(getChildElemText(elem, ELEM_BOOLEAN_LITERALS));
					info.setPackage(pkg);
					info.setClassName(className);
					info.setExtendedClass(extended);
					info.setClassDoc(classDoc);
					info.setIgnoreCase(ignoreCase);
					info.setBooleanLiterals(booleanLiterals);
				}

				else if (ELEM_COMMENTS.equals(elemName)) {
					Element lineElem = (Element)elem.getElementsByTagName(ELEM_LINE_COMMENT).item(0);
					boolean enabled = Boolean.parseBoolean(lineElem.getAttribute(ATTR_ENABLED));
					info.setLineCommentsEnabled(enabled);
					info.setLineCommentStart(lineElem.getAttribute(ATTR_START));
					Element mlcElem = (Element)elem.getElementsByTagName(ELEM_MLC).item(0);
					enabled = Boolean.parseBoolean(mlcElem.getAttribute(ATTR_ENABLED));
					info.setMultilineCommentsEnabled(enabled);
					info.setMultilineCommentStart(mlcElem.getAttribute(ATTR_START));
					info.setMultilineCommentEnd(mlcElem.getAttribute(ATTR_END));
					Element docElem = (Element)elem.getElementsByTagName(ELEM_DOC_COMMENT).item(0);
					enabled = Boolean.parseBoolean(docElem.getAttribute(ATTR_ENABLED));
					info.setDocCommentsEnabled(enabled);
					info.setDocCommentStart(docElem.getAttribute(ATTR_START));
					info.setDocCommentEnd(docElem.getAttribute(ATTR_END));
				}

				else if (ELEM_KEYWORDS.equals(elemName)) {
					info.setKeywords(getChildElemTexts(elem, ELEM_KEYWORD));
				}

				else if (ELEM_KEYWORDS_2.equals(elemName)) {
					info.setKeywords2(getChildElemTexts(elem, ELEM_KEYWORD));
				}

				else if (ELEM_DATA_TYPES.equals(elemName)) {
					info.setDataTypes(getChildElemTexts(elem, ELEM_DATA_TYPE));
				}

				else if (ELEM_FUNCTIONS.equals(elemName)) {
					info.setFunctions(getChildElemTexts(elem, ELEM_FUNCTION));
				}

				else if (ELEM_NUMBERS.equals(elemName)) {
					String text = getChildElemText(elem, ELEM_NUMBERS_HEX);
					if (text!=null && text.length()>0) {
						info.hexLiteralFormat = HexLiteralFormat.getByFormat(text);
					}
					text = getChildElemText(elem, ELEM_NUMBERS_INT);
					if (text!=null && text.length()>0) {
						info.intLiteralFormat = IntLiteralFormat.getByFormat(text);
					}
					text = getChildElemText(elem, ELEM_NUMBERS_FLOAT);
					if (text!=null && text.length()>0) {
						info.floatLiteralFormat = FloatLiteralFormat.getByFormat(text);
					}
				}

				else if (ELEM_STRINGS.equals(elemName)) {
					boolean enabled = Boolean.parseBoolean(elem.getAttribute(ATTR_ENABLED));
					info.setStringsEnabled(enabled);
				}

				else if (ELEM_CHARS.equals(elemName)) {
					boolean enabled = Boolean.parseBoolean(elem.getAttribute(ATTR_ENABLED));
					info.setCharsEnabled(enabled);
				}

				else if (ELEM_BACKTICKS.equals(elemName)) {
					boolean enabled = Boolean.parseBoolean(elem.getAttribute(ATTR_ENABLED));
					info.setBackticksEnabled(enabled);
				}

				else if (ELEM_OPERATORS.equals(elemName)) {
					info.setOperators(getChildElemTexts(elem, ELEM_OPERATOR));
				}

			}

		}

		return info;

	}


	private String loadResourceText(String resource) throws IOException {

		StringBuilder sb = new StringBuilder();

		BufferedReader r = null;
		try {
			InputStream in = getClass().getResourceAsStream("/"+resource);
			if (in==null) { // Debugging in Eclipse
				in = new FileInputStream("src/" + resource);
			}
			r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line=r.readLine())!=null) {
				line = replaceTokens(line);
				sb.append(line).append('\n');
			}
		} finally {
			if (r!=null) {
				r.close();
			}
		}

		return sb.toString();

	}


	/**
	 * Escapes chars that need escaping to be in Strings in JFlex.
	 *
	 * @param s The string to possibly escape characters in.
	 * @return The string with characters possibly escaped.
	 */
	private static final String possiblyEscapeStringChars(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			char ch = s.charAt(i);
			if (ch=='"') {
				sb.append("\\\\");
			}
			else if (ch=='\\') {
				sb.append("\\\\\\");
			}
			sb.append(ch);
		}
		return sb.toString();
	}


	/**
	 * Replaces tokens in some text with their substituted values.
	 *
	 * @param text The text.
	 * @return The text, with <code>@tokens@</code> substituted.
	 */
	private String replaceTokens(String text) {

		if (text.indexOf('@')>=0) {
			Matcher m = p.matcher(text);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String token = m.group().substring(1, m.group().length()-1);
				String replacement = flexValuesMap.get(token);
				if (replacement==null) {
					replacement = "!UNEXPECTED_TOKEN_" + token + "!";
				}
				// Cannot quote replacement as that hoses our '\' chars, etc.
				//replacement = Matcher.quoteReplacement(replacement);
				m.appendReplacement(sb, replacement);
			}
			m.appendTail(sb);
			text = sb.toString();
		}

		return text;
	}


	public void saveToXML(File file) throws IOException {

		// Create an XML DOM structure to write to.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		}
		Document doc = db.newDocument();
		Element root = doc.createElement(ELEM_TOKEN_MAKER_DEF);
		doc.appendChild(root);

		// Basic attributes.
		Element generalElem = doc.createElement(ELEM_GENERAL);
		root.appendChild(generalElem);
		Element tempElem = doc.createElement(ELEM_PACKAGE);
		generalElem.appendChild(tempElem);
		tempElem.setTextContent(getPackage());
		tempElem = doc.createElement(ELEM_CLASS_NAME);
		generalElem.appendChild(tempElem);
		tempElem.setTextContent(getClassName());
		tempElem = doc.createElement(ELEM_EXTENDED_CLASS);
		generalElem.appendChild(tempElem);
		tempElem.setTextContent(getExtendedClass());
		tempElem = doc.createElement(ELEM_CLASS_COMMENT);
		generalElem.appendChild(tempElem);
		tempElem.setTextContent(getClassDoc());
		tempElem = doc.createElement(ELEM_IGNORE_CASE);
		generalElem.appendChild(tempElem);
		tempElem.setTextContent(Boolean.toString(getIgnoreCase()));
		tempElem = doc.createElement(ELEM_BOOLEAN_LITERALS);
		generalElem.appendChild(tempElem);
		tempElem.setTextContent(Boolean.toString(getBooleanLiterals()));

		// Store main comment info.
		Element commentsElem = doc.createElement(ELEM_COMMENTS);
		root.appendChild(commentsElem);

		// Line comments.
		boolean enabled = getLineCommentsEnabled();
		String start = getLineCommentStart();
		Element lineCommentElem = doc.createElement(ELEM_LINE_COMMENT);
		commentsElem.appendChild(lineCommentElem);
		lineCommentElem.setAttribute(ATTR_ENABLED, Boolean.toString(enabled));
		lineCommentElem.setAttribute(ATTR_START, start);

		// Multi-line comments
		enabled = getMultilineCommentsEnabled();
		start = getMultilineCommentStart();
		String end = getMultilineCommentEnd();
		Element mlcElem = doc.createElement(ELEM_MLC);
		commentsElem.appendChild(mlcElem);
		mlcElem.setAttribute(ATTR_ENABLED, Boolean.toString(enabled));
		mlcElem.setAttribute(ATTR_START, start);
		mlcElem.setAttribute(ATTR_END, end);

		// Documentation comments
		enabled = getDocCommentsEnabled();
		start = getDocCommentStart();
		end = getDocCommentEnd();
		Element docElem = doc.createElement(ELEM_DOC_COMMENT);
		commentsElem.appendChild(docElem);
		docElem.setAttribute(ATTR_ENABLED, Boolean.toString(enabled));
		docElem.setAttribute(ATTR_START, start);
		docElem.setAttribute(ATTR_END, end);

		// Store keywords
		Element keywordsElem = doc.createElement(ELEM_KEYWORDS);
		root.appendChild(keywordsElem);
		List<String> keywords = getKeywords();
		for (String keyword : keywords) {
			Element temp = doc.createElement(ELEM_KEYWORD);
			temp.setTextContent(keyword);
			keywordsElem.appendChild(temp);
		}
		keywordsElem = doc.createElement(ELEM_KEYWORDS_2);
		root.appendChild(keywordsElem);
		List<String> keywords2 = getKeywords2();
		for (String keyword : keywords2) {
			Element temp = doc.createElement(ELEM_KEYWORD);
			temp.setTextContent(keyword);
			keywordsElem.appendChild(temp);
		}

		// Store data types
		Element dtElem = doc.createElement(ELEM_DATA_TYPES);
		root.appendChild(dtElem);
		List<String> dataTypes = getDataTypes();
		for (String dataType : dataTypes) {
			Element temp = doc.createElement(ELEM_DATA_TYPE);
			temp.setTextContent(dataType);
			dtElem.appendChild(temp);
		}

		// Store functions
		Element funcElem = doc.createElement(ELEM_FUNCTIONS);
		root.appendChild(funcElem);
		List<String> functions = getFunctions();
		for (String function : functions) {
			Element temp = doc.createElement(ELEM_FUNCTION);
			temp.setTextContent(function);
			funcElem.appendChild(temp);
		}

		// Store number formats
		Element numberElem = doc.createElement(ELEM_NUMBERS);
		root.appendChild(numberElem);
		Element hexElem = doc.createElement(ELEM_NUMBERS_HEX);
		if (hexLiteralFormat!=null) {
			hexElem.setTextContent(hexLiteralFormat.getFormat());
		}
		numberElem.appendChild(hexElem);
		Element intElem = doc.createElement(ELEM_NUMBERS_INT);
		if (intLiteralFormat!=null) {
			intElem.setTextContent(intLiteralFormat.getFormat());
		}
		numberElem.appendChild(intElem);
		Element floatElem = doc.createElement(ELEM_NUMBERS_FLOAT);
		if (floatLiteralFormat!=null) {
			floatElem.setTextContent(floatLiteralFormat.getFormat());
		}
		numberElem.appendChild(floatElem);

		// String info
		Element stringElem = doc.createElement(ELEM_STRINGS);
		root.appendChild(stringElem);
		stringElem.setAttribute(ATTR_ENABLED, Boolean.toString(getStringsEnabled()));
		stringElem.setAttribute(ATTR_MULTI_LINE, Boolean.toString(getStringsMultiLine()));

		// Char literal info
		Element charElem = doc.createElement(ELEM_CHARS);
		root.appendChild(charElem);
		charElem.setAttribute(ATTR_ENABLED, Boolean.toString(getCharsEnabled()));
		charElem.setAttribute(ATTR_MULTI_LINE, Boolean.toString(getCharsMultiLine()));

		// Backtick literal info
		Element backtickElem = doc.createElement(ELEM_BACKTICKS);
		root.appendChild(backtickElem);
		backtickElem.setAttribute(ATTR_ENABLED, Boolean.toString(getBackticksEnabled()));

		// Operators
		Element opElem = doc.createElement(ELEM_OPERATORS);
		root.appendChild(opElem);
		List<String> operators = getOperators();
		for (String operator : operators) {
			Element temp = doc.createElement(ELEM_OPERATOR);
			temp.setTextContent(operator);
			opElem.appendChild(temp);
		}

		// Save to file
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException tce) {
			throw new IOException(tce);
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.ENCODING, FILE_ENCODING);
		StreamResult result = new StreamResult(file);
		DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, result);
		} catch (TransformerException te) {
			throw new IOException(te);
		}

	}


	public void setBackticksEnabled(boolean enabled) {
		backticksEnabled = enabled;
	}


	public void setBooleanLiterals(boolean booleanLiterals) {
		this.booleanLiterals = booleanLiterals;
	}


	public void setCharsEnabled(boolean enabled) {
		charsEnabled = enabled;
	}


	public void setCharsMultiLine(boolean multiLine) {
		charsMultiLine = multiLine;
	}


	public void setClassDoc(String classDoc) {
		this.classDoc = classDoc;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public void setDataTypes(List<String> dataTypes) {
		this.dataTypes = dataTypes;
	}


	public void setDocCommentsEnabled(boolean enabled) {
		docCommentsEnabled = enabled;
	}


	public void setDocCommentEnd(String end) {
		docCommentEnd = end;
	}


	public void setDocCommentStart(String start) {
		docCommentStart = start;
	}


	public void setExtendedClass(String extended) {
		extendedClass = extended;
	}


	public void setFloatLiteralFormat(FloatLiteralFormat format) {
		this.floatLiteralFormat = format;
	}


	public void setFunctions(List<String> functions) {
		this.functions = functions;
	}


	public void setHexLiteralFormat(HexLiteralFormat format) {
		this.hexLiteralFormat = format;
	}


	public void setIgnoreCase(boolean ignore) {
		ignoreCase = ignore;
	}


	public void setIntLiteralFormat(IntLiteralFormat format) {
		this.intLiteralFormat = format;
	}


	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}


	public void setKeywords2(List<String> keywords2) {
		this.keywords2 = keywords2;
	}


	public void setLineCommentsEnabled(boolean enabled) {
		lineCommentsEnabled = enabled;
	}


	public void setLineCommentStart(String start) {
		lineCommentStart = start;
	}


	public void setMultilineCommentsEnabled(boolean enabled) {
		mlcsEnabled = enabled;
	}


	public void setMultilineCommentStart(String start) {
		mlcStart = start;
	}


	public void setMultilineCommentEnd(String end) {
		mlcEnd = end;
	}


	public void setOperators(List<String> operators) {
		this.operators = operators;
	}


	public void setPackage(String pkg) {
		this.pkg = pkg;
	}


	public void setStringsEnabled(boolean enabled) {
		stringsEnabled = enabled;
	}


	public void setStringsMultiLine(boolean multiLine) {
		stringsMultiLine = multiLine;
	}


}