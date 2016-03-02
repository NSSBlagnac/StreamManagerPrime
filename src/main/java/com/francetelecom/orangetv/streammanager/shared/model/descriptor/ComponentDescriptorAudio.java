package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

/**
 * Stream content: 0x06
 * audio: Component type: 0x03
 * audio description : Component type: 0x40
 * 
 * @author sylvie
 * 
 */
public class ComponentDescriptorAudio extends AbstractComponentDescriptor {

	private static final long serialVersionUID = 1L;

	private boolean audioDescription = false;
	private boolean dolby = false;
	private boolean dts = false;

	public boolean isDolby() {
		return this.dolby;
	}

	public boolean isDts() {
		return this.dts;
	}

	public boolean isAudioDescription() {
		return this.audioDescription;
	}

	// ------------------------------------------ constructor
	public ComponentDescriptorAudio() {
		this(Language.FRE, false, false, false);
	}

	public ComponentDescriptorAudio(String langCode, boolean audioDescription, boolean dolby, boolean dts) {
		this(Language.get(langCode), audioDescription, dolby, dts);
	}

	public ComponentDescriptorAudio(String langCode, String componentTypeCode, boolean dolby, boolean dts) {
		super(Type.AUDIO, langCode, getStreamContent(dolby, dts), componentTypeCode);

		this.audioDescription = (this.getComponentType() == ComponentType.AUDIO_AAC_AD);
		this.dolby = dolby;
		this.dts = dts;
	}

	public ComponentDescriptorAudio(Language language, boolean audioDescription, boolean dolby, boolean dts) {
		super(Type.AUDIO, language, getComponentType(audioDescription, dolby, dts));
		this.audioDescription = audioDescription;
		this.dolby = dolby;
		this.dts = dts;
	}

	public static ComponentType getComponentType(boolean audioDescription, boolean dolby, boolean dts) {

		StreamContent streamContent = getStreamContent(dolby, dts);

		switch (streamContent) {
		case AUDIO_AAC: {
			return (audioDescription) ? ComponentType.AUDIO_AAC_AD : ComponentType.AUDIO_AAC;
		}
		case AUDIO_AC3: {
			return (audioDescription) ? ComponentType.AUDIO_AC3_AD : ComponentType.AUDIO_AC3;
		}
		case AUDIO_DTS:
			return ComponentType.AUDIO_DTS;

		default:
			return ComponentType.AUDIO_AAC;
		}

	}

	public static StreamContent getStreamContent(boolean dolby, boolean dts) {

		if (dolby) {
			return StreamContent.AUDIO_AC3;
		} else if (dts) {
			return StreamContent.AUDIO_DTS;
		} else {
			return StreamContent.AUDIO_AAC;
		}
	}

}
