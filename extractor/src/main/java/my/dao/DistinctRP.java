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
@Table(name = "distinct_rp")
public class DistinctRP {
	@Id @GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "ancestor2name")
	private String ancestor2name;
	
	@Column(name = "ancestor2value")
	private String ancestor2value;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "alias_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Alias alias;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAncestor2name() {
		return ancestor2name;
	}

	public void setAncestor2name(String ancestor2name) {
		this.ancestor2name = ancestor2name;
	}

	public String getAncestor2value() {
		return ancestor2value;
	}

	public void setAncestor2value(String ancestor2value) {
		this.ancestor2value = ancestor2value;
	}

	
	public Alias getAlias() {
		return alias;
	}

	public void setAlias(Alias alias) {
		this.alias = alias;
	}

}
