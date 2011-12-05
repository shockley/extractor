package my.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "path")
public class Path {
	@Id @GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "path")
	private String xpath;
	
	@Column(name = "unique")
	private boolean uniqueInPage;

	public void setUniqueInPage(boolean uniqueInPage) {
		this.uniqueInPage = uniqueInPage;
	}

	public boolean isUniqueInPage() {
		return uniqueInPage;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getXpath() {
		return xpath;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public boolean equals(Object o){
		if(o instanceof Path){
			Path po = (Path) o;
			return this.hashCode() == po.hashCode();
		}
		return false;
	}
	
	public int hashCode(){
		return id;
	}
}
