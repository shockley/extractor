package my.action.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import my.action.HTMLHandler;
import my.action.PathCounterService;
import my.dao.Attribute;
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
	public PathCounterService pcService = PathCounterService.getInstance();
	public static String BASE_DIR = "d:\\work\\forge.mirror\\";
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
	public void findMaxSupport(Forge forge){
		//fetch seeds
		List<Seed> seeds = null;
		List<Project> projects = null;
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		String hql1 = "from Seed s";
		String hql2 = "from Project p where p.forge = :forge";
		Query q1 = session.createQuery(hql1);
		
		seeds = q1.list();
		if(seeds==null)
			return;
		Query q2 = session.createQuery(hql2);
		q2.setParameter("forge", forge);
		projects = q2.list();
		if(projects==null)
			return;	
		tx.commit();
		session.close();
		//prepare site
		/*String[] pageDirs = {};
		File dir = new File(BASE_DIR+"\\"+forge);
		if(!dir.exists()||!dir.isDirectory()){
			prepareSite(forge);
		}
		pageDirs = dir.list();*/
		for(int i=0;i<projects.size();i++){
			logger.info("We handled "+ i+" projects");
			tm.tranverseHTML(projects.get(i), seeds, forge);	
		}
		//logger.info(pcService.printMP());
	}
	
	
	
	public static void main(String args[]){
		Trainer t = new Trainer();
		Session s = t.hs.getSession();
		Transaction tx = s.beginTransaction();
		Query q = s.createQuery("from Forge f where f.name = \'freshmeat\'");
		Forge forge = (Forge) q.list().get(0);
		tx.commit();
		s.close();
		t.findMaxSupport(forge);
	}
}
