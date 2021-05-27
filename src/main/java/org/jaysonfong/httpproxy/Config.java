/*
 * The MIT License
 *
 * Copyright 2021 Jayson Fong <contact@jaysonfong.org>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jaysonfong.httpproxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.ApplicationPath;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.org>
 */
@Singleton
@Startup
@ApplicationPath("/")
public class Config {
    
    private static final String CONFIG_PATH = "config.properties";
    
    public Configuration configuration;
    
    public enum Attribute {
        
        AGENT("request.agent"),
        TYPES("request.types"),
        USECACHE("request.usecache"),
        TIMEOUT("request.timeout"),
        DEFAULT("request.default"),
        
        LENGTH("response.maxlength"),
                
        SECRET("security.secret"),
        ALGORITHM("security.algorithm");
        
        private final String key;
        
        private Attribute(final String iKey) {
            this.key = iKey;
        }
        
        public String getKey() {
            return this.key;
        }
        
    }
    
    @PostConstruct
    public void createConfig() {
        Configurations configurations = new Configurations();
        try {
            this.configuration = configurations.properties(Config.CONFIG_PATH);
        } catch (ConfigurationException configurationException) {
            Logger logger = Logger.getLogger(Config.class.getName());
            logger.log(Level.SEVERE, configurationException.getLocalizedMessage());
            throw new RuntimeException(configurationException);
        }
    }
    
    public String getValue(Attribute key) {
        return this.configuration.getString(key.getKey());
    }
    
    public int getInteger(Attribute key) {
        return this.configuration.getInt(key.getKey());
    }
    
    public boolean getBoolean(Attribute key) {
        return this.configuration.getBoolean(key.getKey());
    }
    
    public URL getURL(Attribute key) {
        try {
            return new URL(this.configuration.getString(key.getKey()));
        } catch (MalformedURLException malformedURLException) {
            Logger logger = Logger.getLogger(Config.class.getName());
            logger.log(Level.SEVERE, malformedURLException.getLocalizedMessage());
            throw new RuntimeException(malformedURLException);
        }
    }
    
}
