package uk.co.malbec.machinery.consumers;

import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PassConsumerTest {

    @Test
    public void test() {

        Scalar<String> result = new Scalar<>(null);

        Consumer<String> consumer = new PassConsumer<>(() -> result);

        consumer.accept("test");

        assertThat(result.getValue(), is("test"));
    }
}