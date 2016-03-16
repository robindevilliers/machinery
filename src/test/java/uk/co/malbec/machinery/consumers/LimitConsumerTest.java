package uk.co.malbec.machinery.consumers;

import org.junit.Test;
import uk.co.malbec.machinery.collector.Vector;

import java.util.function.Consumer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LimitConsumerTest {

    @Test
    public void testAccept() throws Exception {

        Object value1 = new Object();
        Object value2 = new Object();
        Object value3 = new Object();

        Vector<Object> results = new Vector<>();

        Consumer<Object> limitConsumer = new LimitConsumer<>(2, () -> results);

        limitConsumer.accept(value1);
        limitConsumer.accept(value2);
        limitConsumer.accept(value3);

        assertThat(results.getValues().size(), is(2));
        assertThat(results.getValues().get(0), is(value1));
        assertThat(results.getValues().get(1), is(value2));
    }
}