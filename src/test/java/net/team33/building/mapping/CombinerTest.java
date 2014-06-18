package net.team33.building.mapping;

import net.team33.building.Extractable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class CombinerTest {

    private static net.team33.building.Builder<? extends Set<?>> builder(final Object... values) {
        return new SetBuilder(values);
    }

    private static Combiner combiner() {
        return new Combiner();
    }

    @Test
    public final void testSet() throws Exception {
        final Collection<Data> result = new LinkedList<>();
        final Combiner subject = combiner()
                .set(Key.ABC, builder(1, 2, 3))
                .set(Key.DEF, builder(3, 4, 5))
                .set(Key.GHI, builder(5, 6, 7));
        for (final Data entry : subject) {
            result.add(entry);
        }
        Assert.assertEquals(result.toString(), 27, result.size());
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "UnusedDeclaration"})
    private enum Key implements net.team33.building.mapping.Key {

        ABC, DEF, GHI, JKL;

        @Override
        public Class<?> getValueClass() {
            return Object.class;
        }

        @Override
        public boolean isNullable() {
            return true;
        }

        @Override
        public Object getInitial() {
            return null;
        }
    }

    private static class Data extends EnumMapped<Key> implements Extractable<Data, Builder> {
        private Data(final Mapper<Key, ?> mapper) {
            super(mapper);
        }

        @Override
        public final Builder extract() {
            return new Builder().set(asMap());
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private static class Builder extends EnumMapped.Mapper<Key, Builder> implements net.team33.building.Builder<Data> {
        private Builder() {
            super(Key.class);
        }

        @Override
        public final Data build() {
            return new Data(this);
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private static class Combiner extends net.team33.building.mapping.Combiner<Key, Builder, Data, Combiner> {
        private Combiner() {
            super(Key.class, new Builder().build());
        }
    }

    private static class SetBuilder implements net.team33.building.Builder<Set<?>> {
        private final HashSet<Object> values;

        private SetBuilder(final Object[] values) {
            this.values = new HashSet<>(asList(values));
        }

        @Override
        public final Set<?> build() {
            return unmodifiableSet(new HashSet<>(values));
        }
    }
}