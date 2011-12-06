package my.action.algorithm;

import org.hibernate.Session;

import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

public class Validator {
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	public void validate(){
		Session s = hs.getSession();
		String hql = "select * from projects where";
		s.createSQLQuery("");
	}
}
