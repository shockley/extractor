package my.action;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import my.action.algorithm.SimMatcher;
import my.dao.Alias;
import my.dao.Attribute;
import my.dao.DistinctRP;
import my.dao.Forge;
import my.dao.MatchPath;
import my.dao.MaxMatchPath;
import my.dao.Path;
import my.dao.Project;
import my.dao.RelativePath;
import my.dao.Seed;
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
import org.dom4j.tree.DefaultText;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * Using visitor paradigm to tranverse an html, specifically the visitor is
 * MyVisitor,
 * 
 * @author Shockley
 * 
 */
public class HTMLHandler {

	public static Logger logger = Logger.getLogger(HTMLHandler.class);
	/**
	 * mps : local variablr accessed ,modified, maintained by MyVisitor
	 */
	private List<MatchPath> mps = new ArrayList<MatchPath>();
	public HibernateService hs = DataSourceFactory.getHibernateInstance();

	/**
	 * 
	 * Define the behavior of visiting each DOM node, particularly how should it
	 * visit a text node, basically it counts the match path
	 * 
	 * @author Shockley
	 * 
	 */
	private class TrainingVisitor implements Visitor {
		public SimMatcher wm = new SimMatcher();

		private List<Seed> seeds;
		private Project project;
		private Session session;
		public TrainingVisitor(List<Seed> seeds, Project p, Session s) {
			this.project = p;
			this.seeds = seeds;
			session = s;
		}

		public void visit(Text node) {
			// TODO Auto-generated method stub
			String text = node.getText();
			for (Seed seed : seeds) {
				Attribute attrib = seed.getAttribute();
				String toFind = seed.getValue();
				if (wm.whetherMatches(text.toLowerCase().trim(), toFind
						.toLowerCase().trim())) {
					logger.info("winner!" + text + " v.s. "+toFind);
					String pathExp = node.getPath();
					Query q2 = session
					.createSQLQuery("insert into new_mp (seed_id, project_id, path, value, tofind) VALUES (:seedid, :projectid, :path, :value, :tofind)");
					q2.setParameter("seedid", seed.getId());
					q2.setParameter("projectid", project.getId());
					q2.setParameter("path", pathExp);
					q2.setParameter("value", text);
					q2.setParameter("tofind", toFind);
					q2.executeUpdate();
					/*String pathExpToSearch = pathExp.replace("'", "''");
					Path path = null;
					Query q2 = session
							.createQuery("from Alias a where a.forge =:f and a.attribute =:attr");
					q2.setParameter("f", project.getForge());
					q2.setParameter("attr", attrib);
					List<Alias> aliases = q2.list();
					if (aliases == null || aliases.size() != 1) {
						logger.info("unrecognized seed");
						continue;
						// session.save(path);
					}
					Query q = session
							.createQuery("from Path p where p.xpath = \'"
									+ pathExpToSearch + "\'");
					if (q.list() == null || q.list().size() != 1) {
						path = new Path();
						path.setXpath(pathExp);
						path.setUniqueInPage(false);
						// session.save(path);
					} else {
						path = (Path) q.list().get(0);
					}

					// 时间换空间，但是空间小了可以不存数据库，进而又换了更多的时间
					MatchPath mp = new MatchPath();
					mp.setAlias(aliases.get(0));
					mp.setPath(path);
					if (mps.contains(mp)) {
						int s = mps.get(mps.indexOf(mp)).getSupport();
						mp.setSupport(s + 1);
						mps.remove(mp);
						mps.add(mp);
					} else {
						mp.setSupport(1);
						mps.add(mp);
					}*/
				}
			}			
		}

		public void visit(Document document) {
		}

		public void visit(DocumentType documentType) {
		}

		public void visit(Element node) {
		}

		public void visit(org.dom4j.Attribute node) {
		}

		public void visit(CDATA node) {
		}

		public void visit(Comment node) {
		}

		public void visit(Entity node) {
		}

		public void visit(Namespace namespace) {
		}

		public void visit(ProcessingInstruction node) {
		}
	}

	/**
	 * Search for seed appearances in a page
	 * 
	 * @param project
	 * @param seeds
	 *            : act as keyword
	 * @param forge
	 */
	public void searchCloseTerms(Project project, List<Seed> seeds, Session session) {
		DOMParser parser = new DOMParser();
		if (project == null || project.getHtml() == null){
			logger.info("no html, for project "+project.getName());
			return;
		}
			
		try {
			parser.parse(new InputSource(new StringReader(project.getHtml())));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.w3c.dom.Document w3cDoc = parser.getDocument();
		DOMReader domReader = new DOMReader();
		Document document = domReader.read(w3cDoc);
		Element root = document.getRootElement();
		TrainingVisitor v = new TrainingVisitor(seeds, project ,session);
		root.accept(v);
		for (MatchPath mp : mps) {
			//session.clear();
			session.save(mp);
		}
	}

	/**
	 * Find the "candidate attribute names" in a page, based on the Text node of
	 * "attr value: and a relative path
	 * 
	 * @param project
	 *            denote the page
	 * @param path
	 * @param valueText
	 * @return the nodes that are conformed to the rp empty if something wrong
	 *         or it's indeed empty
	 */
	@SuppressWarnings("unchecked")
	public List<String> findItsNames(Project project, RelativePath path,
			Text valueText) {
		List<String> names = new ArrayList<String>();
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new StringReader(project.getHtml())));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.w3c.dom.Document w3cDoc = parser.getDocument();
		DOMReader domReader = new DOMReader();
		Document document = domReader.read(w3cDoc);
		Element root = document.getRootElement();
		String valuePath = valueText.getPath();
		int sw = valuePath.indexOf(path.getAncestor2value());
		if (sw > 0) {
			String pathAn = valuePath.substring(0, sw);
			String namePath = pathAn + path.getAncestor2name();
			List<Node> results = root.selectNodes(namePath);
			if (results == null || results.size() < 1)
				logger.info("f!");
			for (Node n : results) {
				String nameText = n.getText();
				names.add(nameText);
			}
		}
		return names;
	}

	/**
	 * get HIT set, denoted by project,alias
	 * 
	 * @param project
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Node> getCandidateValues(Project project, Alias alias) {
		List<Node> hits = new ArrayList<Node>();
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new StringReader(project.getHtml())));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.w3c.dom.Document w3cDoc = parser.getDocument();
		DOMReader domReader = new DOMReader();
		Document document = domReader.read(w3cDoc);
		MaxMatchPath mp = alias.getMmp();
		if(mp==null){
			logger.info("No mp found for the alias");
			return null;
		}
		hits = document.selectNodes(mp.getPath().getXpath());
		return hits;
	}

	/**
	 * if any one of the result can get to its alias, then we keep the candidate
	 * otherwise we wipe it out
	 * 
	 * @param hits
	 * @param rp
	 * @return null if no rp
	 */
	@SuppressWarnings("unchecked")
	public List<Node> siftHits(List<Node> hits, Alias alias) {
		List<Node> newHits = new ArrayList<Node>();
		DistinctRP rp = alias.getDrp();
		if(rp==null){
			return null;
		}
		out: for (Node hit : hits) {
			String aliasName = alias.getValue();
			String a2v = rp.getAncestor2value();
			String a2n = rp.getAncestor2name();
			Element ancestor = hit.getParent();
			while (// halt till the ancestor is found
			!hit.getPath(ancestor).equals(a2v)) {
				// climbing up the tree
				ancestor = ancestor.getParent();
				if (ancestor == null) {
					logger.info("No ancestor found for hit:" + hit.getText()
							+ ", for alias: " + alias.getValue());
					continue out;
				}
			}

			List<Node> names = ancestor.selectNodes(a2n);
			if (names == null || names.size() == 0) {
				logger.info("No name found for hit:" + hit.getText()
						+ ", for alias: " + alias.getValue());
				continue;
			}
			for (Node name : names) {
				if (name.getText() == null) {
					logger.info("Impossible:null name node! for hit:"
							+ hit.getText() + ", for alias: "
							+ alias.getValue());
					continue;
				}
				if (name.getText().trim().equals(aliasName)){
					// finally we have a winner!
					logger.info("Save the Winner:" + hit.getText()
							+ ", for alias: " + alias.getValue());
					newHits.add(hit);
				}
			}

		}
		return newHits;
	}

	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		DOMParser parser = new DOMParser();
		try {
			parser.parse("D:\\work\\forge.mirror\\from.local.db\\ow2\\accord");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.w3c.dom.Document w3cDoc = parser.getDocument();
		DOMReader domReader = new DOMReader();
		Document document = domReader.read(w3cDoc);
		String xpath = "/HTML/BODY/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/UL/LI/text()";
		List<DefaultText> txts = document.selectNodes(xpath);
		for (DefaultText n : txts) {
			logger.info(n.getText());
		}
	}
}
