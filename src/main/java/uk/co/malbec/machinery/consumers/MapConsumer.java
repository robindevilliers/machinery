package uk.co.malbec.machinery.consumers;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapConsumer<T, R> implements Consumer<T> {

    private Function<T, R> mapper;
    private Supplier<Consumer<R>> consumerSupplier;

    public MapConsumer(Function<T, R> mapper, Supplier<Consumer<R>> consumerSupplier) {
        this.mapper = mapper;
        this.consumerSupplier = consumerSupplier;
    }

    @Override
    public void accept(T t) {
        consumerSupplier.get().accept(mapper.apply(t));
    }
}
