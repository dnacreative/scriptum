package gr.scriptum.domain;

// Generated Nov 23, 2011 9:45:19 AM by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * ProtocolBook generated by hbm2java
 */
public class ProtocolBook implements java.io.Serializable {

	public static final Integer IS_ACTIVE = 1;
	
	public static final Integer IS_NOT_ACTIVE= 0;
	
	private Integer id;
	private String protocolSeries;
	private Integer isIncoming;
	private String protocolNumber;
	private Integer protocolYear;
	private String protocolPath;
	private Integer isActive;
	private String createUser;
	private String updateUser;
	private Date createDt;
	private Date updateTs;
	private Integer isPreferred;
	private Set<OutgoingProtocol> outgoingProtocols = new HashSet<OutgoingProtocol>(
			0);
	private Set<ProtocolNumber> protocolNumbers = new HashSet<ProtocolNumber>(0);
	private Set<IncomingProtocol> incomingProtocols = new HashSet<IncomingProtocol>(
			0);

	public ProtocolBook() {
	}

	public ProtocolBook(String protocolSeries, Integer isIncoming,
			String protocolNumber, Integer protocolYear, String protocolPath,
			Integer isActive, String createUser, String updateUser,
			Date createDt, Date updateTs, Integer isPreferred,
			Set<OutgoingProtocol> outgoingProtocols,
			Set<ProtocolNumber> protocolNumbers,
			Set<IncomingProtocol> incomingProtocols) {
		this.protocolSeries = protocolSeries;
		this.isIncoming = isIncoming;
		this.protocolNumber = protocolNumber;
		this.protocolYear = protocolYear;
		this.protocolPath = protocolPath;
		this.isActive = isActive;
		this.createUser = createUser;
		this.updateUser = updateUser;
		this.createDt = createDt;
		this.updateTs = updateTs;
		this.isPreferred = isPreferred;
		this.outgoingProtocols = outgoingProtocols;
		this.protocolNumbers = protocolNumbers;
		this.incomingProtocols = incomingProtocols;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProtocolSeries() {
		return this.protocolSeries;
	}

	public void setProtocolSeries(String protocolSeries) {
		this.protocolSeries = protocolSeries;
	}

	public Integer getIsIncoming() {
		return this.isIncoming;
	}

	public void setIsIncoming(Integer isIncoming) {
		this.isIncoming = isIncoming;
	}

	public String getProtocolNumber() {
		return this.protocolNumber;
	}

	public void setProtocolNumber(String protocolNumber) {
		this.protocolNumber = protocolNumber;
	}

	public Integer getProtocolYear() {
		return this.protocolYear;
	}

	public void setProtocolYear(Integer protocolYear) {
		this.protocolYear = protocolYear;
	}

	public String getProtocolPath() {
		return this.protocolPath;
	}

	public void setProtocolPath(String protocolPath) {
		this.protocolPath = protocolPath;
	}

	public Integer getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getCreateDt() {
		return this.createDt;
	}

	public void setCreateDt(Date createDt) {
		this.createDt = createDt;
	}

	public Date getUpdateTs() {
		return this.updateTs;
	}

	public void setUpdateTs(Date updateTs) {
		this.updateTs = updateTs;
	}

	public Integer getIsPreferred() {
		return this.isPreferred;
	}

	public void setIsPreferred(Integer isPreferred) {
		this.isPreferred = isPreferred;
	}

	public Set<OutgoingProtocol> getOutgoingProtocols() {
		return this.outgoingProtocols;
	}

	public void setOutgoingProtocols(Set<OutgoingProtocol> outgoingProtocols) {
		this.outgoingProtocols = outgoingProtocols;
	}

	public Set<ProtocolNumber> getProtocolNumbers() {
		return this.protocolNumbers;
	}

	public void setProtocolNumbers(Set<ProtocolNumber> protocolNumbers) {
		this.protocolNumbers = protocolNumbers;
	}

	public Set<IncomingProtocol> getIncomingProtocols() {
		return this.incomingProtocols;
	}

	public void setIncomingProtocols(Set<IncomingProtocol> incomingProtocols) {
		this.incomingProtocols = incomingProtocols;
	}

}
