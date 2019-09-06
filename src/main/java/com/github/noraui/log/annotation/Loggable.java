package com.github.noraui.log.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Nicolas HALLOUIN & Stéohane GRILLON
 *         This annotation can be used on a class to inject a static Slf4J logger typed by the class of the declared logger.
 */
@Target({ ElementType.TYPE })
@Retention(RUNTIME)
public @interface Loggable {
}
