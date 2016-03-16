package uk.co.malbec.machinery.consumers;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PassConsumer<T> implements Consumer<T> {

    private Supplier<Consumer<T>> consumerSupplier;

    public PassConsumer(Supplier<Consumer<T>> consumerSupplier) {
        this.consumerSupplier = consumerSupplier;
    }

    @Override
    public void accept(T t) {
        consumerSupplier.get().accept(t);
    }
}
