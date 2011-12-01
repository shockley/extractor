package my.action;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import my.dao.Attribute;
import my.dao.Forge;
import my.dao.Project;

import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
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
	
	
	/**
	 * Tranverse Html page of a given project, using MyVisitor
	 * @param project
	 * @param tofind
	 * @param attri
	 * @param forge
	 */
	public void tranverseHTML(Project project, String tofind, Attribute attri, Forge forge){
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
		MyVisitor v = new MyVisitor(tofind, attri, forge);
		for(Iterator<Element> i = root.elementIterator();i.hasNext();){
			Element el = (Element)i.next();
			el.accept(v);
		}
	}
	
	public String treadAlongPath(String path, String pageContent, String forge, String project, String aName){
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new StringReader(pageContent)));
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
		List<Node> nodes = document.selectNodes(path);
		for(Node n : nodes){
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element em = (Element)n;
				String s = em.getText();
			}
		}
		Element root = document.getRootElement();

		return null;
	}
}
