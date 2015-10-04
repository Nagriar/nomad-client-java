package nomad_project;

import nomad_project.Client.HTTP_METHOD;

public class Node {
	
	private Client client;
	private String id;
	
	public Node(Client client, String id) {
		this.id = id;
		this.client = client;
	}
	
	public Client.Response get() throws NomadClientException {
		return client.request(HTTP_METHOD.get, String.format("/%s/node/%s", client.version, id), null);
	}
	
	public Client.Response getAllocations() throws NomadClientException {
		return client.request(HTTP_METHOD.get, String.format("/%s/node/%s/allocations", client.version, id), null);
	}
	
	public Client.Response evaluate() throws NomadClientException {
		return client.request(HTTP_METHOD.post, String.format("/%s/node/%s/evaluate", client.version, id), null);
	}

	public Client.Response drain(boolean enable) throws NomadClientException {
		return client.request(HTTP_METHOD.post, String.format("/%s/node/%s/drain?enable=%b", client.version, id, enable), null);
	}
}
