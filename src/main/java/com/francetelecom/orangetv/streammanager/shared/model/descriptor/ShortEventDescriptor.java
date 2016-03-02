package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;

/**
 * 0x4D
 * The short event descriptor provides the name of the event and a short
 * description of the event in text form
 * 
 * @author sylvie
 * 
 */
public class ShortEventDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String text;
	private String codelang;

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public Language getLang() {
		return Language.get(codelang);
	}

	public ShortEventDescriptor() {
		this(null, null, Language.FRE);
	}

	public ShortEventDescriptor(String name, String text, Language language) {
		this.name = name;
		this.text = text;
		this.codelang = language.getCode();
	}

	public ShortEventDescriptor(String name, String text, String langCode) {
		this(name, text, Language.get(langCode));
	}

}