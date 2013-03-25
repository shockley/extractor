package my.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import my.action.algorithm.SimMatcher;
import net.trustie.datasource.DataSourceFactory;
import net.trustie.datasource.HibernateService;

public class Validator {
	public static Logger logger = Logger.getLogger(Validator.class);
	public static List<String> VALIDATIONS_RF = new ArrayList<String>();
	public static List<String> VALIDATIONS_INLUX = new ArrayList<String>();
	public static List<String> VALIDATIONS_FM = new ArrayList<String>();
	public static List<String> VALIDATIONS_OW2 = new ArrayList<String>();
	public HibernateService hs = DataSourceFactory.getHibernateInstance();
	static {
		VALIDATIONS_RF.add("rf_project_topic");
		VALIDATIONS_RF.add("rf_project_programming_language");
		VALIDATIONS_RF.add("rf_project_licenses");
		VALIDATIONS_RF.add("rf_project_operating_system");
		VALIDATIONS_INLUX.add("topic");
		VALIDATIONS_INLUX.add("programming_language");
		VALIDATIONS_INLUX.add("license");
		VALIDATIONS_INLUX.add("operating_system");
	}

	public void createIndexes(String settings){
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		String [] indexsqls = {
				"create index value_id on value_"+settings+"(value_id)",
				"create index proj_id on value_"+settings+"(proj_id)",
				"create index value on value_"+settings+"(value)",
				"create index attribute_id on value_"+settings+"(attribute_id)",
				"create index test_proj_id on value_"+settings+"(test_proj_id)"
		};
		for(String sql1 : indexsqls){
			session.createSQLQuery(sql1).executeUpdate();
		}
		tx.commit();
		logger.info("index created : " + settings);
	}
	
	/**
	 * @param attribute_id
	 * @param seed_count
	 * @param forge
	 */
	public void validate(int attribute_id, int seed_count, String forge) {
		String forgeAbbr = forge;
		if(forge.equals("sourceforge")){
			forgeAbbr = "sf_";
		}else if(forge.equals("freshmeat")){
			forgeAbbr = "fm_";
		}else if(forge.equals("ow2")){
			forgeAbbr = "ow2_";
		}else if(forge.equals("rubyforge")){
			forgeAbbr = "rf_";
		}
		Session session = hs.getSession();
		Transaction tx = session.beginTransaction();
		String settings = "a_" + attribute_id + "s_" + seed_count
		+ "f_"+forge+"t_" + (int) (SimMatcher.THRESHOLD * 10);
		
		
		
		String ttable = "truth_" + forgeAbbr + "a_"+ attribute_id;
		String vtable = "value_" + settings;
		
		/*String sql1 = "";
		sql1 += "insert into";
		sql1 += " metric_total (";
		sql1 += "   total_extracted_result,";
		sql1 += "   total_truth, ";
		sql1 += "   settings)";
		sql1 += "select";
		sql1 += "	count(distinct v.value_id) as extracted_total,";
		sql1 += "	count(distinct t.id) as truth_total,";
		sql1 += "	\'"+settings+"\' ";
		sql1 += "from";
		sql1 += "	" + vtable;
		sql1 += "	" + ttable + " t";
		
		session.createSQLQuery(sql1).executeUpdate();*/
		
		String sql1 = "";
		sql1 += "insert into ";
		sql1 += "  metric_match (";
		sql1 += "	matched_extracted_result, ";
		sql1 += "	matched_truth,";
		sql1 += "	settings) ";
		sql1 += "select";
		sql1 += "	count(distinct v.value_id) as correct, ";
		sql1 += "	count(distinct t.id) as recall, ";
		sql1 += "   \'"+settings+"\' ";
		sql1 += "from";
		sql1 += "   " + ttable + " t,";
		sql1 += "   projects_test pt,";
		sql1 += "	" + vtable + " v ";
		sql1 += "where";
		sql1 += "	pt.proj_short_name = t.proj_short_name &&";
		sql1 += "	t.description = v.value &&";
		sql1 += "	v.test_proj_id = pt.proj_id &&" +
				"   v.truth = 0";

		
		
		
		
		
		String sql2 = "";
		sql2 += "update";
		sql2 += "  metric_match ";
		sql2 += "set";
		sql2 += "  total_extracted = ( ";
		sql2 += "    select   ";
		sql2 += "        count(v.value_id)           ";
		sql2 += "    from   ";
		sql2 += "        " + vtable + " v" +
				"    where  " +
				"        v.truth = 0) ";
		sql2 += "where";
		sql2 += "	settings = \'" + settings + "\'";

		String sql3 = "";
		sql3 += "update";
		sql3 += "  metric_match ";
		sql3 += "set";
		sql3 += "  total_truth = ( ";
		sql3 += "    select   ";
		sql3 += "        count(t.id)           ";
		sql3 += "    from   ";
		sql3 += "        " + ttable + " t) ";
		sql3 += "where";
		sql3 += "	settings = \'" + settings + "\'";
		
		
		session.createSQLQuery(sql1).executeUpdate();
		session.createSQLQuery(sql2).executeUpdate();
		session.createSQLQuery(sql3).executeUpdate();
		tx.commit();
		logger.info("validated " + forge);
	}
}
