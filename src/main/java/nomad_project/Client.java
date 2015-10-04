package nomad_project;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class Client {
	private List<String> entryPoints;
	private HttpClient httpClient;
	private String region;
	private String index;
	protected final String version = "v1";
	
	public Client(List<String> entryPoints) {
		this.entryPoints = entryPoints;
		httpClient = HttpClientBuilder.create().build();
	}
	
	public Client setRegion(String region) {
		this.region = region;
		return this;
	}
	
	public Client setIndex(String index) {
		this.index = index;
		return this;
	}
	
	public Allocation initAllocation(String id) {
		return new Allocation(this, id);
	}
	
	public Job initJog(String id) {
		return new Job(this, id);
	}
	
	public Evaluation initEvaluation(String id) {
		return new Evaluation(this, id);
	}
	
	public Response getAgentState() throws NomadClientException {
		return request(HTTP_METHOD.get, String.format("/%s/agent/self", version), null);
	}
	
	public Response getAgentMembers() throws NomadClientException {
		return request(HTTP_METHOD.get, String.format("/%s/agent/members", version), null);
	}
	
	public Response forceLeaveAgent(String nodeName) throws NomadClientException {
		try {
			return request(HTTP_METHOD.post, String.format("/%s/agent/force-leave?node=%s", version, URLEncoder.encode(nodeName, "utf-8")), null);
		} catch (UnsupportedEncodingException e) {
			throw new NomadClientException(String.format("UnsupportedEncodingException: %s", e.getMessage()));
		}
	}
	
	public Response getJobs() throws NomadClientException {
		return request(HTTP_METHOD.get, String.format("/%s/jobs", version), null);
	}
	
	public Response addJob(Job job) throws NomadClientException {
		return request(HTTP_METHOD.post, String.format("/%s/jobs/%s", version, job.getID()), job.getBody());
	}
	
	public Response getNodes() throws NomadClientException {
		return request(HTTP_METHOD.get, String.format("/%s/nodes", version), null);
	}
	
	public Response getAllocations() throws NomadClientException {
		return request(HTTP_METHOD.get, String.format("/%s/allocations", version), null);
	}
	
	public Response getEvaluations() throws NomadClientException {
		return request(HTTP_METHOD.get, String.format("/%s/evaluations", version), null);
	}
	
	public class Response {
		public JSONObject body;
		public String lastContact;
		public String knownLeader;
		
		public Response(JSONObject body, String lastContact, String knownLeader) {
			this.body = body;
			this.lastContact = lastContact;
			this.knownLeader = knownLeader;
		}
	}
	
	public enum HTTP_METHOD {
		delete,
		post,
		get,
		put
	};
	
	protected Response request(HTTP_METHOD method, String url, String body) throws NomadClientException {
		HttpRequestBase request = new HttpDelete();
		String fragment = null;
		Map<String, Exception> errors = new HashMap<String, Exception>();
		
		switch (method) {
		case delete:
			request = new HttpDelete();
			break;
		case post:
			request = new HttpPost();
			break;
		case get:
			request = new HttpGet();
			break;
		case put:
			request = new HttpPut();
			break;
		};
		
		{
			StringBuilder fragmentBuilder = new StringBuilder();
			if (region != null) {
				try {
					fragmentBuilder.append(String.format("region=%s", URLEncoder.encode(region, "utf-8")));
				} catch (UnsupportedEncodingException e) {
					throw new NomadClientException(String.format("UnsupportedEncodingException: %s", e.getMessage()));
				}
			}
		}
		
		if (index != null) {
			request.setHeader("X-Nomad-Index", index);
		}
		
		if (body != null) {
			StringEntity se = new StringEntity(body, Charset.forName("utf-8"));
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			((HttpEntityEnclosingRequestBase) request).setEntity(se);
		}
		
		
		
		for (String host : entryPoints) {
			try {
				request.setURI(new URI("https://", host, url, fragment));
			} catch (URISyntaxException e1) {
				throw new NomadClientException(String.format("URISyntaxException: %s", e1.getMessage()));
			}
			HttpResponse response = null;
			JSONObject responseBody = null;
			try {
				response = httpClient.execute(request);
				
				StringBuilder sb = new StringBuilder();
				byte[] buffer = new byte[4096];
				int nbRead = 0;
				while ((nbRead = response.getEntity().getContent().read(buffer)) > 0) {
					sb.append(new String(buffer, 0, nbRead));
				}
				responseBody =  new JSONObject(sb.toString());
			} catch (ClientProtocolException e) {
				errors.put(host, e);
				continue;
			} catch (IOException e) {
				errors.put(host, e);
				continue;
			} catch (JSONException e) {
				errors.put(host, e);
			}
			String lastContact = response.getHeaders("X-Nomad-LastContact").length > 0 ? response.getHeaders("X-Nomad-LastContact")[0].getValue() : null;
			String knownLeader = response.getHeaders("X-Nomad-KnownLeader").length > 0 ? response.getHeaders("X-Nomad-KnownLeader")[0].getValue() : null;
			return new Response(responseBody, lastContact, knownLeader);
		}
		
		StringBuilder errorMsg = new StringBuilder();
		for (Map.Entry<String, Exception> entry : errors.entrySet()) {
			errorMsg.append(String.format("%s=%s", entry.getKey(), entry.getValue().getMessage()));
		}
		throw new NomadClientException(errorMsg.toString());
	}
}
