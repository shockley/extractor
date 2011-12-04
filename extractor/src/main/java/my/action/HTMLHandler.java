package my.action;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import my.dao.Forge;
import my.dao.Path;
import my.dao.Project;
import my.dao.RelativePath;
import my.dao.Seed;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.DOMReader;
import org.dom4j.tree.DefaultText;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * Using visitor paradigm to tranverse an html, specifically
 * the visitor is MyVisitor,
 * @author Shockley
 *
 */
public class HTMLHandler {
	
	public static Logger logger = Logger.getLogger(HTMLHandler.class);
	/**
	 * Tranverse Html page of a given project, using MyVisitor
	 * @param project
	 * @param seeds
	 * @param forge
	 */
	public void tranverseHTML(Project project, List<Seed> seeds, Forge forge){
		DOMParser parser = new DOMParser();
		if(project==null || project.getHtml()==null)
			return;
		try {
			parser.parse(new InputSource(new StringReader(project.getHtml())));
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
		MyVisitor v = new MyVisitor(seeds, forge);
		for(Iterator<Element> i = root.elementIterator();i.hasNext();){
			Element el = (Element)i.next();
			el.accept(v);
		}
	}
	
	/**
	 * Find the "candidate attribute names" in a page, based on the Text node of "attr value:
	 * and a relative path
	 * @param project denote the page
	 * @param path
	 * @param valueText
	 * @return the nodes that are conformed to the rp
	 * empty if something wrong or it's indeed empty
	 */
	public List<String> findItsNames(Project project, RelativePath path, Text valueText){
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
		org.w3c.dom.Document w3cDoc=parser.getDocument(); 
		DOMReader domReader=new DOMReader();
		Document document=domReader.read(w3cDoc);
		Element root = document.getRootElement();
		String valuePath = valueText.getPath();
		int sw = valuePath.indexOf(path.getAncestor2value());
		if(sw>0){
			String pathAn = valuePath.substring(0, sw);
			String namePath = pathAn + path.getAncestor2name();
			List<Node> results = root.selectNodes(namePath);
			if(results==null||results.size()<1)
				logger.info("f!");
			for(Node n:results){
				String nameText = n.getText();
				names.add(nameText);
			}
		}
		return names;
	}
	
	public static void main(String args[]){
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
		org.w3c.dom.Document w3cDoc=parser.getDocument(); 
		DOMReader domReader=new DOMReader();
		Document document=domReader.read(w3cDoc);
		String xpath = "/HTML/BODY/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/UL/LI/text()";
		List<DefaultText> txts = document.selectNodes(xpath);
		for(DefaultText n : txts){
			logger.info(n.getText());
		}
	}
}
