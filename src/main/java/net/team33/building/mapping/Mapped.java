package net.team33.building.mapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * Provides a basic implementation of a data object with its properties represented by a specific
 * {@link Set} of keys and backed by a {@link Map} containing an entry for each of those keys.
 * <p/>
 * May not be instantiated or derived directly but through either {@link Immutable} or {@link Mutable}.
 *
 * @param <K> The specific type of the keys representing the properties.
 */
@SuppressWarnings("PublicInnerClass")
public abstract class Mapped<K> {

    private static final String ILLEGAL_KEY = "Illegal key <%s>";
    private static final String ILLEGAL_KEYS = "<origin> contains illegal keys: <%s>";
    private static final String VALUE_IS_NULL = "<value> must not be <null>";

    /**
     * May not be instantiated or derived directly but through either {@link Immutable} or {@link Mutable}.
     */
    private Mapped() {
    }

    /**
     * Intended to initialize or update a map used as backing for a Mapped, a derivative or a relating Builder.
     *
     * @throws NullPointerException
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    protected static <K extends Key, M extends Map<K, Object>> M copy(
            final Map<? extends K, ?> origin, final Collection<? extends K> keys,
            final boolean reset, final boolean ignoreOverhead, final M result) {

        if (ignoreOverhead || keys.containsAll(origin.keySet())) {
            for (final K key : keys) {
                final boolean containsKey = origin.containsKey(key);
                if (reset || containsKey) {
                    final Object value = containsKey ? origin.get(key) : key.getInitial();
                    result.put(key, valid(key, value));
                }
            }
            return result;

        } else {
            throw new IllegalArgumentException(format(ILLEGAL_KEYS, origin.keySet()));
        }
    }

    /**
     * @throws NullPointerException
     * @throws ClassCastException
     */
    protected static Object valid(final Key key, final Object value) {
        if ((null != value) || key.isNullable()) {
            // may cause a ClassCastException ...
            return key.getValueClass().cast(value);
        } else {
            // noinspection ProhibitedExceptionThrown
            throw new NullPointerException(VALUE_IS_NULL);
        }
    }

    /**
     * Supplies a Map representing the properties of this instance.
     * <p/>
     * An implementation must specify weather or not the result is mutable.
     */
    public abstract Map<K, Object> asMap();

    /**
     * Retrieves the specified property value. Intended to easily implement a property specific, well typed getter.
     *
     * @param key The property specification.
     * @throws NullPointerException     (optional)
     *                                  if {@code key} is {@code null} and {@code null} is not supported by the
     *                                  underlying map.
     * @throws IllegalArgumentException if the underlying map does not contain the specified {@code key}.
     * @throws ClassCastException       if not applied in the correct class context.
     */
    public final <T> T get(final K key) {
        if (asMap().containsKey(key)) {
            // May cause a ClassCastException just like an explicit outer cast which otherwise were necessary ...
            // noinspection unchecked
            return (T) asMap().get(key);
        } else {
            throw new IllegalArgumentException(format(ILLEGAL_KEY, key));
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation assumes equality simply depending on {@linkplain #isTypeCompatible(Object)
     * type compatibility} and the {@linkplain #asMap() underlying map}.
     */
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || (isTypeCompatible(obj) && asMap().equals(((Mapped<?>) obj).asMap()));
    }

    /**
     * Indicates the type compatibility between this and the other object.
     * <p/>
     * The base implementation assumes compatibility only if both are of the same class.
     * <p/>
     * A derivative may override to loose the restriction (think of reflexivity of equals())
     */
    @SuppressWarnings("DesignForExtension")
    protected boolean isTypeCompatible(final Object other) {
        return (null != other) && getClass().equals(other.getClass());
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation retrieves the hash code simply from the {@linkplain #asMap() underlying map}.
     */
    @Override
    public final int hashCode() {
        return asMap().hashCode();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation supplies the {@linkplain Map#toString() string representation} of the
     * {@linkplain #asMap() underlying map}, prefixed by {@link #toStringPrefix()}.
     * <p/>
     * If desired a derivative may override (replace or modify) this implementation
     * (or simply override {@link #toStringPrefix()}).
     */
    @SuppressWarnings("DesignForExtension")
    @Override
    public String toString() {
        return toStringPrefix() + asMap().toString();
    }

    /**
     * Supplies a prefix used by {@link #toString()} (while not overridden itself).
     * <p/>
     * The default implementation supplies the simple name of this' {@linkplain #getClass() class representation}.
     * <p/>
     * If desired a derivative may override (replace or modify) this implementation.
     */
    @SuppressWarnings("DesignForExtension")
    protected String toStringPrefix() {
        return getClass().getSimpleName();
    }

    /**
     * Basic implementation (in particular specification) of an immutable {@link Mapped}.
     *
     * @param <K> The specific type of the keys representing the properties.
     */
    public abstract static class Immutable<K> extends Mapped<K> {

        /**
         * May derived directly (in opposite to {@link Mapped} itself).
         */
        protected Immutable() {
        }

        /**
         * {@inheritDoc}
         * <p/>
         * An immutable implementation is expected to supply an immutable map.
         */
        @SuppressWarnings("AbstractMethodOverridesAbstractMethod") // Supplemented specification (JavaDoc)
        @Override
        public abstract Map<K, Object> asMap();
    }

    /**
     * Basic implementation of a mutable {@link Mapped}.
     *
     * @param <K> The specific type of the keys representing the properties.
     * @param <B> The final (relevant) derivation of this class
     */
    @SuppressWarnings("ReturnOfThis")
    public abstract static class Mutable<K extends Key, B extends Mutable<K, B>> extends Mapped<K> {

        /**
         * May derived directly (in opposite to {@link Mapped} itself).
         */
        protected Mutable() {
        }

        /**
         * {@inheritDoc}
         * <p/>
         * An immutable implementation is expected to supply an immutable map.
         */
        @SuppressWarnings("AbstractMethodOverridesAbstractMethod") // Supplemented specification (JavaDoc)
        @Override
        public abstract Map<K, Object> asMap();

        /**
         * Sets the {@code value} for a specific {@code key}, if it's part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if {@code value} is {@code null} and the specified {@code key}
         *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
         * @throws ClassCastException       if {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
         *                                  class} associated with the specified {@code key}.
         * @throws IllegalArgumentException if the specified {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set}.
         */
        public final B set(final K key, final Object value) {
            return set(key, value, false);
        }

        /**
         * Sets the {@code value} for a specific {@code key}, if it's part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores the value or throws an IllegalArgumentException.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if {@code value} is {@code null} and the specified {@code key} indicates
         *                                  to be not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
         * @throws ClassCastException       if {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
         *                                  value class} associated with the specified {@code key}.
         * @throws IllegalArgumentException if the specified {@code key} is not part of the {@linkplain #keySet()}
         *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
         */
        public final B set(final K key, final Object value, final boolean ignoreOverhead) {
            if (keySet().contains(key)) {
                asMap().put(key, valid(key, value));
            } else if (!ignoreOverhead) {
                throw new IllegalArgumentException(format(ILLEGAL_KEY, key));
            }
            // <this> must be an instance of <B> ...
            // noinspection unchecked
            return (B) this;
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will remain as is.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set}.
         */
        public final B set(final Map<? extends K, ?> origin) {
            return set(origin, false);
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will remain as is.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
         */
        public final B set(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
            return set(origin, false, ignoreOverhead);
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will be reset to their
         * {@linkplain net.team33.building.mapping.Key#getInitial() defaults}.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set}.
         */
        public final B reset(final Map<? extends K, ?> origin) {
            return reset(origin, false);
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will be reset to their
         * {@linkplain net.team33.building.mapping.Key#getInitial() defaults}.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain net.team33.building.mapping.Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain net.team33.building.mapping.Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
         */
        public final B reset(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
            return set(origin, true, ignoreOverhead);
        }

        private B set(final Map<? extends K, ?> origin, final boolean reset, final boolean ignoreOverhead) {
            copy(origin, keySet(), reset, ignoreOverhead, asMap());
            // <this> must be an instance of <B> ...
            // noinspection unchecked
            return (B) this;
        }

        /**
         * Supplies the intended key set.
         * <p/>
         * Must be a separate immutable set - not simply the {@link java.util.Map#keySet()} for the
         * {@linkplain #asMap() underlying map} - otherwise expect strange side effects!
         */
        protected abstract Set<K> keySet();
    }
}
