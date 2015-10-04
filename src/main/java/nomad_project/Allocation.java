package nomad_project;

import nomad_project.Client.HTTP_METHOD;
import nomad_project.Client.Response;

public class Allocation {
	
	private Client client;
	private String id;
	
	public Allocation(Client client, String id) {
		this.id = id;
		this.client = client;
	}
	
	public Response get() throws NomadClientException {
		return client.request(HTTP_METHOD.get, String.format("/%s/allocation/%s", client.version, id), null);
	}

}
