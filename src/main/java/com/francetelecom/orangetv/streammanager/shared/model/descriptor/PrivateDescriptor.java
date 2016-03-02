package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Private descriptor to inject apha-numeric tokens
 * Tag between 0x80 and 0xFF
 * 
 * @author sylvie
 * 
 */
public class PrivateDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private List<PrivateToken> listPrivateTokens = new ArrayList<PrivateToken>();

	public List<PrivateToken> getListPrivateTokens() {
		return this.listPrivateTokens;
	}

	public boolean hasToken() {
		return this.listPrivateTokens != null && !this.listPrivateTokens.isEmpty();
	}

	public void addPrivateToken(PrivateToken privateToken) {
		this.listPrivateTokens.add(privateToken);
	}

	// ----------------------------- constructor
	public PrivateDescriptor() {
	}

	// =========================== INNER CLASS ===
	public static class PrivateToken implements Serializable {

		private static final long serialVersionUID = 1L;

		// tag du descriptor
		// ex : 0xAA | OxAB
		private String tag;
		private String token;

		public String getTag() {
			return this.tag;
		}

		public String getToken() {
			return this.token;
		}

		public PrivateToken() {
			this(null, null);
		}

		public PrivateToken(String tag, String token) {
			this.tag = tag;
			this.token = token;
		}
	}

}
