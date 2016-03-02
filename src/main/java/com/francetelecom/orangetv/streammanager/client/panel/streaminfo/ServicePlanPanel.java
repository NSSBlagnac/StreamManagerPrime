package com.francetelecom.orangetv.streammanager.client.panel.streaminfo;

import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel proposant les modifications du service plan (tsp/esp)
 * en fonction des donnees du StreamInfo en cours d'édition
 * 
 * @author ndmz2720
 *
 */
public class ServicePlanPanel extends AbstractPanel {

	protected final HorizontalPanel main = new HorizontalPanel();

	private static final String INF = "&lt;";
	private static final String SUP = "&gt;";
	private static final String SPACE = "&nbsp;";
	private static final String G = "\"";

	private final TspPanel tspPanel = new TspPanel();
	private final EspPanel espPanel = new EspPanel();

	// --------------------------------- constructor
	public ServicePlanPanel() {
		this.initComposants();
		this.initWidget(this.buildMainPanel());
	}

	// ------------------------------------------- package method
	void setDatas(StreamInfo streamInfo) {
		this.espPanel.setDatas(streamInfo);
		this.tspPanel.setDatas(streamInfo);
	}

	// ------------------------------------------- private methods
	private Widget buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);
		this.main.setBorderWidth(1);
		this.main.setStyleName(STYLE_SP_DIV);

		final VerticalPanel vpEsp = new VerticalPanel();
		vpEsp.setSpacing(PANEL_SPACING);
		Label labelEsp = new Label("ESP");
		labelEsp.addStyleName(STYLE_SP_TITLE);
		vpEsp.add(labelEsp);
		vpEsp.add(this.espPanel);

		final VerticalPanel vpTsp = new VerticalPanel();
		vpTsp.setSpacing(PANEL_SPACING);
		Label labelTsp = new Label("TSP");
		labelTsp.addStyleName(STYLE_SP_TITLE);
		vpTsp.add(labelTsp);
		vpTsp.add(this.tspPanel);

		this.main.add(vpEsp);
		this.main.add(vpTsp);

		return this.main;
	}

	private void initComposants() {
		// TODO Auto-generated method stub

	}

	// -------------------------- Element HTML dans les panel de suggestion xml
	// et json
	private Element createDiv(String text, String styleName) {
		Element div = this.createDiv(styleName);
		div.setInnerHTML(text);
		return div;
	}

	private Element createDiv(String styleName) {
		Element div = DOM.createDiv();
		div.setClassName(styleName);
		return div;
	}

	private Element createSpan(String text, String styleName) {
		Element span = DOM.createSpan();
		span.addClassName(styleName);
		span.setInnerHTML(text);
		return span;
	}

	private Element createJsonLine(String text) {
		return createDiv(text, STYLE_SP_DIV);
	}

	// "Srv_List": [
	private Element createJsonLineSymbole(String balise, String symbole) {

		Element item = createDiv(STYLE_SP_DIV);
		item.appendChild(createSpan(G + balise + G + ":", STYLE_SP_SPAN));
		item.appendChild(createSpan(symbole + "", STYLE_SP_SPAN));
		return item;
	}

	// "EPG_ID": 809,
	private Element createJsonLine(String balise, int value) {

		Element item = createDiv(STYLE_SP_DIV);
		item.appendChild(createSpan(G + balise + G + ":", STYLE_SP_SPAN));
		item.appendChild(createSpan(SPACE + value + SPACE, STYLE_SP_SPAN_DATA));
		item.appendChild(createSpan(",", STYLE_SP_SPAN));
		return item;
	}

	// "PIP_allowed": true,
	private Element createJsonLine(String balise, boolean value) {

		Element item = createDiv(STYLE_SP_DIV);
		item.appendChild(createSpan(G + balise + G + ":", STYLE_SP_SPAN));
		item.appendChild(createSpan(SPACE + value + SPACE, STYLE_SP_SPAN_DATA));
		item.appendChild(createSpan(",", STYLE_SP_SPAN));
		return item;
	}

	// "Sht_nme": "HD CSA 2",
	private Element createJsonLine(String balise, String value) {

		value = (value == null) ? "???" : value;
		Element item = createDiv(STYLE_SP_DIV);
		item.appendChild(createSpan(G + balise + G + ":", STYLE_SP_SPAN));
		item.appendChild(createSpan(SPACE + G + value + G + SPACE, STYLE_SP_SPAN_DATA));
		item.appendChild(createSpan(",", STYLE_SP_SPAN));
		return item;
	}

	// ex <USI>2502</USI>
	private Element createXmlLine(String balise, String value) {

		value = (value == null) ? "???" : value;

		Element item = createDiv(STYLE_SP_DIV);
		item.appendChild(createSpan(INF + balise + SUP, STYLE_SP_SPAN));
		item.appendChild(createSpan(value, STYLE_SP_SPAN_DATA));
		item.appendChild(createSpan(INF + "/" + balise + SUP, STYLE_SP_SPAN));

		return item;
	}

	private Element createXmlLineBegin(String balise) {
		return createDiv(INF + balise + SUP, STYLE_SP_DIV);
	}

	private Element createXmlLineEnd(String balise) {
		return createDiv(INF + "/" + balise + SUP, STYLE_SP_DIV);
	}

	// ============================== INNER CLASS

	private abstract class AbstractAreaPanel extends Composite {

		private final SimplePanel lineContainer = new SimplePanel();
		protected Element divArea;

		private AbstractAreaPanel() {
			this.divArea = this.lineContainer.getElement();
			this.initWidget(this.lineContainer);
		}

		void setDatas(StreamInfo streamInfo) {

			this.divArea.removeAllChildren();
			this._setDatas(streamInfo);
		}

		protected abstract void _setDatas(StreamInfo streamInfo);

	}

	private class TspPanel extends AbstractAreaPanel {

		@Override
		protected void _setDatas(StreamInfo streamInfo) {

			/*
			 *   <IPSrv>
			    <USI>2502</USI>
			    <ON_ID>167</ON_ID>
			    <TS_ID>3</TS_ID>
			    <S_ID>0</S_ID>
			    <Type>1</Type>
			    <IP>232.0.6.2</IP>
			    <Port>8200</Port>
			    <Ptcl>1</Ptcl>
			    
			    <C_R>1</C_R>
			    <A_C>0</A_C>
			  </IPSrv>
			 */

			// <IPSrv>
			this.divArea.appendChild(createXmlLineBegin("IPSrv"));
			// <USI>2502</USI>
			this.divArea.appendChild(createXmlLine("USI", streamInfo.getUsi() + ""));

			// TripletDvb
			TripletDvb tripletDvb = new TripletDvb(streamInfo.getTripletDvd());
			// <ON_ID>167</ON_ID>
			this.divArea.appendChild(createXmlLine("ON_ID", tripletDvb.getOnid() + ""));
			// <TS_ID>3</TS_ID>
			this.divArea.appendChild(createXmlLine("TS_ID", tripletDvb.getTsid() + ""));
			// <S_ID>0</S_ID>
			this.divArea.appendChild(createXmlLine("S_ID", tripletDvb.getSid() + ""));

			// <Type>1</Type>
			this.divArea.appendChild(createXmlLine("Type", "1"));
			// <IP>232.0.6.2</IP>
			this.divArea.appendChild(createXmlLine("IP", streamInfo.getAddress()));
			// <Port>8200</Port>
			this.divArea.appendChild(createXmlLine("Port", streamInfo.getPort() + ""));
			// <Ptcl>1</Ptcl>
			this.divArea.appendChild(createXmlLine("Ptcl", "1"));
			// <C_R>1</C_R>
			this.divArea.appendChild(createXmlLine("C_R", "1"));
			// <A_C>0</A_C>
			this.divArea.appendChild(createXmlLine("A_C", "0"));

			// </IPSrv>
			this.divArea.appendChild(createXmlLineEnd("IPSrv"));

		}

	}

	private class EspPanel extends AbstractAreaPanel {

		@Override
		protected void _setDatas(StreamInfo streamInfo) {
			/*
			 * {
			"LCN": 809,
			"Srv_List": [
			 {
			"EPG_ID": 809,
			"USI": 2502,
			"Src": "IP",
			"S_Type": 1,
			"Sht_nme": "HD CSA 2",
			"PIP_allowed": true,
			"Lng_nme": "IP HD CSA 2 (eitinjector 2)",
			"Kind": "Cha\u00eenes de la TNT",
			"ResT": "HD",
			"MoralityLevel": 2,
			"EPG_reco_rule_tag": "reco_epg",
			"TS": 1,
			"PVR": [
			"Pub"
			],
			"ConfigId": 23,
			"List_Rights_ID": []
			}]}
			 */
			// {
			this.divArea.appendChild(createJsonLine("{"));
			// "LCN": 809,
			this.divArea.appendChild(createJsonLine("LCN", streamInfo.getLcn()));
			// "Srv_List": [
			this.divArea.appendChild(createJsonLineSymbole("Srv_List", "["));
			// {
			this.divArea.appendChild(createJsonLine("{"));

			// "EPG_ID": 809,
			this.divArea.appendChild(createJsonLine("EPG_ID", streamInfo.getLcn()));
			// "USI": 2502,
			this.divArea.appendChild(createJsonLine("USI", streamInfo.getUsi()));
			// "Src": "IP",
			this.divArea.appendChild(createJsonLine("Src", "IP"));
			// "S_Type": 1,
			this.divArea.appendChild(createJsonLine("S_Type", 1));
			// "Sht_nme": "HD CSA 2",
			this.divArea.appendChild(createJsonLine("Sht_nme", streamInfo.getName()));
			// "PIP_allowed": true,
			this.divArea.appendChild(createJsonLine("PIP_allowed", true));
			// "Lng_nme": "IP HD CSA 2 (eitinjector 2)",
			this.divArea.appendChild(createJsonLine("Lng_nme", streamInfo.getDescription()));
			// "Kind": "Cha\u00eenes de la TNT",
			this.divArea.appendChild(createJsonLine("Kind", "Cha\u00eenes IP test"));
			// "ResT": "HD",
			this.divArea.appendChild(createJsonLine("ResT", "HD"));
			// "MoralityLevel": 2,
			this.divArea.appendChild(createJsonLine("MoralityLevel", 0));
			// "EPG_reco_rule_tag": "reco_epg",
			this.divArea.appendChild(createJsonLine("EPG_reco_rule_tag", "reco_epg"));
			// "TS": 1,
			this.divArea.appendChild(createJsonLine("TS", 1));
			// "PVR": [
			this.divArea.appendChild(createJsonLineSymbole("PVR", "["));
			// "Pub"
			this.divArea.appendChild(createJsonLine(G + "Pub" + G));
			// ],
			this.divArea.appendChild(createJsonLine("],"));
			// "ConfigId": 23,
			this.divArea.appendChild(createJsonLine("ConfigId", 23));
			// "List_Rights_ID": []
			this.divArea.appendChild(createJsonLineSymbole("List_Rights_ID", "[]"));
			// }]}
			this.divArea.appendChild(createJsonLine("}]}"));

		}
	}
}
