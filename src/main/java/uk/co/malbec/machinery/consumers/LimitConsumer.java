package uk.co.malbec.machinery.consumers;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LimitConsumer<T> implements Consumer<T> {

    private int limit;

    private int count = 0;

    private Supplier<Consumer<T>> consumerSupplier;

    public LimitConsumer(int limit, Supplier<Consumer<T>> consumerSupplier) {
        this.limit = limit;
        this.consumerSupplier = consumerSupplier;
    }

    @Override
    public void accept(T t) {
        if (count < limit){
            count++;
            consumerSupplier.get().accept(t);
        }
    }
}
