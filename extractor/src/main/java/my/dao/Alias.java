package my.dao;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
	
	
	@OneToOne(mappedBy = "alias")
	@Basic(fetch = FetchType.LAZY)
	private DistinctRP rp;
	
	@OneToOne(mappedBy = "alias")
	@Basic(fetch = FetchType.LAZY)
	private MaxMatchPath mmp;
	
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
	
	public boolean equals(Object o){
		if(o instanceof Alias){
			Alias ao = (Alias) o;
			return this.hashCode() == ao.hashCode();
		}
		return false;
	}
	
	public int hashCode(){
		return id;
	}

	public void setMmp(MaxMatchPath mmp) {
		this.mmp = mmp;
	}

	public MaxMatchPath getMmp() {
		return mmp;
	}

	public void setRp(DistinctRP rp) {
		this.rp = rp;
	}

	public DistinctRP getDrp() {
		return rp;
	}
}
