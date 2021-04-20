package application.businessLogic;

import java.util.ArrayList;

import application.entity.Task_list;
import application.entity.User_id;

public interface BusinessLogic {

	//ログインロジック
	boolean login(User_id user_id);

	//ログアウトロジック
	//void logout();

	//Task保存ロジック
	void taskstorage(Task_list task_list);

	//一覧取得ロジック
	ArrayList<Task_list> getList();

	//Task削除ロジック
	void deleteTask(String[] taskNumList);

	//Task番号採番
	String taskNum();

	//user登録ロジック
	//void register(User_id user_id) throws DuplicationError;

}

