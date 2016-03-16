package uk.co.malbec.machinery.consumers;


import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class AllConsumer<T> implements Consumer<T> {

    private List<Consumer<T>> consumers;

    @SafeVarargs
    public AllConsumer(Consumer<T>... consumers){
        this.consumers = asList(consumers);
    }

    @Override
    public void accept(T t) {
        for (Consumer<T> consumer : consumers){
            consumer.accept(t);
        }
    }
}
