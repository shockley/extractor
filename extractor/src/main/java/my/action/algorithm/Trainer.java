package my.action.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import my.action.PathCounterService;
import my.action.HTMLHandler;
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
	/**
	 * @variable : Seed -- attribute values
	 * key : attribute name
	 * value : list of unique values
	 */
	private HashMap<String,List<String>> SA = new HashMap<String,List<String>>();
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
		for(Seed s : seeds){
			Attribute sa = s.getAttribute();
			String sv = s.getValue();
			for(Project p : projects){
				tm.tranverseHTML(p, sv, sa, forge);
			}
		}
		//logger.info(pcService.printMP());
	}
	
	/**
	 * prepare the seeds: SA
	 */
	private void init() {
		try {
			File file = new File("d:\\work\\forge.mirror\\seed");
			String[] names = file.list();
			Session session = hs.getSession();
			for (String name : names) {
				Transaction tx = session.beginTransaction();
				Attribute att;
				String hql = "from Attribute a where a.name = \'"+name+"\'";
				Query q = session.createQuery(hql);
				List list = q.list();
				//if a new attribute found, save it
				if(list==null || list.size()<1){
					att = new Attribute();
					att.setName(name);
				}else{
					att = (Attribute) list.get(0);
				}
				
				BufferedReader reader = new BufferedReader(
						new FileReader("d:\\work\\forge.mirror\\seed\\" + name));
				String value = null;
				//for each seed value
				while((value = reader.readLine())!=null){			
					Seed seed = new Seed();
					seed.setAttribute(att);
					seed.setValue(value);
					//expect cascade save
					session.save(seed);				
				}
				tx.commit();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void prepareSite(String forge) {
		Session s = hs.getSession();
		Transaction tx = s.beginTransaction();
		String hql1 = "from Project p where p.forge in (from Forge f where f.name = \'"
				+ forge + "\')";
		String hql2 = "from Project p where p.forge.name =  \'" + forge + "\'";
		Query q = s.createQuery(hql1);
		//q.setMaxResults(50);
		List<Project> projects = q.list();
		File forgeDir = new File(BASE_DIR + forge);
		if (!forgeDir.exists() || !forgeDir.isDirectory()) {
			forgeDir.mkdir();
		}
		for (Project p : projects) {
			File pf = new File(BASE_DIR + forge + "\\" + p.getName());
			try {
				if (!pf.createNewFile())
					logger.warn("can't over ride" + pf.getAbsolutePath());
				else {
					PrintStream out = new PrintStream(new FileOutputStream(pf));
					out.print(p.getHtml());
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		tx.commit();
		s.close();
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
