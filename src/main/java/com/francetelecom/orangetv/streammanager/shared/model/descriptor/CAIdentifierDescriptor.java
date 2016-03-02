package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DVB Descriptor 0x53: CA_identifier_descriptor
 * The CA identifier descriptor indicates whether a particular bouquet, service
 * or event is associated with a
 * conditional access system and identifies the CA system type by means of the
 * CA_system_id
 * 
 * @author sylvie
 * 
 */
public class CAIdentifierDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private List<CASystemId> listSystemIds = new ArrayList<CASystemId>();

	public List<CASystemId> getListSystemIds() {
		return this.listSystemIds;
	}

	public void addSystemId(CASystemId systemId) {
		this.listSystemIds.add(systemId);
	}

	// ----------------------------- constructor
	public CAIdentifierDescriptor() {
	}

	// ------------------------------------ overriding IDescriptor
	@Override
	public boolean isEnabled() {
		return false;
	}

	// =========================== INNER CLASS ===
	public static class CASystemId implements Serializable {

		private static final long serialVersionUID = 1L;

		private int id;

		public int getId() {
			return this.id;
		}

		public CASystemId() {
			this(-1);
		}

		public CASystemId(int id) {
			this.id = id;
		}
	}

}
