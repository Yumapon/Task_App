package application.entity;

import jisaku_jpa.annotation.Entity;
import jisaku_jpa.annotation.Table;
import jisaku_jpa.annotation.TimestampToDate;
import jisaku_jpa.annotation.column;
import jisaku_jpa.annotation.id;
import jisaku_servlet.annotation.SessionScoped;

@SessionScoped("tasklist")
@Entity
@Table("TASK_LIST")
public class Task_list {

	@id
	@column
	private String num;

	@TimestampToDate
	@column
	private java.sql.Timestamp deadline;

	@column
	private String name;

	@column
	private String content;

	@column
	private String client;

	public void setNum(String num) {
		this.num = num;
 	}

	public String getNum() {
		return this.num;
 	}

	public void setDeadline(java.sql.Date deadline) {
		this.deadline = new java.sql.Timestamp(deadline.getTime());;
 	}

	public java.sql.Date getDeadline() {
		if(this.deadline == null) {
			return null;
		}else {
			return new java.sql.Date(this.deadline.getTime());
		}
 	}

	public void setName(String name) {
		this.name = name;
 	}

	public String getName() {
		return this.name;
 	}

	public void setContent(String content) {
		this.content = content;
 	}

	public String getContent() {
		return this.content;
 	}

	public void setClient(String client) {
		this.client = client;
 	}

	public String getClient() {
		return this.client;
 	}

}
