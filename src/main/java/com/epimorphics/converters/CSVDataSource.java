/******************************************************************
 * File:        CSVDataSource.java
 * Created by:  Dave Reynolds
 * Created on:  4 Jul 2011
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
 * $Id:  $
 *****************************************************************/

package com.epimorphics.converters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.epimorphics.util.EpiException;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Reader for CVS files with assumed header row.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 * @version $Revision: $
 */
public class CSVDataSource implements RawDataSource {

    public static final String ROW_VARNAME = "row";
    
    protected CSVReader csvIn;
    protected List<String> headers;
    protected String[] row;
    protected long rowNum = 0;
    
    protected List<String[]> allrows;
    protected boolean preload = false;
    
    public CSVDataSource(File file) throws IOException {
        this(file, false);
    }
    
    public CSVDataSource(Reader reader) throws IOException {
        this(reader, false);
    }
    
    public CSVDataSource(File file, boolean preload) throws IOException {
        this(new FileReader(file), preload);
    }
    
    public CSVDataSource(Reader reader, boolean preload) throws IOException {
        this.preload = preload;
//        csvIn = new CSVReader(reader);
        csvIn = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, '\0');
        String[] headerRow = csvIn.readNext();
        if (headerRow == null) {
            csvIn = null;
        } else {
            headers = new ArrayList<String>( headerRow.length );
            for (String h : headerRow) {
                headers.add( normalizeHeader(h) );
            }
            rowNum++;
        }
        
        if (preload) {
            allrows = csvIn.readAll();
            csvIn.close();
            csvIn = null;
        }
    }
    
    private String normalizeHeader(String header) {
        String h = header.replaceAll("\\W+?", "_");
        if (Character.isUpperCase(h.charAt(0))) {
            // Force leading lower case to avoid issues with ruby constants
            return h.substring(0, 1).toLowerCase() + h.substring(1);
        } else {
            return h;
        }
    }
    
    public long getRowsProcessed() {
        return rowNum - 1;
    }
    
    @Override
    public void remove() {
        throw new EpiException("Remove not support in CSV sources" );
    }

    @Override
    public List<String> getFieldNames() {
        return headers;
    }

    private String[] step() {
        if (preload) {
            int i = (int)rowNum -1;
            if (i < allrows.size()) {
                row = allrows.get(i);
            } else {
                row = null;
                allrows = null;
            }
        } else {
            if (row == null && csvIn != null) {
                try {
                    row = csvIn.readNext();
                    if (row == null) {
                        csvIn.close();
                        csvIn = null;
                    }
                } catch (IOException e) {
                    throw new EpiException("Problem during CSV parsing", e);
                }
            }
        }
        return row;
    }
    
    @Override
    public boolean hasNext() {
        return step() != null;
    }

    @Override
    public Map<String, Object> next() {
        if (step() != null) {
            Map<String, Object> result = new HashMap<String, Object>();
            Value[] rowvals = new Value[headers.size()];
            for (int i = 0; i < headers.size(); i++) {
                SimpleValue val = new SimpleValue( row[i] );
                result.put( headers.get(i), val );
                rowvals[i] = val;
            }
            result.put(INDEX, rowNum);
            result.put(ROW_VARNAME, rowvals);
            row = null;
            rowNum ++;
            return result;
        } else {
            return null;
        }
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return this;
    }

    @Override
    public long size() {
        if (preload) {
            return allrows.size();
        } else {
            return -1;
        }
    }

}

