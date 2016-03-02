package com.francetelecom.orangetv.streammanager.shared.model;

import java.io.Serializable;
import java.util.List;

import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ExtendedEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ListComponentDescriptors;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor;

public class EitEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private ShortEventDescriptor shortEventDescriptor;
	private ExtendedEventDescriptor extendedEventDescriptor;
	private CAIdentifierDescriptor caIdentifierDescriptor;
	private ContentDescriptor contentDescriptor;
	private ShortSmoothingBufferDescriptor shortSmoothingBufferDescriptor;
	private ParentalRatingDescriptor parentalRatingDescriptor;

	private ListComponentDescriptors listComponentDescriptors = new ListComponentDescriptors();
	private PrivateDescriptor privateDescriptor;

	public PrivateDescriptor getPrivateDescriptor() {
		return privateDescriptor;
	}

	public void setPrivateDescriptor(PrivateDescriptor privateDescriptor) {
		this.privateDescriptor = privateDescriptor;
	}

	public CAIdentifierDescriptor getCaIdentifierDescriptor() {
		return caIdentifierDescriptor;
	}

	public void setCaIdentifierDescriptor(CAIdentifierDescriptor caIdentifierDescriptor) {
		this.caIdentifierDescriptor = caIdentifierDescriptor;
	}

	public ShortSmoothingBufferDescriptor getShortSmoothingBufferDescriptor() {
		return shortSmoothingBufferDescriptor;
	}

	public void setShortSmoothingBufferDescriptor(ShortSmoothingBufferDescriptor shortSmoothingBufferDescriptor) {
		this.shortSmoothingBufferDescriptor = shortSmoothingBufferDescriptor;
	}

	public ContentDescriptor getContentDescriptor() {
		return contentDescriptor;
	}

	public void setContentDescriptor(ContentDescriptor contentDescriptor) {
		this.contentDescriptor = contentDescriptor;
	}

	public CAIdentifierDescriptor getCAIdentifierDescriptor() {
		return caIdentifierDescriptor;
	}

	public void setCAIdentifierDescriptor(CAIdentifierDescriptor caIdentifierDescriptor) {
		this.caIdentifierDescriptor = caIdentifierDescriptor;
	}

	public void addComponentDescriptor(AbstractComponentDescriptor componentDescriptor) {
		this.listComponentDescriptors.add(componentDescriptor);
	}

	public List<AbstractComponentDescriptor> getListComponentDescriptors() {
		return listComponentDescriptors;
	}

	public void setListComponentDescriptors(ListComponentDescriptors listComponentDescriptor) {
		this.listComponentDescriptors = listComponentDescriptor;
	}

	public ShortEventDescriptor getShortEventDescriptor() {
		return shortEventDescriptor;
	}

	public void setShortEventDescriptor(ShortEventDescriptor shortEventDescriptor) {
		this.shortEventDescriptor = shortEventDescriptor;
	}

	public ExtendedEventDescriptor getExtendedEventDescriptor() {
		return extendedEventDescriptor;
	}

	public void setExtendedEventDescriptor(ExtendedEventDescriptor extendedEventDescriptor) {
		this.extendedEventDescriptor = extendedEventDescriptor;
	}

	public ParentalRatingDescriptor getParentalRatingDescriptor() {
		return parentalRatingDescriptor;
	}

	public void setParentalRatingDescriptor(ParentalRatingDescriptor parentalRatingDescriptor) {
		this.parentalRatingDescriptor = parentalRatingDescriptor;
	}

}
