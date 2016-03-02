package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;

import com.francetelecom.orangetv.streammanager.shared.model.divers.CodeAndDescription;

/**
 * DVB descriptor 0x50: Component descriptor
 * ABstract class for audio, subtitle and aspect radio descriptor
 * 
 * @author sylvie
 * 
 */
public abstract class AbstractComponentDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	// permet de distinguer les sous-classes
	public enum Type {

		AUDIO("audio"), SUBTITLE("subtitle"), ASPECT_RATIO("aspect ratio");

		private String label;

		public String getLabel() {
			return this.label;
		}

		private Type(String label) {
			this.label = label;
		}
	}

	private Type type;
	private String text;
	private String codelang;
	private String codeStreamContent;
	private String codeComponentType;

	public Type getType() {
		return this.type;
	}

	public StreamContent getStreamContent() {
		return StreamContent.get(codeStreamContent);
	}

	public ComponentType getComponentType() {
		return ComponentType.get(getStreamContent(), codeComponentType);
	}

	public String getText() {
		return text;
	}

	public Language getLang() {
		return Language.get(codelang);
	}

	// ------------------------------------------ constructor
	public AbstractComponentDescriptor() {
		this(null, Language.FRE, null);
	}

	public AbstractComponentDescriptor(Type type, String langCode, StreamContent streamContent, String componentTypeCode) {
		this(type, Language.get(langCode), ComponentType.get(streamContent, componentTypeCode));
	}

	public AbstractComponentDescriptor(Type type, Language language, ComponentType componentType) {

		this.init(type, language, componentType);
	}

	protected void init(Type type, Language language, ComponentType componentType) {

		this.type = type;
		this.codeStreamContent = (componentType == null) ? null : ((componentType.getParent() == null) ? null
				: componentType.getParent().getCode());

		this.text = (type == null) ? "" : type.getLabel();
		this.codelang = language.getCode();
		this.codeComponentType = (componentType == null) ? null : componentType.getCode();
	}

	// ==================== INNER CLASS ================
	public static enum StreamContent {

		// aspect ratio
		ASPECT_RATIO_MPEG(Type.ASPECT_RATIO, "0x01", "MPEG-2 video aspect ratio"), ASPECT_RATIO_H264(Type.ASPECT_RATIO,
				"0x05", "H.264/AVC aspect ratio"),
		// audio
		AUDIO_AAC(Type.AUDIO, "0x06", "HE-AAC audio"), AUDIO_AC3(Type.AUDIO, "0x04", "AC-3 audio modes (dolby)"), AUDIO_DTS(
				Type.AUDIO, "0x07", "DTS audio modes"),

		// subtitle
		SUBTITLE(Type.SUBTITLE, "0x03", "DVB subtitles");

		public static StreamContent get(String code) {

			for (StreamContent streamContent : StreamContent.values()) {
				if (streamContent.getCode().equals(code)) {
					return streamContent;
				}
			}

			return AUDIO_AAC;
		}

		private Type type;

		public Type getType() {
			return this.type;
		}

		private CodeAndDescription codeAndDescription;

		public String getCode() {
			return this.codeAndDescription.getCode();
		}

		public String getDescription() {
			return this.codeAndDescription.getDescription();
		}

		private StreamContent() {
		}

		private StreamContent(Type type, String code, String description) {
			this.type = type;
			this.codeAndDescription = new CodeAndDescription(code, description);
		}
	}

	public static enum ComponentType {

		// audio AAC, AC3 (Dolby), DTS
		AUDIO_AAC_AD(StreamContent.AUDIO_AAC, "0x40", "HE-AAC audio description for the visually impaired"), AUDIO_AAC(
				StreamContent.AUDIO_AAC, "0x03", "HE-AAC audio, stereo"),

		AUDIO_AC3_AD(StreamContent.AUDIO_AC3, "0x10", "AC-3 audio description for the visually impaired"), AUDIO_AC3(
				StreamContent.AUDIO_AC3, "0x75", "AC-3 audio modes"),

		AUDIO_DTS(StreamContent.AUDIO_DTS, "0x7F", "DTS audio modes"),

		// subtitles
		SUBTITLE(StreamContent.SUBTITLE, "0x10", "DVB subtitles (normal) with no monitor aspect ratio criticality"), SUBTITLE_HOH(
				StreamContent.SUBTITLE, "0x20",
				"DVB subtitles (for the hard of hearing) with no monitor aspect ratio criticality"),

		// aspect ratio
		RATIO_H264_SD_4_3(StreamContent.ASPECT_RATIO_H264, "0x01",
				"H.264/AVC standard definition video, 4:3 aspect ratio, 25 Hz"), RATIO_H264_SD_16_9(
				StreamContent.ASPECT_RATIO_H264, "0x03",
				"H.264/AVC standard definition video, 16:9 aspect ratio, 25 Hz"), RATIO_H264_HD_16_9(
				StreamContent.ASPECT_RATIO_H264, "0x0B", "H.264/AVC high definition video, 16:9 aspect ratio, 25 Hz"),

		RATIO_MPEG_HD_4_3(StreamContent.ASPECT_RATIO_MPEG, "0x09",
				"MPEG-2 high definition video, 4:3 aspect ratio, 25 Hz"), RATIO_MPEG_SD_4_3(
				StreamContent.ASPECT_RATIO_MPEG, "0x01", "MPEG-2 video, 4:3 aspect ratio, 25 Hz"), RATIO_MPEG_HD_16_9(
				StreamContent.ASPECT_RATIO_MPEG, "0x0A",
				"MPEG-2 high definition video, 16:9 aspect ratio with pan vectors, 25 Hz"), RATIO_MPEG_SD_16_9(
				StreamContent.ASPECT_RATIO_MPEG, "0x02", "MPEG-2 video, 16:9 aspect ratio with pan vectors, 25 Hz");

		public static ComponentType get(StreamContent streamContent, String code) {

			for (ComponentType componentType : ComponentType.values()) {
				if (streamContent == componentType.getParent() && componentType.getCode().equals(code)) {
					return componentType;
				}
			}
			return AUDIO_AAC;
		}

		private StreamContent parent;

		public StreamContent getParent() {
			return this.parent;
		}

		private CodeAndDescription codeAndDescription;

		public String getCode() {
			return this.codeAndDescription.getCode();
		}

		public String getDescription() {
			return this.codeAndDescription.getDescription();
		}

		private ComponentType() {
		}

		private ComponentType(StreamContent parent, String code, String description) {
			this.parent = parent;
			this.codeAndDescription = new CodeAndDescription(code, description);
		}
	}
}