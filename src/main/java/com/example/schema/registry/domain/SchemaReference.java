package com.example.schema.registry.domain;

public class SchemaReference {
	private String subject;
	private String format;
	private int version;
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
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public SchemaReference() {
		super();
	}
	public SchemaReference(String subject, String format, int version) {
		super();
		this.subject = subject;
		this.format = format;
		this.version = version;
	}
	
}
