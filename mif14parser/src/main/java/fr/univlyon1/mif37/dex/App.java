package fr.univlyon1.mif37.dex;

import fr.univlyon1.mif37.dex.mapping.Literal;
import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.mapping.Relation;
import fr.univlyon1.mif37.dex.mapping.Tgd;
import fr.univlyon1.mif37.dex.parser.MappingParser;
import fr.univlyon1.mif37.dex.parser.ParseException;
import fr.univlyon1.mif37.dex.work.Sql;
import fr.univlyon1.mif37.dex.work.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws ParseException, IOException, SQLException {

        Reader input = new InputStreamReader(new FileInputStream(new File("sample-mapping.txt")));
        MappingParser parser = new MappingParser(input);
        Mapping m = parser.mapping();
        // MappingParser mp = new MappingParser(System.in);
        //Mapping mapping = mp.mapping();
        LOG.info("Parsed {} edb(s), {} idb(s) and {} tgd(s).",
                m.getEDB().size(),
                m.getIDB().size(),
                m.getTgds().size());

        Work w = new Work(m);
        Sql sql = new Sql(w);

        System.out.println();
        //sql.execThemAll(sql.Tables(w.getFinal_edb()));
        sql.execThemAll(sql.Tables((ArrayList<Relation>)m.getEDB()));
    }
}
