package formreader;

public class Example {
	public static void main(String[] args) {

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
	}
}
		
