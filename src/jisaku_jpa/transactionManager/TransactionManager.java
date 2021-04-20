package jisaku_jpa.transactionManager;

import jisaku_jpa.exception.AlreadyTransactionBeganException;

/**
 * Transactionを管理するためのインターフェース</br>
 * 実装クラスは{@link jisaku_jpa.transactionManager.TransactionManagerImpl}</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 */
public interface TransactionManager {

	/**
	 * トランザクションの開始
	 * @throws AlreadyTransactionBeganException
	 */
	void beginTransaction() throws AlreadyTransactionBeganException;

	/**
	 * ロールバック
	 */
	void rollback();

	/**
	 * トランザクションの終了
	 */
	void endTransaction();

	/**
	 * トランザクションの状態確認
	 * @return
	 */
	boolean isTransaction();

	/**
	 * コネクションの確保
	 * @param transactionID
	 */
	void getConnection(String transactionID);

	/**
	 * コネクションの返却
	 * @param transactionID
	 */
	void returnConnection(String transactionID);

}
