package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor.ParentalRating;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ParentalRatingDescriptorPanel extends AbstractDescriptorPanel {

	private final CheckBox cbCanalCodes = new CheckBox("Canal custom codes");
	private LabelAndListWidget parentalRatingCountryList;
	// private final LabelAndBoxWidget wParentRatRating = new
	// LabelAndBoxWidget("rating", 150, 200);
	private LabelAndListWidget csaRatingList;
	private ListBox lbParentalRating;

	// ---------------------------------- constructor
	public ParentalRatingDescriptorPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.setWidget(this.buildContentPanel());
		this.initHandlers();
	}

	// ------------------------------------ implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();
		ParentalRatingDescriptor descriptor = (eitEvent == null) ? null : eitEvent.getParentalRatingDescriptor();
		if (super.display(descriptor)) {
			this.parentalRatingCountryList.setValue(descriptor.getCountry().getCode());

			int rating = descriptor.getParentalRating().getRating();
			boolean orangeCode = CsaMoralityLevel.isOrangeCodes(rating);
			this.cbCanalCodes.setValue(!orangeCode);
			this.refreshListBox(orangeCode);
			this.csaRatingList.setValue(rating + "");
		}
	}

	@Override
	public IDescriptor getDataFromWidget() {

		String csaRatingCode = csaRatingList.getListUserInput();
		CsaMoralityLevel csaMoralityLevel = CsaMoralityLevel.get(csaRatingCode);
		final int eitRating = csaMoralityLevel.getEitRating(!this.cbCanalCodes.getValue());

		final String countryCode = parentalRatingCountryList.getListUserInput();
		final ParentalRatingDescriptor descriptor = new ParentalRatingDescriptor(new ParentalRating(eitRating),
				countryCode);
		return descriptor;
	}

	// ----------------------------------- private methods
	private void clean() {
		this.parentalRatingCountryList.setValue(null);
		this.csaRatingList.setValue(null);
	}

	private void initHandlers() {

		this.cbCanalCodes.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				modifyListCodes();
			}
		});
	}

	private void modifyListCodes() {

		boolean orange = !this.cbCanalCodes.getValue();
		this.refreshListBox(orange);
	}

	private Panel buildContentPanel() {

		this.parentalRatingCountryList = new LabelAndListWidget("country", 100, 210, EnumListManager.get()
				.buildListBoxCountry(), COUNTRY_DEFAULT_CODE);

		VerticalPanel content = new VerticalPanel();
		content.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		content.setSpacing(PANEL_SPACING);
		// content.add(this.wParentRatRating);

		this.cbCanalCodes.setValue(false);
		content.add(this.cbCanalCodes);

		this.lbParentalRating = EnumListManager.get().buildListBoxCsaMoralityLevel(true);
		this.csaRatingList = new LabelAndListWidget("CSA rating", 100, 150, this.lbParentalRating, 1);
		content.add(csaRatingList);

		content.add(this.parentalRatingCountryList);
		return content;
	}

	/**
	 * Permet de basculer d'une liste code orange vers liste de code canal
	 * 
	 * @param orange
	 */
	private void refreshListBox(boolean orange) {

		int selectedIndex = this.lbParentalRating.getSelectedIndex();
		ListBox listBox = EnumListManager.get().buildListBoxCsaMoralityLevel(orange);
		this.lbParentalRating.clear();

		for (int i = 0; i < listBox.getItemCount(); i++) {

			String value = listBox.getValue(i);
			String text = listBox.getItemText(i);

			this.lbParentalRating.addItem(text, value);

		}
		this.lbParentalRating.setSelectedIndex(selectedIndex);

	}

	// ========================== INNER CLASS
	public static enum CsaMoralityLevel {
		// test avec CSA5 Canal+
		CSA1(0, 16), CSA2(7, 17), CSA3(9, 18), CSA4(13, 19), CSA5(15, 20);
		// CSA1(0), CSA2(7), CSA3(9), CSA4(13), CSA5(15);

		private final int eitOrangeRating;
		private final int eitCanalRating;

		public static CsaMoralityLevel get(String code) {

			int rating = ValueHelper.getIntValue(code, 0);

			for (CsaMoralityLevel csaMoralityLevel : CsaMoralityLevel.values()) {
				if (csaMoralityLevel.getEitOrangeRating() == rating || csaMoralityLevel.getEitCanalRating() == rating) {
					return csaMoralityLevel;
				}
			}
			return CSA1;
		}

		public static boolean isOrangeCodes(int rating) {
			return rating < 16;
		}

		public int getEitOrangeRating() {
			return this.eitOrangeRating;
		}

		public int getEitCanalRating() {
			return this.eitCanalRating;
		}

		public int getEitRating(boolean orange) {
			return orange ? this.eitOrangeRating : this.eitCanalRating;
		}

		public String getDescription(boolean orange) {
			return this.name() + " (" + this.getEitRating(orange) + ")";
		}

		public String getCode(boolean orange) {
			return this.getEitRating(orange) + "";
		}

		private CsaMoralityLevel(int eitRating, int eitCanalRating) {
			this.eitOrangeRating = eitRating;
			this.eitCanalRating = eitCanalRating;
		}
	}

}
