package net.team33.building.mapping.jaxb;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import static java.lang.String.format;

@SuppressWarnings("JUnitTestClassNamingConvention")
public class EnumMappedTrial {

    private static final String EXPECTED_FIRST = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>%n" +
            "<root>%n" +
            "    <address>%n" +
            "        <country>ZZZ</country>%n" +
            "        <firstName>unknown</firstName>%n" +
            "        <name>unknown</name>%n" +
            "    </address>%n" +
            "</root>%n";
    private static final String ANOTHER_NAME = "another name";
    private static final String ANOTHER_FIRST_NAME = "another first name";

    private static <T> T unMarshal(final String xml, final Class<T> dataClass) throws IOException {
        try (final Reader reader = new StringReader(xml)) {
            return JAXB.unmarshal(reader, dataClass);
        }
    }

    private static String marshal(final Object origin) throws IOException {
        try (final StringWriter out = new StringWriter()) {
            JAXB.marshal(origin, out);
            return out.toString();
        }
    }

    @Test
    public final void testUnMarshalFirst() throws IOException {
        Assert.assertEquals(
                new RootPlain(),
                unMarshal(format(EXPECTED_FIRST), RootPlain.class)
        );
    }

    @Test
    public final void testUnMarshalSecond() throws IOException {
        Assert.assertEquals(
                new RootMapped(),
                unMarshal(format(EXPECTED_FIRST), RootMapped.class)
        );
    }

    @Test
    public final void testUnMarshalThird() throws IOException {
        final RootMapped expected = new RootMapped();
        expected.address = MappedData.builder()
                .setName(ANOTHER_NAME)
                .setFirstName(ANOTHER_FIRST_NAME)
                .setCountry(Country.AUT)
                .build();
        Assert.assertEquals(
                expected,
                unMarshal(marshal(expected), RootMapped.class)
        );
    }

    @Test
    public final void testMarshalFirst() throws IOException {
        Assert.assertEquals(
                format(EXPECTED_FIRST),
                marshal(new RootPlain())
        );
    }

    @Test
    public final void testMarshalSecond() throws IOException {
        Assert.assertEquals(
                marshal(new RootPlain()),
                marshal(new RootMapped())
        );
    }

    @SuppressWarnings("PublicInnerClass")
    public abstract static class Root {

        abstract Object member();

        @Override
        public final boolean equals(final Object obj) {
            return (this == obj) || ((obj instanceof Root) && member().equals(((Root) obj).member()));
        }

        @Override
        public final String toString() {
            return member().toString();
        }

        @Override
        public final int hashCode() {
            return member().hashCode();
        }
    }

    @SuppressWarnings({"PublicInnerClass", "PublicField"})
    @XmlRootElement(name = "root")
    public static class RootPlain extends Root {
        public PlainData address = new PlainData();

        @Override
        final Object member() {
            return address;
        }
    }

    @SuppressWarnings({"PublicInnerClass", "PublicField"})
    @XmlRootElement(name = "root")
    public static class RootMapped extends Root {
        public MappedData address = MappedData.builder().build();

        @Override
        final Object member() {
            return address;
        }
    }
}
