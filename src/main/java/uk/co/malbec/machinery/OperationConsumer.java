package uk.co.malbec.machinery;

import uk.co.malbec.machinery.collector.Scalar;

import java.math.BigInteger;
import java.util.function.*;

public class OperationConsumer<T> implements Consumer<T> {

    private Function<T, BigInteger> lhs;
    private BiFunction<BigInteger, BigInteger, BigInteger> operation;
    private Function<T, BigInteger> rhs;
    private Supplier<Consumer<BigInteger>> result;

    public OperationConsumer(Function<T, BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Function<T, BigInteger> rhs, Supplier<Consumer<BigInteger>> result) {
        this.lhs = lhs;
        this.operation = operation;
        this.rhs = rhs;
        this.result = result;
    }

    @Override
    public void accept(T t) {
        result.get().accept(operation.apply(lhs.apply(t), rhs.apply(t)));
    }
}
