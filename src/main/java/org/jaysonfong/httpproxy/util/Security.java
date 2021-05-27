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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.jaysonfong.httpproxy.Config;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.org>
 */
public class Security {
 
    public static String getToken(final String url, final Config config) {
        Mac hmac;
        String result, algorithm = config.getValue(Config.Attribute.ALGORITHM);
        
        try {
            final byte[] secret = config.getValue(Config.Attribute.SECRET)
                .getBytes(StandardCharsets.UTF_8);
            hmac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret, algorithm);
            hmac.init(secretKeySpec);
            byte[] macBytes = hmac.doFinal(url.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(macBytes);
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }

        return result;
    }
    
}
