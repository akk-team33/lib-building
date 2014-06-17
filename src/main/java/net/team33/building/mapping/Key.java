package net.team33.building.mapping;

/**
 * Abstracts a key that provides information about a represented property.
 */
@SuppressWarnings("InterfaceNamingConvention")
public interface Key {

    /**
     * Supplies the {@linkplain Class class representation} of values that may be associated with this key.
     */
    Class<?> getValueClass();

    /**
     * Indicates weather or not {@code null} may be associated with this key.
     */
    boolean isNullable();

    /**
     * Supplies a default value to be initially associated with this key.
     */
    Object getInitial();
}
