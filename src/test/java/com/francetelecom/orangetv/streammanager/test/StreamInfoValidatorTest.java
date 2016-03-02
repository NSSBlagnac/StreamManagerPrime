package com.francetelecom.orangetv.streammanager.test;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.AddressStream;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.ProtectedStreamDatas;
import com.francetelecom.orangetv.streammanager.server.util.StreamInfoValidator;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;

public class StreamInfoValidatorTest {

	private static final StreamInfoValidator validator = StreamInfoValidator.get();

	// ===========================================
	// VALIDATE LCN
	// ==========================================
	@Test
	public void testValidateProtectedStreamLcn() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		protectedStreamDatas.setLcnInf(800);
		protectedStreamDatas.setLcnSup(899);

		// LCN correcte pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:8:8", "8.8.8.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

		// LCN correcte pour stream no protected
		streamInfo = this.buildStreamInfo(999, 8888, "8:8:8", "8.8.8.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);
	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongProtetedLcn() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		protectedStreamDatas.setLcnInf(800);
		protectedStreamDatas.setLcnSup(899);

		// LCN incorrecte pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(988, 8888, "8:8:8", "8.8.8.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongNoProtetedLcn() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		protectedStreamDatas.setLcnInf(800);
		protectedStreamDatas.setLcnSup(899);

		// LCN incorrecte pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:8:8", "8.8.8.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	// ===========================================
	// VALIDATE USI
	// ==========================================
	@Test
	public void testValidateProtectedStreamUsi() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		protectedStreamDatas.setUsiInf(2500);
		protectedStreamDatas.setUsiSup(2599);

		// USI correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 2588, "8:8:8", "8.8.8.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

		// USI correct pour stream no protected
		streamInfo = this.buildStreamInfo(999, 3088, "8:8:8", "8.8.8.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);
	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongProtetedUsi() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		protectedStreamDatas.setUsiInf(2500);
		protectedStreamDatas.setUsiSup(2599);

		// LCN incorrecte pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(988, 3088, "8:8:8", "8.8.8.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongNoProtetedUsi() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		protectedStreamDatas.setUsiInf(2500);
		protectedStreamDatas.setUsiSup(2599);

		// LCN incorrecte pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(988, 2588, "8:8:8", "8.8.8.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	// ===========================================
	// VALIDATE ADDRESS
	// ==========================================
	@Test
	public void testValidateProtectedStreamAddress() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		protectedStreamDatas.setAddressInf(new AddressStream("232.0.6.1"));
		protectedStreamDatas.setAddressSup(new AddressStream("232.0.6.99"));

		// Address correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:8:8", "232.0.6.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

		// Address correct pour stream no protected
		streamInfo = this.buildStreamInfo(999, 3088, "8:8:8", "232.1.6.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongProtectedAddress() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		protectedStreamDatas.setAddressInf(new AddressStream("232.0.6.1"));
		protectedStreamDatas.setAddressSup(new AddressStream("232.0.6.99"));

		// Address correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:8:8", "232.1.6.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongNoProtectedAddress() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		protectedStreamDatas.setAddressInf(new AddressStream("232.0.6.1"));
		protectedStreamDatas.setAddressSup(new AddressStream("232.0.6.99"));

		// Address correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:8:8", "232.0.6.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	// ===========================================
	// VALIDATE TRIPLET
	// ==========================================
	@Test
	public void testValidateProtectedStreamTriplet() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		protectedStreamDatas.setTripletInf(new TripletDvb("1:0:167"));
		protectedStreamDatas.setTripletSup(new TripletDvb("99:0:167"));

		// Triplet correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:0:167", "8.8.8.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

		// Address correct pour stream no protected
		streamInfo = this.buildStreamInfo(999, 3088, "8:1:167", "8.8.8.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongProtectedTriplet() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		protectedStreamDatas.setTripletInf(new TripletDvb("1:0:167"));
		protectedStreamDatas.setTripletSup(new TripletDvb("99:0:167"));

		// Triplet no correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:1:167", "8.8.8.8", true);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	@Test(expected = EitException.class)
	public void testValidateProtectedStreamWrongNoProtectedTriplet() throws Exception {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		protectedStreamDatas.setTripletInf(new TripletDvb("1:0:167"));
		protectedStreamDatas.setTripletSup(new TripletDvb("99:0:167"));

		// Triplet no correct pour stream protected
		StreamInfo streamInfo = this.buildStreamInfo(888, 8888, "8:0:167", "8.8.8.8", false);
		validator.validateProtectedStream(streamInfo, protectedStreamDatas);

	}

	private StreamInfo buildStreamInfo(int lcn, int usi, String triplet, String address, boolean entryProtected) {

		StreamInfo streamInfo = new StreamInfo();
		streamInfo.setLcn(lcn);
		streamInfo.setUsi(usi);
		streamInfo.setAddress(address);
		streamInfo.setTripleDvb(triplet);
		streamInfo.setEntryProtected(entryProtected);

		return streamInfo;
	}

}
