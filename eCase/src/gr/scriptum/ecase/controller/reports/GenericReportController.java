package gr.scriptum.ecase.controller.reports;

/**
 * Template ZK Controller for all reports. 
 * @author Mike Mountrakis mountrakis@uit.gr
 */


import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import gr.scriptum.controller.BaseController;
import gr.scriptum.dao.reports.ReportProducerDAO;



public class GenericReportController< T, DAO extends ReportProducerDAO>  
       extends BaseController   {
	
	private static final long serialVersionUID = -4764822451990267965L;
	private static Log log = LogFactory.getLog(TaskNumberPerUserController.class);

	/* components */
	Window win;
	Paging reportPgng;
	Bandbox reportBndbx;
	
	private Class<T>   entityClass = null;

	private Class<DAO> daoClass = null;

	/* data binding */
	protected List<T> reportResults = null;

	protected T entity = null;
	

	
	
	@SuppressWarnings("unchecked")
	private T initEntity() throws Exception {
		return (T) Class.forName(entityClass.getName()).newInstance();
	}

	@SuppressWarnings("unchecked")
	private DAO initDAO() throws Exception {
		return (DAO) Class.forName(daoClass.getName()).newInstance();
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//Beware!!!!!!!!!!!!!!!!!!!!!
		//Use the following to set AssignerReportController as the data binding entity
		page.setAttribute(this.getClass().getSimpleName(), this);
		
		entityClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		entity = initEntity();

		daoClass = (Class<DAO>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
		
	}
	

	@SuppressWarnings("unchecked")
	private void createReport(int startIndex) throws Exception {
		// Use the Domain to load the daos.
		DAO dao = initDAO();
	
		// set up paging by counting records first
		Integer totalSize = dao.countReportRows(getUserInSession());;
		reportPgng.setTotalSize(totalSize);
		reportResults = (List<T>)dao.createReport(getUserInSession(),
				startIndex, reportPgng.getPageSize());
	}
	
	

	public void onClick$createBtn() throws Exception {
		createReport(0);
		log.info("Gathered Results Count:" + reportResults.size());
		getBinder(win).loadAll();
	}

	public void onPaging$reportPgng(PagingEvent event)
			throws Exception {

		int activePage = event.getActivePage();
		int startIndex = activePage * reportPgng.getPageSize();
		createReport(startIndex);
		getBinder(win).loadAll();

	}

	public List<T> getReportResults() {
		return reportResults;
	}

	public void setReportResults(List<T> reportResults) {
		this.reportResults = reportResults;
	}
	
	

}
