package com.example.avro.schema.store.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.avro.schema.provider.RegistrySchemaProvider;
import com.example.schema.domain.VersionedSchema;
import com.example.schema.registry.service.SchemaRegistryClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import com.example.schema.registry.domain.SchemaRegistrationRequest;
import com.example.schema.registry.domain.SchemaResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.avro.Schema;
/**
 *
 * @param <T>
 */
public class SchemaRegistryStore<T> {
	private SchemaRegistryClient service;
	private static final Logger logger = LoggerFactory.getLogger(SchemaRegistryStore.class);

    public SchemaRegistryStore(SchemaRegistryClient service) {
		super();
		this.service = service;
	}
    
    public VersionedSchema add(T record) throws Exception {
    	VersionedSchema rec = (VersionedSchema)record;
    	SchemaRegistrationRequest req = new SchemaRegistrationRequest();
    	req.setSubject(rec.getName());
    	req.setFormat(RegistrySchemaProvider.FORMAT_AVRO);
    	req.setDefinition(rec.getSchema().toString());
    	SchemaResponse s = service.register(req);
    	rec.setId(s.getId());
    	return rec;
    }

    public void close() throws Exception {
    }




}
