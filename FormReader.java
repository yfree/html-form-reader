/* HTML Form Reader by Yaakov Freedman, please use and have fun! */
package formreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class FormReader {

	public ParsedForm parsedForm;
	public ArrayList<ParsedForm> parsedForms;

	private String originalHtmlContent, fileName;

	public FormReader(String theFileName) {
		boolean multipleFormFlag = false;
		this.fileName = theFileName;
		this.initialize(theFileName, multipleFormFlag);
	}

	public FormReader(String theFileName, boolean theMultipleFormFlag) {
		this.initialize(theFileName, theMultipleFormFlag);
	}

	public String getOriginalHtml() {
		return this.originalHtmlContent;
	}

	/*
	 * Extracts the HTML content, prepares the text for parsing, and creates the
	 * formElements
	 */
	private void initialize(String theFileName, boolean theMultipleFormFlag) {
		this.originalHtmlContent = this.readFileContent(theFileName);
		this.parsedForms = new ArrayList<ParsedForm>();

		String htmlContent = prepareHtmlContent(this.originalHtmlContent);
		this.runFormParser(htmlContent, theFileName);
	}

	private String readFileContent(String filePath) {
		String result = "";

		try {

			result = new String(Files.readAllBytes(Paths.get(filePath)));

		}

		catch (IOException e) {

			System.out.println("Could not read the file/URL input due to the following exception:");
			System.out.println(e.toString());
			System.exit(1);

		}
		return result;

	}

	/* Remove the newlines and all extra spaces from the HTML content */
	private String prepareHtmlContent(String theOriginalHtmlContent) {
		String htmlContent;
		htmlContent = theOriginalHtmlContent.replaceAll("\n", "").replaceAll("\r", "");
		htmlContent = htmlContent.trim().replaceAll(" +", " ");

		return htmlContent;
	}

	public void runFormParser(String htmlText, String theFileName) {
		ArrayList<String> formElements = new ArrayList<String>();

		formElements = parseFormElements(htmlText);

		for (String formElement : formElements) {
			this.parsedForm = new ParsedForm(formElement, this.fileName);

			this.parsedForms.add(this.parsedForm);
		}
	}

	/* Extracts all of the form elements from a string of text */
	private ArrayList<String> parseFormElements(String textToParse) {
		String regex = "<form\\s.*?\\/?form>";
		ArrayList<String> results = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(textToParse);

		while (matcher.find()) {
			results.add(matcher.group());
		}

		return results;

	}
}