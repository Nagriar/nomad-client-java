package nomad_project;

import nomad_project.Client.HTTP_METHOD;
import nomad_project.Client.Response;

public class Evaluation {
	private Client client;
	private String id;
	
	public Evaluation(Client client, String id) {
		this.id = id;
		this.client = client;
	}
	
	public Response getAllocations() throws NomadClientException {
		return client.request(HTTP_METHOD.get, String.format("/%s/evaluation/%s/allocations", client.version, id), null);
	}
}
