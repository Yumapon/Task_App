package jisaku_dicontainer.container.definition_entity;

/*
 * BeanDefinition.yamlファイルの値を格納するクラス </br>
 * {@link jisaku_dicontainer.container.ReaderImpl}クラスに生成される</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.13
 */
public class BeanDefinition {
	String name;//インスタンス名（コンテナからBean取得する際のKey）
	String type;//インスタンスのクラスパス

	public String getName() {return name;}
	public String getType() {return type;}

	public void setType(String type) {this.type = type;}
	public void setName(String name) {this.name = name;}

	@Override
	public String toString() {return this.name;}

}
