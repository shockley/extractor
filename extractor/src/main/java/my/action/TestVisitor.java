package my.action;

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

/**
 * 
 * a visitor to do a lot of tests
 * @author Shockley
 *
 */
public class TestVisitor implements Visitor{
	public TestVisitor(String tomatch) {
		super();
		this.tomatch = tomatch;
	}
	public static Logger logger = Logger.getLogger(TestVisitor.class);
	private String tomatch = null;
	public void visit(Document document) {}
	public void visit(DocumentType documentType) {}
	public void visit(Element node) {
		// TODO Auto-generated method stub
		if(node.getText().equals(tomatch)){
			logger.info(" found it");
		}
	}
	public void visit(org.dom4j.Attribute node) {}
	public void visit(CDATA node) {}
	public void visit(Comment node) {}
	public void visit(Entity node) {}
	public void visit(Namespace namespace) {}
	public void visit(ProcessingInstruction node) {}
	public void visit(Text node) {}
}
