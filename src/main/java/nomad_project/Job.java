package nomad_project;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nomad_project.Client.HTTP_METHOD;
import nomad_project.Client.Response;
import job.Constraint;
import job.Group;
import job.Task;

public class Job {
	
	private String id;
	private Client client;
	
	public class Update {
		private Integer maxParallel;
		private String stagger;
		
		public Update setMaxParallel(int maxParallel) {
			this.maxParallel = new Integer(maxParallel);
			return this;
		}
		
		public Update setStagger(String value) {
			this.stagger = value;
			return this;
		}
		
		public JSONObject toJson() throws NomadClientException {
			JSONObject json = new JSONObject();
			
			try {
				if (maxParallel != null) {
					json.put("max_parallel", maxParallel);
				}
				if (stagger != null) {
					json.put("stagger", stagger);
				}
			} catch (JSONException e) {
				throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
			}
			
			
			return json;
		}
	}
	
	private String region;
	private List<String> datacenters;
	private Update update;
	private List<Constraint> constraints;
	private List<Group> groups;
	private Boolean allAtOnce;
	private Integer priority;
	private List<Task> tasks;
	private String type;
	private Map<String, String> meta;
	
	public Job(Client client, String id) {
		this.id = id;
		this.client = client;
	}

	public Object getID() {
		return id;
	}
	
	public Response getJob() throws NomadClientException {
		return client.request(HTTP_METHOD.get, String.format("/%s/node/%s", client.version, id), null);
	}
	
	public Job setAllAtOnce(boolean enable) {
		allAtOnce = new Boolean(enable);
		return this;
	}
	
	public Job addConstraint(Constraint c) {
		constraints.add(c);
		return this;
	}
	
	public Job addDataCenters(String dc) {
		datacenters.add(dc);
		return this;
	}
	
	public Job addGroup(Group g) {
		groups.add(g);
		return this;
	}
	
	public Job addMeta(String key, String value) {
		meta.put(key, value);
		return this;
	}
	
	public Job setPriority(int priority) {
		priority = new Integer(priority);
		return this;
	}
	
	public Job setRegion(String region) {
		this.region = region;
		return this;
	}
	
	public Job addTask(Task t) {
		tasks.add(t);
		return this;
	}
	
	public Job setType(String type) {
		this.type = type;
		return this;
	}
	
	public Job setUpate(Update u) {
		this.update = u;
		return this;
	}

	public String getBody() throws NomadClientException {
		JSONObject json = new JSONObject();
		
		try {
			if (allAtOnce) {
				json.put("all_at_once", allAtOnce);
			}
			if (constraints.size() > 0) {
				JSONArray array = new JSONArray();
				for (Constraint c : constraints) {
					array.put(c.toJson());
				}
				json.put("constraint", array);
			}
			if (groups.size() > 0) {
				JSONArray array = new JSONArray();
				for (Group g : groups) {
					array.put(g.toJson());
				}
				json.put("group", array);
			}
			if (meta.size() > 0) {
				JSONObject map = new JSONObject();
				for (Map.Entry<String, String> elt : meta.entrySet()) {
					map.put(elt.getKey(), elt.getValue());
				}
				json.put("meta", map);
			}
			if (tasks.size() > 0) {
				JSONArray array = new JSONArray();
				for (Task t : tasks) {
					array.put(t.toJson());
				}
				json.put("task", array);
			}
			if (datacenters.size() > 0) {
				json.put("datacenters", datacenters);
			}
			if (priority != null) {
				json.put("priority", priority);
			}
			if (region != null) {
				json.put("region", region);
			}
			if (type != null) {
				json.put("type", type);
			}
			if (update != null) {
				json.put("update", update);
			}
			
		} catch (JSONException e) {
			throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
		}
		
		return json.toString();
	}

}
