package jisaku_jpa.transactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jisaku_jpa.dbConnection.ConnectionPool;
import jisaku_jpa.dbConnection.DBAccess;
import jisaku_jpa.exception.AlreadyTransactionBeganException;
import jisaku_jpa.exception.DoNotHaveDBAccessException;
import jisaku_jpa.exception.NoDBAccessException;
import jisaku_jpa.exception.NotBeginTransactionException;

/**
 * Transactionを管理するクラス</br>
 *
 * {@link jisaku_jpa.transactionManager.TransactionManager}の実装クラス</br>
 * 各メソッドの説明は、{@link jisaku_jpa.transactionManager.TransactionManager}を参照してください</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 */
public class TransactionManagerImpl implements TransactionManager{

	final Logger logger = Logger.getLogger(TransactionManagerImpl.class.getName());//ロガークラス

	boolean transactionStatus = false;//Transactionの状態

	ConnectionPool cp = ConnectionPool.getInstance();//コネクションプール
	Connection conn = null;//コネクション
	DBAccess dba = null;

	final int SLEEPTIME = 300;//タイムアウト値
	boolean repeat = false;

	String transactionID = null;//トランザクションID

	/**
	 * トランザクションの開始メソッド
	 */
	@Override
	public void beginTransaction() throws AlreadyTransactionBeganException {
		//すでにトランザクション開始されている場合は、実行時にエラーを吐く
		if (transactionStatus) {
			throw new AlreadyTransactionBeganException();
		}
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションを開始します");
		//TransactionStatusを開始状態に
		transactionStatus = true;
		//トランザクションIDの発行
		Thread currentThread = Thread.currentThread(); // 自分自身のスレッドを取得
		long threadID = currentThread.getId();
		transactionID = String.valueOf(threadID);

		getConnection(transactionID);
	}

	/**
	 * ロールバックメソッド
	 */
	@Override
	public void rollback() {
		try {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ロールバックを実施します");
			conn.rollback();
			dba.close();
			transactionStatus = false;
		} catch (SQLException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ロールバックに失敗しました");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
	}

	/**
	 * トランザクションの状態管理メソッド
	 * 開始されている場合はTRUE、開始されていない場合はFALSEが戻ります
	 */
	@Override
	public boolean isTransaction() {
		return transactionStatus;
	}

    /**
     * トランザクションの終了メソッド
     */
	@Override
	public void endTransaction() {
		if(this.isTransaction()) {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションを終了します");
			try {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBへ結果をCOMMITします");
				cp.getDBAccess(transactionID).getConnection().commit();
			} catch (NotBeginTransactionException | SQLException e) {
				logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションが開始されていないか、DBに接続できません");
				e.printStackTrace();
				logger.log(Level.INFO, "例外のスローを捕捉", e);
			}
		}
		//TransactionStatusを終了状態に
		transactionStatus = false;
		//コネクションの返却
		this.returnConnection(transactionID);

	}

	/**
	 * コネクションを確保するメソッド
	 * （ここを動かす動かさないでコネクションの確保確保しないは管理できる）
	 */
	@Override
	public void getConnection(String transactionID) {
		if (this.isTransaction()) {
			//Connectionの確保
			try {
				cp.checkoutDBAccess(transactionID);
			} catch (NoDBAccessException e) {
				if (!repeat) {
					//時間をおいてもう一度アクセス
					try {
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションの取得に再チャレンジします。");
						Thread.sleep(SLEEPTIME);
						repeat = true;
					} catch (InterruptedException e1) {
						logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "システムエラー");
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}
				} else {
					logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "処理中のトランザクションの数が設定値を超えています。");
				}
			}
		} else {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションを開始してください");
		}
	}

	/**
	 * コネクションの返却
	 */
	@Override
	public void returnConnection(String transactionID) {
		try {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "コネクションを返却しました。");
			cp.returnDBAccess(transactionID);
		} catch (DoNotHaveDBAccessException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "DBAccessを所持していません。");
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		dba = null;//DBAccessをからにします
	}
}
