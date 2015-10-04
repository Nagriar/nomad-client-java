package job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nomad_project.NomadClientException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Task {
	
	private String driver;
	private List<Constraint> constraints;
	private Map<String, String> config;
	private Ressources ressources;
	private Map<String, String> meta;
	
	public Task(String driver) {
		this.driver = driver;
		constraints = new ArrayList<Constraint>();
		config = new HashMap<String, String>();
		meta = new HashMap<String, String>();
	}
	
	public Task addConstraint(Constraint c) {
		constraints.add(c);
		return this;
	}
	
	public Task addConfig(String key, String value) {
		config.put(key, value);
		return this;
	}
	
	public Task setRessources(Ressources r) {
		ressources = r;
		return this;
	}
	
	public Task setMeta(String key, String value) {
		meta.put(key, value);
		return this;
	}
	
	public JSONObject toJson() throws NomadClientException {
		JSONObject json = new JSONObject();
		try {
			json.put("driver", driver);
			if (constraints.size() > 0) {
				JSONArray array = new JSONArray();
				for (Constraint c : constraints) {
					array.put(c.toJson());
				}
				json.put("constraint", array);
			}
			if (meta.size() > 0) {
				JSONObject map = new JSONObject();
				for (Map.Entry<String, String> elt : meta.entrySet()) {
					map.put(elt.getKey(), elt.getValue());
				}
				json.put("meta", map);
			}
			if (config.size() > 0) {
				JSONObject map = new JSONObject();
				for (Map.Entry<String, String> elt : config.entrySet()) {
					map.put(elt.getKey(), elt.getValue());
				}
				json.put("config", map);
			}
			if (ressources != null) {
				json.put("ressources", ressources.toJson());
			}
		} catch (JSONException e) {
			throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
		}

		return json;
	}
}
