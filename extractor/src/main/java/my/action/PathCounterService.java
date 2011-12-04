package my.action;

import java.util.ArrayList;
import java.util.List;

import my.dao.MatchPath;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * This singleton service calculate all the instance counts
 * @author Shockley
 */
public class PathCounterService {
	private static PathCounterService theInstance = null;
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	/**
	 * key -- aname
	 * value -- xpathes matched
	 * value.key  -- the ith path
	 * value.value   -- support of the ith path
	 * Deprecated after we use db
	 */
	@Deprecated
	private List<MatchPath> MP = new ArrayList<MatchPath>();

	public static PathCounterService getInstance(){
		if(theInstance==null)
			theInstance = new PathCounterService();
		return theInstance;
	}
	
	/**
	 * Accumulate suport count while training using hashmap
	 */
	public void addMP(MatchPath mp){
		MP.add(mp);
	}
	
	public List<MatchPath> getMP() {
		return MP;
	}

	public void flushToDb(){
		Session s = hs.getSession();
		for(MatchPath mp: MP){
			Transaction tx = s.beginTransaction();
			s.save(mp);
			tx.commit();
		}
		s.close();
	}
}
