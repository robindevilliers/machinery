package uk.co.malbec.machinery;


import org.junit.Test;
import uk.co.malbec.machinery.collector.Scalar;
import uk.co.malbec.machinery.collector.Vector;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.co.malbec.machinery.Machinery.*;

public class BasicExamples {

    static class SuperHero {
        private String name;
        private int age;
        private int badGuysBeatenUp;

        public SuperHero(String name, int age, int badGuysBeatenUp) {
            this.name = name;
            this.age = age;
            this.badGuysBeatenUp = badGuysBeatenUp;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Integer getBadGuysBeatenUp() {
            return badGuysBeatenUp;
        }
    }

    List<SuperHero> superHeroes = asList(
            new SuperHero("batman", 34, 3),
            new SuperHero("superman", 32, 7),
            new SuperHero("flash", 29, 1),
            new SuperHero("wonder woman", 122, 10)
    );

    @Test
    public void calculate_number_of_hardworking_super_heroes() {

        Scalar<BigInteger> count = scalar(ZERO);

        Consumer<SuperHero> processor = filter(e -> e.getBadGuysBeatenUp() > 5,
                operation(count, BigInteger::add, ONE, count)
        );

        superHeroes.forEach(processor::accept);

        assertThat(count.getValue().intValue(), is(2));
    }

    @Test
    public void find_youngest_super_hero_alternative_method() {
        Scalar<SuperHero> youngest = new Scalar<>(null);

        Consumer<SuperHero> processor = filter(isYoungerThan(youngest), youngest);

        superHeroes.forEach(processor::accept);

        assertThat(youngest.getValue().name, is("flash"));
    }

    public static Predicate<SuperHero> isYoungerThan(Scalar<SuperHero> heroScalar) {
        return subject -> heroScalar.getValue() == null || subject.getAge() < heroScalar.getValue().getAge();
    }

    @Test
    public void find_youngest_hardworking_superhero() {

        Scalar<SuperHero> youngest = new Scalar<>(null);

        Consumer<SuperHero> processor = filter(e -> e.getBadGuysBeatenUp() > 5,
                filter(isYoungerThan(youngest), youngest)
        );

        superHeroes.forEach(processor::accept);

        assertThat(youngest.getValue().name, is("superman"));
    }

    @Test
    public void sum_bad_guys_beaten_up() {

        Scalar<BigInteger> badGuysBeatenUp = scalar(ZERO);

        Consumer<SuperHero> processor = map(SuperHero::getBadGuysBeatenUp,
                map(Machinery::castToBigInteger,
                        operation(input(), BigInteger::add, badGuysBeatenUp, badGuysBeatenUp)
                )
        );

        superHeroes.forEach(processor::accept);

        assertThat(badGuysBeatenUp.getValue().intValue(), is(21));
    }

    @Test
    public void calculate_bad_guys_beaten_up_over_3() {
        Scalar<BigInteger> badGuysBeatenUpOver3 = new Scalar<>(ZERO);

        Consumer<SuperHero> processor = map(SuperHero::getBadGuysBeatenUp,
                filter(d -> d > 3,
                        map(Machinery::castToBigInteger,
                                operation(input(), BigInteger::subtract, BigInteger.valueOf(3),
                                        operation(input(), BigInteger::add, badGuysBeatenUpOver3, badGuysBeatenUpOver3)
                                )
                        )
                )
        );

        superHeroes.forEach(processor::accept);

        assertThat(badGuysBeatenUpOver3.getValue().intValue(), is(11));
    }

    @Test
    public void find_first_2_superheroes_that_are_not_quite_slackers() {

        Vector<SuperHero> hardWorkingSuperHeroes = vector();

        Consumer<SuperHero> processor = filter(e -> e.getBadGuysBeatenUp() < 8,
                limit(2, hardWorkingSuperHeroes)
        );

        superHeroes.forEach(processor::accept);

        assertThat(hardWorkingSuperHeroes.getValues().size(), is(2));
        assertThat(hardWorkingSuperHeroes.getValues().get(0), is(superHeroes.get(0)));
        assertThat(hardWorkingSuperHeroes.getValues().get(1), is(superHeroes.get(1)));
    }
}
