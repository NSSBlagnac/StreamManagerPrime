package com.francetelecom.orangetv.streammanager.server.util;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.AddressStream;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.ProtectedStreamDatas;
import com.francetelecom.orangetv.streammanager.server.model.DbFullStreamEntry;
import com.francetelecom.orangetv.streammanager.shared.dto.AbstractStreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;

public class StreamInfoValidator extends AbstractValidator {

	private static final Logger log = Logger.getLogger(StreamInfoValidator.class.getName());
	private static StreamInfoValidator instance;

	public static StreamInfoValidator get() {
		if (instance == null) {
			instance = new StreamInfoValidator();
		}
		return instance;
	}

	private StreamInfoValidator() {
	}

	// les règles de validation par rapport aux stream protégés (plages
	// de valeur)
	public void validateProtectedStream(StreamInfo streamInfo, ProtectedStreamDatas protectedStreamDatas)
			throws EitException {

		final boolean streamProtected = streamInfo.isEntryProtected();

		// controle LCN
		this.validateInOutRange(streamProtected, streamInfo.getLcn(), protectedStreamDatas.getLcnInf(),
				protectedStreamDatas.getLcnSup(), "lcn", null);

		// controle USI
		this.validateInOutRange(streamProtected, streamInfo.getUsi(), protectedStreamDatas.getUsiInf(),
				protectedStreamDatas.getUsiSup(), "usi", null);

		// controle Address
		AddressStream addressInf = protectedStreamDatas.getAddressInf();
		AddressStream addressSup = protectedStreamDatas.getAddressSup();
		this.validateAddressRange(streamProtected, streamInfo.getAddress(), addressInf, addressSup);

		// controle triplet DVB (pas de controle)
		// TripletDvb tripletInf = protectedStreamDatas.getTripletInf();
		// TripletDvb tripletSup = protectedStreamDatas.getTripletSup();
		// this.validateTripletRange(streamProtected,
		// streamInfo.getTripletDvd(), tripletInf, tripletSup);

	}

	private void validateAddressRange(boolean inRange, String addressToValidate, AddressStream addressInf,
			AddressStream addressSup) throws EitException {

		if (addressInf == null || addressSup == null) {
			return;
		}
		AddressStream address = new AddressStream(addressToValidate);
		long valueInf = this.itemsToLongValue(addressInf.getAddressItems(), ADDRESS_DIGITS);
		long valueSup = this.itemsToLongValue(addressSup.getAddressItems(), ADDRESS_DIGITS);
		long value = this.itemsToLongValue(address.getAddressItems(), ADDRESS_DIGITS);

		this.validateInOutRange(inRange, value, valueInf, valueSup, "address",
				this.getRangeAddress(addressInf, addressSup));

	}

	private String getRangeAddress(AddressStream addressInf, AddressStream addressSup) {

		if (addressInf == null || addressSup == null) {
			return null;
		}
		return "[" + addressInf.toString() + ", " + addressSup.toString() + "]";
	}

	// private void validateTripletRange(boolean inRange, String
	// tripletToValidate, TripletDvb tripletInf,
	// TripletDvb tripletSup) throws EitException {
	//
	// if (tripletInf == null || tripletSup == null) {
	// return;
	// }
	// TripletDvb triplet = new TripletDvb(tripletToValidate);
	// long valueInf = this.itemsToLongValue(tripletInf.getItems(),
	// TRIPLET_DIGITS, true);
	// long valueSup = this.itemsToLongValue(tripletSup.getItems(),
	// TRIPLET_DIGITS, true);
	// long value = this.itemsToLongValue(triplet.getItems(), TRIPLET_DIGITS,
	// true);
	//
	// String requiredRange = "[" + tripletInf.toString() + ", " +
	// tripletSup.toString() + "]";
	// this.validateInOutRange(inRange, value, valueInf, valueSup,
	// "triplet DVB", requiredRange);
	//
	// }

	public void validateStreamInfo(StreamInfo streamInfo) throws EitException {

		// les règles de validation de valeurs

		// name length > 3i
		validateString(streamInfo.getName(), 3, "name");
		// description length > 5
		validateString(streamInfo.getDescription(), 5, "description");
		// user length > 3
		validateString(streamInfo.getUser(), 3, "user");

		// videofile not empty
		validateRequired(streamInfo.getVideoFilename(), "video file");

		// triplet DVB
		validateTripletDVB(streamInfo.getTripletDvd());

		// Url Address
		validateUrlAddress(streamInfo.getAddress());

		// port > 0
		validateIntValue(streamInfo.getPort(), 0, "Port");

		// usi > 0
		validateIntValue(streamInfo.getUsi(), 0, "Usi");

		// lcn
		validateIntValue(streamInfo.getLcn(), 0, "LCN");

	}

	public void validateUnicityRules(StreamInfo streamInfo, List<DbFullStreamEntry> listAllEntries) throws EitException {
		if (listAllEntries != null) {

			TripletDvb tripletDvb = this.validateTripletDVB(streamInfo.getTripletDvd());

			for (DbFullStreamEntry dbEitEntry : listAllEntries) {
				if (dbEitEntry.getId() == streamInfo.getId()) {
					continue; // next entry
				}
				// LCN unique
				if (dbEitEntry.getLcn() == streamInfo.getLcn()) {
					throw new EitException("LCN " + streamInfo.getLcn() + " is already in use!");
				}
				// USI unique
				if (dbEitEntry.getUsi() == streamInfo.getUsi()) {
					throw new EitException("USI " + streamInfo.getUsi() + " is already in use!");
				}
				// Triplet DBV unique
				if (dbEitEntry.getTripletDvd().equals(tripletDvb)) {
					throw new EitException("Triplet DVB " + streamInfo.getTripletDvd() + " is already in use!");
				}
				// Adress unique
				if (dbEitEntry.getAddress().equals(streamInfo.getAddress())) {
					throw new EitException("Url Address " + streamInfo.getAddress() + " is already in use!");
				}
				// name unique
				if (dbEitEntry.getName().equals(streamInfo.getName())) {
					throw new EitException("Name " + streamInfo.getName() + " is already in use!");
				}
			}

		}

	}

	public StreamInfoForList buildStreamInfoForList(DbFullStreamEntry dbStreamEntry) {

		StreamInfoForList streamInfo = new StreamInfoForList();
		populateStreamInfo(streamInfo, dbStreamEntry);

		return streamInfo;
	}

	public StreamInfo buildStreamInfo(DbFullStreamEntry dbStreamEntry) {

		StreamInfo streamInfo = new StreamInfo();
		populateStreamInfo(streamInfo, dbStreamEntry);
		return streamInfo;
	}

	public void populateRangeValuesForStreamInfo(AbstractStreamInfo streamInfo,
			ProtectedStreamDatas protectedStreamDatas) {

		if (streamInfo == null || protectedStreamDatas == null) {
			return;
		}

		streamInfo.setRangeLcn(this.getRange(protectedStreamDatas.getLcnInf(), protectedStreamDatas.getLcnSup()));
		streamInfo.setRangeUSI(this.getRange(protectedStreamDatas.getUsiInf(), protectedStreamDatas.getUsiSup()));

		streamInfo.setRangeAddress(this.getRangeAddress(protectedStreamDatas.getAddressInf(),
				protectedStreamDatas.getAddressSup()));
	}

	private void populateStreamInfo(AbstractStreamInfo streamInfo, DbFullStreamEntry dbStreamEntry) {

		if (dbStreamEntry != null) {
			streamInfo.setId(dbStreamEntry.getId());
			streamInfo.setAddress(dbStreamEntry.getAddress());
			streamInfo.setDescription(dbStreamEntry.getDescription());
			streamInfo.setEnable(dbStreamEntry.isEnable());
			streamInfo.setLcn(dbStreamEntry.getLcn());
			streamInfo.setName(dbStreamEntry.getName());
			streamInfo.setPort(dbStreamEntry.getPort());
			streamInfo.setStatus(dbStreamEntry.getStatus());
			streamInfo.setUser(dbStreamEntry.getUser());
			streamInfo.setUsi(dbStreamEntry.getUsi());
			streamInfo.setTripleDvb(dbStreamEntry.getTripletDvd().toString());
			streamInfo.setVideoId(dbStreamEntry.getVideoId());
			streamInfo.setEntryProtected(dbStreamEntry.isEntryProtected());
			streamInfo.setEitToInject(dbStreamEntry.isEitToInject());

			streamInfo.setVideoEnabled(dbStreamEntry.isVideoEnabled());
			streamInfo.setVideoFilename(dbStreamEntry.getVideoFilename());
			streamInfo.setVideoStatus(dbStreamEntry.getVideoStatus());

		}

	}

	public TripletDvb validateTripletDVB(String tripletDVB) throws EitException {
		// triplet DVB not empty
		validateRequired(tripletDVB, "triplet DVB");
		String[] triplets = tripletDVB.split(":");
		if (triplets == null || triplets.length != TRIPLET_DIGITS.length) {
			throw new EitException("Invalid triplet DVB!");
		}
		// xx:xx:xxx
		int tsid = validateNumber(triplets[0], TRIPLET_DIGITS[0], "tsid");
		int sid = validateNumber(triplets[1], TRIPLET_DIGITS[1], "sid");
		int onid = validateNumber(triplets[2], TRIPLET_DIGITS[2], "onid");

		return new TripletDvb(tsid, sid, onid);
	}

}
