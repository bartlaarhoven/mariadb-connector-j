/*
 *
 * MariaDB Client for Java
 *
 * Copyright (c) 2012-2014 Monty Program Ab.
 * Copyright (c) 2015-2017 MariaDB Ab.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to Monty Program Ab info@montyprogram.com.
 *
 * This particular MariaDB Client for Java file is work
 * derived from a Drizzle-JDBC. Drizzle-JDBC file which is covered by subject to
 * the following copyright and notice provisions:
 *
 * Copyright (c) 2009-2011, Marcus Eriksson
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of the driver nor the names of its contributors may not be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS  AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.mariadb.jdbc.internal.com.send.parameters;

import org.mariadb.jdbc.internal.ColumnType;
import org.mariadb.jdbc.internal.io.output.PacketOutputStream;

import java.io.IOException;
import java.io.Reader;

public class ReaderParameter implements Cloneable, ParameterHolder {

    private Reader reader;
    private long length;
    private boolean noBackslashEscapes;

    /**
     * Constructor.
     *
     * @param reader             reader to write
     * @param length             max length to write (can be null)
     * @param noBackslashEscapes must backslash be escape
     */
    public ReaderParameter(Reader reader, long length, boolean noBackslashEscapes) {
        this.reader = reader;
        this.length = length;
        this.noBackslashEscapes = noBackslashEscapes;
    }

    public ReaderParameter(Reader reader, boolean noBackslashEscapes) {
        this(reader, Long.MAX_VALUE, noBackslashEscapes);
    }

    /**
     * Write reader to database in text format.
     *
     * @param pos database outputStream
     * @throws IOException if any error occur when reading reader
     */
    public void writeTo(PacketOutputStream pos) throws IOException {
        pos.write(QUOTE);
        if (length == Long.MAX_VALUE) {
            pos.write(reader, true, noBackslashEscapes);
        } else {
            pos.write(reader, length, true, noBackslashEscapes);
        }
        pos.write(QUOTE);
    }

    /**
     * Return approximated data calculated length for rewriting queries
     *
     * @return approximated data length.
     * @throws IOException if error reading stream
     */
    public long getApproximateTextProtocolLength() throws IOException {
        return -1;
    }

    /**
     * Write data to socket in binary format.
     *
     * @param pos socket output stream
     * @throws IOException if socket error occur
     */
    public void writeBinary(final PacketOutputStream pos) throws IOException {
        if (length == Long.MAX_VALUE) {
            pos.write(reader, false, noBackslashEscapes);
        } else {
            pos.write(reader, length, false, noBackslashEscapes);
        }
    }

    public ColumnType getColumnType() {
        return ColumnType.STRING;
    }


    @Override
    public String toString() {
        return "<Reader>";
    }

    public boolean isNullData() {
        return false;
    }

    public boolean isLongData() {
        return true;
    }

}
