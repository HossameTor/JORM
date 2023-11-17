package org.jorm.actions;

import org.jorm.DatabaseConnection;
import org.jorm.annotations.Model;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class Insert {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public boolean save(Object object){
        if(isClassAnnotatedWithModel(object)) throw new RuntimeException("Class is not annotated with @Model");

        Field[] fields = getClassFields(object);

        String query = createInsertQuery(object, fields);
        System.out.println("-----------------------------------\n"+query);
        insertObject(query);

        return true;
    }

    private void insertObject(String query) {
        Connection con = db.getConn();
        try {
            con.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String createInsertQuery(Object object, Field[] fields) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(object.getClass().getSimpleName()).append(" (");
        for (int i = 0; i < fields.length; i++) {
            if(i!=0) query.append(",");
            query.append(fields[i].getName());
        }
        query.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            if(i!=0) query.append(",");
            query.append("?");
        }
        query.append(");");
        return query.toString();
    }

    private Field[] getClassFields(Object object) {
        return object.getClass().getDeclaredFields();
    }

    private boolean isClassAnnotatedWithModel(Object object) {
        return object.getClass().isAnnotationPresent(Model.class);
    }

}

