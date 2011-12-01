/**
 * @author: Shockley
 * @date: 2011-11-30
 */
package my.deprecated;


import my.action.algorithm.SimMatcher;

/**
 * @author Shockley
 *
 */
public class MyFilter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String txtvalue; 
	public static my.action.algorithm.SimMatcher m = new SimMatcher();
	public boolean matches(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof String){
			String objs = (String) obj;
			return m.whetherMatches(objs, txtvalue);
		}
		else
			return false;
	}

}
