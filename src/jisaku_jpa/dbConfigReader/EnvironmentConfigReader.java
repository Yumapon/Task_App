package jisaku_jpa.dbConfigReader;

import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
 * DBの設定内容（Driver名、URLなど）をyamlファイルから取得するクラス</br>
 * Yamlからのデータ取得には、SnakeYamlを使用しています。（おいおい修正必要）</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
public class EnvironmentConfigReader implements Reader<DBConfig>{

	final Logger logger = Logger.getLogger(EnvironmentConfigReader.class.getName());// Loggerクラスのインスタンスを生成する
	static String configFileName = "DBProfile.yaml";//DBの設定ファイル名

	/**
	 * yamlファイルからDB設定を取得するメソッド(引数なし)
	 * @see Reader#read()
	 */
	@Override
	public DBConfig read() {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + configFileName + "の読み込みを開始します。");

		DBConfig dbc = (DBConfig) new Yaml().load(getClass().getResourceAsStream(configFileName));

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + configFileName + "の読み込みが完了しました。");

		return dbc;
	}

	/**
	 * yamlファイルからDB設定を取得するメソッド(引数あり)
	 * yamlファイルの名前をDBProfile.yaml以外で指定する場合、このメソッドを指定
	 * @see Reader#read(String)
	 * @param DB設定を記載しているファイル名
	 */
	@Override
	public DBConfig read(String setConfigFileName) {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + configFileName + "の読み込みを開始します。");

		//指定されたファイル名をセット
		if (configFileName != null) {
			configFileName = setConfigFileName;
		}

		DBConfig dbc = (DBConfig) new Yaml().load(getClass().getResourceAsStream(configFileName));

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + configFileName + "の読み込みが完了しました。");

		return dbc;
	}
}
