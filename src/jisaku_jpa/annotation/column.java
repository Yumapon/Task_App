package jisaku_jpa.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * このアノテーションはDBとやりとりするエンティティクラスのカラム変数に付与する。</br>
*
* @author okamotoyuuma
* @version 2.0.0
* @version 2021.04.11
*/
@Retention(RUNTIME)
@Target(FIELD)
public @interface column {

}
