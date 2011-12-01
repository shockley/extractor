/**
 * 
 */
package my.prop;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shockley
 *
 */
public class WebSite {
	private List<String> pages = new ArrayList<String>();
	private Long id;
	public List<String> getPages() {
		return pages;
	}

	public void setPages(List<String> pages) {
		this.pages = pages;
	}
}
