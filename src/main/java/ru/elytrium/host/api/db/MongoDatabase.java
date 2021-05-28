package ru.elytrium.host.api.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class MongoDatabase extends Database {
    public MongoDatabase(String uri) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));
    }

    @Override
    public void query(String query) {

    }

    @Override
    public void insertMap(String table, Map<String, Object> toInsert, boolean update) {

    }

    @Override
    public void updateMap(String table, Map<String, Object> whereUpdate, Map<String, Object> toUpdate) {

    }

    @Override
    public void deleteMap(String table, Map<String, Object> whereDelete) {

    }

    @Override
    public <T> void insert(String table, T toInsert, Class<T> type, boolean update) {

    }

    @Override
    public <T> T getItem(String table, Map<String, Object> where, Class<T> type) {
        return null;
    }

    @Override
    public <T> List<T> getItems(String table, Class<T> type) {
        return null;
    }

    @Override
    public <T> T queryResult(String query, Class<T> type) {
        return null;
    }

    @Override
    public <T> List<T> queryResultList(String query, Class<T> type) {
        return null;
    }
}
