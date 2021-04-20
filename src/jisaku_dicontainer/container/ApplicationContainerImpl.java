/**
 *ApplicationContainerクラス
 */
package jisaku_dicontainer.container;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jisaku_dicontainer.annotation.FormInjection;
import jisaku_dicontainer.annotation.Service;
import jisaku_dicontainer.container.definition_entity.ActionDefinition;
import jisaku_dicontainer.container.definition_entity.BeanDefinition;
import jisaku_dicontainer.container.definition_entity.BusinessLogicDefinition;

/**
 * DIコンテナの実装クラス
 * インタフェースは{@link jisaku_dicontainer.container.ApplocationContainer}
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.13
 */
public class ApplicationContainerImpl implements ApplicationContainer{

	final Logger logger = Logger.getLogger(ApplicationContainerImpl.class.getName());// Loggerクラスのインスタンスを生成する

	//生成するインスタンスの場所
	private String instancePlace;

	//Reader
	Reader<BeanDefinition> beanReader = new ReaderImpl<>();//Bean定義取得クラス
	Reader<BusinessLogicDefinition> businessLogicReader = new ReaderImpl<>();//BusinessLogic定義取得ファイル
	Reader<ActionDefinition> actionReader = new ReaderImpl<>();//Action定義取得クラス

	//Config
	static HashMap<String, BeanDefinition> beanDefinitions;//Bean定義
	static HashMap<String, ActionDefinition> actionDefinitions;//Action定義
	static HashMap<String, BusinessLogicDefinition> businessLogicDefinitios;//BusinessLogic定義

	//設定ファイルの読み込み（初回のみ）
	private void readConfig(String beanConfigFileName, String actionConfigFileName, String businessLogicConfigName) {
		if(beanDefinitions == null && actionDefinitions == null && businessLogicDefinitios == null) {
			//設定ファイルの読み込み
			beanDefinitions = beanReader.read(beanConfigFileName);
			actionDefinitions = actionReader.read(actionConfigFileName);
			businessLogicDefinitios = businessLogicReader.read(businessLogicConfigName);
		}
	}

	/**
	 * デフォルトコンストラクタ
	 * 引数なし（設定ファイルの名前がデフォルトで良い場合）
	 */
	public ApplicationContainerImpl(){
		readConfig(DefaultSettingValueFile.BEANCONFIGFILENAME, DefaultSettingValueFile.ACTIONCONFIGFILENAME, DefaultSettingValueFile.BUSINESSLOGICCONFIGFILENAME);
	}

	//引数あり（設定ファイルの名前を独自に設定された場合）
	/**
	 * 引数ありコンストラクタ
	 * @param instancePlace
	 */
	public ApplicationContainerImpl(String beanConfigFileName, String actionConfigFileName, String businessLogicConfigName){
		readConfig(beanConfigFileName, actionConfigFileName, businessLogicConfigName);
	}

	/**
	 * 設定ファイル名を変更し、Bean定義ファイルを再度取得する
	 * @param configFileName
	 */
	@Override
	public void beanDefinitionReload(String configFileName) {
		beanDefinitions = beanReader.read(configFileName);
	}

	/**
	 * Beanの生成(インスタンス名から生成)
	 * @param instanceName
	 * @return Bean
	 */
	@Override
	public Object generator(String instanceName) {

		//生成するインスタンス
		Object obj = null;
		//インスタンス名からクラスパスを取得
		instancePlace = beanDefinitions.get(instanceName).getType();

		try {
			//インスタンスの作成
			Class<?> instanceClass = Class.forName(instancePlace);
			obj = instanceClass.getDeclaredConstructor().newInstance();
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "指定されたクラスパスのBean生成が完了しました。");
		} catch (ClassNotFoundException e) {
			logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "<error>指定されたクラスパスが見つかりませんでした。（" + instancePlace + "が見つかりません。）");
			//スタックトレース
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}catch (Exception e) {
			//スタックトレース
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		return obj;
	}


	//インスタンス生成メソッド(引数なし)
	/**
	 * Beanの生成(クラスパスを指定して生成する場合)
	 * @return Bean
	 */
		@Override
		public Object generator() {

			//生成するインスタンス
			Object obj = null;

			try {
				//インスタンスの作成
				Class<?> instanceClass = Class.forName(instancePlace);
				obj = instanceClass.getDeclaredConstructor().newInstance();
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "指定されたクラスパスのBean生成が完了しました。");
			} catch (ClassNotFoundException e) {
				//例外内容
				if(instancePlace == null) {
					logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "クラスパスが指定されていません。");
					System.out.println("<error>クラスパスが指定されていません。");
				}else {
					logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "指定されたクラスパスが見つかりませんでした。（" + instancePlace + "が見つかりません。）");
				}
			}catch (Exception e) {
				//スタックトレース
				e.printStackTrace();
				logger.log(Level.INFO, "例外のスローを捕捉", e);
			}
			return obj;
		}


	/**
	 * Actionクラス生成メソッド
	 * @param actionName
	 * @return Object
	 */
	@Override
	public Object getAction(String actionName) {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "getActionメソッドを実行します。");

		Object actionObj = null;

		//actionNameからクラスの場所を取得
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "actionNameからactionPlaceを取得します。");
		String actionPlace = actionDefinitions.get(actionName).getType();
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "actionPlaceの取得が完了しました。actionPlace = " + actionPlace);

		try {
			//Actionクラスの生成
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "クラスの取得を行います。");
			Class<?> actionClazz = Class.forName(actionPlace);

			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "インスタンスの生成を行います");
			actionObj = actionClazz.getDeclaredConstructor().newInstance();

			//Fieldの取得と、ビジネスロジックのインジェクション
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "フィールドの取得を行います");
			Field[] fields = actionClazz.getDeclaredFields();
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "フィールドの取得が完了しました。");

			for(Field f : fields) {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "フィールドのアノテーションを確認中");
				//@Serviceがついている場合、ビジネスロジックを取得し、インジェクト
				if(f.isAnnotationPresent(Service.class)) {
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + f.getName() + "に@Serviceが付与されているのを確認しました。");

					//ビジネスロジックを取得
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + f.getName() + "ビジネスロジックを取得します。");
					Object blObject = new BusinessLogicFactory(businessLogicDefinitios).getBusinessLogic(f.getName());
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + f.getName() + "ビジネスロジックの取得が完了しました。");

					//ビジネスロジックをセット
					f.setAccessible(true);//無理やり書き込む。
					f.set(actionObj, blObject);
					f.setAccessible(false);

	            }else if(f.isAnnotationPresent(FormInjection.class)){
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + f.getName() + "に@FormInjectionが付与されているのを確認しました。");

					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + f.getName() + "のFormインスタンスを生成します。");
					Object bean = generator(f.getName());

					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Formをインジェクトします。");
					f.setAccessible(true);//無理やり書き込む。
					f.set(actionObj, bean);
					f.setAccessible(false);

	            }else {
					logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + f.getName() + "@Injectionも@Serviceも付与されていない。");
	            }
	        }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		return actionObj;
	}
	//getter・setter
	/**
	 * Beanクラスパスの取得
	 * @return Beanのクラスパス
	 */
	public String getInstancePlace() {
		return instancePlace;
	}

	/**
	 * Beanクラスパスの設定
	 * @param instancePlace
	 */
	public void setInstancePlace(String instancePlace) {
		this.instancePlace = instancePlace;
	}


	/**
	 * ビジネスロジック生成メソッド
	 * →外部ファイルに切り出し済み
	 * @param businessLogicName
	 * @return Object
	 */
/*	private class BusinessLogicFactory{
		Object getBusinessLogic(String businessLogicName) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("getBusinessLogicメソッドを実行します。");

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("getBusinessLogicのクラスパスを取得します。");
			String businessLogicPlace = businessLogicDefinitios.get(businessLogicName).getType();

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("getBusinessLogicのインターフェースを取得します。");
			String businessLogicInterface = businessLogicDefinitios.get(businessLogicName).getInterfaceClass();

			//BusinessLogic格納クラス
			Object bl = null;

			try {

				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("BusinessLogicFactoryクラスから、BusinessLogicクラスを取得します。");

				Class<?> businessLogicClazz = Class.forName(businessLogicPlace);
				Class<?> businessLogicInterfaceClazz = Class.forName(businessLogicInterface);

				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("BusinessLogicInterfaceのクラスローダーの取得");
				ClassLoader classLoader = businessLogicClazz.getClassLoader();

				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("BusinessLogicInterfaceの取得");
				Class<?>[] interfaces = new Class[] { businessLogicInterfaceClazz };

				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("ハンドラーの取得");
				InvocationHandler hundler = new TransactionHandler(businessLogicClazz.getDeclaredConstructor().newInstance());

				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("プロキシクラスの取得");
				bl = Proxy.newProxyInstance(classLoader, interfaces, hundler);

			} catch (ClassNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IllegalArgumentException | SecurityException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			return bl;
		}
	}
	*/
/*
	//test用
	public Object getbl(String bl1) {
		BusinessLogicFactory blf = new BusinessLogicFactory();
		Object obj = blf.getBusinessLogic(bl1);
		return obj;
	}
	*/

}

//バインディング用オブジェクト取得メソッド
/*
public InstanceAndClassObjectforServlet getCAMS(String instanceName) {
	//ログ発生箇所
	System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
	//処理内容
	System.out.println("getCAMSメソッドを実行します。");

	InstanceAndClassObjectforServlet cams = new InstanceAndClassObjectforServlet();
	//インスタンス名からクラスパスを取得
	instancePlace = beanDefinitions.get(instanceName);

	try {
		//インスタンスの作成
		Class<?> instanceClass = Class.forName(instancePlace);
		bean = instanceClass.getDeclaredConstructor().newInstance();
		//camsにインスタンスを格納
		cams.setObj(bean);
		//camsにメソッドを格納
		cams.setClazz(instanceClass);
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("指定されたクラスパスのBean生成が完了しました。");
	} catch (ClassNotFoundException e) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//例外内容
		if(instancePlace == null) {
			System.out.println("<error>クラスパスが指定されていません。");
		}else {
			System.out.println("<error>指定されたクラスパスが見つかりませんでした。（" + instancePlace + "が見つかりません。）");
		}
	}catch (Exception e) {
		//スタックトレース
		e.printStackTrace();
	}

	return cams;
}
*/

