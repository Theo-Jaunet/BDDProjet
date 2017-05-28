package fr.univlyon1.mif37.dex.work;

import fr.univlyon1.mif37.dex.mapping.AbstractArgument;
import fr.univlyon1.mif37.dex.mapping.AbstractRelation;
import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.mapping.Relation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by theo on 5/27/17.
 */
public class Sql {
    Work work;

    public Sql(Mapping m) {
        this.work = new Work(m);
    }
    public Sql(Work w) {
        this.work = w;
    }
    public String Tables(ArrayList<Relation> edb) throws SQLException {
        HashMap<String, Integer> tables = new HashMap<String, Integer>();
        for (Relation data : edb) {
            if (!tables.containsKey(data.getName())) {
                tables.put(data.getName(), data.getAttributes().length);
            }
        }
        return EdbtoSql(tables,edb);
    }

    public void clear(HashMap<String, Integer> tables, ArrayList<Relation> edb) throws SQLException {
        for (Map.Entry<String, Integer> entry : tables.entrySet()) {
            this.exec("Drop table "+entry.getKey());
        }
    }

    public String EdbtoSql(HashMap<String, Integer> tables, ArrayList<Relation> edb) throws SQLException {
        String request = "";

        for (Map.Entry<String, Integer> entry : tables.entrySet()) {
            request += "Create or replace Table " + entry.getKey() + " ( ";
            for (int i = 0; i < entry.getValue(); i++) {
                request += entry.getKey() + i + " VARCHAR(150)";
                if (i < entry.getValue() - 1) {
                    request += ", ";
                }
            }
            request += " ); ";
        }
        clear(tables,edb);
        return request + " " + FillThemAll(tables, edb);
    }

    public String feedIt(String Table, ArrayList<Relation> data) {
        String request = "";

        for (Relation relation : data) {
            if (relation.getName().equals(Table)) {
                request += "Insert into " + Table + " Values " + ArrayToString(relation.getAttributes()) + " ; ";
            }
        }
        return request;
    }

    public String ArrayToString(String[] args) {
        String arguments = "(";

        for (int i = 0; i < args.length; i++) {
            arguments += "'" + args[i] + "'";
            if (i < args.length - 1) {
                arguments += " ,";
            }
        }
        arguments += ")";
        return arguments;
    }

    public String FillThemAll(HashMap<String, Integer> tables, ArrayList<Relation> edb) {
        String request = "";
        ArrayList<String> done = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tables.entrySet()) {
            done.add(entry.getKey());
            request += feedIt(entry.getKey(), edb) + " ";
        }
        return request;
    }

    public void exec(String request) throws SQLException {
        System.out.println(coAndExecUpdate(request));
    }
    public void execThemAll(String requests) throws SQLException {
        String[] req = requests.split(";");
        for (int i =0;i<req.length;i++){
            System.out.println(req[i]);
                this.exec(req[i]);
        }
    }

    public ResultSet coAndExecQuery(String sql) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/gdw/gdw");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        }
        catch (SQLException e) {
            System.out.println(e);
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    return null;
    }

    public Boolean coAndExecUpdate(String sql) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/gdw/gdw;AUTO_SERVER=TRUE");
            Statement stmt = conn.createStatement();
            Boolean rs = stmt.execute(sql);
            conn.close();
            return rs;
        }
        catch (SQLException e) {
            System.out.println(e);
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generateView(ArrayList<AbstractRelation> IDB){
        String req= "Create or repalce view ";

        for(AbstractRelation ar :IDB ){
            req+= ar.getName() + " As ";
            for (AbstractArgument aa: ar.getAttributes()){
                req+= aa.getAtt();
                //TODO : COMPLETE VIEWS |||  (USE TGD) ?
            }
        }
    }
}
