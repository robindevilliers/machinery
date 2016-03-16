package uk.co.malbec.machinery.consumers;

import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;

import java.util.function.Consumer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MapConsumerTest {

    @Test
    public void testAccept() throws Exception {

        Scalar<String> result = new Scalar<>(null);

        Consumer<Long> mapConsumer = new MapConsumer<>(l -> Long.toString(l), () -> result);

        mapConsumer.accept(1L);

        assertThat(result.getValue(), is("1"));
    }
}