/**
 * @author: Shockley
 * @date: 2011-11-30
 */
package my.deprecated;

import my.action.algorithm.SimMatcher;

import org.apache.log4j.Logger;
import org.jdom.filter.Filter;

/**
 * @author Shockley
 *
 */
public class MyJDOMFilter implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static SimMatcher wm = new SimMatcher();
	public static Logger logger = Logger.getLogger(MyJDOMFilter.class);
	private String tofind;
	public String getTofind() {
		return tofind;
	}
	
	public void setTofind(String tofind) {
		this.tofind = tofind;
	}
	
	public MyJDOMFilter(String s){
		tofind = s;
	}

	public boolean matches(Object obj) {
		// TODO Auto-generated method stub
		wm.whetherMatches(obj.toString(), tofind);
		return false;
	}

}
