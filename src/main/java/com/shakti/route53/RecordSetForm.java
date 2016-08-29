package com.shakti.route53;

import com.amazonaws.services.route53.model.RRType;

public class RecordSetForm {
	
	private String domainName;
	private String value;
	private RRType type;
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public RRType getType() {
		return type;
	}
	public void setType(RRType type) {
		this.type = type;
	}

}
