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
@Table(name = "attribute")
public class Attribute {
	@Id @GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "attribute_name")
	private String name;
	
	@OneToMany(mappedBy = "attribute")
	@Basic(fetch = FetchType.LAZY)
	private List<Alias> aliases;
	
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
	public String toString(){
		if(name==null)
			return "";
		return name.toString(); 
	}
	public void setAliases(List<Alias> aliases) {
		this.aliases = aliases;
	}
	public List<Alias> getAliases() {
		return aliases;
	}
}
