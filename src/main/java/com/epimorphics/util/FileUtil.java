/******************************************************************
 * File:        FileUtill.java
 * Created by:  Dave Reynolds
 * Created on:  24 Apr 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *****************************************************************/

package com.epimorphics.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.CRC32;

import com.hp.hpl.jena.util.FileUtils;

public class FileUtil {

    /**
     * Copy a source file to the output writer, reading the source as UTF-8.
     * Returns number of chars copied. Leaves output open.
     */
    public static long copyResource(File src, Writer out) throws IOException {
        return copyResource ( FileUtils.asUTF8( new FileInputStream(src) ), out) ;
    }

    /**
     * Copy a source reader to the output writer.
     * Returns number of chars copied. Leaves output open.
     */
    public static long copyResource(Reader in, Writer out) throws IOException {
        try {

            long len = 0;
            char[] buf = new char[1024];
            int n;

            while ((n = in.read(buf, 0, buf.length)) >= 0) {
                out.write( buf, 0, n );
                len += n;
            }

            return len;
        } finally {
            in.close();
        }
    }

    /**
     * Copy a source file to the output stream, byte for byte.
     * Returns number of bytes copied. Leaves output open.
     */
    public static long copyResource(File src, OutputStream os) throws IOException {
        return copyResource(new FileInputStream(src), os);
    }

    /**
     * Copy a source file to the output stream, byte for byte.
     * Returns number of bytes copied. Leaves output open.
     */
    public static long copyResource(InputStream is, OutputStream os) throws IOException {
        try {
            long len = 0;
            byte[] buf = new byte[1024];
            int n;

            while ((n = is.read(buf, 0, buf.length)) >= 0) {
                os.write( buf, 0, n );
                len += n;
            }
            return len;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Copy a source file to the output stream, byte for byte.
     * Returns number of bytes copied. Leaves output open.
     */
    public static long copyResource(InputStream is, Writer out) throws IOException {
        return copyResource(FileUtils.asUTF8(is), out);
    }

    /**
     * Copy a source file to a destination file
     */
    public static void copyResource(String src, String dest) throws IOException {
        OutputStream os = new BufferedOutputStream( new FileOutputStream(dest) );
        copyResource(new File(src), os);
        os.close();
    }
    
    /**
     * Copy a directory tree from a source location to a destination
     */
    public static void copyDirectory(final Path src, final Path dest) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                Path targetPath = dest.resolve(src.relativize(dir));
                if (!Files.exists(targetPath)) {
                    Files.createDirectory(targetPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, dest.resolve(src.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
            
        });
    }

    /**
     * Ensure the given directory exists and is writable. Tries
     * to create it if necessary.
     * @throws ModalExeption if the directory exists but is not accessible
     */
    public static File ensureDir(String dir) throws EpiException {
        File fdir = new File(dir);
        if ( fdir.exists() ) {
            if ( !( fdir.isDirectory() && fdir.canRead() && fdir.canWrite() ) ) {
                throw new EpiException("Can't access directory " + dir);
            }
        } else {
            if (! fdir.mkdirs()) {
                throw new EpiException("Failed to create directory " + dir);
            }
        }
        return fdir;
    }

    /**
     * Remove the directory.
     */
    public static void deleteDirectory(String directory) {
        FileUtil.deleteDirectory(new File(directory));
    }

    /**
     * Remove the directory.
     */
    public static void deleteDirectory(File directory) {
        if (directory.exists()) {
            FileUtil.forceDeleteDirectory(directory);
        }
    }

    // forced delete of a file or empty directory
    static void forceDeleteFile(File file) {
        boolean deleted = file.delete();
        if (!deleted) {
            throw new RuntimeException("failed to delete: " + file);
        }
    }

    static void forceDelete(File file) {
        if (file.isDirectory()) {
            FileUtil.forceDeleteDirectory(file);
        } else {
            forceDeleteFile(file);
        }
    }

    static void forceDeleteDirectory(File directory) {
        File[] children = directory.listFiles();
        for (int i=0; i<children.length; i++) {
            forceDelete(children[i]);
        }
        forceDeleteFile(directory);
    }

    /**
     * Construct a File reference for "file". If "file" is absolute
     * then use that, if it is relative then make it relative to the given parent.
     */
    public static File absoluteFile(String parent, String file) {
        File f = new File(file);
        if (!f.isAbsolute()) {
            f = new File(parent, file);
        }
        return f;
    }

    /**
     * Construct a File reference for "file". If "file" is absolute
     * then use that, if it is relative then make it relative to the given parent.
     */
    public static File absoluteFile(File parent, String file) {
        File f = new File(file);
        if (!f.isAbsolute()) {
            f = new File(parent, file);
        }
        return f;
    }

    /**
     * Compute a checksum for a file for use in detecting changes.
     * May not check whole file contents.
     */
    public static long checksum(String filename) {
        return checksum(new File(filename));
    }

    /**
     * Compute a checksum for a file for use in detecting changes.
     * May not check whole file contents.
     */
    public static long checksum(File file) {
        if (file.isFile()) {
            CRC32 crc = new CRC32();
            checksum(file.lastModified(), crc);
            checksum(file.length(), crc);
            return crc.getValue();
        } else {
            return 0;   // Don't track directories
        }
    }

    /**
     * Compute a checksum for a bundle URL for use in detecting changes.
     * May not check whole file contents.
     */
    public static long checksum(URL url) {
        try {
            URLConnection conn = url.openConnection();
            CRC32 crc = new CRC32();
            checksum(conn.getLastModified(), crc);
            checksum(conn.getContentLength(), crc);
            return crc.getValue();
        } catch (IOException e ) {
            return -1;
        }
    }

    private static void checksum(long l, CRC32 crc)    {
        for (int i = 0; i < 8; i++)        {
            crc.update((int) (l & 0x000000ff));
            l >>= 8;
        }
    }
}
