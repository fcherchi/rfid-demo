/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;

import java.time.Instant;

/**
 * Encapsulates a tag report but contains also reader and timstamps information
 * @author Fernando
 */
public class ExtendedTagReport {
	
	

	/** The reader id of the reader generating this report */
	private String readerId;
	 
	/** The inner tag report itself */
	private TagReport tagReport;

	/** The timestamp of the first time the tag was seen */
	private Instant firstSeen;
	
	/** The timestamp of the last time the tag was seen */
	private Instant lastSeen;
	
	
	public ExtendedTagReport(String readerId, TagReport tagReport, Instant firstSeen, Instant lastSeen) {
		this.readerId = readerId;
		this.tagReport = tagReport;
		this.firstSeen = firstSeen;
		this.lastSeen = lastSeen;
	}


	/**
	 * @return the readerId
	 */
	public String getReaderId() {
		return readerId;
	}


	/**
	 * @return the tagReport
	 */
	public TagReport getTagReport() {
		return tagReport;
	}


	/**
	 * @param readerId the readerId to set
	 */
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}


	/**
	 * @param tagReport the tagReport to set
	 */
	public void setTagReport(TagReport tagReport) {
		this.tagReport = tagReport;
	}


	/**
	 * @return the firstSeen
	 */
	public Instant getFirstSeen() {
		return firstSeen;
	}


	/**
	 * @return the lastSeen
	 */
	public Instant getLastSeen() {
		return lastSeen;
	}


	/**
	 * @param firstSeen the firstSeen to set
	 */
	public void setFirstSeen(Instant firstSeen) {
		this.firstSeen = firstSeen;
	}


	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(Instant lastSeen) {
		this.lastSeen = lastSeen;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtendedTagReport [readerId=" + readerId + ", tagReport=" + tagReport + ", firstSeen=" + firstSeen + ", lastSeen=" + lastSeen + "]";
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstSeen == null) ? 0 : firstSeen.hashCode());
		result = prime * result + ((lastSeen == null) ? 0 : lastSeen.hashCode());
		result = prime * result + ((readerId == null) ? 0 : readerId.hashCode());
		result = prime * result + ((tagReport == null) ? 0 : tagReport.hashCode());
		return result;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedTagReport other = (ExtendedTagReport) obj;
		if (firstSeen == null) {
			if (other.firstSeen != null)
				return false;
		} else if (!firstSeen.equals(other.firstSeen))
			return false;
		if (lastSeen == null) {
			if (other.lastSeen != null)
				return false;
		} else if (!lastSeen.equals(other.lastSeen))
			return false;
		if (readerId == null) {
			if (other.readerId != null)
				return false;
		} else if (!readerId.equals(other.readerId))
			return false;
		if (tagReport == null) {
			if (other.tagReport != null)
				return false;
		} else if (!tagReport.equals(other.tagReport))
			return false;
		return true;
	}


}
