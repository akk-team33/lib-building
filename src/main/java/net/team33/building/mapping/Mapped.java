package net.team33.building.mapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * Provides a basic implementation of an immutable data object with its properties represented by a specific
 * {@link Set} of keys and backed by a {@link Map} containing an entry for each of those keys.
 *
 * @param <K> The specific type of the keys representing the properties.
 */
public abstract class Mapped<K extends Key> {

    static final String ILLEGAL_KEY = "Illegal key <%s>";
    private static final String ILLEGAL_KEYS = "<origin> contains illegal keys: <%s>";
    private static final String VALUE_IS_NULL = "<value> must not be <null>";

    /**
     * Intended to initialize or update a map used as backing for a Mapped, a derivative or a relating Builder.
     *
     * @throws NullPointerException
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    static <K extends Key, M extends Map<K, Object>> M copy(
            final Map<? extends K, ?> origin, final Collection<? extends K> keySet,
            final boolean reset, final boolean ignoreOverhead, final M result) {

        if (ignoreOverhead || keySet.containsAll(origin.keySet())) {
            for (final K key : keySet) {
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
    static Object valid(final Key key, final Object value) {
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
     * Commonly this Map may and should be immutable.
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
            // Causes a ClassCastException just like an explicit outer cast which otherwise were necessary ...
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
    protected String toStringPrefix() {
        return getClass().getSimpleName();
    }

}
