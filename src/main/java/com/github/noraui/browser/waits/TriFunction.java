/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.browser.waits;

@FunctionalInterface
public interface TriFunction<A, B, C, O> {
    public O apply(A a, B b, C c);
}