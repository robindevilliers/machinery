package uk.co.malbec.machinery;

import uk.co.malbec.machinery.collector.Scalar;
import uk.co.malbec.machinery.collector.Vector;
import uk.co.malbec.machinery.consumers.*;
import uk.co.malbec.machinery.partitions.DynamicCategoryGroup;
import uk.co.malbec.machinery.partitions.LiteralCategoryGroup;

import java.math.BigInteger;
import java.util.function.*;

public class Machinery {

    public static <T> Scalar<T> scalar(T t) {
        return new Scalar<>(t);
    }

    public static <T> Vector<T> vector() {
        return new Vector<>();
    }

    @SafeVarargs
    public static <CATEGORY, COLLECTOR> LiteralCategoryGroup<CATEGORY, COLLECTOR> literalCategorization(Supplier<COLLECTOR> collectorSupplier, CATEGORY... breakpoints) {
        return new LiteralCategoryGroup<>(collectorSupplier, breakpoints);
    }

    public static <CATEGORY, COLLECTOR> DynamicCategoryGroup<CATEGORY, COLLECTOR> dynamicCategorization(Supplier<COLLECTOR> collectorSupplier) {
        return new DynamicCategoryGroup<>(collectorSupplier);
    }

    @SafeVarargs
    public static <T> Consumer<T> all(Consumer<T>... consumers) {
        return new AllConsumer<>(consumers);
    }

    public static <T, R, S> Consumer<T> partitionBy(Function<T, R> accessor, BiPredicate<R, R> acceptor, CategoryGroup<R, S> categoryGroup, Consumer<T> consumer) {
        return new PartitionByConsumer<>(accessor, acceptor, () -> categoryGroup, consumer);
    }

    public static <T, R, S> Consumer<T> partitionBy(Function<T, R> accessor, BiPredicate<R, R> acceptor, Supplier<CategoryGroup<R, S>> categoryGroupSupplier, Consumer<T> consumer) {
        return new PartitionByConsumer<>(accessor, acceptor, categoryGroupSupplier, consumer);
    }

    public static <T> Consumer<T> filter(Predicate<T> predicate, Consumer<T> consumer) {
        return new FilterConsumer<T>(predicate, consumer);
    }

    public static <T, R> Consumer<T> map(Function<T, R> mapper, Consumer<R> consumer) {
        return new MapConsumer<T, R>(mapper, () -> consumer);
    }

    public static <T> Consumer<T> limit(int limit, Consumer<T> consumer){
        return new LimitConsumer<>(limit, () -> consumer);
    }

    public static <T> Consumer<T> pass(Supplier<Consumer<T>> consumerReference){
        return new PassConsumer<>(consumerReference);
    }

    public static <T> Consumer<T> operation(Supplier<BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<BigInteger> rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>((t)-> lhs.get(), operation, (t) -> rhs.get(), () -> result);
    }

    public static <T> Consumer<T> operation(Supplier<BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, BigInteger rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>((t)-> lhs.get(), operation, (t) -> rhs, () -> result);
    }

    public static <T> Consumer<T> operation(BigInteger lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<BigInteger> rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>((t) -> lhs, operation, (t) -> rhs.get(), () -> result);
    }

    public static <T> Consumer<T> operation(Function<T, BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<BigInteger> rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>(lhs, operation, (t) -> rhs.get(), () -> result);
    }

    public static <T> Consumer<T> operation(Function<T, BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, BigInteger rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>(lhs, operation, (t) -> rhs, () -> result);
    }

    public static <T> Consumer<T> operation(Supplier<BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Function<T, BigInteger> rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>((t) -> lhs.get(), operation, rhs, () -> result);
    }

    public static <T> Consumer<T> operation(BigInteger lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Function<T, BigInteger> rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>((t) -> lhs, operation, rhs, () -> result);
    }

    public static <T> Consumer<T> operation(Supplier<Supplier<BigInteger>> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, BigInteger rhs, Supplier<Consumer<BigInteger>> result){
        return new OperationConsumer<T>((t)-> lhs.get().get(), operation, (t) -> rhs, result);
    }

    public static <T> Consumer<T> operation(Supplier<Supplier<BigInteger>> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<BigInteger> rhs, Supplier<Consumer<BigInteger>> result){
        return new OperationConsumer<T>((t)-> lhs.get().get(), operation, (t) -> rhs.get(), result);
    }

    public static <T> Consumer<T> operation(Supplier<Supplier<BigInteger>> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Function<T, BigInteger> rhs, Supplier<Consumer<BigInteger>> result){
        return new OperationConsumer<T>((t)-> lhs.get().get(), operation, rhs, result);
    }

    public static <T> Consumer<T> operation(BigInteger lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<Supplier<BigInteger>> rhs, Supplier<Consumer<BigInteger>> result){
        return new OperationConsumer<T>((t)-> lhs, operation, (t) -> rhs.get().get(), result);
    }

    //TODO - same type erasure as another method - We need for for a-b and b-a. (commutativity)
    /*public static <T> Consumer<T> operation(Supplier<BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<Supplier<BigInteger>> rhs, Supplier<Consumer<BigInteger>> result){
        return new OperationConsumer<T>((t)-> lhs.get(), operation, (t) -> rhs.get().get(), result);
    }*/

    public static <T> Consumer<T> operation(Function<T, BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Supplier<Supplier<BigInteger>> rhs, Supplier<Consumer<BigInteger>> result){
        return new OperationConsumer<T>(lhs, operation, (t) -> rhs.get().get(), result);
    }

    public static <T> Consumer<T> operation(Function<T, BigInteger> lhs, BiFunction<BigInteger, BigInteger, BigInteger> operation, Function<T, BigInteger> rhs, Consumer<BigInteger> result){
        return new OperationConsumer<T>(lhs, operation, rhs, () -> result);
    }

    public static <T extends Number> boolean lessThan(T lhs, T rhs) {
        return rhs.doubleValue() - lhs.doubleValue() > 0;
    }

    public static <T extends Number> boolean greaterThan(T lhs, T rhs) {
        return rhs.doubleValue() - lhs.doubleValue() < 0;
    }

    public static <T extends Number> boolean equals(T lhs, T rhs) {
        return rhs.doubleValue() - lhs.doubleValue() == 0;
    }


    static Function<BigInteger, BigInteger> input() {
        return t -> t;
    }

    static BigInteger castToBigInteger(Integer integer){
        return BigInteger.valueOf(integer);
    }
}
