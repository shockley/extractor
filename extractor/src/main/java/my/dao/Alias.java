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
@Table(name = "alias")
public class Alias {
	@Id @GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "value")
	private String value;
	

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "forge_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Forge forge;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "attribute_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Attribute attribute;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Forge getForge() {
		return forge;
	}

	public void setForge(Forge forge) {
		this.forge = forge;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}


}
