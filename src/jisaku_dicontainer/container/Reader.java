package jisaku_dicontainer.container;

import java.util.HashMap;

/**
 * Jisaku_DIContainer用のyaml読み込みインターフェイス
 * 設定ファイルを読み込むクラスのインタフェース</br>
 * 実装クラスは、</br>
 * {@link jisaku_dicontainer.container.ActionConfigReader}</br>
 * {@link jisaku_dicontainer.container.BeanConfigReader}</br>
 * {@link jisaku_dicontainer.container.BusinessLogicConfigReader}</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 *
 * @param <T>
 */
public interface Reader <T>{

	/**
	 * 設定ファイルを読み込む（ファイル名デフォルトの場合）
	 * @return 設定値を格納したインスタンス
	 */
	HashMap<String, T> read();

	/**
	 * 設定ファイルを読み込む（設定ファイル変更時）
	 * @param fileName 設定ファイルの名前をデフォルトから変更する場合、その名前を指定する。
	 * @return
	 */
	HashMap<String, T> read(String fileName);

}