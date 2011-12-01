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
	@Column(name = "path_id")
	private int id;
	
	@Column(name = "xpath")
	private String xpath;
	
	@Column(name = "unique_in_page")
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
}
