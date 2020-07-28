package com.example.avro.schema.provider;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.schema.domain.VersionedSchema;
import com.example.schema.registry.service.SchemaRegistryClient;
import com.example.avro.schema.store.SchemaStore;
import com.example.avro.schema.store.impl.InMemorySchemaStore;
import com.example.avro.schema.store.impl.SchemaRegistryStore;
import java.util.Collection;
import java.util.Map;

public class RegistrySchemaProvider implements SchemaStore {

    public static final String FORMAT_AVRO= "avro";
    public static final String ENDPOINT_CONF = "schema.registry.endpoint";
    
    public static final class RegistrySchemaProviderFactory implements SchemaProviderFactory {

        @Override
        public SchemaProvider getProvider(Map<String, ?> config) throws Exception {
        	SchemaRegistryClient service = new SchemaRegistryClient();
        	if(config.get(ENDPOINT_CONF)!=null)service.setEndpoint(config.get(ENDPOINT_CONF).toString());
        	InMemorySchemaStore cache = new InMemorySchemaStore(service);
            SchemaRegistryStore<VersionedSchema> store = new SchemaRegistryStore<>(service);
            return new RegistrySchemaProvider(cache, store);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(RegistrySchemaProvider.class);

    private final InMemorySchemaStore cache;

    private final SchemaRegistryStore<VersionedSchema> store;

    public RegistrySchemaProvider(InMemorySchemaStore cache, SchemaRegistryStore<VersionedSchema> store) {
        this.store = store;
        this.cache = cache;
    }

    @Override
    public VersionedSchema get(int id) {
        return cache.get(id);
    }

    @Override
    public VersionedSchema get(String schemaName, int schemaVersion) {
        return cache.get(schemaName, schemaVersion);
    }

    @Override
    public VersionedSchema getMetadata(Schema schema) {
        return cache.getMetadata(schema);
    }

    public Collection<VersionedSchema> getAllSchemas() {
        return cache.getAllSchemas();
    }

    public void add(VersionedSchema schema) throws Exception {
    	VersionedSchema s = store.add(schema);
    }

    @Override
    public void close() throws Exception {
        cache.close();
        try {
            store.close();
        } catch (Exception e) {
            logger.error("Could not close store.", e);
        }
    }
}
