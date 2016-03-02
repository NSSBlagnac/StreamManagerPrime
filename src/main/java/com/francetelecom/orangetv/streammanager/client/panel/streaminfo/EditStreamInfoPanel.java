package com.francetelecom.orangetv.streammanager.client.panel.streaminfo;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.StatusUtils;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;

/**
 * Panel d'édition des infos annexes d'un stream
 * 
 * @author ndmz2720
 * 
 */
public class EditStreamInfoPanel extends AbstractStreamInfoPanel implements CssConstants {

	private final static Logger log = Logger.getLogger("EditStreamPanel");

	@Override
	protected Logger logger() {
		return log;
	}

	// --------------------------------- constructor
	public EditStreamInfoPanel() {
		this.initComposants();
		this.initWidget(super.buildMainPanel());
	}

	// ------------------------- public method

	@Override
	protected void _setData(StreamInfo streamInfo) {

		StreamStatus status = streamInfo.getStatus();
		boolean readOnly = !streamInfo.getDtoProtection().getActionUpdate().isActive();

		if (this.streamInfo == null || readOnly) {
			this.refreshStreamInfo(streamInfo);
		}

		this.refreshStatus(status, readOnly);
	}

	// ------------------------ Private method
	private void initComposants() {

	}

	private void refreshStatus(StreamStatus status, boolean readOnly) {

		this.pStatus.clear();
		this.pStatus.add(StatusUtils.buildLabelStatus(status));

	}

	private void setReadOnly(boolean readOnly) {

		this.tbLcn.setEnabled(!readOnly);
		this.tbUsi.setEnabled(!readOnly);
		this.tbName.setEnabled(!readOnly);
		this.tbDescription.setEnabled(!readOnly);
		this.tbUser.setEnabled(!readOnly);
		this.tbTripletDvb.setEnabled(!readOnly);
		this.tbUrlAdress.setEnabled(!readOnly);
		this.tbUrlPort.setEnabled(!readOnly);
		this.lbVideoFileList.setEnabled(!readOnly);

		this.wEnabledCheckBox.setEnabled(!readOnly);

		if (this.container != null) {
			this.container.setReadOnly(readOnly);
		}

	}

}
