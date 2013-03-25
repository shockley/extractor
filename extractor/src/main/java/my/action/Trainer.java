package my.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import my.dao.Project;
import my.dao.Seed;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
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
	public void findMaxSupport(String forge){
		//fetch seeds
		List<Seed> seeds = null;
		List<Project> projects = null;
		String hql1 = "from Seed s";
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		
		Query q1 = session.createQuery(hql1);
		seeds = q1.list();
		if(seeds==null)
			return;
		
		String hql2 = "from Project p where p.forge.name = :forge";
		Query q2 = session.createQuery(hql2);
		q2.setParameter("forge", forge);
		projects = q2.list();
		if(projects==null || projects.size()<1){
			logger.error("no projects found for forge : "+forge);
			return;
		}
		SQLQuery sqlquery = session.createSQLQuery("truncate table new_mp");
		sqlquery.executeUpdate();
		tx.commit();
		
		for(int i=0;i<projects.size();i++){
			logger.info("We handled "+ i+" projects");
			tm.searchCloseTerms(projects.get(i), seeds);	
		}
	}
	
	
	public void doSQls(){
		try {
			Session session = hs.getSession();
			session.beginTransaction();
			BufferedReader reader = new BufferedReader(
					new FileReader("D:\\git\\repos\\extractor\\extractor\\src\\main\\resources\\reusable.sqls.sql"));
			String sql = null;
			while((sql = reader.readLine())!=null){
				session.createSQLQuery(sql).executeUpdate();
			}
			session.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		Trainer t = new Trainer();
		t.findMaxSupport("");
	}
}
