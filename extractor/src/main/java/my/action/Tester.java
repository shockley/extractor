package my.action;

import java.util.List;

import my.dao.Alias;
import my.dao.Forge;
import my.dao.TestProject;
import my.dao.Value;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class Tester {
	public static Logger logger = Logger.getLogger(Tester.class);
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	public HTMLHandler tm = new HTMLHandler();
	
	public void findValues(String f){
		Session session = hs.getSession();
		//Transaction tx = session.beginTransaction();
		Transaction tx = session.beginTransaction();
		SQLQuery sqlquery = session.createSQLQuery("delete from value where truth = 0");
		sqlquery.executeUpdate();
		Forge forge = (Forge) session.createQuery("from Forge f where f.name = \'"+f+"\'").list().get(0);
		List<Alias> aliases = forge.getAliases();
		List<TestProject> projects = forge.getTestprojects();
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
		for(TestProject tp :projects){
			for(Alias alias : aliases){
				List<Node> hits = tm.getCandidateValues(tp, alias);
				if(hits==null || hits.size()<1){
					//logger.info("No hit! for alias " +alias.getValue());
					continue;
				}
				List<Node> hitsAfterSift = tm.siftHits(hits, alias);
				if(hitsAfterSift==null || hitsAfterSift.size()<1){
					//logger.info("No hit after sifting! for alias " +alias.getValue()+" project: "+tp.getName());
					continue;
				}
				for(Node n : hitsAfterSift){
					Value v =new Value();
					v.setTruth(false);
					v.setTestproject(tp);
					v.setAttribute(alias.getAttribute());
					v.setValue(n.getText());
					session.save(v);
				}
				logger.info("We've just handled " +alias.getValue()+" project: "+tp.getName());
			}
		}
		tx.commit();
		//session.close();
	}
	
	public  static void main(String [] args){
		Tester t= new Tester();
		t.findValues("");
	}
}
