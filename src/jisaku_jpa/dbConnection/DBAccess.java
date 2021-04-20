package jisaku_jpa.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jisaku_jpa.dbConfigReader.DBConfig;

/**
 * DBとのコネクションを管理するクラス</br>
 * {@link jisaku_jpa.dbConnection.ConnectionPool}に使用される。</br>
 * {@link java.lang.AutoCloseable}クラスを継承しているため、try-with-resorcesに対応しています</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
public class DBAccess implements AutoCloseable{

	private Connection conn = null;//コネクションを保持する変数
	final Logger logger = Logger.getLogger(DBAccess.class.getName());//ロガークラス

	public DBAccess(final DBConfig dbc) {//コンストラクタ
		try {
			Class.forName(dbc.getDriver());//Driverのロード
			conn = DriverManager.getConnection(dbc.getUrl(), dbc.getUser(), dbc.getPassword());
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBに接続しました。");
			conn.setAutoCommit(false);//自動コミットOFF
		} catch (SQLException | ClassNotFoundException e) {//コネクションの確立に失敗
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			logger.warning("DBとの接続に失敗しました");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
	}

	/**
	 * コネクションを渡す
	 * @return コネクション
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * コネクションの破棄
	 */
	@Override
	public void close() {
		try {
			conn.close();
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBから切断しました");
		} catch (SQLException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBから切断できませんでした");
			e.printStackTrace();
			logger.warning(e.getMessage());
		}
	}

}
