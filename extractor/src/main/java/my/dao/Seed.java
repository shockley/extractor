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
@Table(name = "seed")
public class Seed {
	@Id @GeneratedValue
	@Column(name = "seed_id")
	private int id;
	
	@Column(name = "value")
	private String value;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "attribute_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Attribute attribute;

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
