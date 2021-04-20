package application.entity;

import java.math.BigDecimal;

import jisaku_jpa.annotation.Entity;
import jisaku_jpa.annotation.Table;
import jisaku_jpa.annotation.column;
import jisaku_jpa.annotation.id;

@Entity
@Table("USER_ID")
public class User_id {

	@id
	@column
	private BigDecimal id;

	@column
	private String password;

	public void setId(BigDecimal id) {
		this.id = id;
 	}

	public BigDecimal getId() {
		return this.id;
 	}

	public void setPassword(String password) {
		this.password = password;
 	}

	public String getPassword() {
		return this.password;
 	}

}
