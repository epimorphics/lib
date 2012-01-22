/******************************************************************
 * File:        SPARQLUpdate.java
 * Created by:  Dave Reynolds
 * Created on:  10 Aug 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 * $Id:  $
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.epimorphics.util.EpiException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.FileUtils;

/**
 * Low level support for POSTing SPARQL update requests to a server.
 * Supports logging of the entire request to a file and serialization of models.
 * Typical sequence:
 * <pre>
 *   SPARQLUpdate up = new SPARQLUpdate(server);
 *   up.open();
 *   up.sendPrefixes(model);
 *   up.send(command);
 *   up.send(model, true);
 *   up.send(command);
 *   up.close();
 *   int status = up.getStatus();
 *   boolean ok = up.wasSuccessful();
 * </pre>
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 * @version $Revision: $
 */
public class SPARQLUpdate {

    protected BufferedWriter logFile;
    protected URL target;
    protected int status;
    protected String responseMessage;
    protected String responseBody;
    protected HttpURLConnection conn;
    protected BufferedWriter out;

    /**
     * Constructor. Throws an unchecked exception (EpiException) if the server URL is malformed.
     * @param server URL of the of the server which should be send the request 
     */
    public SPARQLUpdate(String server) {
        if (server.startsWith("http:") || server.startsWith("https")) {
            try {
                target = new URL(server);
            } catch (MalformedURLException e) {
                throw new EpiException(e);
            }
        } else {
            throw new EpiException("Update request URL must be http/https");
        }
    }
    
    /**
     * Request that the entire formatted update request be recorded in a timestamped
     * file with the name "<base>-<timestamp>.log". 
     * Throws an unchecked exception (EpiException) if the
     * file can't be created or is not writable
     * @param file  name of the file to log to
     */
    public void setLogFileBase(String filebase) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss-S").format(new Date().getTime());
            String file = filebase + "-" + timestamp + ".log";
            logFile = new BufferedWriter( new FileWriter( new File(file), true) );
        } catch (IOException e) {
            throw new EpiException(e);
        }
    }
    
    /**
     * Open the POST channel to the server
     */
    public void open() {
        try {
            URLConnection _conn = target.openConnection();
            if (_conn instanceof HttpURLConnection) {
                conn = (HttpURLConnection) _conn;
                conn.setRequestProperty("Content-Type", "application/sparql-update");
                conn.setRequestMethod("POST");
    
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                out = new BufferedWriter(wr);           // Is buffering right or will network stack do appropriate buffering anyway?
                
                if (logFile != null) {
                    logFile.write("# Update sent to " + target + " on " + DateFormat.getInstance().format( new Date() ) + "\n");
                }
            } else {
                throw new EpiException("Expected an http(s) URL");
            }
        } catch (IOException e) {
            throw new EpiException(e);
        }
    }
    
    /**
     * Send a string (hopefully part of a SPARQL update command!) to the server
     */
    public void send(String command) {
        if (out != null) {
            try {
                if (logFile != null) {
                    logFile.write(command);
                }
                out.write(command);
            } catch (IOException e) {
                throw new EpiException(e);
            }
        } else {
            throw new EpiException("Update connection not open");
        }
    }
    
    /**
     * Write the prefixes from the given model (or other prefix mapping)
     * in SPARQL update syntax. This is separated from send(model) because
     * in SPARQL update the prefix block comes before the command.
     */
    public void sendPrefixes(PrefixMapping pm) {
        if (out == null) throw new EpiException("Update connection not open");
        try {
            SPARQLUpdateWriter.writeUpdatePrefixes(pm, out);
            if (logFile != null) {
                SPARQLUpdateWriter.writeUpdatePrefixes(pm, logFile);
            }
        } catch (IOException e){
            throw new EpiException(e);
        }
    }
    
    /**
     * Write a model as part of the update request, typically the body
     * of and INSERT DATA {} or DELETE DATA {} request but it is up
     * to the caller to send the surrounding command syntax.
     * @param model the model to send
     * @param turtle if true then the model will be send in compact turtle syntax using any
     * prefixes declared in the model (the caller must have issued sendPrefixes at the start
     * of the update for this to be legal), if false then N-Triple syntax will be used which
     * is more verbose but faster to write and does not require prefixes
     */
    public void send(Model model, boolean turtle) {
        if (out == null) throw new EpiException("Update connection not open");
        try {
            if (turtle) {
                SPARQLUpdateWriter uw = new SPARQLUpdateWriter();
                if (logFile != null) {
                    uw.writeUpdateBody(model, logFile);
                }
                uw.writeUpdateBody(model, out);
            } else {
                if (logFile != null) {
                    model.write(logFile, FileUtils.langNTriple);
                }
                model.write(out, FileUtils.langNTriple);
            }
        } catch (IOException e) {
            throw new EpiException(e);
        }
    }
    
    /**
     * Write a model as part of the update request, typically the body
     * of and INSERT DATA {} or DELETE DATA {} request but it is up
     * to the caller to send the surrounding command syntax.
     * Uses N-triple syntax so that the model data is self-contained with
     * no requirement to have declared prefixes.
     */
    public void send(Model model) {
        send(model, false);
    }
    
    /**
     * Convenience packaging of common pattern of use.
     * Opens the connection if not already open, sends Model prefixes, then preamble command,
     * then the model (using compact Turtle) then postamble command then closes.
     */
    
    public void performUpdate(String preamble, Model model, String postamble) {
        if (out == null) {
            open();
        }
        sendPrefixes(model);
        send(preamble);
        send(model, true);
        send(postamble);
        close();
    }
    
    /**
     * Finish the update command, capturing the result status and any return message
     */
    public void close() {
        if (out == null) throw new EpiException("Update connection not open");
        try {
            if (logFile != null) {
                logFile.flush();
                logFile.close();
            }

            out.flush();
            
//            if (status != 204) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer response = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                responseBody = response.toString();
//            }
    
            status = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
    
            out.close();
            
            out = null;
            logFile = null;
            conn = null;
        } catch (IOException e) {
            throw new EpiException(e);
        }
    }

    /**
     * Get the returned status code from the request 
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get the message part of the HTTP response, generally not interesting
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Get the body of the HTTP response, in the event of an error response
     * the server may return useful human readable information in the body.
     * May be null if the there was no response body (e.g. 204 return)
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Return true if the update succeeded (response code 2xx or 3xx)
     * @return
     */
    public boolean wasSuccessful() {
        return (status >= 200) && (status < 400);
    }
}

