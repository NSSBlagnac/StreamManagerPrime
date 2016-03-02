package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import com.francetelecom.orangetv.streammanager.shared.model.divers.CodeAndDescription;

/**
 * Stream content: 0x05
 * 
 * @author sylvie
 * 
 */
public class ComponentDescriptorAspectRatio extends AbstractComponentDescriptor {

	private static final long serialVersionUID = 1L;

	public AspectRatio getAspectRatio() {
		return this.getAspectRatioFromComponentType();
	}

	// ------------------------------------------ constructor
	public ComponentDescriptorAspectRatio() {
		this(Language.FRE, null);
	}

	public ComponentDescriptorAspectRatio(String langCode, StreamContent streamContent, String componentTypeCode) {
		super(Type.ASPECT_RATIO, langCode, streamContent, componentTypeCode);
	}

	public ComponentDescriptorAspectRatio(String langCode, String componentTypeCode) {
		super(Type.ASPECT_RATIO, langCode, StreamContent.ASPECT_RATIO_H264, componentTypeCode);

	}

	public ComponentDescriptorAspectRatio(Language language, AspectRatio aspectRatio) {
		super.init(Type.ASPECT_RATIO, language, getComponentTypeFromAspectRatio(aspectRatio));
	}

	// ---------------------------------------------------- private method
	private ComponentType getComponentTypeFromAspectRatio(AspectRatio aspectRatio) {

		if (aspectRatio != null) {
			switch (aspectRatio) {
			case AR_H264_HD_16_9:
				return ComponentType.RATIO_H264_HD_16_9;

			case AR_H264_SD_16_9:
				return ComponentType.RATIO_H264_SD_16_9;
			case AR_H264_SD_4_3:
				return ComponentType.RATIO_H264_SD_4_3;
			case AR_MPEG_HD_4_3:
				return ComponentType.RATIO_MPEG_HD_4_3;
			case AR_MPEG_HD_16_9:
				return ComponentType.RATIO_MPEG_HD_16_9;
			case AR_MPEG_SD_4_3:
				return ComponentType.RATIO_MPEG_SD_4_3;
			case AR_MPEG_SD_16_9:
				return ComponentType.RATIO_MPEG_SD_16_9;

			}
		}

		return ComponentType.RATIO_H264_HD_16_9;
	}

	private AspectRatio getAspectRatioFromComponentType() {

		ComponentType componentType = this.getComponentType();

		if (componentType != null) {
			switch (componentType) {
			case RATIO_H264_HD_16_9:
				return AspectRatio.AR_H264_HD_16_9;
			case RATIO_H264_SD_4_3:
				return AspectRatio.AR_H264_SD_4_3;
			case RATIO_H264_SD_16_9:
				return AspectRatio.AR_H264_SD_16_9;
			case RATIO_MPEG_HD_4_3:
				return AspectRatio.AR_MPEG_HD_4_3;
			case RATIO_MPEG_HD_16_9:
				return AspectRatio.AR_MPEG_HD_16_9;
			case RATIO_MPEG_SD_4_3:
				return AspectRatio.AR_MPEG_SD_4_3;
			case RATIO_MPEG_SD_16_9:
				return AspectRatio.AR_MPEG_SD_16_9;

			default:
				return AspectRatio.AR_H264_HD_16_9;

			}
		}

		return AspectRatio.AR_H264_HD_16_9;

	}

	// =============== INNER CLASS =============
	public static enum AspectRatio {

		AR_H264_SD_4_3("AR_H264_SD_4_3", "H264 SD 4/3"), AR_H264_SD_16_9("AR_H264_SD_16_9", "H264 SD 16/9"), AR_H264_HD_16_9(
				"AR_HD_16_9", "H264 HD 16/9"),

		AR_MPEG_HD_4_3("AR_MPEG_HD_4_3", "MPEG HD 4/3"), AR_MPEG_HD_16_9("AR_HD_16_9", "MPEG HD 19/9"), AR_MPEG_SD_4_3(
				"AR_MPEG_SD_4_3", "MPEG SD 4/3"), AR_MPEG_SD_16_9("AR_MPEG_SD_16_9", "MPEG SD 16/9");

		public static AspectRatio get(String code) {
			for (AspectRatio aspectRation : AspectRatio.values()) {
				if (aspectRation.getCode().equals(code)) {
					return aspectRation;
				}
			}
			return AR_H264_SD_16_9;
		}

		private CodeAndDescription codeAndDescription;

		public String getCode() {
			return this.codeAndDescription.getCode();
		}

		public String getDescription() {
			return this.codeAndDescription.getDescription();
		}

		private AspectRatio() {
		}

		private AspectRatio(String code, String description) {
			this.codeAndDescription = new CodeAndDescription(code, description);
		}
	}

}
