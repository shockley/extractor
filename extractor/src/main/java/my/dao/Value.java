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
@Table(name = "value")
public class Value {
	@Id @GeneratedValue
	@Column(name = "value_id")
	private int id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "attribute_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Attribute attribute;
	
	@Column(name = "value")
	private String value;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "proj_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Project project;
	
	@Column(name = "truth")
	private boolean truth;

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public void setTruth(boolean truth) {
		this.truth = truth;
	}

	public boolean isTruth() {
		return truth;
	}

}
