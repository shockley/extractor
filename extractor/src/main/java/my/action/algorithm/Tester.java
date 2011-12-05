package my.action.algorithm;

import java.util.List;

import my.action.HTMLHandler;
import my.dao.Alias;
import my.dao.Forge;
import my.dao.Project;
import my.dao.Value;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class Tester {
	public static Logger logger = Logger.getLogger(Tester.class);
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	public HTMLHandler tm = new HTMLHandler();
	
	public void findValues(Forge forge, Transaction tx){
		Session session = hs.getSession();
		//Transaction tx = session.beginTransaction();
		List<Alias> aliases = forge.getAliases();
		List<Project> projects = forge.getProjects();
		if(aliases==null || aliases.size()<1){
			logger.info("No alias for this forge!");
			return;
		}
		if(projects==null || projects.size()<1){
			logger.info("No project for this forge!");
			return;
		}
		tx.commit();
		//session.close();
		session = hs.getSession();
		tx = session.beginTransaction();
		for(Project p :projects){
			for(Alias alias : aliases){
				List<Node> hits = tm.getCandidateValues(p, alias);
				if(hits==null || hits.size()<1){
					logger.info("No hit! for alias " +alias.getValue());
					continue;
				}
				List<Node> hitsAfterSift = tm.siftHits(hits, alias);
				if(hitsAfterSift==null || hitsAfterSift.size()<1){
					logger.info("No hit after sifting! for alias " +alias.getValue()+" project: "+p.getName());
					continue;
				}
				for(Node n : hitsAfterSift){
					Value v =new Value();
					v.setTruth(false);
					v.setProject(p);
					v.setAttribute(alias.getAttribute());
					v.setValue(n.getText());
					session.save(v);
				}
			}
		}
		tx.commit();
		session.close();
	}
	
	public  static void main(String [] args){
		
		Tester t= new Tester();
		Session session = t.hs.getSession();
		Transaction tx = session.beginTransaction();
		Forge forge = (Forge) session.createQuery("from Forge f where f.name = \'freshmeat\'").list().get(0);
		
		t.findValues(forge,tx);
	}
}
