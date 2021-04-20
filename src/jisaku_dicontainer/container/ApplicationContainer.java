package jisaku_dicontainer.container;

/**
 * DIコンテナのインターフェースクラス
 * 実装クラスは{@link jisaku_dicontainer.container.ApplocationContainerImpl}
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.13
 */
public interface ApplicationContainer {

	void beanDefinitionReload(String configFileName);

    /**
     * Bean生成メソッド
     * @param instanceName
     * @return Bean
     */
	Object generator(String instanceName);

    /**
     * Bean生成メソッド
     * @param instanceName
     */
	Object generator();

	/**
	 * Action生成メソッド
	 * @param actionName
	 * @return
	 */
	Object getAction(String actionName);

}
