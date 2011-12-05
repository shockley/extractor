package my.action.algorithm;

import java.util.List;

import my.action.HTMLHandler;
import my.dao.Forge;
import my.dao.Project;
import my.dao.Seed;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Trainer {
	public static Logger logger = Logger.getLogger(Trainer.class);
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	public HTMLHandler tm = new HTMLHandler();
	/**
	 * CONSTRUCTOR
	 */
	public Trainer(){
		//init();
	}
	
	/**
	 * The main algorithm
	 * @param forge
	 * @author Shockley
	 */
	@SuppressWarnings("unchecked")
	public void findMaxSupport(Forge forge, Session session){
		//fetch seeds
		List<Seed> seeds = null;
		List<Project> projects = null;
		String hql1 = "from Seed s";
		Query q1 = session.createQuery(hql1);
		seeds = q1.list();
		if(seeds==null)
			return;
		
		String hql2 = "from Project p where p.forge = :forge";
		Query q2 = session.createQuery(hql2);
		q2.setParameter("forge", forge);
		forge = null;
		projects = q2.list();
		if(projects==null)
			return;	
		for(int i=0;i<projects.size();i++){
			logger.info("We handled "+ i+" projects");
			tm.searchCloseTerms(projects.get(i), seeds, session);	
		}
	}
	
	
	
	public static void main(String args[]){
		Trainer t = new Trainer();
		Session s = t.hs.getSession();
		Transaction tx = s.beginTransaction();
		Query q = s.createQuery("from Forge f where f.name = \'rubyforge\'");
		Forge forge = (Forge) q.list().get(0);
		t.findMaxSupport(forge, s);
		tx.commit();
	}
}
