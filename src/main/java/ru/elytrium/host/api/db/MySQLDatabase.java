package ru.elytrium.host.api.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unused"})
public class MySQLDatabase extends Database {
    private final Connection connection;

    public MySQLDatabase(String host, String database, String user, String password) throws SQLException {
        String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false";
        this.connection = DriverManager.getConnection(url, user, password);
    }

    @Override
    public void query(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void insertMap(String table, Map<String, Object> toInsert, boolean update) {
        String keyString = keyFromStream(toInsert.keySet().stream().map(s -> s));
        String valueString = keyFromStream(toInsert.values().stream());

        String request = "INSERT INTO %s (%s) VALUES(%s)";
        if (update) request += " ON DUPLICATE KEY UPDATE";

        query(String.format(request, table, keyString, valueString));
    }

    @Override
    public void updateMap(String table, Map<String, Object> whereUpdate, Map<String, Object> toUpdate) {
        String where = entryFromStream(whereUpdate.entrySet().stream(), " AND ");
        String set = entryFromStream(toUpdate.entrySet().stream(), ", ");

        query(String.format("UPDATE %s SET %s WHERE %s", table, set, where));
    }

    @Override
    public void deleteMap(String table, Map<String, Object> whereDelete) {
        String entryString = entryFromStream(whereDelete.entrySet().stream(), " AND ");
        query(String.format("DELETE FROM %s WHERE %s", table, entryString));
    }

    @Override
    public <T> void insert(String table, T toInsert, Class<T> type, boolean update) {
        try {
            insertMap(table, serializeToMap(toInsert, type), update);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T getItem(String table, Map<String, Object> where, Class<T> type) {
        String entryString = entryFromStream(where.entrySet().stream(), " AND ");
        return queryResult(String.format("SELECT * FROM %s WHERE %s", table, entryString), type);
    }

    @Override
    public <T> List<T> getItems(String table, Class<T> type) {
        return queryResultList(String.format("SELECT * FROM %s", table), type);
    }

    @Override
    public <T> T queryResult(String query, Class<T> type) {
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);

            if (set != null && set.next()) {
                T result = serializeFromSet(set, type);
                statement.close();
                return result;
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public <T> List<T> queryResultList(String query, Class<T> type) {
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);

            List<T> resultList = new ArrayList<>();

            while (set != null && set.next()) {
                T result = serializeFromSet(set, type);
                resultList.add(result);
            }

            statement.close();
            return resultList;
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T> Map<String, Object> serializeToMap(T from, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        Map<String, Object> map = new HashMap<>();

        for (Field field : type.getFields()) {
            String fieldName = field.getName();
            Object fieldValue = field.get(from);

            map.put(fieldName, fieldName);
        }

        return map;
    }

    private <T> T serializeFromSet(ResultSet set, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        T result = type.getConstructor().newInstance();

        for (Field field : type.getFields()) {
            String fieldName = field.getName();
            Object fieldValue = set.getObject(fieldName, field.getType());

            field.set(result, fieldValue);
        }

        return result;
    }

    private String keyFromStream(Stream<Object> stream) {
        return stream
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private String entryFromStream(Stream<Map.Entry<String, Object>> stream, String delimiter) {
        return stream
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(delimiter));
    }
}
