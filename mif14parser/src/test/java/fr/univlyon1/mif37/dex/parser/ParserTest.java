package fr.univlyon1.mif37.dex.parser;

import fr.univlyon1.mif37.dex.mapping.Mapping;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    @Test
    public void testSample() throws ParseException {
        Reader input = new InputStreamReader(ParserTest.class.getResourceAsStream("/sample-mapping.txt"));
        MappingParser parser = new MappingParser(input);
        Mapping m = parser.mapping();
        assertNotNull(m);
        assertEquals(4,m.getEDB().size());
        assertEquals(3,m.getIDB().size());
        assertEquals(5,m.getTgds().size());
    }
}
