package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel de gestion du repertoire des videos
 * liste et suppression
 * 
 * @author ndmz2720
 *
 */
public class VideoCmdPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("VideoCmdPanel");

	enum ActionVideo {
		list(false, false), delete(true, true), auxfile(true, true), showPmt(false, false);

		private final boolean confirmCommand;
		private final boolean listAfterAction;

		private ActionVideo(boolean confirmCommand, boolean listAfterAction) {
			this.confirmCommand = confirmCommand;
			this.listAfterAction = listAfterAction;
		}
	}

	private final HorizontalPanel main = new HorizontalPanel();

	private final VideoActionButton btListVideo = new VideoActionButton(ActionVideo.list, "list videos");
	private final VideoActionButton btDeleteVideo = new VideoActionButton(ActionVideo.delete, "delete video");
	private final VideoActionButton btCreateAuxFile = new VideoActionButton(ActionVideo.auxfile, "create aux file");
	private final VideoActionButton btShowTablePMT = new VideoActionButton(ActionVideo.showPmt, "Show PMT table");

	private final ListBox lbVideos = new ListBox();
	private final LabelAndListWidget wListVideos = new LabelAndListWidget("videos:", 50, 250, lbVideos, 1);

	private ActionVideo pendingAction = ActionVideo.list;

	// ---------------------------------- overriding AbstractCmdPanel
	@Override
	Logger getLog() {
		return log;
	}

	@Override
	AbstractCmdDatas getDataFromWidget() {

		String videoFilename = (this.pendingAction != ActionVideo.list) ? this.wListVideos.getListUserInput() : null;
		VideoCmdDatas datas = new VideoCmdDatas(this.pendingAction, videoFilename);

		return datas;
	}

	VideoCmdPanel() {
		this.initWidget(this.buildMainPanel());
		this.initComposants();
		this.initHandlers();
	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

		if (cmdResponse == null || !cmdResponse.isSuccess()) {
			return;
		}
		if (this.pendingAction == ActionVideo.list) {
			// mettre a jour la liste des videos
			this.lbVideos.clear();
			if (cmdResponse.getResponseLines() != null && !cmdResponse.getResponseLines().isEmpty()) {
				for (String line : cmdResponse.getResponseLines()) {
					this.lbVideos.addItem(line);
				}
				this.btDeleteVideo.setVisible(true);
			} else {
				this.btDeleteVideo.setVisible(false);
			}
			this.afterChangeFile();
		} else {
			// next action
			this.pendingAction = ActionVideo.list;
		}
	}

	// ------------------------------------- private methods
	private void initHandlers() {

		ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object source = event.getSource();
				if (source != null && source instanceof VideoActionButton) {
					VideoActionButton button = (VideoActionButton) source;
					pendingAction = button.actionVideo;

					if (argumentsChangeHandler != null) {
						argumentsChangeHandler.onChange(null);
					}

					autoexecute();
				}

			}
		};
		this.btListVideo.addClickHandler(clickHandler);
		this.btDeleteVideo.addClickHandler(clickHandler);
		this.btCreateAuxFile.addClickHandler(clickHandler);
		this.btShowTablePMT.addClickHandler(clickHandler);

		this.lbVideos.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				afterChangeFile();
			}
		});

	}

	private void afterChangeFile() {
		String filename = this.wListVideos.getListUserInput();
		if (filename != null && !filename.endsWith(".aux")) {
			this.btCreateAuxFile.setVisible(true);
			this.btShowTablePMT.setVisible(true);
		} else {
			this.btCreateAuxFile.setVisible(false);
			this.btShowTablePMT.setVisible(false);
		}
	}

	private Widget buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);
		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.main.add(this.btListVideo);
		this.main.add(this.wListVideos);
		this.main.add(this.btDeleteVideo);
		this.main.add(this.btCreateAuxFile);
		this.main.add(this.btShowTablePMT);

		// il faut le mettre sinon autoexecute ne fonctionne pas!
		this.main.add(this.btExecute);

		return this.main;
	}

	private void initComposants() {
		super.btExecute.setVisible(false);
		this.btDeleteVideo.setVisible(false);
		this.btCreateAuxFile.setVisible(false);
		this.btShowTablePMT.setVisible(false);
	}

	// ================================== INNER CLASS
	private class VideoActionButton extends Button {
		private final ActionVideo actionVideo;

		private VideoActionButton(ActionVideo actionVideo, String text) {
			super(text);
			this.actionVideo = actionVideo;
		}
	}

	class VideoCmdDatas extends AbstractCmdDatas {

		private final ActionVideo actionVideo;
		private final String videoName;

		ActionVideo getActionVideo() {
			return this.actionVideo;
		}

		String getVideoName() {
			return this.videoName;
		}

		private VideoCmdDatas(ActionVideo actionVideo, String videoName) {
			this.actionVideo = actionVideo;
			this.videoName = videoName;
			super.setConfirmCommand(actionVideo.confirmCommand);
			if (actionVideo.listAfterAction) {
				super.setNext(new VideoCmdDatas(ActionVideo.list, null));
			}
		}
	}

}
