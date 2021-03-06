package gr.scriptum.dao.reports;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;

import gr.scriptum.dao.GenericDAO;
import gr.scriptum.domain.Users;
import gr.scriptum.domain.reports.TaskPerProject;


/**
 * @author Michael Mountrakis mountrakis@uit.gr
 * Date: Dec 1, 2011
 * Project: SCRIPTUM http://www.scriptum.gr
 * http://www.uit.gr
 */
public class ReportTasksWaitingExternalDAO extends
		GenericDAO<TaskPerProject, Integer> implements ReportProducerDAO {

	
	private static Log log = LogFactory.getLog(ReportTasksWaitingExternalDAO.class);
	
	
	@Override
	public Integer countReportRows(Users user)  {
		
		log.info("countReportRows() started.");
		Query query = getSession()
				.createSQLQuery("SELECT COUNT(*) AS rcount FROM (" + 
								" SELECT  pt.project_id AS pId, pt.id AS ptId, usr.name AS usrName, usr.surname AS usrSurname , p.name AS pName, pt.name  AS ptName , ts.name AS tsName , co.name AS coName,co.surname AS coSurname, com.name AS comName " +  
								" FROM project_task pt  LEFT JOIN project p     ON pt.project_id = p.id " + 
								" LEFT JOIN users usr     ON pt.user_dispatcher_id  = usr.id " +
								" LEFT JOIN task_state ts ON pt.task_state_id = ts.id " +
								" LEFT JOIN contact co    ON co.id = pt.contact_id " + 
								" LEFT JOIN company com   ON com.id = co.company_id " +
								" WHERE  ( p.user_creator_id = :myUserId OR    ( pt.project_id IS NULL AND pt.user_creator_id = :myUserId ) ) " + 
								" AND pt.task_state_id = 3" +
								" ) AS rset");

		query.setParameter("myUserId", user.getId());
		log.info("countReportRows() finished.");
		return ((BigInteger) query.uniqueResult()).intValue();
	}

	@Override
	public List<TaskPerProject> createReport(Users user, Integer firstResult, Integer maxResults) {
		log.info("createReport(1) started.");
		Query query = getSession()
				.createSQLQuery( "SELECT  pt.project_id AS pId, pt.id AS ptId, usr.name AS usrName, usr.surname AS usrSurname , p.name AS pName, pt.name  AS ptName , ts.name AS tsName , co.name AS coName,co.surname AS coSurname, com.name AS comName " +  
								" FROM project_task pt  LEFT JOIN project p     ON pt.project_id = p.id " + 
								" LEFT JOIN users usr     ON pt.user_dispatcher_id  = usr.id " +
								" LEFT JOIN task_state ts ON pt.task_state_id = ts.id " +
								" LEFT JOIN contact co    ON co.id = pt.contact_id " + 
								" LEFT JOIN company com   ON com.id = co.company_id " +
								" WHERE  ( p.user_creator_id = :myUserId OR    ( pt.project_id IS NULL AND pt.user_creator_id = :myUserId ) ) " + 
								" AND pt.task_state_id = 3" +
						        " ORDER BY pt.project_id, pt.id , usr.id  LIMIT " + firstResult + "," + maxResults)
						        .addScalar("pId").addScalar("ptId").addScalar("usrName").addScalar("usrSurname").addScalar("pName").addScalar("ptName").addScalar("tsName").addScalar("coName").addScalar("coSurname").addScalar("comName");

		query.setParameter("myUserId", user.getId());

		List<?> list = query.list();
		log.info("createReport() Report fetched : " + list.size());
		
		List<TaskPerProject> results = new ArrayList<TaskPerProject>();
		for (Object result : list) {
			Object[] row = (Object[]) result;
			TaskPerProject tp = new TaskPerProject();
			tp.setProjectId((Integer) row[0]);
			tp.setTaskId((Integer) row[1]);
			tp.setTaskDispatcher((String) (row[2] + " " + row[3]));
			tp.setProjectName((String) row[4]);
			tp.setTaskName((String) row[5]);
			tp.setTaskState((String) row[6]);
			tp.setContactName((String)row[7]);
			tp.setContactSurname((String) row[8]);
			tp.setCompanyName((String) row[9]);
			results.add(tp);
		}

		log.info("createReport() finished.");
		return  results;
	}

}