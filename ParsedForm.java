/* HTML Form Reader by Yaakov Freedman, please use and have fun! */
package formreader;

import java.util.HashMap;
import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParsedForm {
   
	private String formElement, formTag;

	private ArrayList<String> inputTags;

	/*
	 * inputs is each individual parsed input tag, however multiple input tags can
	 * belong to a single input (in the case of checkboxes, radio buttons). This
	 * variable is a list of input fields, which will later be grouped into lists
	 * where the inputs that all belong to a single logical input are grouped
	 */
	private ArrayList<HashMap<String, String>> inputs;

	/*
	 * These are 'elements' and not tags because Select Lists contain multiple tags
	 * within them (option tags)
	 */
	private ArrayList<String> selectListElements;
	private ArrayList<String> textAreaTags;

	/*
	 * Used to chunk input values by name, these values will be carried over to the
	 * final results where inputs are organized by name
	 */
	private HashMap<String, ArrayList<String>> namedInputValues;

	/* Result: fully structured fields */
	private HashMap<String, String> form;
	private ArrayList<FormItem> textAreas;
	private ArrayList<FormItem> selectLists;
	private ArrayList<FormItem> radioInputs;
	private ArrayList<FormItem> checkBoxInputs;
	private ArrayList<FormItem> textInputs;

	public ParsedForm(String formTag, String theFileName) {

		String id = "";
		String name = "";
		String method = "GET";
		String action = theFileName;
		String type;
		String value;

		// maxLength will be cast into an int when needed
		String maxLength;

		ArrayList<String> values = new ArrayList<String>();
	
		/* Parse the attributes that belong to the form element */ 
		this.form = new HashMap<String, String>();
			
		id = this.parseAttributeValue("id", formTag);
		this.form.put("id", id);

		name = this.parseAttributeValue("name", formTag);
		this.form.put("name", name);

		method = this.parseAttributeValue("method", formTag);
		if (method.length() == 0) {
			// default value for method is GET
			method = "GET";
		}
		this.form.put("method", method.toUpperCase());

		action = this.parseAttributeValue("action", formTag);
		if (action.length() == 0) {
			// default value for action is the same URL as the form is on
			action = "Todo";
		}
		this.form.put("action", action);

		/* Parse the Text Area tags */ 
		this.textAreas = new ArrayList<FormItem>();
		this.textAreaTags = new ArrayList<String>();
		this.textAreaTags = this.parseTextAreaTags(formTag);
		for (String textAreaTag : this.textAreaTags) {
			FormItem textArea = new FormItem();

			/* Parse the individual text area attributes */ 
			id = this.parseAttributeValue("id", textAreaTag);
			textArea.id = id;

			name = this.parseAttributeValue("name", textAreaTag);
			textArea.name = name;

			maxLength = this.parseAttributeValue("maxlength", textAreaTag);
			if (maxLength.length() != 0) {
				textArea.maxLength = Integer.parseInt(maxLength);
			}

			this.textAreas.add(textArea);

		}
		/* Parse the input tags */ 
		this.inputTags = new ArrayList<String>();
		this.inputTags = this.parseInputTags(formTag);

		/* Parse the individual input attributes for each input */
		this.inputs = new ArrayList<HashMap<String, String>>();
		for (String inputTag : this.inputTags) {
			HashMap<String, String> input = new HashMap<String, String>();

			/* Parse the individual input attributes */ 
			id = this.parseAttributeValue("id", inputTag);
			input.put("id", id);

			name = this.parseAttributeValue("name", inputTag);
			input.put("name", name);

			if (this.isDisabled(inputTag)) {
				input.put("disabled", "true");
			}

			if (this.isReadOnly(inputTag)) {
				input.put("readonly", "true");
			}

			type = this.parseAttributeValue("type", inputTag);
			input.put("type", type);

			maxLength = this.parseAttributeValue("maxlength", inputTag);
			if (maxLength.length() != 0) {
				input.put("maxlength", maxLength);
			}

			value = this.parseAttributeValue("value", inputTag);
			input.put("value", value);

			this.inputs.add(input);
		}

		/* Use the namedInputValues ArrayList to chunk inputs by name */
		this.namedInputValues = new HashMap<String, ArrayList<String>>();

		for (HashMap<String, String> input : this.inputs) {

			name = input.get("name");

			if (!this.namedInputValues.containsKey(name)) {
				this.namedInputValues.put(name, new ArrayList<String>());
			}
			this.namedInputValues.get(name).add(input.get("value"));

		}

		
		/* Copy input attributes and value to the correct object based on the 'type'
		 * attribute: text, radio, checkbox */
		 
		this.textInputs = new ArrayList<FormItem>();
		this.radioInputs = new ArrayList<FormItem>();
		this.checkBoxInputs = new ArrayList<FormItem>();

		ArrayList<String> checkedNames = new ArrayList<String>();
		for (HashMap<String, String> input : this.inputs) {

			
			 /* We only need 1 input for each 'name', the values fields have already been
			 * extracted */
			 
			if (checkedNames.contains(input.get("name"))) {
				continue;
			} else {
				checkedNames.add(input.get("name"));
			}

			/* Text Inputs have no predefined values */
			if (input.get("type").equals("text")) {
				FormItem textInput = new FormItem();

				textInput.name = input.get("name");
				textInput.id = input.get("id");
				textInput.disabled = Boolean.parseBoolean(input.get("disabled"));

				textInput.readOnly = Boolean.parseBoolean(input.get("readonly"));

				if (input.containsKey("maxlength")) {
					textInput.maxLength = Integer.parseInt(input.get("maxlength"));
				}
				this.textInputs.add(textInput);
			}

			/* Radio Inputs */
			else if (input.get("type").equals("radio")) {
				FormItem radioInput = new FormItem();

				radioInput.name = input.get("name");
				radioInput.id = input.get("id");
				radioInput.disabled = Boolean.parseBoolean(input.get("disabled"));
				radioInput.readOnly = Boolean.parseBoolean(input.get("readonly"));

				radioInput.values = this.namedInputValues.get(input.get("name"));
				this.radioInputs.add(radioInput);
			}

			/* Check Box Inputs */
			else if (input.get("type").equals("checkbox")) {
				FormItem checkBoxInput = new FormItem();

				checkBoxInput.name = input.get("name");
				checkBoxInput.id = input.get("id");
				checkBoxInput.disabled = Boolean.parseBoolean(input.get("disabled"));
				checkBoxInput.readOnly = Boolean.parseBoolean(input.get("readonly"));

				checkBoxInput.values = this.namedInputValues.get(input.get("name"));
				this.checkBoxInputs.add(checkBoxInput);
			}
		}

		/* Parse the Select List Elements */
		this.selectListElements = new ArrayList<String>();
		this.selectListElements = this.parseSelectListElements(formTag);

		
		 /* Parse the individual option attributes for each select list, including the
		 * option tags' attributes for each Select List */
		 
		this.selectLists = new ArrayList<FormItem>();
		for (String selectListElement : this.selectListElements) {
			FormItem selectList = new FormItem();

			/* Parse the individual option attributes */
			id = this.parseAttributeValue("id", selectListElement);
			selectList.id = id;

			name = this.parseAttributeValue("name", selectListElement);
			selectList.name = name;

			/* Parse the 'value' attributes from the option tags */
			values = this.parseAttributeValues("value", selectListElement);

			selectList.values = values;

			this.selectLists.add(selectList);
		}

	}

	public ArrayList<String> getTextAreaTags() {
		return this.textAreaTags;
	}

	public ArrayList<FormItem> getTextAreas() {
		return this.textAreas;
	}

	public ArrayList<FormItem> getTextInputs() {

		return this.textInputs;
	}

	public ArrayList<FormItem> getRadioInputs() {

		return this.radioInputs;
	}

	public ArrayList<FormItem> getCheckBoxInputs() {

		return this.checkBoxInputs;
	}

	public HashMap<String, ArrayList<String>> getNamedInputValues() {
		return this.namedInputValues;
	}

	public ArrayList<FormItem> getSelectLists() {
		return this.selectLists;
	}

	public ArrayList<HashMap<String, String>> getInputs() {

		return this.inputs;
	}

	public HashMap<String, String> getForm() {

		return this.form;
	}

	public ArrayList<String> getInputTags() {
		return this.inputTags;
	}

	public ArrayList<String> getSelectListElements() {
		return this.selectListElements;
	}

	public String getFormTag() {
		return this.formTag;
	}

	public String getFormElement() {
		return this.formElement;
	}

	private ArrayList<String> parseSelectListElements(String text) {
		String regex = "<select.*?<\\/select>";
		ArrayList<String> results = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			results.add(matcher.group());
		}
		return results;
	}

	private ArrayList<String> parseInputTags(String text) {
		String regex = "<input\\s.*?\\/?>";

		ArrayList<String> results = new ArrayList<String>();

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			results.add(matcher.group());
		}
		return results;
	}

	private ArrayList<String> parseTextAreaTags(String text) {
		String regex = "<textarea\\s.*?\\/?>";

		ArrayList<String> results = new ArrayList<String>();

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			results.add(matcher.group());
		}
		return results;
	}

	/* Returns an ArrayList of the values */
	private ArrayList<String> parseAttributeValues(String attributeName, String tagToParse) {

		String regex = "(" + attributeName + "\\s?=\\s?\")(.*?)(\")";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(tagToParse);

		ArrayList<String> valueStrings = new ArrayList<String>();
		String valueString;

		while (matcher.find()) {
			valueString = this.parseAttributeValue("value", matcher.group());
			valueStrings.add(valueString);
		}

		return valueStrings;
	}

	private String parseAttributeValue(String attributeName, String tagToParse) {
		String result = "";

		String regex = "(" + attributeName + "\\s?=\\s?\")(.*?)(\")";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(tagToParse);

		if (matcher.find()) {

			/* Make sure the second capture group is present */
			if (matcher.groupCount() == 3) {
				result = matcher.group(2);
			}
		}

		return result;
	}

	private boolean isDisabled(String tagToParse) {
		return tagToParse.contains("disabled") ? true : false;
	}

	private boolean isReadOnly(String tagToParse) {
		return tagToParse.contains("readonly") ? true : false;
	}
}