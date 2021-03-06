/**
 * 
 */
package gr.scriptum.ecase.controller;

import gr.scriptum.dao.IncomingProtocolDAO;
import gr.scriptum.dao.OutgoingProtocolDAO;
import gr.scriptum.dao.ParameterDAO;
import gr.scriptum.dao.ProjectTaskDAO;
import gr.scriptum.dao.ProjectUserDAO;
import gr.scriptum.dao.TaskMessageDAO;
import gr.scriptum.dao.UserHierarchyDAO;
import gr.scriptum.domain.Department;
import gr.scriptum.domain.IncomingProtocol;
import gr.scriptum.domain.OutgoingProtocol;
import gr.scriptum.domain.Project;
import gr.scriptum.domain.ProjectTask;
import gr.scriptum.domain.ProjectUser;
import gr.scriptum.domain.ProtocolDocument;
import gr.scriptum.domain.TaskDocument;
import gr.scriptum.domain.TaskMessage;
import gr.scriptum.domain.UserHierarchy;
import gr.scriptum.domain.Users;
import gr.scriptum.ecase.util.IConstants;
import gr.scriptum.eprotocol.mailclients.ImapProtocolDispatcherImpl;
import gr.scriptum.eprotocol.mailclients.MailDispatcherConfig;
import gr.scriptum.eprotocol.mailclients.SendMailReceipt;
import gr.scriptum.eprotocol.ws.ResponseSendDocument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.event.PagingEvent;

/**
 * @author aanagnostopoulos
 * 
 */
public class OutgoingTaskController extends TaskController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4338541320808714303L;

	public static final String PAGE = "task.zul";

	public static final String PARAM_KEY_CLONE = "c";

	public static final String PARAM_KEY_PROTOCOL_INCOMING = "ip";

	public static final String PARAM_KEY_PROTOCOL_OUTGOING = "op";

	public static final String PARAM_CLONE_TRUE = "1";

	public static final Integer AUTO_MANAGED_TASK_YES = 1;

	public static final Integer AUTO_MANAGED_TASK_NO = 0;

	private static Log log = LogFactory.getLog(OutgoingTaskController.class);

	/* Components */
	Checkbox taskTypeChkbx;

	/* Data binding */
	private Department department = null;
	private Integer autoManagedTask = null;
	private Integer incomingProtocolId = null;
	private Integer outgoingProtocolId = null;

	/**
	 * A list of subordinate users. If the task creator belongs to a department
	 * able to assign anywhere, we must distinguish between 'pure' subordinate
	 * users and those belonging to sibling/higher departments.
	 */
	private List<UserHierarchy> subordinates = null;

	private void refreshUserDepartment() {
		// find which department user belongs to
		UserHierarchyDAO userHierarchyDAO = new UserHierarchyDAO();
		List<UserHierarchy> hierarchies = userHierarchyDAO
				.findByUser(getUserInSession());
		if (hierarchies.isEmpty()) { // user hasn't been assigned to a
										// department
			userHierarchies = new ArrayList<UserHierarchy>();
			return;
		}
		// user should only belong to exactly one department
		department = hierarchies.get(0).getDepartment();

	}

	private void refreshUserHierarchies() {
		if (department == null) {
			return;
		}
		UserHierarchyDAO userHierarchyDAO = new UserHierarchyDAO();
		if (department.getCanAssignAnywhere().equals(
				Department.CAN_ASSIGN_ANYWHERE_TRUE)) {
			// fetch all users belonging to all departments
			userHierarchies = userHierarchyDAO.findAll();
			subordinates = userHierarchyDAO
					.findSubordinates(getUserInSession());
		} else {
			// only fetch subordinates
			userHierarchies = userHierarchyDAO
					.findSubordinates(getUserInSession());
			subordinates = null;
		}
	}

	private void highlightProjectParticipants() {
		if (projectTask.getProject() == null) {
			for (UserHierarchy userHierarchy : userHierarchies) {
				userHierarchy.setIsHighlighted(false);
			}
			return;
		}

		ProjectUserDAO projectUserDAO = new ProjectUserDAO();
		List<ProjectUser> projectUsers = projectUserDAO
				.findByProject(projectTask.getProject());
		// we have to use IDs, instead of objects, because of hibernate's proxy
		// mechanism
		List<Integer> participantIds = new ArrayList<Integer>();
		for (ProjectUser projectUser : projectUsers) {
			participantIds.add(projectUser.getId().getUsers().getId());
		}
		// find & highlight participants in user hierarchies drop down
		for (UserHierarchy userHierarchy : userHierarchies) {
			Users user = userHierarchy.getUsers();
			if (participantIds.contains(user.getId())) {
				userHierarchy.setIsHighlighted(true);
			} else {
				userHierarchy.setIsHighlighted(false);
			}
		}
	}

	private void copyProtocolDocuments(Set<ProtocolDocument> protocolDocuments) {
		for (ProtocolDocument protocolDocument : protocolDocuments) {
			TaskDocument taskDocument = new TaskDocument();
			taskDocument.setDocumentName(protocolDocument.getDocumentName());
			taskDocument.setDocumentType(protocolDocument.getDocumentType());
			taskDocument.setDocumentKeywords(protocolDocument
					.getDocumentKeywords());
			taskDocument.setDocumentSize(protocolDocument.getDocumentSize());
			taskDocument
					.setDocumentNumber(protocolDocument.getDocumentNumber());
			// Document content is stored in OpenKM, fetch and copy
			ResponseSendDocument responseSendDocument = fetchDocumentFromOpenKM(protocolDocument);
			taskDocument.setContent(responseSendDocument.getContent());
			taskDocuments.add(taskDocument);
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		refreshUserDepartment();
		autoManagedTask = AUTO_MANAGED_TASK_NO;

		String idString = execution.getParameter(IConstants.PARAM_KEY_ID);
		if (idString != null) { // existing task

			ProjectTaskDAO projectTaskDAO = new ProjectTaskDAO();
			projectTask = projectTaskDAO.findById(Integer.valueOf(idString),
					false);

			if (projectTask == null) {
				Messagebox.show(Labels.getLabel("fetch.notFound"),
						Labels.getLabel("fetch.title"), Messagebox.OK,
						Messagebox.ERROR);
				return;
			}

			// check if user has access to the given task
			boolean authorized = true;
			if (getUserInSession().getId().intValue() != projectTask
					.getUsersByUserCreatorId().getId().intValue()
					&& getUserInSession().getId().intValue() != projectTask
							.getUsersByUserDispatcherId().getId().intValue()) {
				// logged in user is neither the task creator nor the assignee
				authorized = false;
			} else if (getUserInSession().getId().intValue() != projectTask
					.getUsersByUserCreatorId().getId().intValue()) {
				// logged in user is not the task assignee
				authorized = false;
			}

			if (!authorized) {
				Messagebox.show(Labels.getLabel("fetch.notFound"),
						Labels.getLabel("fetch.title"), Messagebox.OK,
						Messagebox.ERROR);
				projectTask = null;
				return;
			}

			// check if a task is being cloned
			String cloneString = execution.getParameter(PARAM_KEY_CLONE);
			if (cloneString != null
					&& cloneString.equalsIgnoreCase(PARAM_CLONE_TRUE)) { // clone
																			// task

				// copy fields
				ProjectTask clone = new ProjectTask();
				// clone.setProject(projectTask.getProject());
				clone.setUsersByUserCreatorId(projectTask
						.getUsersByUserCreatorId());
				clone.setUsersByUserDispatcherId(projectTask
						.getUsersByUserDispatcherId());
				// clone.setProjectTask(projectTask.getProjectTask());
				clone.setTaskType(projectTask.getTaskType());
				clone.setTaskPriority(projectTask.getTaskPriority());
				clone.setName(projectTask.getName());
				clone.setDescription(projectTask.getDescription());
				clone.setComments(projectTask.getComments());
				clone.setDispatcherCloseable(projectTask
						.getDispatcherCloseable());

				// copy task documents (including byte content)
				for (TaskDocument taskDocument : projectTask.getTaskDocuments()) {
					TaskDocument cloneTaskDocument = new TaskDocument();
					cloneTaskDocument.setProjectTask(clone);
					cloneTaskDocument.setDocumentName(taskDocument
							.getDocumentName());
					cloneTaskDocument.setDocumentType(taskDocument
							.getDocumentType());
					cloneTaskDocument.setDocumentKeywords(taskDocument
							.getDocumentKeywords());
					cloneTaskDocument.setDocumentSize(taskDocument
							.getDocumentSize());
					cloneTaskDocument.setDocumentNumber(taskDocument
							.getDocumentNumber());
					// Document content is stored in OpenKM, fetch and copy
					ResponseSendDocument responseSendDocument = fetchDocumentFromOpenKM(taskDocument);
					cloneTaskDocument.setContent(responseSendDocument
							.getContent());
					taskDocuments.add(cloneTaskDocument);
				}
				refreshProjects();

				projectTask = clone;

			} else { // not a cloned task

				projects = new ArrayList<Project>();
				projects.add(projectTask.getProject());
				taskState = projectTask.getTaskState();
				taskResult = projectTask.getTaskResult();
				taskDocuments.addAll(projectTask.getTaskDocuments());
			}

		} else { // new task
			initTask();
			refreshProjects();
			refreshUserHierarchies();

			// set parent task, if any
			String idParentTaskString = execution
					.getParameter(PARAM_KEY_PARENT_TASK);
			Integer idParentTask = null;
			if (idParentTaskString != null) { // parent task set
				try {
					idParentTask = Integer.valueOf(idParentTaskString);
				} catch (Exception e) {
					log.error(e);
					Messagebox.show(Labels.getLabel("fetch.notFound"),
							Labels.getLabel("fetch.title"), Messagebox.OK,
							Messagebox.ERROR);
					projectTask = null;
					return;
				}
				if (idParentTask != null) {
					ProjectTaskDAO projectTaskDAO = new ProjectTaskDAO();
					ProjectTask parentTask = projectTaskDAO.findById(
							idParentTask, false);
					// TODO: check if user has access to the given parent task
					if (parentTask == null) {
						Messagebox.show(Labels.getLabel("fetch.notFound"),
								Labels.getLabel("fetch.title"), Messagebox.OK,
								Messagebox.ERROR);
						projectTask = null;
						return;
					}

					projectTask.setProjectTask(parentTask);
					projectTask.setProject(parentTask.getProject());
					highlightProjectParticipants();
					// copy certain fields
					projectTask
							.setName(parentTaskPrefix + parentTask.getName());
					projectTask.setDescription(parentTask.getDescription());
					projectTask.setTaskType(parentTask.getTaskType());
					projectTask.setTaskPriority(parentTask.getTaskPriority());
					projectTask.setTaskState(parentTask.getTaskState());

					// copy parent task documents (including byte content)
					for (TaskDocument parentTaskDocument : parentTask
							.getTaskDocuments()) {
						TaskDocument taskDocument = new TaskDocument();
						taskDocument.setProjectTask(projectTask);
						taskDocument.setDocumentName(parentTaskDocument
								.getDocumentName());
						taskDocument.setDocumentType(parentTaskDocument
								.getDocumentType());
						taskDocument.setDocumentKeywords(parentTaskDocument
								.getDocumentKeywords());
						taskDocument.setDocumentSize(parentTaskDocument
								.getDocumentSize());
						taskDocument.setDocumentNumber(parentTaskDocument
								.getDocumentNumber());
						// Document content is stored in OpenKM, fetch and
						// copy
						ResponseSendDocument responseSendDocument = fetchDocumentFromOpenKM(parentTaskDocument);
						taskDocument.setContent(responseSendDocument
								.getContent());
						taskDocuments.add(taskDocument);
					}
					return;
				}
			}

			// check if task is being generated based on an existing incoming
			// protocol
			String idIncomingProtocolString = execution
					.getParameter(PARAM_KEY_PROTOCOL_INCOMING);
			Integer idIncomingProtocol = null;
			if (idIncomingProtocolString != null) { // incoming protocol set
				try {
					idIncomingProtocol = Integer
							.valueOf(idIncomingProtocolString);
				} catch (Exception e) {
					log.error(e);
					Messagebox.show(Labels.getLabel("fetch.notFound"),
							Labels.getLabel("fetch.title"), Messagebox.OK,
							Messagebox.ERROR);
					projectTask = null;
					return;
				}

				IncomingProtocolDAO incomingProtocolDAO = new IncomingProtocolDAO();
				IncomingProtocol incomingProtocol = incomingProtocolDAO
						.findById(idIncomingProtocol, false);
				if (incomingProtocol == null) {
					Messagebox.show(Labels.getLabel("fetch.notFound"),
							Labels.getLabel("fetch.title"), Messagebox.OK,
							Messagebox.ERROR);
					projectTask = null;
					return;
				}
				// set task fields
				ParameterDAO parameterDAO = new ParameterDAO();
				String name = parameterDAO
						.getAsString(IConstants.PARAM_TASK_PROTOCOL_INCOMING);
				projectTask.setName(name
						+ incomingProtocol.getFullNumber()
						+ " ("
						+ incomingProtocol.getProtocolBook()
								.getProtocolSeries() + "-"
						+ incomingProtocol.getProtocolBook().getProtocolYear()
						+ ")");
				// copy protocol documents (including byte content)
				copyProtocolDocuments(incomingProtocol.getProtocolDocuments());
				incomingProtocolId = idIncomingProtocol; // store for later use
															// (when saving)
				return;
			}

			// check if task is being generated based on an existing outgoing
			// protocol
			String idOutgoingProtocolString = execution
					.getParameter(PARAM_KEY_PROTOCOL_OUTGOING);
			Integer idOutgoingProtocol = null;
			if (idOutgoingProtocolString != null) { // outgoing protocol set
				try {
					idOutgoingProtocol = Integer
							.valueOf(idOutgoingProtocolString);
				} catch (Exception e) {
					log.error(e);
					Messagebox.show(Labels.getLabel("fetch.notFound"),
							Labels.getLabel("fetch.title"), Messagebox.OK,
							Messagebox.ERROR);
					projectTask = null;
					return;
				}

				OutgoingProtocolDAO outgoingProtocolDAO = new OutgoingProtocolDAO();
				OutgoingProtocol outgoingProtocol = outgoingProtocolDAO
						.findById(idOutgoingProtocol, false);
				if (outgoingProtocol == null) {
					Messagebox.show(Labels.getLabel("fetch.notFound"),
							Labels.getLabel("fetch.title"), Messagebox.OK,
							Messagebox.ERROR);
					projectTask = null;
					return;
				}
				// set task fields
				ParameterDAO parameterDAO = new ParameterDAO();
				String name = parameterDAO
						.getAsString(IConstants.PARAM_TASK_PROTOCOL_OUTGOING);
				projectTask.setName(name
						+ outgoingProtocol.getFullNumber()
						+ " ("
						+ outgoingProtocol.getProtocolBook()
								.getProtocolSeries() + "-"
						+ outgoingProtocol.getProtocolBook().getProtocolYear()
						+ ")");
				// copy protocol documents (including byte content)
				copyProtocolDocuments(outgoingProtocol.getProtocolDocuments());
				outgoingProtocolId = idOutgoingProtocol;// store for later use
														// (when saving)

			}
		}
	}

	public void onSelect$projectCbx(SelectEvent event) {
		refreshUserHierarchies();
		highlightProjectParticipants();
		getBinder(taskWin).loadAll();
	}

	public void onSelect$userHierarchiesLstbx(SelectEvent event) {
		userHierarchyBndbx.close();
		projectTask
				.setUsersByUserDispatcherId(selectedUserHierarchy.getUsers());
		if (department != null
				&& department.getCanAssignAnywhere().equals(
						Department.CAN_ASSIGN_ANYWHERE_TRUE)) {
			autoManagedTask = AUTO_MANAGED_TASK_YES;
		} else {
			autoManagedTask = AUTO_MANAGED_TASK_NO;
		}

		getBinder(taskWin).loadAll();
	}

	public void onChanging$contactBndbx(InputEvent event) {
		term = StringUtils.trimToNull(event.getValue());

		searchContacts(0);
		getBinder(taskWin).loadAll();
	}

	public void onOpen$contactBndbx(OpenEvent event) {
		if (!contacts.isEmpty()) {
			return;
		}
		term = "";
		searchContacts(0);

		getBinder(taskWin).loadAll();
	}

	public void onPaging$contactsPgng(PagingEvent event) {
		int activePage = event.getActivePage();
		int startIndex = activePage * contactsPgng.getPageSize();
		searchContacts(startIndex);
		getBinder(taskWin).loadAll();
	}

	public void onSelect$contactsLstbx(SelectEvent event) {
		// populateContactBndbx();
		contactBndbx.close();
		getBinder(taskWin).loadAll();
	}

	public void onSelect$stateCbx(SelectEvent event)
			throws InterruptedException {
		getBinder(taskWin).loadAll();
	}

	public void onClick$cloneBtn() {
		Executions.getCurrent().sendRedirect(
				PAGE + "?" + IConstants.PARAM_KEY_ID + "="
						+ projectTask.getId() + "&" + PARAM_KEY_CLONE + "="
						+ PARAM_CLONE_TRUE);
	}

	public void onClick$saveBtn() throws InterruptedException {

		validateFields(taskWin);

		if (taskState.getId().intValue() == taskStateClosedId
				&& taskResult == null) {

			Messagebox.show(Labels.getLabel("taskPage.noResultSet"),
					Labels.getLabel("error.title"), Messagebox.OK,
					Messagebox.ERROR);
			return;
		}

		boolean isExistingTask = projectTask.getId() != null ? true : false;

		if (!isExistingTask && autoManagedTask.equals(AUTO_MANAGED_TASK_YES)) {
			// an auto managed task has the same dispatcher and assignee
			projectTask.setUsersByUserCreatorId(projectTask
					.getUsersByUserDispatcherId());
			projectTask.setDispatcherCloseable(true);
			ParameterDAO parameterDAO = new ParameterDAO();
			String prefix = parameterDAO
					.getAsString(IConstants.PARAM_TASK_AUTOMANAGED_PREFIX);
			StringBuffer sb = new StringBuffer();
			sb.append(prefix);
			sb.append(getUserInSession().getName() + " "
					+ getUserInSession().getSurname() + "\n");
			sb.append(projectTask.getComments() != null ? projectTask
					.getComments() : "");
			projectTask.setComments(sb.toString());
		}

		try {

			save();

		} catch (Exception e) {
			log.error(e);
			Messagebox.show(Labels.getLabel("taskPage.errorSaving"),
					Labels.getLabel("error.title"), Messagebox.OK,
					Messagebox.ERROR);
			getBinder(taskWin).loadAll();
			return;
		}

		if (isExistingTask) {
			// send a notification message to the task assignee, letting him/her
			// know that the task may have been modified

			ParameterDAO parameterDAO = new ParameterDAO();
			String subject = parameterDAO
					.getAsString(IConstants.PARAM_TASK_CHANGE_SUBJECT);
			String message = parameterDAO
					.getAsString(IConstants.PARAM_TASK_CHANGE_MESSAGE);

			TaskMessageDAO taskMessageDAO = new TaskMessageDAO();
			TaskMessage taskMessage = new TaskMessage();
			taskMessage.setProjectTask(projectTask);
			taskMessage.setUsersByUserSenderId(getUserInSession());
			taskMessage.setUsersByUserReceiverId(projectTask
					.getUsersByUserDispatcherId());
			taskMessage.setSubject(subject + projectTask.getId());
			taskMessage.setMessage(message);
			taskMessage.setCreatedTs(new Date());
			taskMessageDAO.makePersistent(taskMessage);
		} else {
			/*
			 * mark corresponding related protocol (only for newly created
			 * tasks)
			 */
			if (incomingProtocolId != null) {
				IncomingProtocolDAO incomingProtocolDAO = new IncomingProtocolDAO();
				IncomingProtocol incomingProtocol = incomingProtocolDAO
						.findById(incomingProtocolId, false);
				incomingProtocol.setRelativeTask(projectTask.getId());
				incomingProtocolDAO.makePersistent(incomingProtocol);
			} else if (outgoingProtocolId != null) {
				OutgoingProtocolDAO outgoingProtocolDAO = new OutgoingProtocolDAO();
				OutgoingProtocol outgoingProtocol = outgoingProtocolDAO
						.findById(outgoingProtocolId, false);
				outgoingProtocol.setRelativeTask(projectTask.getId());
				outgoingProtocolDAO.makePersistent(outgoingProtocol);
			}
		}

		/* send a notification email */
		MailDispatcherConfig config = new MailDispatcherConfig();

		if (mailServerType.equalsIgnoreCase(MailDispatcherConfig.IMAP))
			config.setIMAP();
		else if (mailServerType.equalsIgnoreCase(MailDispatcherConfig.POP3))
			config.setPOP3();
		else if (mailServerType.equalsIgnoreCase(MailDispatcherConfig.IMAPS)) {
			config.setIMAPS();
			config.setEnableStarttls(true);
		}
		config.setSmtpHost(smtpHost);
		config.setSmtpPort(smtpPort);
		config.setSmtpUser(smtpUser);
		config.setSmtpPassword(smtpPassword);
		config.setMessageFrom(emailFrom);
		config.setDebug(false);

		ProjectTaskDAO projectTaskDAO = new ProjectTaskDAO();
		projectTaskDAO.attachClean(projectTask);

		ImapProtocolDispatcherImpl disp = new ImapProtocolDispatcherImpl(config);
		SendMailReceipt sendMailReceipt = disp.sendOutgoingTask(projectTask);
		if (sendMailReceipt.isError()) {
			log.error(sendMailReceipt.geteCode() + ":"
					+ sendMailReceipt.geteMessage());
		}

		Messagebox.show(Labels.getLabel("save.OK"),
				Labels.getLabel("save.title"), Messagebox.OK,
				Messagebox.INFORMATION, new EventListener() {
					@Override
					public void onEvent(Event event) throws Exception {
						log.info((Integer) event.getData());
						if (((Integer) event.getData()).intValue() == Messagebox.OK) {
							Executions
									.getCurrent()
									.sendRedirect(
											IndexController.PAGE
													+ "?"
													+ IndexController.PARAM_SELECTED_TAB
													+ "=outgoingTasksTb");
							return;
						} else {
							getBinder(taskWin).loadAll();
						}
					}
				});
	}

	public boolean isProjectCbxVisible() {

		if (isTaskNotCreated() && !hasParentTask()) {
			return true;
		}

		return false;
	}

	public boolean isProjectHbxVisible() {
		return !(isProjectCbxVisible());
	}

	public boolean isResultCbxDisabled() {

		if (isTaskNotCreated()) {
			return true;
		}

		if (isTaskClosed()) {
			return true;
		}

		if (taskState != null
				&& taskState.getId().intValue() != taskStateClosedId) {
			return true;
		}

		return false;
	}

	public boolean isTaskTypeChkBxVisible() {
		if (department != null
				&& department.getCanAssignAnywhere().equals(
						Department.CAN_ASSIGN_ANYWHERE_TRUE)
				&& !isTaskCreated()) {
			return true;
		}
		return false;
	}

	public boolean isTaskTypeChkBxDisabled() {
		if (subordinates != null
				&& !subordinates.contains(selectedUserHierarchy)) {
			// selected assignee is either a manager or belongs to a different
			// hierarchy branch, only one task type allowed
			return true;
		}
		// selected assigne is a subordinate, provide task type option
		return false;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Integer getAutoManagedTask() {
		return autoManagedTask;
	}

	public void setAutoManagedTask(Integer autoManagedTask) {
		this.autoManagedTask = autoManagedTask;
	}

}
