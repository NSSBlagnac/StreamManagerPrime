package com.francetelecom.orangetv.streammanager.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class LocalStorageManager {
	
	public static final String KEY_EIT = "eitinjector.json";
	public static final String KEY_SERVER = "eitinjector.server";
	
	private static final LocalStorageManager impl = GWT.create(LocalStorageManager.class);
	public static final LocalStorageManager get() {
		return impl;
	}
	
	private Boolean localStorageSupported = null;
	private Storage storage;
	
	private Map<String, String> mapIfNotLocalStorageSupported;
		
	//--------------------------------------------- constructeur
	private LocalStorageManager() {
		this.initStorage();
	}

	//---------------------------------------------- public methods
    public boolean isLocalStorageSupported() {
    	return this.localStorageSupported;
    }
    public void removeInfoServer () {
    	this.removeKey(KEY_SERVER);
    }
    public void clearAll() {
    	if (localStorageSupported) {
    		this.storage.clear();
    	} else {
    		this.mapIfNotLocalStorageSupported.clear();
    	}
    }
    /**
     * saisie utilisateur sauvegardés sous forme d'un objet json stringifié
     */
	public void storeEitInfo(final String jsonEitInfo) {
		this.storeKeyValue(KEY_EIT, jsonEitInfo);
	}

	public String retrieveEitInfo() {
		return this.getValueFromKey(KEY_EIT);
	}
	
	/**
	 * serverName:port
	 * @param serverInfo
	 */
	public void storeServerInfo(final String serverInfo) {
		this.storeKeyValue(KEY_SERVER, serverInfo);
	}
	public String retrieveServerInfo() {
		return this.getValueFromKey(KEY_SERVER);
	}

	//---------------------------------------------- private methods
	private void initStorage()  {

		this.storage =  Storage.getLocalStorageIfSupported();
		if (storage == null) {
			this.localStorageSupported = false;
			this.mapIfNotLocalStorageSupported = new HashMap<String, String>();
		} else {
			this.localStorageSupported = true;
		}

}
	private void removeKey(final String key) {
		if (key == null) return;
		if (this.localStorageSupported) {
			this.storage.removeItem(key);
		}
		else {
			this.mapIfNotLocalStorageSupported.remove(key);
		}
	}
	private void storeKeyValue(final String key, final String value) {
		if (key == null || value == null) return;
		if (this.localStorageSupported) {
			this.storage.setItem(key,value);
		}
		else {
			this.mapIfNotLocalStorageSupported.put(key, value);
		}
	}
	private String getValueFromKey(final String key) {
		if (key == null) return null;

		if (this.localStorageSupported) {
			return this.storage.getItem(key);
		}
		else {
			return this.mapIfNotLocalStorageSupported.get(key);
		}
	}

}
