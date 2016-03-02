package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

/**
 * Stream content: 0x03
 * hoh: Component type 0x20
 * other: Component type ??
 * 
 * @author sylvie
 * 
 */
public class ComponentDescriptorSubtitle extends AbstractComponentDescriptor {

	private static final long serialVersionUID = 1L;
	private boolean hardOfHearing;

	public boolean isHardOfHearing() {
		return this.hardOfHearing;
	}

	// -------------------------------------- constructor
	public ComponentDescriptorSubtitle() {
		this(Language.FRE, false);
	}

	public ComponentDescriptorSubtitle(String langCode, boolean hardOfHearing) {
		this(Language.get(langCode), hardOfHearing);
	}

	public ComponentDescriptorSubtitle(String langCode, String componentTypeCode) {
		super(Type.SUBTITLE, langCode, StreamContent.SUBTITLE, componentTypeCode);

		this.hardOfHearing = (this.getComponentType() == ComponentType.SUBTITLE_HOH);
	}

	public ComponentDescriptorSubtitle(Language language, boolean hardOfHearing) {
		super(Type.SUBTITLE, language, (hardOfHearing) ? ComponentType.SUBTITLE_HOH : ComponentType.SUBTITLE);
		this.hardOfHearing = hardOfHearing;
	}
}
