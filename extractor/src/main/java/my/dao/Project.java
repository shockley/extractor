package my.dao;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project {
	@Id @GeneratedValue
	@Column(name = "proj_id")
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "forge_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Forge forge;
	
	@Column(name = "proj_short_name")	
	private String name;
	
	@Column(name = "proj_real_name")	
	private String realname;
	
	@Column(name = "html")
	private String html;
	
	@Column(name = "url")	
	private String url;
	
	@Column(name = "date_collected")
	private Date timestamp;

	@Column(name = "page_index")
	private int page;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setRealName(String realname) {
		this.realname = realname;
	}

	public String getRealName() {
		return realname;
	}
	public void setHtml(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setForge(Forge forge) {
		this.forge = forge;
	}

	public Forge getForge() {
		return forge;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public boolean equals(Object o){
		if(o==null || !(o instanceof Project)) return false;
		Project tmp = (Project) o;
		return this.id.equals(tmp.id);
	}
	
	public int hashCode(){
		return this.id.intValue();
	}
}
