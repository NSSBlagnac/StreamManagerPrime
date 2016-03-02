package com.francetelecom.orangetv.streammanager.shared.model;

import java.io.Serializable;

public class ServerInfoBean implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private final static String SEPARATOR = ":";
	private static final String DEFAULT_NAME = "localhost";
	private static final String DEFAULT_PORT = "8080";
	private String name;
	private String port;
	
	
	
	public String getName() {
		return name;
	}

	public String getPort() {
		return port;
	}

	public String getInfoServer() {
		return this.name + SEPARATOR + this.port;
	}
	public ServerInfoBean(String infoServer) {
		
		 String name = null;
		 String port = null;
		 
		 String[] server = (infoServer == null)?null:infoServer.split(SEPARATOR);
	     if (server != null && server.length == 2) {
	    	   
	    	   name = server[0];
	    	   port = server[1];
	      }

	     this.validateAndSetValues(name, port);
	}
	public ServerInfoBean() {
		this("", "");
	}
	public ServerInfoBean(String name, int port) {
		this(name, "" + port);
	}
	public ServerInfoBean(String name, String port) {
		this.validateAndSetValues(name, port);
	}
	
	private void validateAndSetValues (String name, String port) {
		this.name = this.validate(name)?name:DEFAULT_NAME;
		this.port = this.validate(port)?port:DEFAULT_PORT;
	}
	
	private boolean validate (String value) {
		return value != null && value.length() >= 2;
	}
	
	public String toString() {
		return name + ":" + port;
	}
	
}
