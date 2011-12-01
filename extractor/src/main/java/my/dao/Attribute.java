package my.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "attribute")
public class Attribute {
	@Id @GeneratedValue
	@Column(name = "attribute_id")
	private Long id;

	@Column(name = "attribute_name")
	private String name;
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
}
