package application.businessLogic;

import java.util.ArrayList;

import application.entity.Task_list;
import application.entity.User_id;

/*
 * Mockクラス
 */
public class Mock implements BusinessLogic {

	@Override
	public boolean login(User_id user_id) {
		System.out.println(" _________________");
		System.out.println("<Mockが動きました！>");
		System.out.println("-----------------");
		System.out.println("    \\");
		System.out.println("     \\");
		System.out.println("      \\");
		System.out.println("                   ##        .");
		System.out.println("              ## ## ##       ==");
		System.out.println("           ## ## ## ##      ===");
		System.out.println("       /\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"___/ ===");
		System.out.println("  ~~~ {~~ ~~~~ ~~~ ~~~~ ~~ ~ /  ===- ~~~");
		System.out.println("       \\______ o          __/");
		System.out.println("        \\    \\        __/");
		System.out.println("          \\____\\______/");
		return false;
	}

	@Override
	public void taskstorage(Task_list task_list) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public ArrayList<Task_list> getList() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void deleteTask(String[] taskNumList) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String taskNum() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
