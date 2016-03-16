package uk.co.malbec.machinery.collector;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class VectorTest {

    @Test
    public void test() throws Exception {
        Object value1 = new Object();
        Object value2 = new Object();

        Vector<Object> vector = new Vector<>();

        vector.accept(value1);
        vector.accept(value2);

        assertThat(vector.getValues().size(), is(2));
        assertThat(vector.getValues().get(0), is(value1));
        assertThat(vector.getValues().get(1), is(value2));
    }
}