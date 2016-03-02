package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DVB Descriptor 0x54: Content descriptor
 * 
 * The intention of the content descriptor is to provide
 * classification information for an event.
 * 
 * @author sylvie
 * 
 */
public class ContentDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private List<ContentNibbleLevel2> listCategories = new ArrayList<ContentNibbleLevel2>();

	public List<ContentNibbleLevel2> getistCategories() {
		return this.listCategories;
	}

	public void addCategory(String parentCode, String categoryCode) {

		this.listCategories.add(ContentNibbleLevel2.get(parentCode, categoryCode));
	}
}
