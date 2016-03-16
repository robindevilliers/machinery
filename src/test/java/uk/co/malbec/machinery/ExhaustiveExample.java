package uk.co.malbec.machinery;


import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;
import uk.co.malbec.machinery.collector.Vector;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.co.malbec.machinery.Machinery.*;

public class ExhaustiveExample {

    public static Consumer<Sample> statisticsSet(Referenceable<Statistics> referenceable) {
        return all(
                operation(() -> referenceable.current().getTotalCount(), BigInteger::add, BigInteger.ONE, () -> referenceable.current().getTotalCount()),
                filter(Sample::isOk,
                        operation(() -> referenceable.current().getGood(),BigInteger::add, BigInteger.ONE, () -> referenceable.current().getGood())
                ),
                filter(s -> !s.isOk(),
                        operation(() -> referenceable.current().getBad(),BigInteger::add, BigInteger.ONE, () -> referenceable.current().getBad())
                ),
                map(ExhaustiveExample::time,
                        pass(() -> referenceable.current().getTimeDistribution())
                ),
                map(ExhaustiveExample::time, filter(time -> time.longValue() < referenceable.current().getMin().getValue().longValue(),
                        pass(() -> referenceable.current().getMin()))
                ),
                map(ExhaustiveExample::time, filter(time -> time.longValue() > referenceable.current().getMax().getValue().longValue(),
                        pass(() -> referenceable.current().getMax()))
                ),
                map(ExhaustiveExample::time,
                        operation(input(), BigInteger::add, () -> referenceable.current().getTotalTime(), () -> referenceable.current().getTotalTime())
                )

        );
    }


    public static BigInteger time(Sample sample) {
        return valueOf(sample.getEnd() - sample.getStart());
    }

    public static double interpolateList(int point, List<BigInteger> times) {

        double run = (double) 100 / (times.size() - 1);

        int lowIndex = 0;
        int currentIndex = 0;
        while (currentIndex * run < point) {
            lowIndex = currentIndex;
            currentIndex++;
        }
        int highIndex = currentIndex;
        long low = times.get(lowIndex).longValue();
        long high = times.get(highIndex).longValue();

        return linearInterpolate(lowIndex, low, highIndex, high, point) + low;

    }

    public static double linearInterpolate(double leftX, double leftY, double rightX, double rightY, double point) {
        double rise = rightY - leftY;
        double run = rightX - leftX;
        double coefficient = rise / run;
        double x = (rightX - leftX) * (point / 100);
        double y = x * coefficient;
        return y;
    }

    @Test
    public void generate_statistics() throws Exception {

        List<Sample> data = asList(
                new Sample(true, "user14", "REGISTER", 1445730762017L, 1445730762052L, null, null),
                new Sample(true, "user14", "CHAT", 1445730762084L, 1445730763122L, null, null),
                new Sample(true, "user47", "REGISTER", 1445730762130L, 1445730762160L, null, null),
                new Sample(true, "user19", "REGISTER", 1445730761935L, 1445730764052L, null, null),
                new Sample(false, "user47", "CHAT", 1445730761875L, 1445730762052L, "your auntie", "this is an auntie message"),
                new Sample(true, "user14", "REGISTER", 1445730762085L, 1445730763116L, null, null),
                new Sample(false, "user19", "REGISTER", 1445730761821L, 1445730762052L, "bob's your uncle", "this is a bob message")
        );

        //Given
        Scalar<Long> earliestStartTime = scalar(Long.MAX_VALUE);
        Scalar<Long> latestStartTime = scalar(0L);
        CategoryGroup<BigInteger, Scalar<BigInteger>> indicatorCategories = literalCategorization(() -> new Scalar<>(BigInteger.ZERO), valueOf(800L), valueOf(1200L), valueOf(Long.MAX_VALUE));
        CategoryGroup<String, Statistics> operationNameCategories = dynamicCategorization(Statistics::new);
        Statistics statistics = new Statistics();
        CategoryGroup<String, ErrorSummary> errorMessageCategories = dynamicCategorization(ErrorSummary::new);

        Consumer<Sample> firstPass = all(
                map(Sample::getStart, filter(startTime -> startTime < earliestStartTime.getValue(), earliestStartTime)),
                map(Sample::getStart, filter(startTime -> startTime > latestStartTime.getValue(), latestStartTime)),
                partitionBy(ExhaustiveExample::time, Machinery::lessThan, indicatorCategories,
                        operation(indicatorCategories::current, BigInteger::add, BigInteger.ONE, indicatorCategories::current)
                ),
                partitionBy(Sample::getOperationName, String::equals, operationNameCategories, statisticsSet(operationNameCategories)),
                statisticsSet(() -> statistics),
                filter(s -> !s.isOk(),
                        partitionBy(Sample::getErrorMessage, String::equals, errorMessageCategories,
                                all(
                                        operation(() -> errorMessageCategories.current().getTotalCount(), BigInteger::add, BigInteger.ONE, () -> errorMessageCategories.current().getTotalCount()),
                                        limit(100, pass(() -> errorMessageCategories.current().getErrors()))
                                )
                        )
                )
        );

        //When
        data.forEach(firstPass::accept);

        sort(statistics.getTimeDistribution().getValues());
        double percentile1 = interpolateList(1, statistics.getTimeDistribution().getValues());
        double percentile99 = interpolateList(99, statistics.getTimeDistribution().getValues());

        Consumer<Sample> secondPass = all(
                partitionBy(Sample::getOperationName, String::equals, operationNameCategories,
                        map(ExhaustiveExample::time,
                                all(
                                        operation(() -> operationNameCategories.current().getMean(), BigInteger::subtract, input(),
                                                operation(input(), BigInteger::multiply, input(),
                                                        operation(input(), BigInteger::add, () -> operationNameCategories.current().getSumOfSquares(), () -> operationNameCategories.current().getSumOfSquares())
                                                )
                                        ),
                                        filter(l -> l.longValue() > percentile1 && l.longValue() < percentile99,
                                                all(
                                                        filter(time -> time.longValue() < operationNameCategories.current().getMinimumTime().getValue().longValue(), operationNameCategories.current().getMinimumTime()),
                                                        filter(time -> time.longValue() > operationNameCategories.current().getMaximumTime().getValue().longValue(), operationNameCategories.current().getMaximumTime()),
                                                        pass(() -> operationNameCategories.current().getTimeDistributionExcludingOutliers())
                                                )
                                        )
                                )
                        )
                ),
                map(ExhaustiveExample::time,
                        operation(statistics.getMean(), BigInteger::subtract, input(),
                                operation(input(), BigInteger::multiply, input(),
                                        operation(input(), BigInteger::add, statistics.getSumOfSquares(), statistics.getSumOfSquares())
                                )
                        )
                )
        );

        data.forEach(secondPass::accept);

        //Then
        assertThat(earliestStartTime.getValue(), is(1445730761821L));
        assertThat(latestStartTime.getValue(), is(1445730762130L));
        assertThat(indicatorCategories.get(valueOf(800)).getValue().intValue(), is(4));
        assertThat(indicatorCategories.get(valueOf(1200)).getValue().intValue(), is(2));
        assertThat(indicatorCategories.get(valueOf(Long.MAX_VALUE)).getValue().intValue(), is(1));

        assertThat(operationNameCategories.get("REGISTER").getTotalCount().getValue().intValue(), is(5));
        assertThat(operationNameCategories.get("REGISTER").getGood().getValue().intValue(), is(4));
        assertThat(operationNameCategories.get("REGISTER").getBad().getValue().intValue(), is(1));
        assertThat(operationNameCategories.get("REGISTER").getTimeDistribution().getValues().size(), is(5));
        assertThat(operationNameCategories.get("REGISTER").getMin().getValue().intValue(), is(30));
        assertThat(operationNameCategories.get("REGISTER").getMax().getValue().intValue(), is(2117));
        assertThat(operationNameCategories.get("REGISTER").getTotalTime().getValue().intValue(), is(3444));

        assertThat(operationNameCategories.get("CHAT").getTotalCount().getValue().intValue(), is(2));
        assertThat(operationNameCategories.get("CHAT").getGood().getValue().intValue(), is(1));
        assertThat(operationNameCategories.get("CHAT").getBad().getValue().intValue(), is(1));
        assertThat(operationNameCategories.get("CHAT").getTimeDistribution().getValues().size(), is(2));
        assertThat(operationNameCategories.get("CHAT").getMin().getValue().intValue(), is(177));
        assertThat(operationNameCategories.get("CHAT").getMax().getValue().intValue(), is(1038));
        assertThat(operationNameCategories.get("CHAT").getTotalTime().getValue().intValue(), is(1215));

        assertThat(statistics.getTotalCount().getValue().intValue(), is(7));
        assertThat(statistics.getGood().getValue().intValue(), is(5));
        assertThat(statistics.getBad().getValue().intValue(), is(2));
        assertThat(statistics.getTimeDistribution().getValues().size(), is(7));
        assertThat(statistics.getMin().getValue().intValue(), is(30));
        assertThat(statistics.getMax().getValue().intValue(), is(2117));
        assertThat(statistics.getTotalTime().getValue().intValue(), is(4659));

        assertThat(errorMessageCategories.get("bob's your uncle").getTotalCount().getValue().intValue(), is(1));
        assertThat(errorMessageCategories.get("your auntie").getTotalCount().getValue().intValue(), is(1));

        assertThat(errorMessageCategories.get("bob's your uncle").getErrors().getValues().get(0).getDetailedErrorMessage(), is("this is a bob message"));
        assertThat(errorMessageCategories.get("your auntie").getErrors().getValues().get(0).getDetailedErrorMessage(), is("this is an auntie message"));

        assertThat(statistics.getStandardDeviation(), is(717.9349291544464));
        assertThat(statistics.getStandardError(), is(271.3538971527772));
    }

    public static class Statistics {

        private Scalar<BigInteger> totalCount = scalar(BigInteger.ZERO);
        private Scalar<BigInteger> totalTime = scalar(BigInteger.ZERO);
        private Scalar<BigInteger> good = scalar(BigInteger.ZERO);
        private Scalar<BigInteger> bad = scalar(BigInteger.ZERO);
        private Scalar<BigInteger> min = scalar(valueOf(Long.MAX_VALUE));
        private Scalar<BigInteger> max = scalar(BigInteger.ZERO);
        private Vector<BigInteger> timeDistribution = vector();
        private Scalar<BigInteger> sumOfSquares = scalar(BigInteger.ZERO);
        private Scalar<BigInteger> minimumTime = scalar(BigInteger.ZERO);
        private Scalar<BigInteger> maximumTime = scalar(BigInteger.ZERO);

        private Vector<BigInteger> timeDistributionExcludingOutliers = vector();

        public Scalar<BigInteger> getTotalTime() {
            return totalTime;
        }

        public Scalar<BigInteger> getTotalCount() {
            return totalCount;
        }

        public Scalar<BigInteger> getGood() {
            return good;
        }

        public Scalar<BigInteger> getBad() {
            return bad;
        }

        public Scalar<BigInteger> getMin() {
            return min;
        }

        public Scalar<BigInteger> getMax() {
            return max;
        }

        public Vector<BigInteger> getTimeDistribution() {
            return timeDistribution;
        }

        public Scalar<BigInteger> getSumOfSquares() {
            return sumOfSquares;
        }

        public Scalar<BigInteger> getMinimumTime() {
            return minimumTime;
        }

        public Scalar<BigInteger> getMaximumTime() {
            return maximumTime;
        }

        public Vector<BigInteger> getTimeDistributionExcludingOutliers() {
            return timeDistributionExcludingOutliers;
        }

        public BigInteger getMean() {
            return totalTime.get().divide(totalCount.get());
        }

        public double getStandardDeviation() {
            return Math.sqrt(( sumOfSquares.getValue().floatValue() / totalCount.getValue().floatValue()));
        }

        public double getStandardError() {
            return getStandardDeviation() / Math.sqrt(totalCount.getValue().floatValue());
        }
    }

    public class Sample {

        private boolean ok;

        private String username;

        private String operationName;

        private long start;

        private long end;

        private String errorMessage;

        private String detailedErrorMessage;

        public Sample(boolean ok, String username, String operationName, long start, long end, String errorMessage, String detailedErrorMessage) {
            this.ok = ok;
            this.username = username;
            this.operationName = operationName;
            this.start = start;
            this.end = end;
            this.errorMessage = errorMessage;
            this.detailedErrorMessage = detailedErrorMessage;
        }

        public boolean isOk() {
            return ok;
        }

        public String getUsername() {
            return username;
        }

        public String getOperationName() {
            return operationName;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getDetailedErrorMessage() {
            return detailedErrorMessage;
        }
    }

    public class ErrorSummary {
        private Scalar<BigInteger> totalCount = scalar(BigInteger.ZERO);
        private Vector<Sample> errors = vector();

        public Scalar<BigInteger> getTotalCount() {
            return totalCount;
        }

        public Vector<Sample> getErrors() {
            return errors;
        }
    }
}
