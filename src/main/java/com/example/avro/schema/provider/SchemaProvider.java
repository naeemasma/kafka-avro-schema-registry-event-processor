package com.example.avro.schema.provider;

import org.apache.avro.Schema;

import com.example.schema.domain.VersionedSchema;

public interface SchemaProvider extends AutoCloseable {
	  public VersionedSchema get(int id);
	  public VersionedSchema get(String schemaName, int schemaVersion);
	  public VersionedSchema getMetadata(Schema schema);
	}
