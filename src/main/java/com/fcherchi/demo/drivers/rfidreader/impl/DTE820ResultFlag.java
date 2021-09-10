/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

/**
 * All commands which return Result Flag, have to be parsed following the values of this enum.
 * @author Fernando
 *
 */
public class DTE820ResultFlag {
	public enum Values {
		RRUI4RESULTFLAG_NOERROR((byte)0), 
		RRUI4RESULTFLAG_NODATA((byte)1), 
		RRUI4RESULTFLAG_CRCERROR((byte)2), 
		RRUI4RESULTFLAG_NOLICENSE((byte)3), 
		RRUI4RESULTFLAG_OUTOFRANGE((byte)4), 
		RRUI4RESULTFLAG_NOSTANDARD((byte)5), 
		RRUI4RESULTFLAG_NOANTENNA((byte)6), 
		RRUI4RESULTFLAG_NOFREQUENCY((byte)7), 
		RRUI4RESULTFLAG_NOCARRIER((byte)8), 
		RRUI4RESULTFLAG_ANTENNAERROR((byte)9), 
		RRUI4RESULTFLAG_NOTAG((byte)10), 
		RRUI4RESULTFLAG_MORETHANONETAGINFIELD((byte)11), 
		RRUI4RESULTFLAG_WRONGLICENSEKEY((byte)12), 
		RRUI4RESULTFLAG_FWREJECTED((byte)13), 
		RRUI4RESULTFLAG_WRONGCFM((byte)14), 
		RRUI4RESULTFLAG_NOHANDLE((byte)15), 
		RRUI4RESULTFLAG_NOPROFILE((byte)16),
		RRUI4RESULTFLAG_NONSPECIFIED((byte)0x80);
		
		private byte value;

		private Values(byte value) {
	        this.value = value;
	    }
		public byte getValue() {
			return this.value;
		}
	}
}
