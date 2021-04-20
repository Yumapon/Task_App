package application.beans;

import jisaku_servlet.annotation.SessionScoped;

@SessionScoped(value="createTask")
public class CreateTaskEntity {

	private java.sql.Date deadline;

	private String taskName;

	private String content;

	private String client;

	public java.sql.Date getDeadline() {
		return deadline;
	}

	public void setDeadline(java.sql.Date deadline) {
		this.deadline = deadline;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

}
