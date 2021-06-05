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

import ru.elytrium.host.api.manager.shared.SerializeManager;

import java.io.*;

public abstract class SerializeProvider {

    public abstract void save(SerializeManager config, File file) throws IOException;

    public abstract void save(SerializeManager config, Writer writer);

    public abstract SerializeManager load(File file) throws IOException;

    public abstract SerializeManager load(File file, SerializeManager defaults) throws IOException;

    public abstract SerializeManager load(Reader reader);

    public abstract SerializeManager load(Reader reader, SerializeManager defaults);

    public abstract SerializeManager load(InputStream is);

    public abstract SerializeManager load(InputStream is, SerializeManager defaults);

    public abstract SerializeManager load(String string);

    public abstract SerializeManager load(String string, SerializeManager defaults);
}
