package com.francetelecom.orangetv.streammanager.client.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.widget.cell.LabelCell;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.Action;
import com.francetelecom.orangetv.streammanager.client.util.StatusUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils.MyDialogBox;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoStatus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class VideoPanel extends AbstractGridPanel {

	private final static Logger log = Logger.getLogger("VideoPanel");

	private final static String[] HEADERS = new String[] { "", "name", "edit", "audio", "subtitles", "pmt", "color",
			"resolution", "ocs", "csa5", "format", "enabled", "description", "status", "del" };

	private final static Class<?>[] HEADERS_CLASSES = new Class[] { IdCell.class, StyleLabelCell.class, EditCell.class,
			StyleLabelCell.class, StyleLabelCell.class, ShowPMTCell.class, ActiveCell.class, LabelCell.class,
			CryptedCell.class, Csa5Cell.class, LabelCell.class, ActiveCell.class, LabelCell.class, StatusCell.class,
			DeleteCell.class };

	private final static int[] COL_SIZE = new int[] { 0, 200, 25, 50, 50, 50, 25, 50, 25, 25, 50, 25, 200, 100, 25 };

	private Map<Integer, FullVideoInfo> mapIdToVideo = new HashMap<Integer, FullVideoInfo>();

	// private Map<Integer, Integer> mapRowToVideoId = new HashMap<Integer,
	// Integer>();

	// ------------------------------- overriding AbstractGridPanel

	@Override
	protected int[] getColSizes() {
		return COL_SIZE;
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// --------------------------------- constructor
	public VideoPanel() {
		this.initWidget(this.buildMainPanel());
	}

	// ---------------------------------------- public methods

	public FullVideoInfo getVideoInfo(int videoId) {
		return this.mapIdToVideo.get(videoId);
	}

	public void refresh(List<FullVideoInfo> listVideos, boolean update) {
		this.mapIdToVideo.clear();
		// this.mapRowToVideoId.clear();

		log.config("refresh() - update: " + update);
		if (listVideos == null) {
			this.model.removeAll();
			this.model.clearRemovedRows();
			return;
		}

		if (update && this.grid.getRowCount() != listVideos.size()) {
			update = false;
		}
		if (!update) {
			this.model.removeAll();
			this.model.clearRemovedRows();
		}

		int i = 0;
		for (FullVideoInfo videoInfo : listVideos) {

			this.mapIdToVideo.put(videoInfo.getId(), videoInfo);

			int row = i++;
			// this.mapRowToVideoId.put(row, videoInfo.getId());

			if (!update) {
				model.addRow(row, this.buildRowDatas(videoInfo));
			} else {
				model.updateRow(row, this.buildRowDatas(videoInfo));
			}
		}

		this.defineColSize();
		this.gridPanel.unlock();
	}

	// ---------------------------------- private methods
	@SuppressWarnings("unchecked")
	private Widget buildMainPanel() {

		this.main.addStyleName(PANEL_TABLE_STREAM);
		this.main.setSpacing(PANEL_SPACING);

		// create a new editable grid and put it into the panel
		this.grid = gridPanel.createMyEditableGrid(HEADERS, HEADERS_CLASSES, model);
		this.grid.setGridCellfactory(new MyGridCellFactory(this.grid));

		gridPanel.setBottomPagerVisible(true);
		gridPanel.setTopToolbarVisible(false);
		gridPanel.setTopPagerVisible(false);
		gridPanel.setSpacing(PANEL_SPACING);
		this.initColumns();
		this.defineColSize();

		this.main.add(gridPanel);

		this.main.add(this.labelResult);
		this.main.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// display all
		gridPanel.display();
		return main;
	}

	private void doEdit(int rowIndex) {
		this.doAction(Action.editVideo, rowIndex);

	}

	private void doDelete(int rowIndex) {
		this.doAction(Action.deleteVideo, rowIndex);

	}

	private Object[] buildRowDatas(FullVideoInfo videoInfo) {

		DtoProtection protection = videoInfo.getDtoProtection();
		int i = 0;
		Object[] row = new Object[COL_SIZE.length];
		row[i++] = videoInfo.getId();
		row[i++] = new TextAndStyle(videoInfo.getName(), STYLE_LABEL_BOLD, "video name");

		// edit cell (DtoActionProtection)
		row[i++] = protection.getActionUpdate();

		// infos issus de la table PMT
		row[i++] = new TextAndStyle(videoInfo.getAudioTracks().replaceAll(",", HTML_BR), STYLE_LABEL_VIDEONAME_ON,
				"audio tracks");
		row[i++] = new TextAndStyle(videoInfo.getSubtitleTracks().replaceAll(",", HTML_BR), STYLE_LABEL_VIDEONAME_ON,
				"subtitle tracks");
		row[i++] = videoInfo.getTablePmt();

		row[i++] = videoInfo.isColor();

		row[i++] = videoInfo.getResolution();
		row[i++] = videoInfo.isOcs();
		row[i++] = videoInfo.isCsa5();
		row[i++] = videoInfo.getFormat();

		row[i++] = videoInfo.isEnabled();
		row[i++] = videoInfo.getDescription();

		row[i++] = videoInfo.getFullVideoStatus();

		// delete cell (DtoActionProtection)
		row[i++] = protection.getActionDelete();

		return row;

	}

	// ======================================= INNER CLASS
	private class MyGridCellFactory extends AbstractMyGridCellFactory {

		public MyGridCellFactory(EditableGrid<EditableGridDataModel> grid) {
			super(grid);
		}

		@Override
		public GridCell create(int row, int column, Object data) {
			GridCell cell = null;

			@SuppressWarnings("rawtypes")
			Class columnType = getGrid().getColumnWidgetClasses()[getGrid().getModelColumn(column)];

			if (ActiveCell.class.equals(columnType)) {
				cell = this.createActiveCell((Boolean) data, row);
			} else if (EditCell.class.equals(columnType)) {
				cell = this.createEditCell(row);
			} else if (DeleteCell.class.equals(columnType)) {
				cell = this.createDeleteCell(row);
			} else if (StatusCell.class.equals(columnType)) {
				cell = create((FullVideoStatus) data);
			} else if (CryptedCell.class.equals(columnType)) {
				cell = this.createCryptedCell((Boolean) data, row);
			} else if (Csa5Cell.class.equals(columnType)) {
				cell = this.createCsa5Cell((Boolean) data, row);
			} else if (IdCell.class.equals(columnType)) {
				cell = new IdCell();
			} else if (StyleLabelCell.class.equals(columnType)) {
				cell = new StyleLabelCell();
			} else if (ShowPMTCell.class.equals(columnType)) {
				cell = new ShowPMTCell(row);
			}

			if (cell != null) {
				super.prepareCell(cell, row, column, data);
				return cell;
			}

			return super.create(row, column, data);
		}

		private GridCell createCryptedCell(Boolean crypted, int row) {
			CryptedCell cell = new CryptedCell(row);
			cell.setValue(crypted);
			return cell;
		}

		private GridCell createCsa5Cell(Boolean crypted, int row) {
			Csa5Cell cell = new Csa5Cell(row);
			cell.setValue(crypted);
			return cell;
		}

		private GridCell create(FullVideoStatus data) {
			StatusCell cell = new StatusCell();
			cell.setValue(data);
			return cell;
		}

		private DeleteCell createDeleteCell(int row) {
			DeleteCell cell = new DeleteCell(row);
			return cell;
		}

		private EditCell createEditCell(int row) {
			EditCell cell = new EditCell(row);
			// cell.setValue(select);
			return cell;
		}

	}

	private static class CryptedCell extends AbstractEnabledCell {

		public CryptedCell(int rowIndex) {
			super(rowIndex);
		}

		@Override
		protected String getStyleName(boolean enabled) {
			return STYLE_IMG_ACTION + " " + ((enabled) ? STYLE_IMG_CRYPTED : STYLE_IMG_INVISIBLE);

		}

	}

	private static class Csa5Cell extends AbstractEnabledCell {

		public Csa5Cell(int rowIndex) {
			super(rowIndex);
		}

		@Override
		protected String getStyleName(boolean enabled) {
			return STYLE_IMG_ACTION + " " + ((enabled) ? STYLE_IMG_WARN : STYLE_IMG_INVISIBLE);

		}

	}

	/**
	 * Button delete stream
	 * 
	 */
	private class DeleteCell extends AbstractDeleteCell {

		public DeleteCell(final int rowIndex) {

			super(rowIndex, "delete video");
		}

		@Override
		protected ClickHandler buildClickHandler(final int rowIndex) {

			return new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					VideoPanel.this.doDelete(rowIndex);
				}
			};
		}

	}

	private static class StatusCell extends AbstractStatusCell {

		@Override
		protected Label buildWidget() {
			FullVideoStatus status = (FullVideoStatus) super.getValue();
			return StatusUtils.buildLabelStatus(status);
		}

	}

	/**
	 * Bouton show PMT table
	 * 
	 * @author sylvie
	 * 
	 */
	private class ShowPMTCell extends AbstractButtonCell {

		private String[] information;

		public ShowPMTCell(final int rowIndex) {
			super("Show PMT table information", rowIndex, STYLE_IMG_SHOW);
		}

		private void showInformation() {

			if (this.information != null) {
				MyDialogBox box = WidgetUtils.buildDialogBox("PMT Table information", this.information, null, false,
						true, true, null);
				WidgetUtils.centerDialogAndShow(box);
			}
		}

		private void buildInformation(Object value) {
			if (value == null) {
				return;
			}

			String lines = value.toString();
			this.information = lines.split("\n");
		}

		@Override
		protected ClickHandler buildClickHandler(final int rowIndex) {

			return new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					showInformation();
				}
			};
		}

		@Override
		public void setValue(Object value) {

			Boolean enabled = value != null;
			// super.enableButton(enabled);
			super.setValue(enabled);

			this.buildInformation(value);
		}
	}

	/**
	 * Button edit stream
	 * 
	 */
	private class EditCell extends AbstractEditCell {

		public EditCell(final int rowIndex) {

			super(rowIndex, "edit video");
		}

		@Override
		protected ClickHandler buildClickHandler(final int rowIndex) {

			return new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					VideoPanel.this.doEdit(rowIndex);

				}
			};
		}

	}

}
