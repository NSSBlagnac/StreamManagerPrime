package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;

import com.francetelecom.orangetv.streammanager.shared.model.divers.CodeAndDescription;

/**
 * DVB Descriptor 0x61: Short smoothing buffer descriptor
 * This descriptor may be included in the EIT to signal the bit-rate for each
 * event.
 * 
 * @author sylvie
 * 
 */
public class ShortSmoothingBufferDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private SbLeakRate sbLeakRate;

	public SbLeakRate getSbLeakRate() {
		return this.sbLeakRate;
	}

	// -------------------------------------- construtor
	public ShortSmoothingBufferDescriptor() {
		this(SbLeakRate.LR1_0);
	}

	public ShortSmoothingBufferDescriptor(String rateCode) {
		this(SbLeakRate.get(rateCode));
	}

	public ShortSmoothingBufferDescriptor(SbLeakRate sbLeakRate) {
		this.sbLeakRate = sbLeakRate;
	}

	// ------------------------------------ overriding IDescriptor
	@Override
	public boolean isEnabled() {
		return false;
	}

	// ======================== INNER CLASS
	public static enum SbLeakRate {

		LR0_5("10", "0.5 Mbits/s"), LR1_0("13", "1.0 Mbits/s"), LR1_5("15", "1.5 Mbits/s"), LR2_0("17", "2.0 Mbits/s"), LR2_5(
				"19", "2.5 Mbits/s"), LR3_0("20", "3.0 Mbits/s"), LR3_5("22", "3.5 Mbits/s"), LR4_0("23", "4.0 Mbits/s"), LR4_5(
				"24", "4.5 Mbits/s"), LR5_0("25", "5.0 Mbits/s"), LR5_5("26", "5.5 Mbits/s"), LR6_0("27", "6.0 Mbits/s"), LR6_5(
				"28", "6.5 Mbits/s"), LR13_5("38", "13.5 Mbits/s"), LR27("48", "27 Mbits/s"), LR44("56", "44 Mbits/s"), LR48(
				"57", "48 Mbits/s"), LR54("58", "54 Mbits/s"), LR72("59", "72 Mbits/s"), LR108("60", "108 Mbits/s");

		public static SbLeakRate get(String code) {
			for (SbLeakRate sbLeakRate : SbLeakRate.values()) {
				if (sbLeakRate.getCode().equals(code)) {
					return sbLeakRate;
				}
			}
			return LR1_0;
		}

		private CodeAndDescription codeAndDescription;

		public String getCode() {
			return this.codeAndDescription.getCode();
		}

		public String getDescription() {
			return this.codeAndDescription.getDescription();
		}

		private SbLeakRate() {
		}

		private SbLeakRate(String code, String description) {
			this.codeAndDescription = new CodeAndDescription(code, description);
		}

	}
}
