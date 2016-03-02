package com.francetelecom.orangetv.streammanager.server.manager;

import com.francetelecom.orangetv.streammanager.shared.dto.AbstractStreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection.Rules;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoStreamProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoVideoProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;

/**
 * centralise les règles metier sur les stream et video
 * 
 * @author ndmz2720
 *
 */
public class ProtectionManager {

	private static ProtectionManager instance;

	public static ProtectionManager get() {
		if (instance == null) {
			instance = new ProtectionManager();
		}
		return instance;
	}

	private ProtectionManager() {
	}

	// ---------------------------------- public methods

	//
	/**
	 * Active des protection en modification/suppression
	 * 
	 * @param dbEitEntry
	 */
	public void setProtectionOnVideoInfo(FullVideoInfo videoInfo, boolean profileAtLeastManager) {

		boolean newVideo = videoInfo.getId() == IDto.ID_UNDEFINED;

		// Par défaut on active la protection pour les profiles admin & qualif
		if (newVideo) {
			videoInfo.setEntryProtected(profileAtLeastManager);
		}

		DtoVideoProtection protection = this._buildVideoProtection(videoInfo, profileAtLeastManager);
		videoInfo.setDtoProtection(protection);
	}

	/**
	 * Active des protection en modification/suppression
	 */
	public void setProtectionOnStreamInfo(AbstractStreamInfo streamInfo, boolean profileAtLeastManager,
			boolean profileAdmin) {

		boolean newStream = streamInfo.getId() == IDto.ID_UNDEFINED;

		// Par défaut on active 'entry protected" pour les profiles admin &
		// qualif
		if (newStream) {
			streamInfo.setEntryProtected(profileAtLeastManager);
		}

		DtoStreamProtection protection = this.buildStreamProtection(streamInfo, profileAtLeastManager, profileAdmin);
		streamInfo.setDtoProtection(protection);
	}

	// ---------------------------------- private methods
	private DtoVideoProtection _buildVideoProtection(FullVideoInfo videoInfo, boolean profileAtLeastManager) {

		final boolean created = videoInfo.getId() == IDto.ID_UNDEFINED;
		DtoVideoProtection dtoProtection = new DtoVideoProtection();
		dtoProtection.setAtLeastProfileManager(profileAtLeastManager);

		// ===================================== profile rules

		// si protected alors il faut au moins profile qualif pour supprimer ou
		// modifier
		boolean rightToModify = (videoInfo.isEntryProtected()) ? profileAtLeastManager : true;
		dtoProtection.setReadOnly(!rightToModify, "video protected can be modified only by manager profile!",
				Rules.profile);

		// =========================================== functionnal rules

		if (rightToModify) {

			// ne pas pouvoir supprimer une video utilisée dans un stream
			if (videoInfo.isUsedByStream()) {
				dtoProtection.getActionDelete().setActive(false, "video used by stream!", Rules.functionnal);
			}

			// ne pas pouvoir desactiver la video si utilisée par un stream
			// enabled
			if (videoInfo.isEnabled() && videoInfo.isAtLastOneStreamEnabled()) {
				dtoProtection.getActionEnableDisable().setActive(false,
						"disable not autorized:  video used by an active stream!", Rules.functionnal);
			}

			// upload possible si status NEW ou READY et non utilisé par
			// stream enabled
			boolean canUpload = created
					|| (!videoInfo.isAtLastOneStreamEnabled() && (videoInfo.getStatus() == VideoStatus.NEW || videoInfo
							.getStatus() == VideoStatus.READY));
			dtoProtection.getActionUpload().setActive(canUpload,
					"upload not autorized if status not new or not ready or if used by enabled stream!",
					Rules.functionnal);
		}

		return dtoProtection;
	}

	private DtoStreamProtection buildStreamProtection(AbstractStreamInfo streamInfo, boolean profileAtLeastManager,
			boolean profileAdmin) {

		final boolean created = streamInfo.getId() == IDto.ID_UNDEFINED;
		final StreamStatus status = streamInfo.getStatus();
		boolean entryProtected = streamInfo.isEntryProtected();

		boolean statusNewOrStopped = status != null
				&& (status == StreamStatus.STOPPED || status == StreamStatus.NEW || status == StreamStatus.START_FAILURE);

		final DtoStreamProtection protection = new DtoStreamProtection();
		protection.setAtLeastProfileManager(profileAtLeastManager);
		final DtoActionProtection actionStartStop = protection.getActionStartOrStop();

		// ===================================== profile rules

		// si protected alors il faut au moins profile qualif pour supprimer ou
		// modifier, start & stop
		boolean rightToModify = (entryProtected) ? profileAtLeastManager : true;
		protection.setReadOnly(!rightToModify, "stream protected can be modified only by manager profile!",
				Rules.profile);
		actionStartStop.setActive(rightToModify, "start/stop only for manager profile!", Rules.profile);
		protection.getActionModifProtectedAttribut().setActive(profileAtLeastManager,
				"can be modified only by manager profile!", Rules.profile);

		// si profil anonyme & !eitToInject alors ne pas pouvoir visualiser les
		// eit
		if (!streamInfo.isEitToInject() && !profileAtLeastManager) {
			protection.getActionDisplayEit().setActive(false, "cannot show eit!", Rules.profile);

		}

		// =========================================== functionnal rules
		if (rightToModify) {

			// update, start|stop, delete n'est possible que si stream
			// new or stopped or startfailure
			String message = " available only if stream new, stopped or start failure!";
			actionStartStop.setActive(statusNewOrStopped, "start|stop" + message, Rules.functionnal);
			protection.getActionDelete().setActive(statusNewOrStopped, "delete" + message, Rules.functionnal);
			protection.getActionUpdate().setActive(statusNewOrStopped, "update" + message, Rules.functionnal);

			// start & stop uniquement si video available
			actionStartStop.setActive(streamInfo.isVideoAvailable(), "video is not available (enabled & READY)!",
					Rules.functionnal);

			// admin peut changer le status des stream != STARTED && != STOPPED
			// &&
			// != NEW
			if (profileAdmin && status != StreamStatus.STARTED && status != StreamStatus.STOPPED
					&& status != StreamStatus.NEW) {
				protection.setCanChangeStatus(true);
			}

			// ne pas pouvoir reactiver un stream si video not available
			if (!created && !streamInfo.isEnable()) {
				protection.getActionEnableDisable().setActive(streamInfo.isVideoAvailable(),
						"enable stream not autorized: video not available (enabled & READY)!", Rules.functionnal);
			}

		}

		return protection;
	}

	// ======================================= INNER CLASS

	/**
	 * Valeurs reservee aux stream protected
	 * 
	 * @author ndmz2720
	 *
	 */
	public static class ProtectedStreamDatas {

		private int lcnInf = -1;
		private int lcnSup = -1;

		private int usiInf = -1;
		private int usiSup = -1;

		private TripletDvb tripletInf;
		private TripletDvb tripletSup;

		private AddressStream addressInf;
		private AddressStream addressSup;

		public int getLcnInf() {
			return lcnInf;
		}

		public void setLcnInf(int lcnInf) {
			this.lcnInf = lcnInf;
		}

		public int getLcnSup() {
			return lcnSup;
		}

		public void setLcnSup(int lcnSup) {
			this.lcnSup = lcnSup;
		}

		public int getUsiInf() {
			return usiInf;
		}

		public void setUsiInf(int usiInf) {
			this.usiInf = usiInf;
		}

		public int getUsiSup() {
			return usiSup;
		}

		public void setUsiSup(int usiSup) {
			this.usiSup = usiSup;
		}

		public TripletDvb getTripletInf() {
			return tripletInf;
		}

		public void setTripletInf(TripletDvb tripletInf) {
			this.tripletInf = tripletInf;
		}

		public TripletDvb getTripletSup() {
			return tripletSup;
		}

		public void setTripletSup(TripletDvb tripletSup) {
			this.tripletSup = tripletSup;
		}

		public AddressStream getAddressInf() {
			return addressInf;
		}

		public void setAddressInf(AddressStream addressInf) {
			this.addressInf = addressInf;
		}

		public AddressStream getAddressSup() {
			return addressSup;
		}

		public void setAddressSup(AddressStream addressSup) {
			this.addressSup = addressSup;
		}

	}

	public static class AddressStream {

		private final int[] addressItems = new int[4];
		private final String address;

		public AddressStream(String address) {

			this.address = address;
			if (address != null) {

				String[] items = address.split("\\.");
				if (items != null && items.length == 4) {
					for (int i = 0; i < 4; i++) {
						String item = items[i];
						int value = ValueHelper.getIntValue(item, 0);
						addressItems[i] = value;
					}
				}

			}

		}

		public int[] getAddressItems() {
			return addressItems;
		}

		@Override
		public String toString() {
			return this.address;
		}

	}

}
