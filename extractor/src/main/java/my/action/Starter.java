/**
 * 
 */
package my.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import my.action.algorithm.SimMatcher;
import my.dao.Alias;
import my.dao.Attribute;
import my.dao.Project;
import my.dao.RelativePath;
import my.dao.Seed;
import my.dao.Value;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.io.DOMReader;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * 
 * Do some prepraration in this class
 * @author Shockley
 * 
 */
public class Starter{
	public static Logger logger = Logger.getLogger(Starter.class);
	public static HibernateService hs = DataSourceFactory.getHibernateInstance();
	public static String [] ATTRIB_NAMES = {"categories", "language", "license","platform","status"};
	public static String [] FORGES = {"sourceforge","ow2","freshmeat","rubyforge"};
	
	public static int PL_NUM_2 = 100;
	
	
	
	
	public static int OS_NUM_4 = 4;
	public static int LICENSE_NUM_3 = 78;
	public static int TOPIC_NUM_1 = 20;
	public static int SEED_TYPE_COUNT[] = {20, 100, 78, 4};
	private String settings = "";
	private Text rpName = null;
	private Text rpValue = null;
	
	private class SimpleVisitor implements Visitor{
		private String name;
		private String value;
		
		public SimpleVisitor(String name1,String value1){
			name = name1;
			value = value1;
		}
		public void visit(Document document) {}
		public void visit(DocumentType documentType) {}
		//element is not the same with attribute
		public void visit(Element node) {
			//the trim method is necessary
			
		}
		public void visit(org.dom4j.Attribute node) {}
		public void visit(CDATA node) {}
		public void visit(Comment node) {}
		public void visit(Entity node) {}
		public void visit(Namespace namespace) {}
		public void visit(ProcessingInstruction node) {}
		public void visit(Text node) {
			if(name.equals(node.getText().trim())){
				rpName =  node;
			}
			if(value.equals(node.getText().trim())){
				rpValue = node;
			}
		}
	}
	
	/**
	 * Prepare relative pathes and distinct_rp , truncate first
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	private void prepareRP(){
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		Query q2 = session.createSQLQuery("truncate table relative_path");
		q2.executeUpdate();
		Query q = session.createQuery("from Value a where a.truth = 1");	
		List<Value> values = q.list();
		loop1 : for(Value value : values){
			Alias alias = null;
			Query q1 = session.createQuery("from Alias a where a.attribute = :att and a.forge = :f");
			q1.setParameter("att", value.getAttribute());
			q1.setParameter("f", value.getProject().getForge());
			List<Alias> aliases = q1.list();
			if(aliases!=null && aliases.size()==1){
				alias = aliases.get(0);
			}else{
				logger.error("impossible");
				return;
			}
			String stringName = alias.getValue();
			String stringValue = value.getValue();
			Project p = value.getProject();
			DOMParser parser = new DOMParser();
			String pathName = null;
			String pathValue = null;
			String ancestorToName = null;
			String ancestorToValue = null;
			try {
				parser.parse(new InputSource(new StringReader(p.getHtml())));
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			org.w3c.dom.Document w3cDoc=parser.getDocument(); 
			DOMReader domReader=new DOMReader();
			Document document=domReader.read(w3cDoc);
			//visitor to get the matched node
			if(stringValue.equals("OS Independent") && stringName.trim().equals("Operating System:"))
				logger.info("to debug");
			document.accept(new SimpleVisitor(stringName,stringValue));
			
			if(rpValue!=null && rpName!=null){
				//find their common ancestor
				pathName = rpName.getUniquePath();
				pathValue = rpValue.getUniquePath();
				int i;
				if(pathName.equals(pathValue)){
					logger.error("!");
					return;
				}
				Element ancestor = rpValue.getParent();
				while(//found the common ancestor, so halt
						rpName.getUniquePath(ancestor).equals(rpName.getUniquePath())){
					//climbing up the tree
					Element temp = ancestor.getParent();
					if(temp==null)
						continue loop1;
					ancestor = temp;
				}
				for(i=1; i<=pathValue.length();i++){
					if(!pathName.startsWith(pathValue.substring(0,i), 0))
						break;
				}
				ancestorToName = rpName.getPath(ancestor).replace("'", "\'");
				ancestorToValue = rpValue.getPath(ancestor).replace("'", "\'");
			}
			
			logger.info(ancestorToName);
			logger.info(ancestorToValue);
			
			RelativePath rp = new RelativePath();
			rp.setAlias(alias);
			rp.setAncestor2name(ancestorToName);
			rp.setAncestor2value(ancestorToValue);
			session.save(rp);
		}
		//create distinct_rp
		q2 = session.createSQLQuery("truncate table distinct_rp;");
		q2.executeUpdate();
		q2 = session.createSQLQuery("insert into distinct_rp SELECT * from relative_path" +
" group by relative_path.alias_id, relative_path.ancestor2value, relative_path.ancestor2name");
		q2.executeUpdate();
		tx.commit();
		//session.close();
	}

	@SuppressWarnings("unchecked")
	public void readAllSeeds(){
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		SQLQuery sqlquery = session.createSQLQuery("truncate table seed");
		sqlquery.executeUpdate();
		for(String attribname: ATTRIB_NAMES){
			Attribute att;
			String hql = "from Attribute a where a.name = \'"+attribname+"\'";
			Query q = session.createQuery(hql);
			List list = q.list();
			//if a new attribute found, save it
			if(list==null || list.size()<1){
				att = new Attribute();
				att.setName(attribname);
			}else{
				att = (Attribute) list.get(0);
			}
			BufferedReader reader;
			String value = null;
			//for each seed value
			try {
				reader = new BufferedReader(
						new FileReader("d:\\work\\forge.mirror\\seed\\" + attribname));
				while((value = reader.readLine())!=null){
					Seed seed = new Seed();
					seed.setAttribute(att);
					seed.setValue(value);
					//expect cascade save
					session.save(seed);	
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HibernateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tx.commit();
	}
	
	/**
	 * @param attribname
	 * @param linesToRead
	 */
	@SuppressWarnings("unchecked")
	private void rereadSeeds(String attribname, int linesToRead) {
		try {
			Session session = hs.getSession();
			Transaction tx = session.beginTransaction();
			Attribute att;
			String hql = "from Attribute a where a.name = \'"+attribname+"\'";
			Query q = session.createQuery(hql);
			List list = q.list();
			//if a new attribute found, save it
			if(list==null || list.size()<1){
				att = new Attribute();
				att.setName(attribname);
			}else{
				att = (Attribute) list.get(0);
			}
			SQLQuery sqlquery = session.createSQLQuery("truncate table seed");
			sqlquery.executeUpdate();
			BufferedReader reader = new BufferedReader(
					new FileReader("d:\\work\\forge.mirror\\seed\\" + attribname));
			String value = null;
			//for each seed value
			for(int i=0 ;i<linesToRead; i++){
				if((value = reader.readLine())==null){
					logger.error("not that much seed for this attribute!");
					break;
				}
				Seed seed = new Seed();
				seed.setAttribute(att);
				seed.setValue(value);
				//expect cascade save
				session.save(seed);	
			}
			tx.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Backup the experiment results
	 */
	public void doFinalSqls(){
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		
		
		String [] sqls = {
				"drop table if exists value_"+settings,
				"create table value_"+settings+" engine myisam select * from value",
				
				"drop table if exists max_mp_"+settings,
				"create table max_mp_"+settings+" engine myisam select * from max_mp",
				"drop table if exists new_mp_"+settings,
				"create table new_mp_"+settings+" engine myisam select * from new_mp"
		};
		for(int i = 0;i<sqls.length; i++){
			SQLQuery sqlquery = session.createSQLQuery(sqls[i]);
			sqlquery.executeUpdate();
		}
		tx.commit();
	}
	
	public static void variedSeedExperiment(){
		// TODO Auto-generated method stub
		Starter starter = new Starter();
		starter.prepareRP();
		for(int attrib_id = 1;attrib_id<5;attrib_id++){
			//if(attrib_id!=4) continue;
			int count = SEED_TYPE_COUNT[attrib_id-1];
			for(int linesToRead = 10;linesToRead <= count;linesToRead += 10){
				starter.rereadSeeds(ATTRIB_NAMES[attrib_id-1], linesToRead);
				Trainer trainer = new Trainer();
				Tester tester = new Tester();
				Validator validator = new Validator();
				for(String forge : FORGES){
					starter.settings = "a_"+attrib_id+"s_"+linesToRead+"f_"+forge+"t_"+(int)(SimMatcher.THRESHOLD * 10);
					logger.info(starter.settings);
					
					//trainer.findMaxSupport(forge);
					//trainer.doSQls();
					//tester.findValues(forge);
					//starter.doFinalSqls();
					
					validator.validate(attrib_id,linesToRead, forge);
				}
			}
		}
	}
	
	public static void variedThresholdExperiment(){
		// TODO Auto-generated method stub
		Starter starter = new Starter();
		starter.prepareRP();
		//recovery from a checkpoint, so starts from attribute 4
		for(int attrib_id = 1;attrib_id<5;attrib_id++){
			int count = SEED_TYPE_COUNT[attrib_id-1];
			for(double t = 0.9; t >= 0.1; t=t-0.1){
				SimMatcher.THRESHOLD = t;
				starter.rereadSeeds(ATTRIB_NAMES[attrib_id-1], count);
				Trainer trainer = new Trainer();
				Tester tester = new Tester();
				Validator validator = new Validator();
				for(String forge : FORGES){
					starter.settings = "a_"+attrib_id+"s_"+count+"f_"+forge+"t_"+(int)(SimMatcher.THRESHOLD * 10);
					logger.info(starter.settings);
					
				    //trainer.findMaxSupport(forge);
				    //trainer.doSQls();
				   // tester.findValues(forge);
				    //starter.doFinalSqls();
					validator.validate(attrib_id, count,forge);
				}
			}
		}
	}
	
	public static void validateThemAll(){
		Starter starter = new Starter();
		Validator validator = new Validator();
		for(int attrib_id = 1;attrib_id<=4;attrib_id++){
			int seedincrement = 10;
			int initseed = 10;
			if(attrib_id == 4){
				initseed = 1;
				seedincrement = 1;
			}
			int count = SEED_TYPE_COUNT[attrib_id-1];	
			for(int linesToRead = initseed;linesToRead <= count;linesToRead += seedincrement){
				if(attrib_id == 3 && count==78 && linesToRead == 70)
					seedincrement = 8;
				for(double t = 0.9; t >= 0.1; t=t-0.1){
					if(linesToRead!=count && t!=0.9)
						continue;
					SimMatcher.THRESHOLD = t;
					for(String forge : FORGES){
						starter.settings = "a_"+attrib_id+"s_"+linesToRead+"f_"+forge+"t_"+(int)(SimMatcher.THRESHOLD * 10);
						logger.info(starter.settings);
						
						//trainer.findMaxSupport(forge);
						//trainer.doSQls();
						//tester.findValues(forge);
						//starter.doFinalSqls();			
						validator.validate(attrib_id,linesToRead, forge);
					}
				}
			}
		}
	}
	
	public static void deleteNullInTruth(){
		Session s = hs.getSession();
		s.beginTransaction();
		for(int a = 1;a<5;a++){
			for(String forge : FORGES){
				String forgeAbbr = forge;
				if(forge.equals("sourceforge")){
					forgeAbbr = "sf_";
				}else if(forge.equals("freshmeat")){
					forgeAbbr = "fm_";
				}else if(forge.equals("ow2")){
					forgeAbbr = "ow2_";
				}else if(forge.equals("rubyforge")){
					forgeAbbr = "rf_";
				}
				String table = "truth_" + forgeAbbr + "a_" + a ;
				String sql = "delete from " + table +
						"     where     " +
						" description is null || " +
						" description = \'\'";
				SQLQuery q = s.createSQLQuery(sql);
				q.executeUpdate();
			}
		}
		s.close();
	}
	
	public static void main(String[] args) {
		//variedSeedExperiment();
		//variedThresholdExperiment();
		validateThemAll();
		
		
		
		
	}
}
