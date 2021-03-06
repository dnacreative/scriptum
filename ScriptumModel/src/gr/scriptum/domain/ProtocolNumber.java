package gr.scriptum.domain;

// Generated Feb 22, 2011 10:06:27 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

/**
 * ProtocolNumber generated by hbm2java
 */
public class ProtocolNumber implements java.io.Serializable {

	public enum ProtocolNumberType {
		INCOMING, OUTGOING, COMMON;
	};

	private Integer id;
	private Long version;
	private ProtocolBook protocolBook;
	private Long number;
	private Integer series;
	private Integer year;
	private Integer type;
	private Date createDt;
	private Date updateTs;

	public ProtocolNumber() {
	}

	public ProtocolNumber(ProtocolBook protocolBook) {
		this.protocolBook = protocolBook;
	}

	public ProtocolNumber(ProtocolBook protocolBook, Long number, Integer series, Integer year,
			Integer type, Date createDt, Date updateTs) {
		this.protocolBook = protocolBook;
		this.number = number;
		this.series = series;
		this.year = year;
		this.type = type;
		this.createDt = createDt;
		this.updateTs = updateTs;
	}

	public ProtocolNumberType getProNumberType() {
		return ProtocolNumberType.values()[type.intValue()];
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getNumber() {
		return this.number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public Integer getSeries() {
		return this.series;
	}

	public void setSeries(Integer series) {
		this.series = series;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public ProtocolBook getProtocolBook() {
		return protocolBook;
	}

	public void setProtocolBook(ProtocolBook protocolBook) {
		this.protocolBook = protocolBook;
	}

}
