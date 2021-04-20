package jisaku_jpa.transactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import jisaku_jpa.annotation.Transactional;

/**
 * TransactionHandlerクラスは、Transactionを管理するためのクラス。</br>
 * DIコンテナから利用され、ビジネスロジックの前後にトランザクション処理を挟む</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 */
public class TransactionHandler implements InvocationHandler{

	final Logger logger = Logger.getLogger(TransactionHandler.class.getName());//ロガークラス

	private final Object target;//BusinessLogicの実装クラス
	private TransactionManager tm = new TransactionManagerImpl();//トランザクション管理クラス
	String transactionID;//トランザクション管理ID

	/*
	 * コンストラクタ
	 */
	public TransactionHandler(Object target) {this.target = target;}

	/**
	 * DIコンテナに呼び出されるメソッド
	 * ビジネスロジックの実行前後にトランザクション処理を挟む
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Proxyクラスの処理を開始します。");

		//トランザクションの開始
		if(target.getClass().isAnnotationPresent(Transactional.class) || target.getClass().getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(Transactional.class)) {
			tm.beginTransaction();
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションは開始されました。");
		}else {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションは開始されていません。");
		}
		Object ret = null;
		try {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ビジネスロジックのメソッドを実行します。");
			ret = method.invoke(target, args);
			return ret;
		} finally {
			if(tm.isTransaction()) {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "トランザクションを終了し、コネクションも返却します。");
				tm.endTransaction();
			}

			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Proxyクラスの処理を終了します。");
		}
	}
}
