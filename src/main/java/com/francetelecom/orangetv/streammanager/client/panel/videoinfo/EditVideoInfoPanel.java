package com.francetelecom.orangetv.streammanager.client.panel.videoinfo;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoStatus;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class EditVideoInfoPanel extends AbstractVideoInfoPanel {
	private final static Logger log = Logger.getLogger("EditVideoInfoPanel");

	@Override
	protected Logger logger() {
		return log;
	}

	// --------------------------------- constructor
	public EditVideoInfoPanel(int maxUploadSizeMo) {
		super(maxUploadSizeMo);
		this.initComposants();
		this.initWidget(super.buildMainPanel());
	}

	// --------------------------------- Overriding Widget

	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				tbDescription.setFocus(true);

			}
		});
	}

	// ------------------------- public method

	@Override
	public void setData(FullVideoInfo videoInfo) {

		FullVideoStatus status = videoInfo.getFullVideoStatus();

		if (this.videoInfo == null) {
			this.refreshVideoInfo(videoInfo);
		}

		this.refreshStatus(status);

	}

	// ------------------------ Private method
	private void initComposants() {

	}

}
