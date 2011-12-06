/**
 * 
 */
package my.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.AbstractDocument.ElementEdit;

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
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.io.DOMReader;
import org.hibernate.Query;
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
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
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
	 * @param args
	 */
	private void prepareRP(){
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
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
			Element root = document.getRootElement();
			//visitor to get the matched node
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
				
				while(//find the common ancestor, so halt
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
			//how to get to value?
			/**/
		}
		tx.commit();
		//session.close();
	}
	/**
	 * prepare the seeds: SA
	 */
	private void prepareSeeds() {
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Starter starter = new Starter();
		Tester tester = new Tester();
		Trainer trainer = new Trainer();
		starter.prepareSeeds();
		starter.prepareRP();
		
	}
}
