package com.example.avro.schema.provider;

import java.util.Map;

public interface SchemaProviderFactory {
    public SchemaProvider getProvider(Map<String, ?> config) throws Exception;
}
