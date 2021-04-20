package jisaku_jpa.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import jisaku_jpa.dbConnection.ConnectionPool;
import jisaku_jpa.exception.NoColumnValueException;
import jisaku_jpa.exception.NoDBAccessException;
import jisaku_jpa.exception.NotBeginTransactionException;

/**
 * {@link jisaku_jpa.query.Query}インターフェースの実装クラス</br>
 *
 * Oracle用のSQLを生成、実行する</br>
 *
 *  各メソッドの説明は、{@link jisaku_jpa.query.Query}を参照してください</br>
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public class OracledbQuery implements Query {

	final Logger logger = Logger.getLogger(OracledbQuery.class.getName());// Loggerクラスのインスタンスを生成する

	ConnectionPool cp = ConnectionPool.getInstance();//ConnectionPoolを取得する
	String transactionID;//TransactionID
	Connection conn;//Connection
	Statement stmt;//Statement
	PreparedStatement pre_statement;//PreparedStatement

	public OracledbQuery(){
		//TransactionIDを取得する
		Thread currentThread = Thread.currentThread(); //スレッドを取得
		long threadID = currentThread.getId();
		this.transactionID = String.valueOf(threadID);
	}

	/**
	 * INSERT文生成メソッド
	 * @param qi
	 * @return INSERT文を返す
	 */
	@Override
	public String createInsertSql(QueryInfo qi) {

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityの情報を取得し、INSERT文の生成を開始します");

		//SQL生成
		StringBuilder column = new StringBuilder();
		StringBuilder columnValue = new StringBuilder();
		for (String str : qi.getColumnNames()) {
			column.append(str)
			      .append(" ,");
			columnValue.append("'")
			           .append(qi.getColumnValues().get(str))
			           .append("',");
		}
		// 末尾から1文字分を削除
		column.deleteCharAt(column.length() - 1);
		columnValue.deleteCharAt(columnValue.length() - 1);
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ")
		   .append(qi.getTableName())
		   .append(" (")
		   .append(column)
		   .append(") values (")
		   .append(columnValue)
		   .append(")");

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "INSERT文の生成が完了しました:");

		return sql.toString();//生成したINSERT文
	}

	/**
	 * UPDATE文生成メソッド
	 * 主キー検索での変更のみ対応。（要望あれば条件検索でも実装必要。。）
	 * @param qi
	 * @return 生成したUPDATE文
	 */
	@Override
	public String createUpdateSql(QueryInfo qi) {

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityの情報を取得し、UPDATE文の生成を開始します");

		//SQL生成
		StringBuilder setValue = new StringBuilder();
		for (String column : qi.getColumnNames()) {
			if (column.equals(qi.getIdName()))
				continue;
			setValue.append(column)
			        .append(" = '")
			        .append(qi.getColumnValues().get(column))
			        .append("',");
		}
		// 末尾から1文字分を削除
		setValue.deleteCharAt(setValue.length() - 1);

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ")
		   .append(qi.getTableName())
		   .append(" SET ")
		   .append(setValue)
		   .append(" WHERE ")
		   .append(qi.getIdName())
		   .append(" = '")
		   .append(qi.getIdValue())
		   .append("'");

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "UPDATE文の生成が完了しました:");

		return sql.toString();//生成したUPDATE文
	}

	/**
	 * SELECT文生成メソッド
	 * @param qi
	 * @return  生成したSELECT文
	 */
	@Override
	public String createSelectSql(QueryInfo qi) {

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityの情報を取得し、SELECT文の生成を開始します");

		//SQL生成
		String idValue = qi.getIdValue();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ")
		   .append(qi.getTableName());
		/*
		//QueryInfoに何もセットされていない場合、全件検索
		 * OracleDBにはLIMI句がないため、一旦削除
		if (idValue == null && qi.getColumnValues().size() == 0)
			sql.append(" LIMIT 1000");
		*/
		//QueryInfoにidValue以外のカラム値が設定されている場合、条件検索
		//else {
		if (!(idValue == null && qi.getColumnValues().size() == 0)){
			sql.append(" WHERE ");
			if (idValue != null) {
				sql.append(qi.getIdName())
				   .append(" = '")
				   .append(idValue)
				   .append("'");
			} else {
				for (String columnName : qi.getColumnNames()) {
					if (qi.getColumnValues().get(columnName) == null || columnName.equals(qi.getIdName())) {
						continue;
					}
					sql.append(columnName)
					   .append(" = '")
					   .append(qi.getColumnValues().get(columnName))
					   .append("' AND ");
				}
				sql.delete(sql.length() - 5, sql.length());
			}
		}

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SELECT文の生成が完了しました:");

		return sql.toString();//生成したSELECT文
	}

	/**
	 * DELETE文生成メソッド
	 * 複数条件指定時、ANDで削除を行う。
	 * ORで削除行いたい場合、２回DELETEを実行する必要あり。（修正必要？）
	 * @param qi
	 * @return 生成したDELETE文
	 * @throws NoColumnValueException
	 * @throws noColumnValueException
	 */
	public String createDeleteSql(QueryInfo qi) {

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityの情報を取得し、DELETE文の生成を開始します");

		//SQL生成
		String idValue = qi.getIdValue();
		StringBuilder sql = new StringBuilder();
		boolean checkColumnValue = false;
		sql.append("DELETE FROM ")
		   .append(qi.getTableName())
		   .append(" WHERE ");
		if (idValue != null)//主キーで削除するパターン
			sql.append(qi.getIdName())
			   .append(" = '")
			   .append(idValue)
			   .append("'");
		else {//条件指定で削除するパターン
			for (String columnName : qi.getColumnNames()) {
				if (qi.getColumnValues().get(columnName) == null || columnName.equals(qi.getIdName())) {
					continue;
				}
				sql.append(columnName)
				   .append(" = '")
				   .append(qi.getColumnValues().get(columnName))
				   .append("' AND ");
				checkColumnValue = true;
			}
			sql.delete(sql.length() - 5, sql.length());
			if (!checkColumnValue) {
				logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "カラムに何も値が入っていません");
				throw new NoColumnValueException();
			}
		}

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DELETE文の生成が完了しました:");

		return sql.toString();//生成したDELETE文
	}

	/**
	 * RECORD有無の確認用メソッド
	 * @param qi
	 * @return
	 */
	@Override
	public String createCheckRecordSql(QueryInfo qi) {

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityの情報を取得し、COUNTCHECK用SQLの生成を開始します");

		//SQL生成
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ")
		   .append(qi.getTableName())
		   .append(" WHERE ")
		   .append(qi.getIdName())
		   .append(" = '")
		   .append(qi.getIdValue())
		   .append("'");

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "COUNTCHECK用SQLの生成が完了しました");

		return sql.toString();
	}

	/**
	 * レコード数をチェックするメソッド
	 * @return SELECT COUNT(*) FROM {テーブル名};
	 */
	@Override
	public String createCheckCountSql(QueryInfo qi) {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityの情報を取得し、COUNTCHECK用SQLの生成を開始します");
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "COUNTCHECK用SQLの生成が完了しました");
		return "SELECT COUNT(*) FROM " + qi.getTableName();
	}

	/**
	 * 更新メソッド
	 * @param sql
	 * @return 更新レコード数
	 */
	@Override
	public int executeUpdate(String sql) {
		//更新レコード数
		int i = 0;

		//Connectionの取得
		try {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションを取得し、ステートメントを生成します");
			stmt = cp.getDBAccess(transactionID).getConnection().createStatement();
			i = stmt.executeUpdate(sql);
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行しました");
		} catch (NotBeginTransactionException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションが開始されていません");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		} catch (SQLException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLエラーが発生しました");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		return i;
	}

	/**
	 * 参照メソッド
	 * @param sql
	 * @return
	 */
	@Override
	public ResultSet executeQuery(String sql) {
		//Connectionの取得
		ResultSet rs = null;
		try {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションを取得し、ステートメントを生成します");
			pre_statement = cp.getDBAccess().getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pre_statement.executeQuery(sql);
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行しました");
		} catch (SQLException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLエラーが発生しました");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		} catch (NoDBAccessException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Connectionが見つかりません");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		return rs;
	}
}
