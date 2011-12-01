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
@Table(name = "match_path")
public class MatchPath {
	@Id @GeneratedValue
	@Column(name = "mp_id")
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "forge_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Forge forge;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "attribute_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Attribute attribute;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "path_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Path path;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public Attribute getAttribute() {
		return attribute;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public Path getPath() {
		return path;
	}
	public void setForge(Forge forge) {
		this.forge = forge;
	}
	public Forge getForge() {
		return forge;
	}
	
}
