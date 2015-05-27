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


import java.io.Serializable;

/**
 * The <code>MSPeerDescriptor</code> class represent a Cyclon node
 * descriptor, containing a Cyclon address and an age.
 * 
 * @author Cosmin Arad <cosmin@sics.se>
 * @author Amir H. Payberah <amir@sics.se>
 */
public class MSPeerDescriptor implements Comparable<MSPeerDescriptor>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1906679375438244117L;

	private final MSPeerAddress msPeerAddress;
	private int age;
	private final int utilityValue;
	private final int utilityLevel;

//-------------------------------------------------------------------
	public MSPeerDescriptor(MSPeerAddress msPeerAddress, int utilityValue) {
		this.msPeerAddress = msPeerAddress;
		this.age = 0;
		this.utilityValue = utilityValue;

		utilityLevel = 0;
	}

//-------------------------------------------------------------------
	public int incrementAndGetAge() {
		age++;
		return age;
	}

//-------------------------------------------------------------------
	public int getAge() {
		return age;
	}

//-------------------------------------------------------------------
	public int getUtilityValue() {
		return utilityValue;
	}

//-------------------------------------------------------------------
	public int getUtilityLevel() {
		return utilityLevel;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getMSPeerAddress() {
		return msPeerAddress;
	}

//-------------------------------------------------------------------
	@Override
	public int compareTo(MSPeerDescriptor that) {
		if (this.age > that.age)
			return 1;
		if (this.age < that.age)
			return -1;
		return 0;
	}

//-------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((msPeerAddress == null) ? 0 : msPeerAddress.hashCode());
		return result;
	}

//-------------------------------------------------------------------
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MSPeerDescriptor other = (MSPeerDescriptor) obj;
		if (msPeerAddress == null) {
			if (other.msPeerAddress != null)
				return false;
		} else if (!msPeerAddress.equals(other.msPeerAddress))
			return false;
		return true;
	}
	
//-------------------------------------------------------------------
	public String toString() {
		return "(" + msPeerAddress + ":" + utilityLevel + ")";
	}
}
