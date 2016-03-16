package uk.co.malbec.machinery;

import org.joda.time.LocalDate;
import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.co.malbec.machinery.Machinery.*;

public class PartitioningExamples {

    static class House {
        private String street;
        private String borough;
        private String county;
        private String type;
        private LocalDate dateOfSale;
        private long value;

        public House(String street, String borough, String county, String type, LocalDate dateOfSale, long value) {
            this.street = street;
            this.borough = borough;
            this.county = county;
            this.type = type;
            this.dateOfSale = dateOfSale;
            this.value = value;
        }

        public String getStreet() {
            return street;
        }

        public String getBorough() {
            return borough;
        }

        public String getCounty() {
            return county;
        }

        public String getType() {
            return type;
        }

        public LocalDate getDateOfSale() {
            return dateOfSale;
        }

        public long getValue() {
            return value;
        }
    }

    List<House> houseSales = asList(
            new House("7 Armstrong House, 58a High Street", "Hillingdon", "Greater London", "Flat", new LocalDate(2016, 1, 15), 260000),
            new House("67 Park Road", "Hillingdon", "Greater London", "Terrace", new LocalDate(2015, 12, 1), 495000),
            new House("6 Norfolk Road", "Hillingdon", "Greater London", "Terrace", new LocalDate(2015, 8, 28), 680000),
            new House("Flat 65 Coral Apartments, 17 Western Gateway", "Newham", "Greater London", "Flat", new LocalDate(2016, 1, 8), 939700),
            new House("Flat 29 The Oxygen, 17 Seagull Lane", "Newham", "Greater London", "Flat", new LocalDate(2015, 10, 2), 154000),
            new House("Flat 20 The Sphere, 1 Hallsville Road", "Newham", "Greater London", "Flat", new LocalDate(2015, 6, 10), 285000)
    );


    @Test
    public void calculate_number_of_houses_by_borough() {

        CategoryGroup<String, Scalar<BigInteger>> boroughs = dynamicCategorization(() -> scalar(ZERO));

        Consumer<House> processor = partitionBy(House::getBorough, String::equals, boroughs,
                operation(boroughs::current, BigInteger::add, ONE, boroughs::current)
        );

        houseSales.forEach(processor::accept);

        assertThat(boroughs.get("Hillingdon").getValue().intValue(), is(3));
        assertThat(boroughs.get("Newham").getValue().intValue(), is(3));
    }

    @Test
    public void calculate_number_of_houses_by_borough_and_type() {
        CategoryGroup<String, CategoryGroup<String, Scalar<BigInteger>>> categoryGroup = dynamicCategorization(() -> dynamicCategorization(() -> scalar(ZERO)));

        Consumer<House> processor = partitionBy(House::getBorough, String::equals, categoryGroup,
                partitionBy(House::getType, String::equals, categoryGroup::current,
                        operation(() -> categoryGroup.current().current(), BigInteger::add, ONE, () -> categoryGroup.current().current())
                )
        );

        houseSales.forEach(processor::accept);

        assertThat(categoryGroup.get("Hillingdon").get("Flat").getValue().intValue(), is(1));
        assertThat(categoryGroup.get("Newham").get("Flat").getValue().intValue(), is(3));
    }

    @Test
    public void calculate_number_of_houses_within_arbitrary_values() {

        CategoryGroup<Long, Scalar<BigInteger>> houseValues = literalCategorization(() -> new Scalar<>(ZERO), 300000L, 600000L, Long.MAX_VALUE);

        Consumer<House> processor = partitionBy(House::getValue, Machinery::lessThan, houseValues,
                operation(houseValues::current, BigInteger::add, ONE, houseValues::current)
        );

        houseSales.forEach(processor::accept);

        assertThat(houseValues.get(300000L).getValue().intValue(), is(3));
        assertThat(houseValues.get(600000L).getValue().intValue(), is(1));
        assertThat(houseValues.get(Long.MAX_VALUE).getValue().intValue(), is(2));
    }
}
