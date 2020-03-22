package com.github.noraui.browser.waits;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChainableWait<T> {
    private WebDriverWait webDriverWait;
    private T chainedValue;

    public ChainableWait(WebDriverWait webDriverWait) {
        this.webDriverWait = webDriverWait;
    }

    public ChainableWait(WebDriverWait webDriverWait, T chainedValue) {
        this.webDriverWait = webDriverWait;
        this.chainedValue = chainedValue;
    }

    public <A, O> ChainableWait<O> wait(Function<A, ExpectedCondition<O>> condition, Function<T, A> func) {
        return new ChainableWait<>(webDriverWait, webDriverWait.until(condition.apply(func.apply(chainedValue))));
    }

    public <A, B, O> ChainableWait<O> wait(BiFunction<A, B, ExpectedCondition<O>> condition, Function<T, Pair<A, B>> func) {
        Pair<A, B> pair = func.apply(chainedValue);
        return new ChainableWait<>(webDriverWait, webDriverWait.until(condition.apply(pair.getValue0(), pair.getValue1())));
    }

    public <A, B, C, O> ChainableWait<O> wait(TriFunction<A, B, C, ExpectedCondition<O>> condition, Function<T, Triplet<A, B, C>> func) {
        Triplet<A, B, C> triplet = func.apply(chainedValue);
        return new ChainableWait<>(webDriverWait, webDriverWait.until(condition.apply(triplet.getValue0(), triplet.getValue1(), triplet.getValue2())));
    }

    public <O> ChainableWait<O> wait(Function<T, ExpectedCondition<O>> func) {
        return new ChainableWait<>(webDriverWait, webDriverWait.until(func.apply(chainedValue)));
    }

    public <O> ChainableWait<O> wait(Supplier<ExpectedCondition<O>> supplier) {
        return new ChainableWait<>(webDriverWait, webDriverWait.until(supplier.get()));
    }

    public ChainableWait<T> wait(ExpectedCondition<T> condition) {
        return new ChainableWait<>(webDriverWait, webDriverWait.until(condition));
    }

    public <A> ChainableWait<A> map(Function<T, A> func) {
        return new ChainableWait<>(webDriverWait, func.apply(chainedValue));
    }

    public <A> ChainableWait<A> map(A value) {
        return new ChainableWait<>(webDriverWait, value);
    }

    public void then(Consumer<T> consumer) {
        consumer.accept(chainedValue);
    }

    public <O> O then(Function<T, O> func) {
        return func.apply(chainedValue);
    }

    public T get() {
        return chainedValue;
    }

    @SuppressWarnings("unchecked")
    public Collection<ChainableWait<T>> all() {
        if (chainedValue instanceof Collection) {
            return ((Collection<T>) chainedValue).stream().map(v -> new ChainableWait<T>(webDriverWait, v)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
