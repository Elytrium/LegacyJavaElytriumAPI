package ru.elytrahost.api.db;

import java.util.List;
import java.util.Map;

public abstract class Database {
    public abstract void query(String query);
    public abstract void insertMap(String table, Map<String, Object> toInsert, boolean update);
    public abstract void updateMap(String table, Map<String, Object> whereUpdate, Map<String, Object> toUpdate);
    public abstract void deleteMap(String table, Map<String, Object> whereDelete);
    public abstract <T> void insert(String table, T toInsert, Class<T> type, boolean update);
    public abstract <T> T getItem(String table, Map<String, Object> where, Class<T> type);
    public abstract <T> List<T> getItems(String table, Class<T> type);
    public abstract <T> T queryResult(String query, Class<T> type);
    public abstract <T> List<T> queryResultList(String query, Class<T> type);
}