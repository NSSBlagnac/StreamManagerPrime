package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Country;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Language;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;

public interface IDescriptorPanel {

	public static final String LANGUAGE_DEFAULT_CODE = Language.FRE.getCode();
	public static final String COUNTRY_DEFAULT_CODE = Country.FRA.getCode();

	/**
	 * Rempli les widget avec les valeur du pojo
	 * 
	 * @param eitEvent
	 */
	public void populateWidgetFromData(EitEvent eitEvent);

	/**
	 * Récupère la saisie de l'utilisateur dans un objet EitInfoModel
	 * 
	 * @return
	 */
	public IDescriptor getDataFromWidget();

}
