package uk.co.malbec.machinery.consumers;

import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;
import uk.co.malbec.machinery.partitions.DynamicCategoryGroup;

import java.math.BigInteger;
import java.util.function.Consumer;

import static java.util.function.Function.identity;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static uk.co.malbec.machinery.Machinery.count;
import static uk.co.malbec.machinery.Machinery.operation;

public class PartitionByConsumerTest {

    @Test
    public void test() throws Exception {

        DynamicCategoryGroup<String, Scalar<BigInteger>> dynamicCategoryGroup = new DynamicCategoryGroup<>(() -> new Scalar<>(BigInteger.ZERO));

        Consumer<String> stringConsumer = new PartitionByConsumer<>(identity(), String::equals, () -> dynamicCategoryGroup,
                operation(dynamicCategoryGroup::current, BigInteger::add, BigInteger.ONE, dynamicCategoryGroup::current)
        );

        stringConsumer.accept("alpha");
        stringConsumer.accept("alpha");
        stringConsumer.accept("alpha");
        stringConsumer.accept("beta");
        stringConsumer.accept("beta");

        assertThat(dynamicCategoryGroup.get("alpha").getValue().intValue(), is(3));
        assertThat(dynamicCategoryGroup.get("beta").getValue().intValue(), is(2));
    }
}