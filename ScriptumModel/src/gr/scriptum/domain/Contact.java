package gr.scriptum.domain;

// Generated Jan 27, 2011 1:23:42 PM by Hibernate Tools 3.3.0.GA

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Contact generated by hbm2java
 */
public class Contact implements java.io.Serializable {

	private Integer id;
	private Company company;
	private String name;
	private String surname;
	private String organization;
	private String department;
	private String job;
	private String address;
	private String postcode;
	private String city;
	private String country;
	private String email;
	private String telephone;
	private String fax;
	private String mobile;
	private String createUser;
	private String updateUser;
	private Date createDt;
	private Date updateTs;
	private String middlename;
	private String street;
	private String streetNo;
	private String prefecture;
	private String web;
	private Set<ProjectTask> projectTasks = new HashSet<ProjectTask>(0);
	private Set<OutgoingRecipient> outgoingRecipients = new HashSet<OutgoingRecipient>(
			0);
	private Set<IncomingProtocol> incomingProtocols = new HashSet<IncomingProtocol>(
			0);

	public Contact() {
	}

	public Contact(Company company) {
		this.company = company;
	}

	public Contact(Company company, String name, String surname,
			String organization, String department, String job, String address,
			String postcode, String city, String country, String email,
			String telephone, String fax, String mobile, String createUser,
			String updateUser, Date createDt, Date updateTs, String middlename,
			String street, String streetNo, String prefecture, String web,
			Set<ProjectTask> projectTasks,
			Set<OutgoingRecipient> outgoingRecipients,
			Set<IncomingProtocol> incomingProtocols) {
		this.company = company;
		this.name = name;
		this.surname = surname;
		this.organization = organization;
		this.department = department;
		this.job = job;
		this.address = address;
		this.postcode = postcode;
		this.city = city;
		this.country = country;
		this.email = email;
		this.telephone = telephone;
		this.fax = fax;
		this.mobile = mobile;
		this.createUser = createUser;
		this.updateUser = updateUser;
		this.createDt = createDt;
		this.updateTs = updateTs;
		this.middlename = middlename;
		this.street = street;
		this.streetNo = streetNo;
		this.prefecture = prefecture;
		this.web = web;
		this.projectTasks = projectTasks;
		this.outgoingRecipients = outgoingRecipients;
		this.incomingProtocols = incomingProtocols;
	}

	public String getFullName() {
		return name + " " + surname;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getJob() {
		return this.job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public String getMiddlename() {
		return this.middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNo() {
		return this.streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getPrefecture() {
		return this.prefecture;
	}

	public void setPrefecture(String prefecture) {
		this.prefecture = prefecture;
	}

	public Set<ProjectTask> getProjectTasks() {
		return this.projectTasks;
	}

	public void setProjectTasks(Set<ProjectTask> projectTasks) {
		this.projectTasks = projectTasks;
	}

	public Set<OutgoingRecipient> getOutgoingRecipients() {
		return this.outgoingRecipients;
	}

	public void setOutgoingRecipients(Set<OutgoingRecipient> outgoingRecipients) {
		this.outgoingRecipients = outgoingRecipients;
	}

	public Set<IncomingProtocol> getIncomingProtocols() {
		return this.incomingProtocols;
	}

	public void setIncomingProtocols(Set<IncomingProtocol> incomingProtocols) {
		this.incomingProtocols = incomingProtocols;
	}

	/**
	 * @return the web
	 */
	public String getWeb() {
		return web;
	}

	/**
	 * @param web
	 *            the web to set
	 */
	public void setWeb(String web) {
		this.web = web;
	}
	@Override
	public String toString() {
		return "Contact [id=" + id + ", company=" + company + ", name=" + name
				+ ", surname=" + surname + ", organization=" + organization
				+ ", department=" + department + ", job=" + job + ", address="
				+ address + ", postcode=" + postcode + ", city=" + city
				+ ", country=" + country + ", email=" + email + ", telephone="
				+ telephone + ", fax=" + fax + ", mobile=" + mobile
				+ ", createUser=" + createUser + ", updateUser=" + updateUser
				+ ", createDt=" + createDt + ", updateTs=" + updateTs
				+ ", middlename=" + middlename + ", street=" + street
				+ ", streetNo=" + streetNo + ", prefecture=" + prefecture
				+ ", web=" + web + ", projectTasks=" + projectTasks
				+ ", outgoingRecipients=" + outgoingRecipients
				+ ", incomingProtocols=" + incomingProtocols + "]";
	}
}
