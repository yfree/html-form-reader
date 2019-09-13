/* HTML Form Reader by Yaakov Freedman, please use and have fun! */
package formreader;

import java.util.ArrayList;

public class FormItem {
	public String name;
	public String id;
	public String type;
	public Integer maxLength;
	public boolean disabled;
	public boolean readOnly;
	public ArrayList<String> values;

	FormItem() {
		// Intentionally blank
	}

	@Override
	    public String toString() {

		StringBuilder stringBuilder = new StringBuilder();
	    if (!this.name.isEmpty()) {
	    	stringBuilder.append("{name: "+ this.name +"}");
	    }
	    if (!this.id.isEmpty()) {
	    	stringBuilder.append("{id: "+ this.id +"}");
	    }
	    if (this.maxLength != null) {
	    	stringBuilder.append("{maxlength: "+ this.maxLength +"}");
	    }
	    if (this.disabled == true) {
	    	stringBuilder.append("{disabled}");
	    }	    	
	    if (this.readOnly == true) {
	    	stringBuilder.append("{readonly}");
	    }
	    if (this.values != null) {
	    	stringBuilder.append(values);
	    }
		return stringBuilder.toString();
	    
	    }

}