package net.team33.building.mapping.jaxb;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Objects;

import static net.team33.building.mapping.jaxb.MappedData.Property.COUNTRY;
import static net.team33.building.mapping.jaxb.MappedData.Property.FIRST_NAME;
import static net.team33.building.mapping.jaxb.MappedData.Property.NAME;

@SuppressWarnings("UnusedDeclaration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class PlainData {

    private static final String PLAIN_DATA_NAME_FIRST_NAME_COUNTRY = "PlainData(name(%s), firstName(%s), country=(%s)}";
    private String name;
    private String firstName;
    private Country country;

    public PlainData() {
        this(NAME.getInitial().toString(), (String) FIRST_NAME.getInitial(), (Country) COUNTRY.getInitial());
    }

    public PlainData(final String name, final String firstName, final Country country) {
        this.name = name;
        this.firstName = firstName;
        this.country = country;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getFirstName() {
        return firstName;
    }

    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public final Country getCountry() {
        return country;
    }

    public final void setCountry(final Country country) {
        this.country = country;
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof PlainData) && equals_((PlainData) obj));
    }

    private boolean equals_(final PlainData other) {
        return Objects.equals(name, other.name)
                && Objects.equals(firstName, other.firstName)
                && Objects.equals(country, other.country);
    }

    @Override
    public final int hashCode() {
        int result = Objects.hashCode(name);
        result = (31 * result) + Objects.hashCode(firstName);
        result = (31 * result) + Objects.hashCode(country);
        return result;
    }

    @Override
    public final String toString() {
        return String.format(PLAIN_DATA_NAME_FIRST_NAME_COUNTRY, name, firstName, country);
    }
}
