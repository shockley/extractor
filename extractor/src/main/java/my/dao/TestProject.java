package my.dao;

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
@Table(name = "projects_test")
public class TestProject {
	@Id @GeneratedValue
	@Column(name = "proj_id")
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "forge_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Forge forge;
	
	@Column(name = "proj_short_name")	
	private String name;
	
	@Column(name = "html")
	private String html;
	
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setHtml(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
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

	public boolean equals(Object o){
		if(o==null || !(o instanceof TestProject)) return false;
		TestProject tmp = (TestProject) o;
		return this.id.equals(tmp.id);
	}
	
	public int hashCode(){
		return this.id.intValue();
	}

}
