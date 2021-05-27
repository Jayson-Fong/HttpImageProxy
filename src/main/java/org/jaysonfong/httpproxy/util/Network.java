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
package org.jaysonfong.httpproxy.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import org.jaysonfong.httpproxy.Config;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.org>
 */
public class Network {
    
    public static Optional<URL> getURL(final String urlParam) {
        try {
            return Optional.of(new URL(urlParam));
        } catch (MalformedURLException malformedURLException) {
            return Optional.empty();
        }
    }
    
    public static URL getDefaultURL(final Config config) {
        return config.getURL(
            Config.Attribute.DEFAULT
        );
    }
    
    public static URL getURLOrDefault(final String urlParam, final Config config) {
        return Network.getURL(urlParam).orElse(
                Network.getDefaultURL(config)
        );
    }
    
    public static boolean filter(final URLConnection connection, final Config config) throws ParseException {
        // Get Config Vars
        final int maxLength = config.getInteger(Config.Attribute.LENGTH);
        final Stream<String> allowedTypes = Stream.of(config.getValue(Config.Attribute.TYPES).split(","));
        final ContentType responseType = new ContentType(connection.getContentType());
        return connection.getContentLength() > maxLength || !allowedTypes.parallel().anyMatch(responseType::match);
    }
    
    public static Optional<BufferedImage> getImage(final URL url, final Config config) {
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", config.getValue(Config.Attribute.AGENT));
            connection.setConnectTimeout(config.getInteger(Config.Attribute.TIMEOUT));
            connection.setUseCaches(config.getBoolean(Config.Attribute.USECACHE));
            
            if (Network.filter(connection, config)) {
                return Optional.empty();
            }
            return Optional.of(ImageIO.read(connection.getInputStream()));
        } catch (IOException | ParseException exception) {
            return Optional.empty();
        }
    }
    
    public static BufferedImage getImageOrDefault(final String urlParam, final Config config) {
        final URL url = Network.getURLOrDefault(urlParam, config);
        
        Optional<BufferedImage> bufferedImageOptional = Network.getImage(url, config);
        
        if (bufferedImageOptional.isPresent()) {
            return bufferedImageOptional.get();
        }
        
        bufferedImageOptional = Network.getImage(
            Network.getDefaultURL(config), config
        );
        
        if (bufferedImageOptional.isPresent()) {
            return bufferedImageOptional.get();
        }
        
        throw new RuntimeException("Failed to Fetch Image: " + url);
    }
    
}
