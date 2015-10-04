package job;

import java.util.ArrayList;
import java.util.List;

import nomad_project.NomadClientException;

import org.json.JSONException;
import org.json.JSONObject;

public class Ressources {

	public class Network {
		private List<String> dynamicPorts;
		Integer mbits;
		List<Integer> ports;
		
		public Network() {
			dynamicPorts = new ArrayList<String>();
			ports = new ArrayList<Integer>();
		}
		
		public Network addDynamicPort(String port) {
			dynamicPorts.add(port);
			return this;
		}
		
		public Network addReservedPorts(int port) {
			ports.add(port);
			return this;
		}
		
		public Network setMbits(int mbits) {
			this.mbits = mbits;
			return this;
		}
		
		public JSONObject toJson() throws NomadClientException {
			JSONObject json = new JSONObject();
			
			try {
				if (mbits != null) {
					json.put("mbits", mbits);
				}
				if (ports.size() > 0) {
					json.put("reserved_ports", ports);
				}
				if (dynamicPorts.size() > 0) {
					json.put("dynamic_ports", dynamicPorts);
				}
			} catch (JSONException e) {
				throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
			}
			
			return json;
		}
	}
	
	private Integer cpu;
	private Integer disk;
	private Integer iops;
	private Integer memory;
	private Network network;
	
	public Ressources setCpu(int cpu) {
		this.cpu = new Integer(cpu);
		return this;
	}
	
	public Ressources setDisk(int disk) {
		this.disk = new Integer(disk);
		return this;
	}
	
	public Ressources setIOPS(int iops) {
		this.iops = new Integer(iops);
		return this;
	}
	
	public Ressources setMemory(int memory) {
		this.memory = memory;
		return this;
	}
	
	public Ressources setNetwork(Network n) {
		this.network = n;
		return this;
	}
	
	public JSONObject toJson() throws NomadClientException {
		JSONObject json = new JSONObject();
		
		try {
			if (cpu != null) {
				json.put("cpu", cpu);
			}
			if (disk != null) {
				json.put("disk", disk);
			}
			if (iops != null) {
				json.put("iops", iops);
			}
			if (memory != null) {
				json.put("memory", memory);
			}
			if (network != null) {
				json.put("network", network.toJson());
			}
		} catch (JSONException e) {
			throw new NomadClientException(String.format("JSONException: %s", e.getMessage()));
		}
		return json;
	}
	
	
}
