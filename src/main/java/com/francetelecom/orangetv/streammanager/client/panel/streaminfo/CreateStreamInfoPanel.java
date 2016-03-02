package com.francetelecom.orangetv.streammanager.client.panel.streaminfo;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.StatusUtils;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;

public class CreateStreamInfoPanel extends AbstractStreamInfoPanel implements CssConstants {

	private final static Logger log = Logger.getLogger("CreateStreamInfoPanel");

	// ----------------------- overriding AbstractStreamInfoPanel
	@Override
	protected Logger logger() {
		return log;
	}

	@Override
	protected void _setData(StreamInfo streamInfo) {
		this.streamInfo = streamInfo;
		this.pStatus.add(StatusUtils.buildLabelStatus(StreamStatus.NEW));

		this.refreshStreamInfo(streamInfo);
	}

	// --------------------------------- constructor
	public CreateStreamInfoPanel() {
		this.initComposants();
		this.initWidget(super.buildMainPanel());
	}

	// ------------------------------- private methods
	private void initComposants() {
		this.wEnabledCheckBox.setEnabled(false);
	}

}
