package application.actions;

import java.math.BigDecimal;
import java.util.ArrayList;

import application.beans.UserInfoEntity;
import application.businessLogic.BusinessLogic;
import application.entity.Task_list;
import application.entity.User_id;
import jisaku_dicontainer.annotation.ActionMethod;
import jisaku_dicontainer.annotation.FormInjection;
import jisaku_dicontainer.annotation.Service;
import jisaku_servlet.annotation.Login;
import jisaku_servlet.servlet.Model;
import jisaku_servlet.servlet.Value;

public class LoginAction {

	@Service
	BusinessLogic bl1;

	@FormInjection
	UserInfoEntity userInfo;

	@ActionMethod("login")
	@Login
	public Model actionMethod3() {
		//Login処理
		User_id user_id = new User_id();
		BigDecimal bigUserId = BigDecimal.valueOf(userInfo.getUser_id());
		user_id.setId(bigUserId);
		user_id.setPassword(userInfo.getPassword());
		if(!bl1.login(user_id)) {
			/*
			 * ログイン失敗
			 * ログイン画面を再度表示
			 */
			Model model  = new Model();
			model.setNextPage("login.jsp");
			model.setLoginCheckerFlag(false);
			return model;
		}

		//task一覧を取得
		ArrayList<Task_list> taskList = bl1.getList();

		//taskListをセッションにセット
		Model model  = new Model();
		Value value = new Value();
		value.setName("tasklist");
		value.setObj(taskList);
		model.getSessionObj().add(value);
		model.setLoginCheckerFlag(true);

		//次画面をセット
		model.setNextPage("list.jsp");

		return model;
	}

}
