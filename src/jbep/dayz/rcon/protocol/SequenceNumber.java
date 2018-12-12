package jbep.dayz.rcon.protocol;

/**
 * --------------------------------------------------------------------
 * 
 * Java BattlEye RCon Protocol - a battleye protocol library.
    Copyright (C) 2015  Rados³aw Skupnik

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * --------------------------------------------------------------------
 * 
 * BattlEye Protocol states that each command packet needs to have a unique sequence number.
 * It also states that the sequence numeration starts from 0. It doesn't state the upper boundary
 * but since it's a byte then the effective range of this sequence number is from 0 to 0xFF.
 * 
 * This class provides a very simple funcionality to retrieve the sequence number.
 * 
 * @author Rados³aw "Myzreal" Skupnik
 *
 */
public class SequenceNumber {

	private int current = 0x00;
	
	/**
	 * Retrieve the next available sequence number.
	 * If the sequence number indicator gets out of bounds it gets reset to 0.
	 * @return - the next available sequence number.
	 */
	public byte next() {
		byte ret = (byte) current;
		current++;
		if (current > 0xFF)
			current = 0;
		return ret;
	}
	
	/**
	 * Does not modify the current indicator.
	 * @return - last used sequence number.
	 */
	public byte last() {
		byte ret = (byte) (current - 1);
		return ret;
	}
}
