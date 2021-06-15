package ru.elytrium.host.api.manager.shared.serializer;

import com.google.common.base.Charsets;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PropertiesConfiguration extends SerializeProvider {

    @Override
    public void save(SerializeManager config, File file) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            save(config, writer);
        }
    }

    @Override
    public void save(SerializeManager config, Writer writer) {
        config.self.forEach((k, v) -> {
            try {
                writer.write(String.format("%s=%s\n", k, v));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public SerializeManager load(File file) throws IOException {
        return load(file, null);
    }

    @Override
    public SerializeManager load(File file, SerializeManager defaults) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            return load(is, defaults);
        }
    }

    @Override
    public SerializeManager load(Reader reader) {
        return load(reader, null);
    }

    @Override
    public SerializeManager load(Reader reader, SerializeManager defaults) {
        Map<String, Object> map = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                int param = str.indexOf("=");
                if (param == -1) continue;

                String name = str.substring(0, param);
                String value = (str.length() == param)? "" : str.substring(param + 1);
                map.put(name, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SerializeManager(map, defaults);
    }

    @Override
    public SerializeManager load(InputStream is) {
        return load(is, null);
    }

    @Override
    public SerializeManager load(InputStream is, SerializeManager defaults) {
        return load(new InputStreamReader(is, Charsets.UTF_8), defaults);
    }

    @Override
    public SerializeManager load(String string) {
        return load(string, null);
    }

    @Override
    public SerializeManager load(String string, SerializeManager defaults) {
        Map<String, Object> map = new HashMap<>();

        for (String str : string.split("\n")) {
            int param = str.indexOf("=");
            if (param == -1) continue;

            String name = str.substring(0, param);
            String value = (str.length() == param)? "" : str.substring(param + 1);
            map.put(name, value);
        }

        return new SerializeManager(map, defaults);
    }
}
