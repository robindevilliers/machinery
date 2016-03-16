package uk.co.malbec.machinery;

import uk.co.malbec.machinery.collector.Scalar;

import java.math.BigInteger;
import java.util.function.BiConsumer;

public class ScalarBigIntOperations {

    public static <T> BiConsumer<Scalar<BigInteger>, T> incrementByOne(){
        return (c, t) -> c.setValue(c.getValue().add(BigInteger.ONE));
    }
}
