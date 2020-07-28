package com.example.schema.registry.domain;

import org.apache.avro.Schema;

public class SchemaRegistrationRequest {
	private String subject;
	private String format;
	private String definition;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public SchemaRegistrationRequest() {
		super();
	}
	public SchemaRegistrationRequest(String subject, String format, String definition) {
		super();
		this.subject = subject;
		this.format = format;
		this.definition = definition;
	}
	
}
