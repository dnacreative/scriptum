/**
 * 
 */
package gr.scriptum.dao;

import gr.scriptum.domain.Contact;
import gr.scriptum.domain.DistributionMethod;
import gr.scriptum.domain.IncomingProtocol;
import gr.scriptum.domain.ProtocolBook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * @author aanagnostopoulos
 * 
 */
public abstract class ProtocolDAO<T, ID extends Serializable> extends
		GenericDAO<T, Serializable> {

	private static Log log = LogFactory.getLog(ProtocolDAO.class);

	private List<Criterion> buildPendingCriteria(Date from, Date to) {
		List<Criterion> criteria = new ArrayList<Criterion>();

		criteria.add(Restrictions.isNull("protocolNumber"));
		// TODO: don't fetched protocols flagged as deleted (if such a feature
		// is implemented)

		if (from != null) {
			criteria.add(Restrictions.ge("updateTs", from));
		}

		if (to != null) {
			/*
			 * add one day to make date search more user-friendly (since the
			 * user tends to think in terms of <= , when it comes to dates)
			 */
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(to);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			criteria.add(Restrictions.lt("updateTs", calendar.getTime()));
		}
		return criteria;
	}

	private Criteria buildSearchCriteria(String protocolNumber, Date from,
			Date to, String subject, String keywords,
			DistributionMethod distributionMethod, boolean includePending,
			boolean includeDeleted, List<ProtocolBook> protocolBooks) {

		Criteria crit = getSession().createCriteria(getPersistentClass());

		if (!includeDeleted) {
			// exclude deleted protocols
			Disjunction notDeletedDisjunction = Restrictions.disjunction();
			notDeletedDisjunction.add(Restrictions.eq("isDeleted", false));
			notDeletedDisjunction.add(Restrictions.isNull("isDeleted"));
			crit.add(notDeletedDisjunction);
		}

		if (protocolNumber != null) {

			Integer protocolNumberParsed = null;
			try {
				protocolNumberParsed = Integer.valueOf(protocolNumber);
			} catch (NumberFormatException e) {
				log.warn(e);
			}

			if (protocolNumberParsed == null) {
				// make sure no results are returned
				crit.add(Restrictions.isNull("id"));
			} else {
				crit.add(Restrictions
						.eq("protocolNumber", protocolNumberParsed));
			}
		}

		if (!includePending) {
			// ensure search is performed in submitted protocols
			crit.add(Restrictions.isNotNull("protocolNumber"));
		}

		if (from != null) {
			crit.add(Restrictions.ge("protocolDate", from));
		}

		if (to != null) {
			/*
			 * add one day to make date search more user-friendly (since the
			 * user tends to think in terms of <= , when it comes to dates)
			 */
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(to);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			crit.add(Restrictions.lt("protocolDate", calendar.getTime()));
		}

		if (subject != null) {
			crit.add(Restrictions.like("subject", subject, MatchMode.START));
		}

		if (distributionMethod != null) {
			crit.add(Restrictions.eq("distributionMethod", distributionMethod));
		}

		if (keywords != null) {
			Criteria documentCriteria = crit
					.createCriteria("protocolDocuments");
			String[] tokens = keywords
					.split(ProtocolDocumentDAO.KEYWORDS_DELIMITER);
			Disjunction disjunction = Restrictions.disjunction();
			for (String token : tokens) {
				disjunction.add(Restrictions.like("documentKeywords", token,
						MatchMode.ANYWHERE));
			}
			documentCriteria.add(disjunction);
		}

		if (protocolBooks != null && !protocolBooks.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.in("protocolBook", protocolBooks));
			disjunction.add(Restrictions.isNull("protocolBook"));
			crit.add(disjunction);
		}
		return crit;

	}

	/**
	 * Stub method, meant to be overriden by extending classes
	 * 
	 * @param protocolNumber
	 * @param from
	 * @param to
	 * @param subject
	 * @param keywords
	 * @param distributionMethod
	 * @param contact
	 * @return
	 */
	protected Criteria buildSearchCriteria(String protocolNumber, Date from,
			Date to, String subject, String keywords,
			DistributionMethod distributionMethod, Contact contact,
			boolean includePending, boolean includeDeleted,
			List<ProtocolBook> protocolBooks) {
		return buildSearchCriteria(protocolNumber, from, to, subject, keywords,
				distributionMethod, includePending, includeDeleted,
				protocolBooks);
	}

	public List<T> findPending(T example, Date from, Date to, Order sortBy,
			Integer firstResult, Integer maxResults) {

		List<Criterion> criteria = buildPendingCriteria(from, to);

		List<Order> sortByList = new LinkedList<Order>();
		sortByList.add(sortBy);

		return findByExample(example, MatchMode.START, criteria, sortByList,
				firstResult, maxResults);

	}

	public Integer countPending(T example, Date from, Date to) {
		List<Criterion> criteria = buildPendingCriteria(from, to);
		return countByExample(example, MatchMode.START, criteria);
	}

	public Integer countSearch(String protocolNumber, Date from, Date to,
			String subject, String keywords,
			DistributionMethod distributionMethod, Contact contact,
			boolean includePending, boolean includeDeleted,
			List<ProtocolBook> protocolBooks) {

		Criteria crit = buildSearchCriteria(protocolNumber, from, to, subject,
				keywords, distributionMethod, contact, includePending,
				includeDeleted, protocolBooks);
		crit.setProjection(Projections.rowCount());
		Integer count = (Integer) crit.uniqueResult();
		log.info("Rows counted:" + count);
		return count;
	}

	@SuppressWarnings("unchecked")
	public List<T> search(String protocolNumber, Date from, Date to,
			String subject, String keywords,
			DistributionMethod distributionMethod, Contact contact,
			boolean includePending, boolean includeDeleted,
			List<ProtocolBook> protocolBooks, Integer firstResult,
			Integer maxResults, Order... sortBy) {

		Criteria crit = buildSearchCriteria(protocolNumber, from, to, subject,
				keywords, distributionMethod, contact, includePending,
				includeDeleted, protocolBooks);

		for (Order order : sortBy) {
			crit.addOrder(order);
		}

		if (firstResult != null) {
			crit.setFirstResult(firstResult);
		}
		if (maxResults != null) {
			crit.setMaxResults(maxResults);
		}

		List<T> results = crit.list();
		log.info("Rows fetched:" + results.size());
		return results;
	}

}
