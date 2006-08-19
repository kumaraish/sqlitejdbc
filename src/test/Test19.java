package test;

import java.sql.*;

public class Test19 implements Test.Case
{
    private String error;
    private Exception ex;

    public boolean run() throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:");
        Statement stat = conn.createStatement();
        stat.executeUpdate(
            "create table test19 (id integer primary key, fn, sn);");
        stat.executeUpdate("create view test19view as select * from test19;");
        stat.close();

        DatabaseMetaData meta = conn.getMetaData();
        if (meta == null) { error = "expected meta data"; return false; }

        ResultSet rs;

        // check getTables
        rs = meta.getTables(null, null, null, null);
        if (rs == null) { error = "expected getTables RS"; return false; }
        if (!rs.next()) { error = "expected table"; return false; }
        if (!"test19".equals(rs.getString("TABLE_NAME"))) {
            error = "bad table name"; return false; }
        if (!"test19".equals(rs.getString(3))) {
            error = "bad table name (ordered)"; return false; }
        if (!"TABLE".equals(rs.getString("TABLE_TYPE"))) {
            error = "bad table type"; return false; }
        if (!"TABLE".equals(rs.getString(4))) {
            error = "bad table type (ordered)"; return false; }

        if (!rs.next()) { error = "expected view"; return false; }
        if (!"test19view".equals(rs.getString("TABLE_NAME"))) {
            error = "bad view name"; return false; }
        if (!"VIEW".equals(rs.getString("TABLE_TYPE"))) {
            error = "bad view type"; return false; }

        rs = meta.getTables(null, null, "bob", null);
        if (rs.next()) { error = "unexpected results"; return false; }

        rs = meta.getTables(null, null, "test19", null);
        if (!rs.next()) { error = "results missing"; return false; }
        if (rs.next()) { error = "unexpected second results"; return false; }


        // check getTableTypes
        rs = meta.getTableTypes();
        if (!rs.next()) { error = "expected type table"; return false; }
        if (!"TABLE".equals(rs.getString("TABLE_TYPE"))) {
            error =" bad table type for 'table'"; return false; }
        if (!rs.next()) { error = "expected type view"; return false; }
        if (!"VIEW".equals(rs.getString("TABLE_TYPE"))) {
            error =" bad table type for 'view'"; return false; }

        // check getTypeInfo
        rs = meta.getTypeInfo();
        String[] types = new String[] {
            "BLOB", "INTEGER", "NULL", "REAL", "TEXT" };
        for (int i=0; i < types.length; i++) {
            if (!rs.next()) { error = "expected type " + i; return false; }
            if (!types[i].equals(rs.getString("TYPE_NAME"))) {
                error = "type " + i + " mismatch"; return false; }
        }

        conn.close();

        return true;
    }

    public String name() { return "DatabaseMetaData"; }
    public String error() { return error; }
    public Exception ex() { return ex; }
}
