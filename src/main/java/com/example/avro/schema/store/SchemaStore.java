package com.example.avro.schema.store;

import com.example.avro.schema.provider.SchemaProvider;
import com.example.schema.domain.VersionedSchema;

public interface SchemaStore extends SchemaProvider {
    public void add(VersionedSchema schema) throws Exception;
}
