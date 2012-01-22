/******************************************************************
 * File:        FileUtill.java
 * Created by:  Dave Reynolds
 * Created on:  24 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.CRC32;

import com.hp.hpl.jena.util.FileUtils;

public class FileUtil {
    
    /**
     * Copy a source file to the output writer, reading the source as UTF-8.
     * Returns number of chars copied
     */
    public static int copyResource(File src, Writer out) throws IOException {
        Reader r = null;
    
        try {
            r = FileUtils.asUTF8( new FileInputStream(src) );
    
//            int len = 0;
            char[] buf = new char[1024];
            int n;
    
            while ((n = r.read(buf, 0, buf.length)) >= 0) {
                out.write( buf, 0, n );
//                len += n;
            }

            return n;
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }
    
    /**
     * Copy a source file to the output stream, byte for byte.
     * Returns number of bytes copied.
     */
    public static int copyResource(File src, OutputStream os) throws IOException {
        InputStream is = null;
    
        try {
            is = new FileInputStream(src);
    
            int len = 0;
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
