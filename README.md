# HTML Form Reader by Yaakov Freedman

A tool for making it a bit easier to emulate web forms

## Overview and Purpose

I write Cucumber/Selenide tests that prod and poke web forms.  I find that it is both annoying and tedious to go
through the HTML and find all of the attributes that I need in order to make requests programmatically when I emulate 
a web form submission.
This tool is useful for both tests and general purposes scripts that submit forms.
HTML Form Reader provides a class that transforms an HTML page containing a form into a structured object with 
all of the fields and attributes of the form. For instance, if I want to emulate a radio button as one of the fields 
I want to submit in a form, I would have to go through the HTML to find all of the inputs with the radio button name, 
then I have to extract each of the acceptable values and compile them as a list for the radio button submission. 
Additionally, if there are attributes present such as ‘disabled’ or ‘readonly’ I will have to look out for these as well. 
And this is for only one field, imagine doing this for a gigantic form. Hopefully this tool will make this sort of 
preparation completely unnecessary.

## Features

* Supports all common field types: text/radio/checkbox inputs, select lists, text areas (all HTML5 inputs coming soon!)
* Supports multiple forms so that you can iterate through all of the forms on a page
* Supports finding the attributes for the form itself as well as its fields
* Supports default values such as GET for form method and the form's page as the default action page
* Supports finding the maxlength attribute, which is important for text based inputs
* Supports ‘readonly’ and ‘disabled’ attributes for relevant input types
* Overloaded toString() method for quick and easy printing of structured data
* Accessors for every single individual attribute value to for immediate use
* Parses the HTML with extreme flexibility; artifacts such as extra spaces and newlines do not affect the output
* If you so choose, you never even have to touch or look at the HTML - you can create an object and then use it directly
in your code

## Upcoming Features

**Todo**: Will support both local documents as well as URLs, no distinction will need to be made when initializing the object, 
the object will recognize whether the parameter provided is a URL or a local file

**Todo**: I will most likely add support for the ‘submit’ input, although this input field is generally of little importance

**Todo**:Add support for hidden fields and all HTML 5 inputs


**Todo**: Any other improvements I can think of or that are suggested to me

## Example: Input from a single form

Taken from the main file FormReader.java:
	
```java

		FormReader testForm = new FormReader("test.html");
		System.out.println("Original HTML:");
		System.out.println();
		System.out.println(testForm.getOriginalHtml());
		System.out.println("===============================================");

		System.out.println("Form Attributes: " + testForm.parsedForm.getForm());
		System.out.println();
		System.out.println("Select Lists Attributes: " + testForm.parsedForm.getSelectLists());
		System.out.println();
		System.out.println("Text Inputs Attributes: " + testForm.parsedForm.getTextInputs());
		System.out.println();
		System.out.println("Radio Inputs Attributes: " + testForm.parsedForm.getRadioInputs());
		System.out.println();
		System.out.println("Check Box Inputs Attributes: " + testForm.parsedForm.getCheckBoxInputs());;
		System.out.println();
		System.out.println("Text Areas Attributes: " + testForm.parsedForm.getTextAreas());
		System.out.println();
		System.out.println("===============================================");
		System.out.println();
		
```

## Output Displayed

```
Original HTML:

<form name="myForm" id="myForm" action="results.php" method="post"/>
<br><br>
<input type="text" name="myTextField1" id="myTextField1" disabled/>
<br><br>
<input type="text" name   =   "myTextField2" id="myTextField2" readonly/>
<br><br>
<input type="text" name = "myTextField3" id="myTextField3" maxlength="10">
<br><br>
<input type="radio" name="myTextField4" id="myTextField4" value="oneValue"/>
<br><br>

<textarea name="myTextArea" id="theTextArea" maxLength="20" disabled readonly></textarea>
<br><br>
<select name="fruit" id="myFruit">
<option value ="none">Nothing</option>
<option value ="guava">Guava</option>
<option value = "lychee">Lychee</option>
<option value ="papaya">Papaya</option>
</select> 

<br><br>
<textarea name="myTextArea2" id="theTextArea2"></textarea>
<br><br>
<input type="radio" name="gender" value="male"> Male<br>
<input type="radio" name="gender" value="female"> Female<br>
<input type="radio" name="gender" value="other"> Other 
<br><br>

<input type="checkbox" name="color" value="purple"> Purple<br>
<input type="checkbox" name="color" value="red"> red<br>
<input type="checkbox" name="color" value="green"> Green 
<br><br><br><br>

<input type="submit" name="submit" id="submit" value="submit">
 </form>
===============================================
Form Attributes: {method=POST, name=myForm, action=results.php, id=myForm}

Select Lists Attributes: [{name: fruit}{id: myFruit}[none, guava, lychee, papaya]]

Text Inputs Attributes: [{name: myTextField1}{id: myTextField1}{disabled}, {name: myTextField2}{id: myTextField2}{readonly}, {name: myTextField3}{id: myTextField3}{maxlength: 10}]

Radio Inputs Attributes: [{name: myTextField4}{id: myTextField4}[oneValue], {name: gender}[male, female, other]]

Check Box Inputs Attributes: [{name: color}[purple, red, green]]

Text Areas Attributes: [{name: myTextArea}{id: theTextArea}{maxlength: 20}, {name: myTextArea2}{id: theTextArea2}]

===============================================```