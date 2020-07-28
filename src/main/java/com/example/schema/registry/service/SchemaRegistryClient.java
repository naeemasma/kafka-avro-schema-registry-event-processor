package com.example.schema.registry.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import com.example.schema.domain.VersionedSchema;
import com.example.schema.registry.domain.SchemaReference;
import com.example.schema.registry.domain.SchemaRegistrationRequest;
import com.example.schema.registry.domain.SchemaResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

@Service
public class SchemaRegistryClient {
		public static final Log logger = LogFactory.getLog(SchemaRegistryClient.class);
	
		private OkHttpClient client = new OkHttpClient();
        
		private RestTemplate restTemplate;

		private String endpoint = "http://localhost:8990";
		
		public SchemaRegistryClient() {
			super();
		}

		public SchemaRegistryClient(String endpoint) {
			super();
			this.setEndpoint(endpoint);
		}

		protected String getEndpoint() {
			return this.endpoint;
		}

		public void setEndpoint(String endpoint) {
			if(endpoint!=null && !endpoint.trim().isEmpty())this.endpoint = endpoint;
		}

		protected RestTemplate getRestTemplate() {
			return this.restTemplate;
		}

		public SchemaResponse register(SchemaRegistrationRequest req) {
			logger.info(req.getDefinition());        
	        //POST A NEW SCHEMA
			Map<String, String> map = new HashMap<>();	
			map.put("subject", req.getSubject());	
			map.put("format", req.getFormat());	
			map.put("definition", req.getDefinition());
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
	        		this.endpoint,
	        		map, Map.class);					
	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
				SchemaResponse registrationResponse = new SchemaResponse();
				Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
				registrationResponse.setId((Integer) responseBody.get("id"));
				registrationResponse.setSubject(req.getSubject());
				registrationResponse.setVersion((Integer) responseBody.get("version"));
				registrationResponse.setFormat(responseBody.get("format").toString());
				registrationResponse.setDefinition(responseBody.get("definition").toString());
				logger.info(registrationResponse.getId() + registrationResponse.getSubject().toString()
						+ registrationResponse.getVersion() + registrationResponse.getFormat() + registrationResponse.getDefinition());
				return registrationResponse;
	        }
				throw new RuntimeException(
						"Failed to register schema: " + responseEntity.toString());
		}
		public SchemaResponse fetch(int id) throws Exception {
			Request  request = new Request.Builder()
	                .url(this.endpoint + "/schemas/" + id)
	                .build();
	        SchemaResponse schemaResponse = new ObjectMapper().readValue(
	        		client.newCall(request).execute().body().string(), SchemaResponse.class);
	        return schemaResponse;
		}
		public SchemaResponse fetch(SchemaReference schemaReference) throws Exception {			

	        Request  request = new Request.Builder()
	                .url(this.endpoint
	    					+ "/" + schemaReference.getSubject() + "/" + schemaReference.getFormat()
	    					+ "/v" + schemaReference.getVersion()
	    					)
	                .build();
	        String s = client.newCall(request).execute().body().string();
	        logger.info(s);
	        SchemaResponse schemaResponse = new ObjectMapper().readValue(
	        		client.newCall(request).execute().body().string(), SchemaResponse.class);	        
	       return schemaResponse;
		}
		public List<SchemaResponse> fetchAll(SchemaReference schemaReference) throws Exception {			

	        Request  request = new Request.Builder()
	                .url(this.endpoint
	    					+ "/" + schemaReference.getSubject() + "/" + schemaReference.getFormat()
	    					).build();
	        String s = client.newCall(request).execute().body().string();
	        logger.info(s);
	        List<SchemaResponse> schemaResponses = new ObjectMapper().readValue(
	        		client.newCall(request).execute().body().string(), new TypeReference<List<SchemaResponse>>(){});	        
	        return schemaResponses;
		}
}
