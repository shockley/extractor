package my.prop;

/**
 * @author Shockley
 * 
 */
public class AttributeSite {
	private long attriID;
	private long siteID;
	
	public boolean equals(Object obj) {
		if (obj instanceof AttributeSite) {
			AttributeSite as = (AttributeSite) obj;
			return (as.attriID == this.attriID && as.siteID == this.siteID);
		}
		return false;
	}
	
	public int hashCode(){
		return (int) (1 + 17 * attriID + 31 * siteID);
	}
	
	public long getAttriID() {
		return attriID;
	}

	public void setAttriID(long attriID) {
		this.attriID = attriID;
	}

	public long getSiteID() {
		return siteID;
	}

	public void setSiteID(long siteID) {
		this.siteID = siteID;
	}

	
}
