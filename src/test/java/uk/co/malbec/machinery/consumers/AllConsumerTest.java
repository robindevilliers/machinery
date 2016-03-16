package uk.co.malbec.machinery.consumers;

import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;

import java.util.function.Consumer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AllConsumerTest {

    @Test
    public void testAccept() throws Exception {

        Object value = new Object();

        Scalar<Object> one = new Scalar<>(null);
        Scalar<Object> two = new Scalar<>(null);

        Consumer<Object> allConsumer = new AllConsumer<>(one, two);

        allConsumer.accept(value);

        assertThat(one.getValue(), is(value));
        assertThat(two.getValue(), is(value));
    }
}