package my.dao;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;







@Entity
@Table(name = "forges")
public class Forge {
	@Id @GeneratedValue
	@Column(name = "forge_id")
	private Long id;
	
	@Column(name = "forge_name",unique = true)
	private String name;
	
	@Column(name = "main_url")
	private String mainUrl;
	
	@Column(name = "proj_list_url")
	private String projListUrl;

	@OneToMany(mappedBy = "forge")
	@Basic(fetch = FetchType.LAZY)
	//the mapped by is necessary unless it's unidirectional
	//Associations marked as mappedBy must not define database mappings 
	//like @JoinTable or @JoinColumn
	private List<Project> projects;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainUrl() {
		return mainUrl;
	}

	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}

	public String getProjListUrl() {
		return projListUrl;
	}

	public void setProjListUrl(String projListUrl) {
		this.projListUrl = projListUrl;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public List<Project> getProjects() {
		return projects;
	}
	
	public String toString(){
		if(name==null)
			return "";
		return name.toString(); 
	}
	
	public boolean equals(Object o){
		boolean toReturn = false;
		if(o instanceof Forge){
			Forge f = (Forge) o;
			if(name!=null)
				return name.equals(f.name);
			else
				return f.name==null;
		}
		return toReturn;
	}
	
	public int hashCode(){
		if(name==null)
			return 0;
		else
			return name.hashCode();
	}
}
