package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.util.ArrayList;

public class ListComponentDescriptors extends ArrayList<AbstractComponentDescriptor> implements IDescriptor {

	private static final long serialVersionUID = 1L;

	// ------------------------------------ overriding IDescriptor
	@Override
	public boolean isEnabled() {
		return true;
	}

}
