package com.francetelecom.orangetv.streammanager.client;

import com.francetelecom.orangetv.streammanager.client.controller.AppController;
import com.francetelecom.orangetv.streammanager.client.service.IStreamMulticatService;
import com.francetelecom.orangetv.streammanager.client.service.IStreamMulticatServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StreamManagerEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		IStreamMulticatServiceAsync service = GWT.create(IStreamMulticatService.class);
		AppController controller = new AppController(service);
		controller.go(RootPanel.get("eitContainer"));

	}

}
