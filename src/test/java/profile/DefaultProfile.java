package profile;

import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация на замену @TestPropertySource.
 * Если указать имя профиля @DefaultProfile(имя_профиля)
 * - application-имя_профиля.properties - Будет использовать его.
 * В противном случае используется профиль по умолчанию - test_local.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ActiveProfiles
public @interface DefaultProfile {

    @AliasFor("profiles")
    String[] value() default "test_local";

    @AliasFor("value")
    String[] profiles() default "test_local";

}
