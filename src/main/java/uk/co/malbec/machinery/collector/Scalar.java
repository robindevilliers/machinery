package uk.co.malbec.machinery.collector;


import java.util.function.Consumer;
import java.util.function.Supplier;

public class Scalar<T> implements Consumer<T>, Supplier<T> {

    private T value;

    public Scalar(T t){
        this.value = t;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void accept(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
