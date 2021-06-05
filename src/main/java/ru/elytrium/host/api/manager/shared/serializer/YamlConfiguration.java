/*
 * This file is part of BungeeCord, licensed under the BSD License (BSD).
 *
 * Copyright (c) 2012 md_5
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * The name of the author may not be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *     * You may not use the software for commercial software hosting services without
 *       written permission from the author.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ru.elytrium.host.api.manager.shared.serializer;

import com.google.common.base.Charsets;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import ru.elytrium.host.api.manager.shared.SerializeManager;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlConfiguration extends SerializeProvider {

    private final ThreadLocal<Yaml> yaml = ThreadLocal.withInitial(() -> {
        Representer representer = new Representer() {
            {
                representers.put(SerializeManager.class, data -> represent(((SerializeManager) data).self));
            }
        };

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return new Yaml(new Constructor(), representer, options);
    });

    @Override
    public void save(SerializeManager config, File file) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            save(config, writer);
        }
    }

    @Override
    public void save(SerializeManager config, Writer writer) {
        yaml.get().dump(config.self, writer);
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
    @SuppressWarnings("unchecked")
    public SerializeManager load(Reader reader, SerializeManager defaults) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new SerializeManager(map, defaults);
    }

    @Override
    public SerializeManager load(InputStream is) {
        return load(is, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializeManager load(InputStream is, SerializeManager defaults) {
        Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new SerializeManager(map, defaults);
    }

    @Override
    public SerializeManager load(String string) {
        return load(string, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializeManager load(String string, SerializeManager defaults) {
        Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new SerializeManager(map, defaults);
    }
}
