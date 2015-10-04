package job;

import nomad_project.NomadClientException;

import org.json.JSONException;
import org.json.JSONObject;

public class Constraint {
	
	private String attribute;
	private boolean hard;
	private String operator;
	private String value;
	
	public Constraint(String attribute, String operator, String value, boolean hard) {
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
		this.hard = hard;
	}
	
	public Constraint(String attribute, String operator, String value) {
		this(attribute, operator, value, true);
	}
	
	public JSONObject toJson() throws NomadClientException {
		JSONObject json = new JSONObject();
		
		try {
			json.put("attribute", attribute);
			json.put("operator", operator);
			json.put("value", value);
			json.put("hard", hard);
		} catch (JSONException e) {
			throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
		}
		
		return json;
	}

}
