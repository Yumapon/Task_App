package application.businessLogic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import application.entity.Task_list;
import application.entity.User_id;
import jisaku_jpa.annotation.Transactional;
import jisaku_jpa.dbMapper.Repository;
import jisaku_jpa.dbMapper.RepositoryImpl;

public class DefaultBusinessLogic implements BusinessLogic {

	Repository<Task_list, String> Task_listRepos = new RepositoryImpl<>();
	Repository<User_id, Integer> User_idRepos = new RepositoryImpl<>();

	@Override
	//ログイン処理の実装(ログイン失敗、該当のユーザIDが存在しない場合は例外処理発生)
	public boolean login(User_id user_id) {

		//確認用
		System.out.println(" _________________");
		System.out.println("<ログイン機能スタート>");
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

		//ログイン処理
		Optional<User_id> uiOpt = User_idRepos.findById(user_id.getId().intValue());
		if(uiOpt.isPresent()) {
			User_id ui = uiOpt.get();
			if(user_id.getPassword().equals(ui.getPassword())) {
				//ログイン成功
				System.out.println("ログインに成功しました。");
				return true;
			}else {
				//ログイン失敗
				System.out.println("ログインに失敗しました。");
				return false;
			}
		}else {
			//ログイン失敗
			System.out.println("ログインに失敗しました。");
			return false;
		}
	}


	//タスクの格納機能
	@Override
	@Transactional
	public void taskstorage(Task_list task) {

		//確認用
		System.out.println("タスク格納機能スタート");

		/*
		 * この１行で、
		 * 1.DBとのコネクション確立
		 * 2.DBへのデータの保存
		 * を行なっている。
		 */
		Task_listRepos.save(task);

	}

	//task一覧取得
	@Override
	public ArrayList<Task_list> getList() {
		System.out.println("getListメソッドを実行しました");
		/*
		 * この１行で、
		 * 1.DBとのコネクション確立
		 * 2.DBからデータの取得
		 * を行なっている。
		 */
		Optional<ArrayList<Task_list>> list  = Task_listRepos.findAll();
		return list.get();
	}

	//Task削除機能
	@Override
	@Transactional
	public void deleteTask(String[] taskNumList) {

		System.out.println(" _________________");
		System.out.println("<タスクを削除します。>");
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
		Task_list task = new Task_list();
		for(String num : taskNumList) {
			task.setNum(num);

			/*
			 * この１行で、
			 * 1.DBとのコネクション確立
			 * 2.DBからデータの削除
			 * を行なっている。
			 */
			Task_listRepos.delete(task);
		}
		System.out.println("タスクの削除が完了しました。");

	}

	/*
	@Override
	//user登録機能
	public void register(UserContext userContext) throws DuplicationError {

		//DAO呼び出し
		System.out.println("ユーザを登録します。");
		dao.registerDAO(userContext);
		System.out.println("ユーザの登録が完了しました。");

	}

	@Override
	public void logout() {
		//特にビジネスロジック側でやることはないので空実装

	}
	*/


	//タスク番号採番の実装
	@Override
	public String taskNum() {

		//確認用
		System.out.println("タスク番号採番機能スタート");

		//一意な番号は、ユーザID+時間分秒ミリ秒の文字列とする
		Calendar cTime = Calendar.getInstance();
		//各要素の取得
		int hour = cTime.get(Calendar.HOUR_OF_DAY);
		int minute =  cTime.get(Calendar.MINUTE);
		int second =  cTime.get(Calendar.SECOND);
		int millisecond =  cTime.get(Calendar.MILLISECOND);

		String taskNum = "" + hour + minute + second + millisecond;

		//テスト用
		System.out.println("タスク番号は：" + taskNum);

		return taskNum;
	}
}