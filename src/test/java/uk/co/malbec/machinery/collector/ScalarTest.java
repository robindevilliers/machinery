package uk.co.malbec.machinery.collector;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ScalarTest {

    @Test
    public void testGetValue() throws Exception {

        Object value = new Object();

        Scalar<Object> scalar = new Scalar<>(value);

        assertThat(scalar.getValue(), is(value));
    }

    @Test
    public void testSetValue() throws Exception {

        Object value = new Object();

        Scalar<Object> scalar = new Scalar<>(null);

        scalar.setValue(value);

        assertThat(scalar.getValue(), is(value));
    }

    @Test
    public void testAccept() throws Exception {

        Object value = new Object();

        Scalar<Object> scalar = new Scalar<>(null);

        scalar.accept(value);

        assertThat(scalar.getValue(), is(value));
    }
}