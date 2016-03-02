package com.francetelecom.orangetv.streammanager.client.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.cell.AbstractCell;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.widget.cell.IntegerCell;
import org.gwt.advanced.client.ui.widget.cell.LabelCell;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.Action;
import com.francetelecom.orangetv.streammanager.client.util.StatusUtils;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection.Rules;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoStreamProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel permettant de gerer la liste des stream et leurs propriéte
 * 
 * @author ndmz2720
 * 
 */
public class StreamPanel extends AbstractGridPanel {

	private final static Logger log = Logger.getLogger("StreamPanel");

	// private final static String RADIO_BUTTON_SELECT_GROUP_NAME = "rbSelect";

	public enum ResultType {
		success, warn, error
	}

	// private final static int ID_INDEX = 0;
	private final static String[] HEADERS = new String[] { "", "eit", "show", "edit", "LCN", "USI", "name", "user",
			"file", "enabled", "status", "action", "tsid:sid:onid", "address", "port", "del" };
	private final static Class<?>[] HEADERS_CLASSES = new Class[] { IdCell.class, ActiveCell.class, ShowEitCell.class,
			EditCell.class, StyleLabelCell.class, StyleLabelCell.class, LabelCell.class, LabelCell.class,
			ColorStateLabelCell.class, ActiveCell.class, StatusCell.class, StartStopCell.class, LabelCell.class,
			LabelCell.class, IntegerCell.class, DeleteCell.class };
	private final static int[] COL_SIZE = new int[] { 0, 25, 25, 15, 30, 50, 250, 100, 150, 25, 150, 25, 75, 50, 25, 25 };

	private Map<Integer, StreamInfoForList> mapIdToStreamEntry = new HashMap<Integer, StreamInfoForList>();

	private ActionStreamClickHandler actionStreamClickHandler;
	private EditStreamClickHandler editStreamClickHandler;
	private DeleteStreamClickHandler deleteStreamClickHandler;
	private ShowEitClickHandler showEitClickHandler;

	// ------------------------------- overriding AbstractGridPanel

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected int[] getColSizes() {
		return COL_SIZE;
	}

	// --------------------------------- constructor
	public StreamPanel() {
		this.initWidget(this.buildMainPanel());
	}

	// ------------------------------------- public methods

	public StreamInfoForList getDbStreamInfo(int eitId) {
		return this.mapIdToStreamEntry.get(eitId);
	}

	public void refresh(List<StreamInfoForList> listEntries, boolean update, int currentEit) {

		this.mapIdToStreamEntry.clear();

		log.fine("refresh() - update: " + update + " - currentEit: " + currentEit);
		if (listEntries == null) {
			this.model.removeAll();
			this.model.clearRemovedRows();
			return;
		}

		if (update && this.grid.getRowCount() != listEntries.size()) {
			update = false;
		}
		if (!update) {
			this.model.removeAll();
			this.model.clearRemovedRows();
		}

		int i = 0;
		for (StreamInfoForList dbEitEntry : listEntries) {

			if (currentEit >= 0 && dbEitEntry.getId() == currentEit) {
				// dbEitEntry.setSelected(true);
			}
			this.mapIdToStreamEntry.put(dbEitEntry.getId(), dbEitEntry);

			int row = i++;

			if (!update) {
				model.addRow(row, this.buildRowDatas(dbEitEntry));
			} else {
				model.updateRow(row, this.buildRowDatas(dbEitEntry));
			}
		}

		this.defineColSize();
		this.gridPanel.unlock();

	}

	// ---------------------------------------- private methods

	private void doImport(int rowIndex) {

		this.doAction(Action.showEit, rowIndex);
	}

	private void doEdit(int rowIndex) {

		this.doAction(Action.editStream, rowIndex);
	}

	private void doDelete(int rowIndex) {

		this.doAction(Action.deleteStream, rowIndex);

	}

	private Object[] buildRowDatas(StreamInfoForList streamInfo) {

		DtoStreamProtection protection = streamInfo.getDtoProtection();

		int i = 0;
		Object[] row = new Object[COL_SIZE.length];
		row[i++] = streamInfo.getId();
		row[i++] = streamInfo.isEitToInject();

		// show eit (DtaActionProtection
		row[i++] = protection.getActionDisplayEit();

		// enable button edit (DtoActionProtection)
		row[i++] = protection.getActionUpdate();

		String styleBoldCenter = STYLE_LABEL_BOLD + " " + STYLE_LABEL_CENTER;
		row[i++] = new TextAndStyle(streamInfo.getLcn() + "", styleBoldCenter, "LCN");
		row[i++] = new TextAndStyle(streamInfo.getUsi() + "", styleBoldCenter, "USI");
		;
		row[i++] = streamInfo.getName();
		row[i++] = streamInfo.getUser();

		TextAndState textAndState = new TextAndState(streamInfo.getVideoFilename(), streamInfo.isVideoAvailable(),
				(streamInfo.isVideoAvailable() ? "video available" : "video no available!"));
		row[i++] = textAndState;
		row[i++] = streamInfo.isEnable();
		StreamStatus status = streamInfo.getStatus();
		row[i++] = status;

		row[i++] = new StatusAndProtection(status, protection);
		// this.getActionFromStatus(status, protection);

		row[i++] = streamInfo.getTripletDvd();

		row[i++] = streamInfo.getAddress();
		row[i++] = streamInfo.getPort();

		// enable button delete (DtoActionProtection)
		row[i++] = protection.getActionDelete();

		return row;

	}

	private Action getActionFromStatus(StatusAndProtection statusAndProtection) {

		boolean canStartOrStop = statusAndProtection.protection.getActionStartOrStop().isActive();

		switch (statusAndProtection.status) {

		case STARTING:
			return (statusAndProtection.protection.isCanChangeStatus()) ? Action.changeStreamStatus : Action.startOff;

		case STOPPED:
		case NEW:
			return (canStartOrStop) ? Action.start : Action.startOff;
		case STARTED:
			return (canStartOrStop) ? Action.stop : Action.stopOff;

		default:
			return (statusAndProtection.protection.isCanChangeStatus()) ? Action.changeStreamStatus : Action.stopOff;

		}

	}

	private static class StatusAndProtection {

		private final StreamStatus status;
		private final DtoStreamProtection protection;

		private StatusAndProtection(StreamStatus status, DtoStreamProtection protection) {
			this.status = status;
			this.protection = protection;
		}
	}

	@SuppressWarnings("unchecked")
	private Panel buildMainPanel() {

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

	// accesseur pour DeleteStreamClickHandler (DeleteCell)
	private DeleteStreamClickHandler getDeleteStreamClickHandler() {
		if (this.deleteStreamClickHandler == null) {
			this.deleteStreamClickHandler = new DeleteStreamClickHandler();
		}
		return this.deleteStreamClickHandler;
	}

	// accesseur pour EditStreamClickHandler (EditCell)
	private EditStreamClickHandler getEditStreamClickHandler() {
		if (this.editStreamClickHandler == null) {
			this.editStreamClickHandler = new EditStreamClickHandler();
		}
		return this.editStreamClickHandler;
	}

	// construire un seul click handler pour les boutons d'action
	private ClickHandler getActionStreamClickHandler() {
		if (this.actionStreamClickHandler == null) {
			this.actionStreamClickHandler = new ActionStreamClickHandler();
		}
		return this.actionStreamClickHandler;
	}

	// construire un seul click handler pour les boutons show eit
	private ClickHandler getShowEitClickHandler() {
		if (this.showEitClickHandler == null) {
			this.showEitClickHandler = new ShowEitClickHandler();
		}
		return this.showEitClickHandler;
	}

	// ============================================================

	private class MyGridCellFactory extends AbstractMyGridCellFactory {

		public MyGridCellFactory(EditableGrid<EditableGridDataModel> grid) {
			super(grid);
		}

		@Override
		public GridCell create(int row, int column, Object data) {
			GridCell cell = null;

			@SuppressWarnings("rawtypes")
			Class columnType = getGrid().getColumnWidgetClasses()[getGrid().getModelColumn(column)];

			if (StatusCell.class.equals(columnType)) {
				cell = create((StreamStatus) data);
			} else if (EditCell.class.equals(columnType)) {
				cell = this.createEditCell(row);
			} else if (StartStopCell.class.equals(columnType)) {
				cell = this.createActionCell(row);
			} else if (ShowEitCell.class.equals(columnType)) {
				cell = this.createShowEitCell(row);
			} else if (ActiveCell.class.equals(columnType)) {
				cell = this.createActiveCell((Boolean) data, row);
			} else if (DeleteCell.class.equals(columnType)) {
				cell = this.createDeleteCell(false, row);
			} else if (IdCell.class.equals(columnType)) {
				cell = new IdCell();
			} else if (ColorStateLabelCell.class.equals(columnType)) {
				cell = new ColorStateLabelCell(STYLE_LABEL_VIDEONAME_ON, STYLE_LABEL_VIDEONAME_OFF);
			} else if (StyleLabelCell.class.equals(columnType)) {
				cell = new StyleLabelCell();
			}

			if (cell != null) {
				super.prepareCell(cell, row, column, data);
				return cell;
			}

			return super.create(row, column, data);
		}

		private DeleteCell createDeleteCell(boolean enabled, int row) {
			DeleteCell cell = new DeleteCell(row);
			// cell.setValue(enabled);
			return cell;
		}

		private ShowEitCell createShowEitCell(int row) {
			ShowEitCell cell = new ShowEitCell(row);
			return cell;
		}

		private StartStopCell createActionCell(int row) {

			StartStopCell cell = new StartStopCell(row);
			return cell;
		}

		private EditCell createEditCell(int row) {
			EditCell cell = new EditCell(row);
			return cell;
		}

		private GridCell create(StreamStatus data) {
			StatusCell cell = new StatusCell();
			cell.setValue(data);
			return cell;
		}

	}

	// click handler dedie au ButtonForAbstractButtonCell des EditButton
	private class ShowEitClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			Object src = event.getSource();
			if (src != null && src instanceof ButtonForAbstractButtonCell) {
				ButtonForAbstractButtonCell srcButton = (ButtonForAbstractButtonCell) src;
				int rowIndex = (srcButton.cell == null) ? -1 : srcButton.cell.rowIndex;
				StreamPanel.this.doImport(rowIndex);
			}
		}
	}

	/**
	 * Bouton import eit
	 * 
	 * @author sylvie
	 * 
	 */
	private class ShowEitCell extends AbstractButtonCell {

		public ShowEitCell(final int rowIndex) {
			super("import and show eit", rowIndex, STYLE_IMG_SHOW);
		}

		@Override
		protected ClickHandler buildClickHandler(final int rowIndex) {

			return StreamPanel.this.getShowEitClickHandler();
		}

		@Override
		public void setValue(Object value) {

			DtoActionProtection actionProtection = (DtoActionProtection) value;
			super.setActionProtection(actionProtection);

		}

	}

	// click handler dedie au ButtonForAbstractButtonCell des EditButton
	private class DeleteStreamClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			Object src = event.getSource();
			if (src != null && src instanceof ButtonForAbstractButtonCell) {
				ButtonForAbstractButtonCell srcButton = (ButtonForAbstractButtonCell) src;
				int rowIndex = (srcButton.cell == null) ? -1 : srcButton.cell.rowIndex;
				StreamPanel.this.doDelete(rowIndex);
			}
		}
	}

	/**
	 * Button delete stream
	 * 
	 */
	private class DeleteCell extends AbstractDeleteCell {

		public DeleteCell(final int rowIndex) {

			super(rowIndex, "delete stream");
		}

		@Override
		protected ClickHandler buildClickHandler(final int rowIndex) {

			return StreamPanel.this.getDeleteStreamClickHandler();
		}

	}

	// click handler dedie au ButtonForAbstractButtonCell des EditButton
	private class EditStreamClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			Object src = event.getSource();
			if (src != null && src instanceof ButtonForAbstractButtonCell) {
				ButtonForAbstractButtonCell srcButton = (ButtonForAbstractButtonCell) src;
				int rowIndex = (srcButton.cell == null) ? -1 : srcButton.cell.rowIndex;
				StreamPanel.this.doEdit(rowIndex);
			}
		}
	}

	/**
	 * Button edit stream
	 * 
	 */
	private class EditCell extends AbstractEditCell {

		public EditCell(final int rowIndex) {

			super(rowIndex, "edit stream");
		}

		@Override
		protected ClickHandler buildClickHandler(final int rowIndex) {

			return StreamPanel.this.getEditStreamClickHandler();
		}

	}

	// click handler dedie au ActionButton des ActionCell
	private class ActionStreamClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			Object src = event.getSource();
			if (src != null && src instanceof StartStopButton) {
				StartStopButton srcButton = (StartStopButton) src;
				int rowIndex = (srcButton.actionCell == null) ? -1 : srcButton.actionCell.rowIndex;
				Action action = (srcButton.actionCell == null) ? null : srcButton.actionCell.action;
				if (action != null && rowIndex != -1) {
					StreamPanel.this.doAction(action, rowIndex);
				}
			}

		}
	}

	// bouton d'action interne a ActionCell
	private class StartStopButton extends Button {

		private final StartStopCell actionCell;

		StartStopButton(StartStopCell actionCell) {
			this.actionCell = actionCell;

			addClickHandler(StreamPanel.this.getActionStreamClickHandler());

		}
	}

	private class StartStopCell extends AbstractCell {

		private StartStopButton widget;
		private Action action;
		private int rowIndex;

		public StartStopCell(final int rowIndex) {
			this.rowIndex = rowIndex;
			this.removeStyleName("active-cell");
			this.removeStyleName("passive-cell");

			this.widget = new StartStopButton(this);
		}

		@Override
		public void setFocus(boolean focus) {
		}

		@Override
		public Object getNewValue() {
			return Action.startOff;
		}

		@Override
		protected Widget createActive() {
			return createInactive();
		}

		@Override
		protected Widget createInactive() {
			return this.widget;
		}

		@Override
		public void setValue(Object value) {

			StatusAndProtection statusAndProtection = (StatusAndProtection) value;
			DtoActionProtection actionProtection = statusAndProtection.protection.getActionStartOrStop();

			this.action = getActionFromStatus(statusAndProtection);

			boolean hidden = !actionProtection.isActive() && actionProtection.getRules() == Rules.profile;

			String styleName = STYLE_IMG_ACTION + " ";
			boolean enabled = false;
			switch (action) {
			case start:
				styleName += STYLE_IMG_START;
				this.widget.setTitle("start stream");
				enabled = true;
				break;
			case stop:
				styleName += STYLE_IMG_STOP;
				this.widget.setTitle("stop stream");
				enabled = true;
				break;
			case startOff:
				styleName += STYLE_IMG_STARTOFF;
				this.widget.setVisible(!hidden);
				this.widget.setTitle(actionProtection.getMessage());
				break;
			case stopOff:
				styleName += STYLE_IMG_STOPOFF;
				this.widget.setVisible(!hidden);
				this.widget.setTitle(actionProtection.getMessage());
				break;
			case editStream:
			case showEit:
				break;
			case changeStreamStatus:
				styleName += STYLE_IMG_CHANGE_STATUS;
				this.widget.setTitle("change status to STOPPED");
				enabled = true;
				break;
			default:
				break;
			}

			this.widget.addStyleName(styleName);

			this.widget.setEnabled(enabled);
			super.setValue(action.name());
		}

	}

	private static class StatusCell extends AbstractStatusCell {

		@Override
		protected Label buildWidget() {
			StreamStatus status = (StreamStatus) super.getValue();
			return StatusUtils.buildLabelStatus(status);
		}

	}
}
