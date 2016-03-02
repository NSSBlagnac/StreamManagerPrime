package com.francetelecom.orangetv.streammanager.client.panel.videoinfo;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoStatus;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class CreateVideoInfoPanel extends AbstractVideoInfoPanel {

	private final static Logger log = Logger.getLogger("CreateStreamInfoPanel");

	// ----------------------- overriding AbstractStreamInfoPanel
	@Override
	protected Logger logger() {
		return log;
	}

	@Override
	public void setData(FullVideoInfo videoInfo) {
		this.videoInfo = videoInfo;
		this.refreshStatus(FullVideoStatus.NEW);

		this.refreshVideoInfo(videoInfo);
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

	// --------------------------------- constructor
	public CreateVideoInfoPanel(int maxUploadSizeMo) {
		super(maxUploadSizeMo);
		this.initComposants();
		this.initWidget(super.buildMainPanel());
	}

	// ------------------------------- private methods
	private void initComposants() {
		this.wEnabledCheckbox.setEnabled(false);

	}

}
