package jisaku_dicontainer.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jisaku_dicontainer.container.definition_entity.BusinessLogicDefinition;
import jisaku_jpa.transactionManager.TransactionHandler;

/**
 * ビジネスロジック生成クラス
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 *
 * @param businessLogicName
 * @return Object (生成したBusinessLogicクラスがObject型で返る)
 */
public class BusinessLogicFactory{

	final Logger logger = Logger.getLogger(BusinessLogicFactory.class.getName());// Loggerクラスのインスタンスを生成する

	//BusinessLogic定義
	HashMap<String, BusinessLogicDefinition> businessLogicDefinitios;

    public BusinessLogicFactory() {//コンストラクタ(こっちだと設定ファイル再読み込みされちゃう)
    	//BusinessLogic定義取得
    	Reader<BusinessLogicDefinition> reader = new ReaderImpl<>();
    	businessLogicDefinitios = reader.read(DefaultSettingValueFile.BUSINESSLOGICCONFIGFILENAME);
    }

    public BusinessLogicFactory(HashMap<String, BusinessLogicDefinition> businessLogicDefinitios) {//一応
    	this.businessLogicDefinitios = businessLogicDefinitios;
    }

	public Object getBusinessLogic(String businessLogicName) {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "getBusinessLogicメソッドを実行します。");

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "getBusinessLogicのクラスパスを取得します。");
		String businessLogicPlace = businessLogicDefinitios.get(businessLogicName).getType();

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "getBusinessLogicのインターフェースを取得します。");
		String businessLogicInterface = businessLogicDefinitios.get(businessLogicName).getInterfaceClass();

		//BusinessLogic格納クラス
		Object bl = null;

		try {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "BusinessLogicFactoryクラスから、BusinessLogicクラスを取得します。");

			//Classの取得
			Class<?> businessLogicClazz = Class.forName(businessLogicPlace);
			Class<?> businessLogicInterfaceClazz = Class.forName(businessLogicInterface);

			//クラスローダーの取得
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "BusinessLogicInterfaceのクラスローダーの取得");
			ClassLoader classLoader = businessLogicClazz.getClassLoader();

			//interfaceの取得
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "BusinessLogicInterfaceの取得");
			Class<?>[] interfaces = new Class[] { businessLogicInterfaceClazz };

			//ハンドラーの取得
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ハンドラーの取得");
			InvocationHandler hundler = new TransactionHandler(businessLogicClazz.getDeclaredConstructor().newInstance());

			//プロキシの取得
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "プロキシクラスの取得");
			bl = Proxy.newProxyInstance(classLoader, interfaces, hundler);

		} catch (ClassNotFoundException | IllegalArgumentException |
				 SecurityException | InstantiationException |
				 IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		return bl;
	}
}

