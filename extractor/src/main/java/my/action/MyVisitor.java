/**
 * @author: Shockley
 * @date: 2011-11-30
 */
package my.action;

import my.action.algorithm.SimMatcher;
import my.dao.Attribute;
import my.dao.Forge;
import my.dao.MatchPath;
import my.dao.Path;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

import org.apache.log4j.Logger;
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * 
 * Define the behavior of visiting each DOM node, particularly
 * how should it visit a text node, basically it counts the match path
 * @author Shockley
 *
 */
public class MyVisitor implements Visitor {
	public static SimMatcher wm = new SimMatcher();
	public static Logger logger = Logger.getLogger(MyVisitor.class);
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	public PathCounterService pcService = PathCounterService.getInstance();
	private String tofind;
	private Attribute attri;
	private Forge forge;
	
	public MyVisitor(String tofind, Attribute attr, Forge forge){
		this.setAttri(attr);
		this.setForge(forge);
		this.setTofind(tofind);
	}

	public void visit(Text node) {
		// TODO Auto-generated method stub
		String s1 = node.getText();
		Session session  = hs.getSession();
		Transaction tx = session.beginTransaction();
		tx.begin();
		if(wm.whetherMatches(s1.toLowerCase(), tofind.toLowerCase())){
			String pathExp =  node.getPath();
			Path path;
			Query q = session.createQuery("from Path p where p.xpath = \'"+pathExp+"\'");
			if(q.list()==null || q.list().size() != 1){
				path = new Path();
				path.setXpath(pathExp);
				path.setUniqueInPage(false);
				//session.save(path);
			}else{
				path = (Path) q.list().get(0);
			}
			MatchPath mp = new MatchPath();
			mp.setAttribute(attri);
			mp.setForge(forge);
			mp.setPath(path);
			session.save(mp);
			//logger.info(s1 + " found at " + path + " " + this);
			//pcService.addMP(attriName, path);
		}
		tx.commit();
		session.close();
	}

	
	public String getTofind() {
		return tofind;
	}
	public void setTofind(String tofind) {
		this.tofind = tofind;
		
	}
	
	public String toString(){
		String s = "";
		if(forge != null)
			s += "FORGE = " + forge;
		if(attri != null)
			s += " ATTRIBUTE = " + attri;
		if(tofind != null)
			s += " SEED_VALUE = " + tofind;
		return s;
	}
	
	public void visit(Document document) {}
	public void visit(DocumentType documentType) {}
	public void visit(Element node) {}
	public void visit(org.dom4j.Attribute node) {}
	public void visit(CDATA node) {}
	public void visit(Comment node) {}
	public void visit(Entity node) {}
	public void visit(Namespace namespace) {}
	public void visit(ProcessingInstruction node) {}

	public void setAttri(Attribute attri) {
		this.attri = attri;
	}

	public Attribute getAttri() {
		return attri;
	}

	public void setForge(Forge forge) {
		this.forge = forge;
	}

	public Forge getForge() {
		return forge;
	}
}
