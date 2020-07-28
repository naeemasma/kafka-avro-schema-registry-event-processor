/**
 * VersionedSchema
 */
package com.example.schema.domain;

import org.apache.avro.Schema;

public class VersionedSchema {
    private int id;
    private  String name;
    private  int version;
	private Schema schema;
	
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}	

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

    public VersionedSchema(int id, String name, int version, Schema schema) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.schema = schema;
    }



}
