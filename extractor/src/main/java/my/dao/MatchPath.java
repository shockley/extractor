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
@Table(name = "mp")
public class MatchPath {
	@Id @GeneratedValue
	@Column(name = "id")
	private int id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "alias_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Alias alias;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "path_id") //necessary
	@Basic(fetch = FetchType.LAZY)
	private Path path;
	
	@Column(name = "support")
	private int support;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	public Path getPath() {
		return path;
	}
	public void setAlias(Alias alias) {
		this.alias = alias;
	}
	public Alias getAlias() {
		return alias;
	}
	public void setSupport(int support) {
		this.support = support;
	}
	public int getSupport() {
		return support;
	}
	public boolean equals(Object o){
		if(o instanceof MatchPath){
			MatchPath omp = (MatchPath) o;
			return hashCode()==omp.hashCode();
		}
		return false;
	}
	
	public int hashCode(){
		if(alias==null){
			if(path==null)
				return 1;
			else
				return 1 + 17 * path.hashCode();
		}else{
			if(path==null)
				return 1 + 31 * alias.hashCode();
			else
				return 1 + 31 * alias.hashCode() + 17 * path.hashCode();
		}
	}
}
