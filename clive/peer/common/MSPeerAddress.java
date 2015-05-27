/**
 * This file is part of the Kompics P2P Framework.
 * 
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS)
 * Copyright (C) 2009 Royal Institute of Technology (KTH)
 *
 * Kompics is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package clive.peer.common;

import java.math.BigInteger;

import se.sics.kompics.address.Address;
import se.sics.kompics.p2p.overlay.OverlayAddress;

/**
 * The <code>CyclonAddress</code> class represents a Cyclon address, which is
 * formed from a regular peer address and a numeric identifier (
 * <code>BigInteger</code>).
 * 
 * @author Cosmin Arad <cosmin@sics.se>
 * @version $Id: CyclonAddress.java 1132 2009-09-01 10:20:27Z Cosmin $
 */
public final class MSPeerAddress extends OverlayAddress implements
		Comparable<MSPeerAddress> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7582889514221620065L;

	private final BigInteger peerId;

	public MSPeerAddress(Address address, BigInteger peerId) {
		super(address);
		this.peerId = peerId;
	}

	public BigInteger getPeerId() {
		return peerId;
	}

	@Override
	public int compareTo(MSPeerAddress that) {
		return peerId.compareTo(that.peerId);
	}

	@Override
	public String toString() {
		return peerId.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((peerId == null) ? 0 : peerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MSPeerAddress other = (MSPeerAddress) obj;
		if (peerId == null) {
			if (other.peerId != null)
				return false;
		} else if (!peerId.equals(other.peerId))
			return false;
		return true;
	}

}
