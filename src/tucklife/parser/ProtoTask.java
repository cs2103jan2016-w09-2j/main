import java.util.ArrayList;
import java.util.Iterator;

public class ProtoTask {
	
	private String type;
	private ArrayList<String> params;
	private ArrayList<String> paramNames;
	
	public ProtoTask(String commandType) {
		type = commandType;
		params = new ArrayList<String>();
		paramNames = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	
	public ArrayList<String> getParams() {
		return params;
	}
	
	public ArrayList<String> getParamNames() {
		return paramNames;
	}
	
	public void addParam(String paramName, String paramInfo) {
		paramNames.add(paramName);
		params.add(paramInfo);
	}
	
	public String toString() {
		String toDisplay = "Command type: " + type + "\n";
		toDisplay += "Parameters: \n";
		
		if (params.isEmpty()) {
			toDisplay += "None\n";
		} else {
			Iterator<String> iterParams = params.iterator();
			Iterator<String> iterParamNames = paramNames.iterator();
			while (iterParams.hasNext()) {
				toDisplay += iterParamNames.next() + ": " + iterParams.next() + "\n";
			}
		}
		
		return toDisplay;
	}
}
