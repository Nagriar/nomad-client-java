package job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nomad_project.NomadClientException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Group {
	
	private Integer count;
	private List<Constraint> constraints;
	private List<Task> tasks;
	private Map<String, String> meta;
	
	public Group() {
		constraints = new ArrayList<Constraint>();
		tasks = new ArrayList<Task>();
		meta = new HashMap<String, String>();
	}
	
	public Group setCount(int count) {
		count = new Integer(count);
		return this;
	}
	
	public Group addConstraint(Constraint c) {
		constraints.add(c);
		return this;
	}
	
	public Group addTask(Task t) {
		tasks.add(t);
		return this;
	}
	
	public Group addMeta(String key, String value) {
		meta.put(key, value);
		return this;
	}
	
	public JSONObject toJson() throws NomadClientException {
		JSONObject json = new JSONObject();
		
		try {
			if (count != null) {
				json.put("count", count);
			}
			
			if (constraints.size() > 0) {
				JSONArray array = new JSONArray();
				for (Constraint c : constraints) {
					array.put(c.toJson());
				}
				json.put("constraint", array);
			}
			if (tasks.size() > 0) {
				JSONArray array = new JSONArray();
				for (Task t : tasks) {
					array.put(t.toJson());
				}
				json.put("task", array);
			}
			if (meta.size() > 0) {
				JSONObject map = new JSONObject();
				for (Map.Entry<String, String> elt : meta.entrySet()) {
					map.put(elt.getKey(), elt.getValue());
				}
				json.put("meta", map);
			}
		} catch (JSONException e) {
			throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
		}
		
		return json;
	}

}
