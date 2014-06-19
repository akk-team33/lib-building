package net.team33.building.mapping;

import net.team33.building.Branchable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

public class MappedTest {

    private static final String A_STRING = "a string";
    private static final String VALUE_01 = A_STRING;
    private static final int VALUE_278 = 278;

    private static Builder builder(final KEY... keys) {
        return new Builder(asList(keys));
    }

    @Test
    public final void testGet_previously_set() {
        final Subject subject = builder(KEY.STRING)
                .set(KEY.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                VALUE_01,
                subject.get(KEY.STRING)
        );
    }

    @Test
    public final void testSet_Map() {
        final Map<KEY, Object> origin = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, A_STRING)
                .set(KEY.INTEGER, VALUE_278)
                .asMap();
        Assert.assertEquals(
                origin,
                builder(KEY.STRING, KEY.INTEGER)
                        .set(origin)
                        .asMap()
        );
    }

    @Test
    public final void testReset_Map() {
        final Map<KEY, Object> expected = builder(KEY.STRING, KEY.INTEGER, KEY.DATE)
                .set(KEY.STRING, A_STRING)
                .set(KEY.INTEGER, VALUE_278)
                .asMap();
        final Map<KEY, Object> origin = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, A_STRING)
                .set(KEY.INTEGER, VALUE_278)
                .asMap();
        Assert.assertEquals(
                expected,
                builder(KEY.STRING, KEY.INTEGER, KEY.DATE)
                        .set(KEY.DATE, new Date())
                        .reset(origin)
                        .asMap()
        );
    }

    @Test
    public final void testGet_default() {
        final Subject subject = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                KEY.INTEGER.getInitial(),
                subject.get(KEY.INTEGER)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGet_foreign_key() {
        final Subject subject = builder(KEY.STRING)
                .set(KEY.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                KEY.INTEGER.getInitial(),
                subject.get(KEY.INTEGER)
        );
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "EnumeratedClassNamingConvention"})
    private enum KEY implements Key {

        STRING {
            @Override
            public Class<String> getValueClass() {
                return String.class;
            }
        },

        INTEGER {
            @Override
            public Class<Integer> getValueClass() {
                return Integer.class;
            }
        },

        DATE {
            @Override
            public Class<Date> getValueClass() {
                return Date.class;
            }
        };

        @Override
        public boolean isNullable() {
            return true;
        }

        @Override
        public Object getInitial() {
            return null;
        }
    }

    private static class Subject extends Mapped.Immutable<KEY> implements Branchable<Subject, Builder> {

        private final Map<KEY, Object> backing;

        private Subject(final Map<? extends KEY, ?> template, final boolean ignoreOverhead) {
            // Want to use HashMap here for some testing purpose ...
            // noinspection MapReplaceableByEnumMap
            backing = unmodifiableMap(
                    copy(template, template.keySet(), true, ignoreOverhead, new HashMap<KEY, Object>(0))
            );
        }

        @Override
        public final Map<KEY, Object> asMap() {
            // Already is immutable ...
            // noinspection ReturnOfCollectionOrArrayField
            return backing;
        }

        @Override
        public final Builder branch() {
            return new Builder(asMap().keySet()).set(asMap());
        }
    }

    @SuppressWarnings({"ReturnOfThis", "ClassNameSameAsAncestorName"})
    private static class Builder extends Mapped.Mutable<KEY, Builder>
            implements net.team33.building.Builder<Subject> {

        private final Set<KEY> keys;
        private final Map<KEY, Object> backing;

        private Builder(final Collection<? extends KEY> keys) {
            // Want to use HashSet here for some testing purpose ...
            // noinspection SetReplaceableByEnumSet
            this.keys = unmodifiableSet(new HashSet<>(keys));
            // Want to use HashMap here for some testing purpose ...
            // noinspection MapReplaceableByEnumMap
            backing = copy(Collections.<KEY, Object>emptyMap(), this.keys, true, false, new HashMap<KEY, Object>(0));
        }

        @Override
        protected final Set<KEY> keySet() {
            // Already is immutable ...
            // noinspection ReturnOfCollectionOrArrayField
            return keys;
        }

        @Override
        public final Map<KEY, Object> asMap() {
            // Intended to be mutable ...
            // noinspection ReturnOfCollectionOrArrayField
            return backing;
        }

        @Override
        public final Subject build() {
            return new Subject(asMap(), true);
        }
    }
}
