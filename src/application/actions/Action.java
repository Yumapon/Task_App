package application.actions;

import java.util.ArrayList;

import application.beans.CreateTaskEntity;
import application.beans.DeleteTaskEntity;
import application.businessLogic.BusinessLogic;
import application.entity.Task_list;
import jisaku_dicontainer.annotation.ActionMethod;
import jisaku_dicontainer.annotation.FormInjection;
import jisaku_dicontainer.annotation.Service;
import jisaku_servlet.annotation.SessionObj;
import jisaku_servlet.servlet.Model;
import jisaku_servlet.servlet.Value;

/**
 * 動作確認用クラス
 * @author okamotoyuuma
 *
 */
//@LoginCheck
public class Action {

	@Service
	BusinessLogic bl1;

	@FormInjection
	CreateTaskEntity createTask;

	@FormInjection
	DeleteTaskEntity deleteTask;

	/*
	@FormInjection
	TestB testb;
	*/

	@ActionMethod("create")
	public Model actionMethod1() {
		System.out.println("アクションクラスのメソッドが実行されました！！！！！");
		Model model = new Model();
		model.setNextPage("createcheck.jsp");

		return model;
	}

	@ActionMethod("createCheck")
	public Model actionMethod2(@SessionObj(value="createTask") CreateTaskEntity createTask) {
		System.out.println("アクションクラスのメソッドが実行されました！！！！！");

		//Entityの生成
		Task_list task = new Task_list();
		//TaskNumの生成
		task.setNum(bl1.taskNum());
		task.setDeadline(createTask.getDeadline());
		task.setName(createTask.getTaskName());
		task.setContent(createTask.getContent());
		task.setClient(createTask.getClient());

		bl1.taskstorage(task);

		//task一覧を取得
		ArrayList<Task_list> taskList = bl1.getList();

		//taskListをセッションにセット
		Model model = new Model();
		Value value = new Value();
		value.setName("tasklist");
		value.setObj(taskList);
		model.getSessionObj().add(value);
		model.setNextPage("list.jsp");

		return model;
	}

	@ActionMethod("delete")
	public Model actionMethod3() {
		System.out.println("アクションクラスのメソッドが実行されました！！！！！");

		//taskの削除
		bl1.deleteTask(deleteTask.getTaskNumList());

		//task一覧を取得
		ArrayList<Task_list> taskList = bl1.getList();

		//taskListをセッションにセット
		Model model = new Model();
		Value value = new Value();
		value.setName("tasklist");
		value.setObj(taskList);
		model.getSessionObj().add(value);
		model.setNextPage("list.jsp");

		return model;
	}

	@ActionMethod("jsonTest")
	public Model actionMethod4() {
		//task一覧を取得
		ArrayList<Task_list> taskList = bl1.getList();

		//taskListをJSONセット
		Model model = new Model();
		model.setJSON();
		model.setJsonObj(taskList);

		return model;

	}

}
