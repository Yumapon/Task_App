package jisaku_dicontainer.container.definition_entity;

/*
 * BusinessLogicDefinition.yamlファイルの値を格納するクラス </br>
 * {@link jisaku_dicontainer.container.ReaderImpl}クラスに生成される</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.13
 */
public class BusinessLogicDefinition {
	String interfaceClass;//インターフェース
	String name;//インスタンス名
	String type;//インスタンスのクラスパス

	public String getInterfaceClass() {return interfaceClass;}
	public String getName() {return name;}
	public String getType() {return type;}

	public void setInterfaceClass(String interfaceClass) {this.interfaceClass = interfaceClass;}
	public void setName(String name) {this.name = name;}
	public void setType(String type) {this.type = type;}

	@Override
	public String toString() {return this.name;}

}
