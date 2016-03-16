package uk.co.malbec.machinery.consumers;

import uk.co.malbec.machinery.CategoryGroup;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class PartitionByConsumer<T, R, B> implements Consumer<T> {

    private Function<T,B> accessor;
    private BiPredicate<B, B> acceptor;
    private Supplier<CategoryGroup<B, R>> categoryGroupSupplier;
    private Consumer<T> consumer;

    public PartitionByConsumer(Function<T, B> accessor, BiPredicate<B, B> acceptor, Supplier<CategoryGroup<B, R>> categoryGroupSupplier, Consumer<T> consumer) {
        this.accessor = accessor;
        this.acceptor = acceptor;
        this.categoryGroupSupplier = categoryGroupSupplier;
        this.consumer = consumer;
    }

    @Override
    public void accept(T t) {
        B value = accessor.apply(t);

        for (B key : categoryGroupSupplier.get().getKeys()){
            if (acceptor.test(value, key)){
                if (categoryGroupSupplier.get().apply(key)){
                    consumer.accept(t);
                }
                return;
            }
        }

        if (categoryGroupSupplier.get().apply(value)){
            consumer.accept(t);
        }
    }
}
