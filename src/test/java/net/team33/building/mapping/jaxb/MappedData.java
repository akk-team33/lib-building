package net.team33.building.mapping.jaxb;

import net.team33.building.mapping.EnumMapped;
import net.team33.building.mapping.Key;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("UnusedDeclaration")
@XmlJavaTypeAdapter(MappedData.XmlAdapter.class)
public class MappedData extends EnumMapped<MappedData.Property> {

    private MappedData(final Mapper<Property, ?> mapper) {
        super(mapper);
    }

    public static Builder builder() {
        return new Builder();
    }

    public final String getName() {
        return get(Property.NAME);
    }

    public final String getFirstName() {
        return get(Property.FIRST_NAME);
    }

    public final Country getCountry() {
        return get(Property.COUNTRY);
    }

    @SuppressWarnings("PublicInnerClass")
    public enum Property implements Key {

        NAME(String.class, false, Constants.UNKNOWN),
        FIRST_NAME(String.class, false, Constants.UNKNOWN),
        COUNTRY(Country.class, false, Country.ZZZ);

        private final boolean nullable;
        private final Class<?> valueClass;
        private final Object initial;

        Property(final Class<?> valueClass, final boolean nullable, final Object initial) {
            this.nullable = nullable;
            this.valueClass = valueClass;
            this.initial = initial;
        }

        @Override
        public final boolean isNullable() {
            return nullable;
        }

        @Override
        public final Class<?> getValueClass() {
            return valueClass;
        }

        @Override
        public final Object getInitial() {
            return initial;
        }

        private static final class Constants {
            private static final String UNKNOWN = "unknown";
        }
    }

    @SuppressWarnings("PublicInnerClass")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
    public static class Builder extends Mapper<Property, Builder> {

        private Builder() {
            super(Property.class);
        }

        public final MappedData build() {
            return new MappedData(this);
        }

        @XmlElement
        public final String getName() {
            return get(Property.NAME);
        }

        public final Builder setName(final String name) {
            return set(Property.NAME, name);
        }

        @XmlElement
        public final String getFirstName() {
            return get(Property.FIRST_NAME);
        }

        public final Builder setFirstName(final String firstName) {
            return set(Property.FIRST_NAME, firstName);
        }

        @XmlElement
        public final Country getCountry() {
            return get(Property.COUNTRY);
        }

        public final Builder setCountry(final Country country) {
            return set(Property.COUNTRY, country);
        }
    }

    @SuppressWarnings({"PackageVisibleInnerClass", "ClassNameSameAsAncestorName"})
    static class XmlAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<Builder, MappedData> {
        @Override
        public final MappedData unmarshal(final Builder v) throws Exception {
            return v.build();
        }

        @Override
        public final Builder marshal(final MappedData v) throws Exception {
            return builder().set(v.asMap());
        }
    }
}
