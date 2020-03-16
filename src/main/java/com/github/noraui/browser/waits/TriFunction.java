package com.github.noraui.browser.waits;

@FunctionalInterface
public interface TriFunction<A, B, C, O> {
    public O apply(A a, B b, C c);
}