package com.francetelecom.orangetv.streammanager.server.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.francetelecom.orangetv.streammanager.shared.util.EitException;

/**
 * Gestion des informations TS d'un stream obtenu a partir de dvb_print_si
 * 
 * @author ndmz2720
 *
 */
public class SIStreamManager {

	private static final Logger log = Logger.getLogger(SIStreamManager.class.getName());
	private static SIStreamManager instance;

	public static SIStreamManager get() {
		if (instance == null) {
			instance = new SIStreamManager();
		}
		return instance;
	}

	private SIStreamManager() {
	}

	// -----------------------------------------------------
	private static final String XML_PMT = "PMT";
	private static final String XML_ES = "ES";
	private static final String XML_TELX_DESC = "TELX_DESC";
	private static final String XML_DESC = "DESC";
	private static final String XML_AC3_DESC = "AC3_DESC";
	private static final String XML_MPEG4_DESC = "MPEG4_AUDIO_DESC";
	private static final String XML_AUDIO_LANGUAGE_DESC = "AUDIO_LANGUAGE_DESC";

	private static final String ATT_PROGRAM = "program";
	private static final String ATT_PCR_PID = "pcrpid";

	private static final String ATT_STREAM_TYPE = "streamtype";
	private static final String ATT_STREAM_TYPE_TXT = "streamtype_txt";
	private static final String ATT_LANGUAGE = "language";
	private static final String ATT_TYPE = "type";
	private static final String ATT_TYPE_TXT = "type_txt";

	private Document document;
	private Element racine;

	// -------------------------------------- public methods
	public SITablePMT readTablePMT(String xmlPmt) throws EitException {

		if (xmlPmt == null) {
			return null;
		}

		SAXBuilder sxb = new SAXBuilder();

		try {

			document = sxb.build(new StringReader(xmlPmt));

			racine = document.getRootElement();

			Element pmtElement = racine.getChild(XML_PMT);

			int programId = this.extractIntAttribute(pmtElement, ATT_PROGRAM, -1);
			int pcrpid = this.extractIntAttribute(pmtElement, ATT_PCR_PID, -1);

			SITablePMT tablePmt = new SITablePMT(programId, pcrpid);

			// chercher les pistes audio/video
			List<Element> listEsElements = pmtElement.getChildren(XML_ES);
			if (listEsElements != null) {
				for (Element esElement : listEsElements) {
					String streamTypeTxt = this.extractStringAttribute(esElement, ATT_STREAM_TYPE_TXT, null);
					if (streamTypeTxt != null) {

						streamTypeTxt = streamTypeTxt.toLowerCase();
						if (streamTypeTxt.contains("video")) {
							tablePmt.getListVideoTracks().add(this.buildSIVideoTrack(esElement));
						} else if (streamTypeTxt.contains("audio")) {
							SIAudioTrack audioTrack = this.buildSIAudioTrack(esElement);
							if (audioTrack != null) {
								tablePmt.getListAudioTracks().add(audioTrack);
							}
						} else if (streamTypeTxt.contains("private data")) {

							// chercher les pistes audio
							SIAudioTrack audioTrack = this.buildSIAudioTrack(esElement);
							if (audioTrack != null) {
								tablePmt.getListAudioTracks().add(audioTrack);
							} else {
								// chercher les sous-titres
								this.buildListSubtitleFromEsPrivateData(tablePmt, esElement);
							}
						}
					}
				}

			}

			return tablePmt;

		} catch (Exception e) {
			String errorMessage = "Error in parsing PMT table: " + e.getMessage();
			log.severe(errorMessage);
			throw new EitException(errorMessage);
		}
	}

	// ----------------------------- private methods
	/*
	 * <ES pid="725" streamtype="0x06" streamtype_txt="13818-1 PES private data">
	* <DESC id="0x56" length="25" value="66726109006672611089667261288864657509526465752950">
	* <TELX_DESC language="fra" type="0x1" type_txt="Initial teletext page" mag="1" page="0x0x"/>
	* <TELX_DESC language="fra" type="0x2" type_txt="Teletext subtitle page" mag="0" page="0x137x"/>
	* <TELX_DESC language="fra" type="0x5" type_txt="Teletext subtitle page for hearing impaired people schedule page" mag="0" page="0x136x"/>
	* <TELX_DESC language="deu" type="0x1" type_txt="Initial teletext page" mag="1" page="0x82x"/>
	* <TELX_DESC language="deu" type="0x5" type_txt="Teletext subtitle page for hearing impaired people schedule page" mag="1" page="0x80x"/>
	* </DESC>
	* </ES>
	 */
	private void buildListSubtitleFromEsPrivateData(SITablePMT tablePmt, Element esElement) {

		List<Element> listDescElements = esElement.getChildren(XML_DESC);
		if (listDescElements != null) {
			for (Element descElement : listDescElements) {

				List<Element> listTelxElements = descElement.getChildren(XML_TELX_DESC);
				if (listTelxElements != null) {
					for (Element telexElement : listTelxElements) {
						tablePmt.getListSubtitleTracks().add(this.buildSISubtitleTrack(telexElement));
					}
				}
			}
		}
	}

	private SISubtitleTrack buildSISubtitleTrack(Element element) {
		SISubtitleTrack subtitleTrack = new SISubtitleTrack();

		subtitleTrack.type = this.extractStringAttribute(element, ATT_TYPE, "");
		subtitleTrack.typeTxt = this.extractStringAttribute(element, ATT_TYPE_TXT, "");
		subtitleTrack.language = this.extractStringAttribute(element, ATT_LANGUAGE, "");

		String typeTxt = subtitleTrack.typeTxt.toLowerCase();
		if (typeTxt.contains("impaired")) {
			subtitleTrack.hardOfHearing = true;
		}

		return subtitleTrack;
	}

	private SIVideoTrack buildSIVideoTrack(Element element) {
		SIVideoTrack videoTrack = new SIVideoTrack();

		videoTrack.streamType = this.extractStringAttribute(element, ATT_STREAM_TYPE, "");
		videoTrack.streamTypeTxt = this.extractStringAttribute(element, ATT_STREAM_TYPE_TXT, "");
		return videoTrack;
	}

	/*
	 * <ES pid="425" streamtype="0x06" streamtype_txt="13818-1 PES private data">
	* <DESC id="0x0a" length="4" value="656e6700">
	*    <AUDIO_LANGUAGE_DESC language="eng" audiotype="0" audiotype_txt="undefined"/>
	* </DESC>
	* <DESC id="0x7a" length="2" value="80c4">
	*    <AC3_DESC component_type_flag="1" component_type="196" bsid_flag="0" bsid="0" mainid_flag="0" mainid="0" asvc_flag="0" asvc="0" mixinfoexists="0" substream1_flag="0" substream1="0" substream2_flag="0" substream2="0" substream3_flag="0" substream3="0"/>
	* </DESC>
	* </ES>
	 */
	/*
	 * <ES pid="325" streamtype="0x11" streamtype_txt="14496-3 Audio with LATM transport syntax (14496-3/AMD 1)">
	* <DESC id="0x0a" length="4" value="66726100">
	* <AUDIO_LANGUAGE_DESC language="fra" audiotype="0" audiotype_txt="undefined"/>
	* </DESC>
	* <DESC id="0x1c" length="1" value="58">
	* <MPEG4_AUDIO_DESC audio_profile_and_level="0x58"/>
	* </DESC>
	* <DESC id="0x7c" length="2" value="5800">
	* <AAC_DESC profile_and_level="0x58" aac_type_flag="0" aac_type="0x00"/>
	* </DESC>
	* </ES>
	 */

	private SIAudioTrack buildSIAudioTrack(Element element) {
		SIAudioTrack audioTrack = new SIAudioTrack();

		audioTrack.streamType = this.extractStringAttribute(element, ATT_STREAM_TYPE, "");
		audioTrack.streamTypeTxt = this.extractStringAttribute(element, ATT_STREAM_TYPE_TXT, "");

		// recherche audiolanguage description
		List<Element> listDescElements = element.getChildren(XML_DESC);
		if (listDescElements != null) {
			for (Element descElement : listDescElements) {

				if (audioTrack.language == null && this.hasChild(descElement, XML_AUDIO_LANGUAGE_DESC)) {

					Element audioLangElement = descElement.getChild(XML_AUDIO_LANGUAGE_DESC);
					audioTrack.language = this.extractStringAttribute(audioLangElement, ATT_LANGUAGE, "");
					continue; // next descElement
				}
				if (this.hasChild(descElement, XML_AC3_DESC)) {
					audioTrack.dolby = true;
					continue; // next descElement
				}

				if (this.hasChild(descElement, XML_MPEG4_DESC)) {
					audioTrack.mpeg4 = true;
					continue; // next descElement
				}
			}
		}

		return (audioTrack.language == null) ? null : audioTrack;
	}

	private boolean hasChild(Element element, String keyChild) {
		Element child = element.getChild(keyChild);
		return (child != null);
	}

	private int extractIntAttribute(Element element, String attributeName, int defaultValue) {

		int value;
		if (element == null || attributeName == null) {
			return defaultValue;
		}

		Attribute attribute = element.getAttribute(attributeName);
		try {
			value = attribute.getIntValue();
		} catch (DataConversionException e) {
			log.severe("Error when getting attribute " + attributeName + " from element " + element.getName() + " !");
			value = defaultValue;
		}

		return value;
	}

	private String extractStringAttribute(Element element, String attributeName, String defaultValue) {

		String value;
		if (element == null || attributeName == null) {
			return defaultValue;
		}

		Attribute attribute = element.getAttribute(attributeName);
		value = attribute.getValue();

		return value;
	}

	// ===================================== INNER CLASS
	public static class SITablePMT {

		private final int programId;
		private final int pcrpid;

		private final List<SIVideoTrack> listVideoTracks = new ArrayList<>(1);

		private final List<SIAudioTrack> listAudioTracks = new ArrayList<>(3);

		private final List<SISubtitleTrack> listSubtitileTracks = new ArrayList<>(3);

		// ---------------------------------- constructor
		private SITablePMT(int programId, int pcrpid) {
			this.programId = programId;
			this.pcrpid = pcrpid;
		}

		// ---------------------------------- accessors
		public int getProgramId() {
			return programId;
		}

		public int getPcrpid() {
			return pcrpid;
		}

		public List<SIVideoTrack> getListVideoTracks() {
			return listVideoTracks;
		}

		public List<SIAudioTrack> getListAudioTracks() {
			return listAudioTracks;
		}

		public List<SISubtitleTrack> getListSubtitleTracks() {
			return listSubtitileTracks;
		}

	}

	public static class SIAudioTrack {

		private String streamType;
		private String streamTypeTxt;

		private String language;
		private boolean dolby = false; // AC3
		private boolean mpeg4 = false;

		// ---------------------------- accessors

		public String getStreamType() {
			return streamType;
		}

		public boolean idMpeg4() {
			return this.mpeg4;
		}

		public boolean isDolby() {
			return dolby;
		}

		public String getStreamTypeTxt() {
			return streamTypeTxt;
		}

		public String getLanguage() {
			return language;
		}

		public boolean isAudioDescription() {
			return "qad".equalsIgnoreCase(this.language);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(streamType);
			sb.append(" - ");
			sb.append(streamTypeTxt);

			sb.append(" - ");
			sb.append(language);
			sb.append((dolby ? " - [DOLBY]" : ""));
			sb.append((this.isAudioDescription() ? " - [AD]" : ""));

			return sb.toString();
		}
	}

	public static class SIVideoTrack {

		private String streamType;
		private String streamTypeTxt;

		// -------------------------- accessors
		public String getStreamType() {
			return streamType;
		}

		public String getStreamTypeTxt() {
			return streamTypeTxt;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(streamType);
			sb.append(" - ");
			sb.append(streamTypeTxt);

			return sb.toString();
		}

	}

	public static class SISubtitleTrack {

		private String type;
		private String typeTxt;

		private String language;
		private boolean hardOfHearing;

		// ------------------------ accessors
		public String getType() {
			return type;
		}

		public String getTypeTxt() {
			return typeTxt;
		}

		public String getLanguage() {
			return language;
		}

		public boolean isHardOfHearing() {
			return hardOfHearing;
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(" - ");
			sb.append(typeTxt);
			sb.append(" - ");
			sb.append(language);
			sb.append((this.hardOfHearing ? " - [HOH]" : ""));

			return sb.toString();
		}

	}
}
