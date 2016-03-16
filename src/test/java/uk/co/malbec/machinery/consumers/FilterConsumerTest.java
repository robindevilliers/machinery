package uk.co.malbec.machinery.consumers;

import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FilterConsumerTest {

    @Test
    public void testFilterAllows() throws Exception {

        Object value = new Object();

        Scalar<Object> result = new Scalar<>(null);

        Consumer<Object> filter = new FilterConsumer<>(i -> i == value, result);

        filter.accept(value);

        assertThat(result.getValue(), is(value));
    }

    @Test
    public void testFilterBlocks() throws Exception {

        Object value = new Object();

        Scalar<Object> result = new Scalar<>(null);

        Consumer<Object> filter = new FilterConsumer<>(i -> i == value, result);

        filter.accept(new Object());

        assertThat(result.getValue(), nullValue());
    }
}