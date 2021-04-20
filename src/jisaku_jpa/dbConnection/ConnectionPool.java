package jisaku_jpa.dbConnection;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import jisaku_jpa.dbConfigReader.DBConfig;
import jisaku_jpa.dbConfigReader.EnvironmentConfigReader;
import jisaku_jpa.exception.DoNotHaveDBAccessException;
import jisaku_jpa.exception.NoDBAccessException;
import jisaku_jpa.exception.NotBeginTransactionException;

/**
 * {@link jisaku_jpa.dbConnection.DBAccess}を使用して、アプリ起動時にコネクションを保持しておくクラス</br>
 * Singletonパターンで実装しているため、インスタンスを取得する場合は{@link #getInstance()}を使用する。</br>
 *
 * コネクションのやりとりには、TransactionAccess(スレッドID)で行う</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public class ConnectionPool implements Serializable {

	final Logger logger = Logger.getLogger(ConnectionPool.class.getName());// Loggerクラスのインスタンスを生成する

	// シリアルバージョンUID
	private static final long serialVersionUID = 1L;

	//コネクション保持用コレクションの作成
	private Map<String, DBAccess> connectionPool = new HashMap<String, DBAccess>();
	//DBコネクションオブジェクト
	DBAccess dba;

	//DB設定の取得
	EnvironmentConfigReader ecr = new EnvironmentConfigReader();
	DBConfig dbc = ecr.read();
	//DBへのコネクション数取得
	int NUMBER_OF_DB_CONNECTIONS = dbc.getNumberOfAccess();
	//コネクションのIDを格納するキューを作成
	private Queue<String> connectionID = new ArrayDeque<>();

	//コンストラクタ
	private ConnectionPool(){
		//コネクションの確立
		for (int i = 0; i < NUMBER_OF_DB_CONNECTIONS; i++) {
			DBAccess dba = new DBAccess(dbc);
			connectionPool.put("unused" + i, dba);
		}
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBへのコネクションを" + NUMBER_OF_DB_CONNECTIONS + "個確立しました");
	}

	/**
	 * インスタンスを返す
	 * @return ConnectionPoolのインスタンス
	 */
	public static ConnectionPool getInstance() {return ConnectionPoolInstanceHolder.INSTANCE;}

	//ConnectionPoolクラスの唯一のインスタンスを保持する内部クラス
    public static class ConnectionPoolInstanceHolder {
    	private static final ConnectionPool INSTANCE = new ConnectionPool();
    }

	/**
	 * DBAccessをチェックアウトする
	 * トランザクションID（スレッドIDを元に、ストックしているDBAccessをチェックアウトする）
	 *
	 * DBAccessは、キュー内にunused(i)※(i)の部分は連番で格納されている。
	 * チェックアウトされると、キューないのunusedなコネクションを検索し、キーをTransactionIDに変更する。
	 *
	 * @param transactionID
	 * @throws NoDBAccessException
	 */
	public void checkoutDBAccess(String transactionID) throws NoDBAccessException {
		String connectionKey = null;
		dba = null;
		for (int i = 0; i < NUMBER_OF_DB_CONNECTIONS; i++) {
			connectionKey = "unused" + i;

			//使用されていないコネクションを取得し、トランザクションIDで格納し直す
			if (connectionPool.containsKey(connectionKey)) {
				connectionID.add(connectionKey);
				dba = connectionPool.get(connectionKey);
				connectionPool.remove(connectionKey);
				connectionPool.put(transactionID, dba);
				break;
			}
		}
		if (dba != null) {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":"
					    + "transactionID＝" + transactionID + "にコネクションをチェックアウトしました");
		} else {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":"
						+ "コネクションが全て使用中か、Transactionが開始されていません。");
			throw new NoDBAccessException();
		}
	}

	/**
	 * TransactionManager以外が使用する、コネクション取得メソッド
	 * Transactionが開始されていない場合、DBAccessがチェックアウトされていないので
	 * @{link jisaku_jpa.exception.NotBeginTransactionException}例外がスローされる
	 *
	 * キュー内に、チェクアウトされたDBAccessが存在する場合にDBAccessを返却する。
	 *
	 * @param transactionID
	 * @return
	 * @throws NotBeginTransactionException
	 */
	public DBAccess getDBAccess(String transactionID) throws NotBeginTransactionException {
		dba = null;
		if(connectionPool.containsKey(transactionID)) {
			dba = connectionPool.get(transactionID);
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションを貸し出しました");
		}else {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションが開始されていない可能性があります");
			throw new NotBeginTransactionException();
		}
		return dba;
	}

	/**
	 * Transaction処理以外でDBAccess使いたい場合に使用するメソッド
	 *
	 * 使用されていないDBAccessをわたす。
	 * Transaction処理に使用しない場合に使う。（commitしなくて良いSELECT飲みの使用時など）
	 *
	 * @return DBAccess
	 * @throws NoDBAccessException
	 */
	public DBAccess getDBAccess() throws NoDBAccessException {
		String connectionKey = null;
		dba = null;
		for (int i = 0; i < NUMBER_OF_DB_CONNECTIONS; i++) {
			connectionKey = "unused" + i;

			//使用されていないコネクションを取得する
			if (connectionPool.containsKey(connectionKey)) {
				dba = connectionPool.get(connectionKey);
				break;
			}
		}
		if (dba != null) {//unusedなDBAccessが存在する場合
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBAccessを取得しました");
		} else {//全てのDBAccessがチェックアウトされている
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションが全て使用中です。");
			throw new NoDBAccessException();
		}
		return dba;
	}
	/**
	 * DBAccessを返してもらうメソッド
	 *
	 * @param transactionID
	 * @throws DoNotHaveDBAccessException
	 */
	public void returnDBAccess(String transactionID) throws DoNotHaveDBAccessException {

		dba = connectionPool.put(transactionID, dba);
		if(dba == null) {
			throw new DoNotHaveDBAccessException();
		}else {
			connectionPool.remove(transactionID);
			connectionPool.put(connectionID.poll(), dba);
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションが返却されました。");
		}
	}
}
