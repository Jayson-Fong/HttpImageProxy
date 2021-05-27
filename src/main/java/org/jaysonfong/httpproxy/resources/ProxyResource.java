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
package org.jaysonfong.httpproxy.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jaysonfong.httpproxy.Config;
import org.jaysonfong.httpproxy.util.Network;
import org.jaysonfong.httpproxy.util.Security;

/**
 *
 * @author Jayson Fong <contact@jaysonfong.org>
 */
@Path("/proxy")
public class ProxyResource {
 
    @Inject
    private Config config;
    
    // Not synchronized, but not important either
    private static int ticker = 0b0;
    
    @GET
    @Produces("image/generic")
    @Path("/{token}")
    public Response getResource(
        @NotNull @PathParam("token") String tokenParam,
        @NotNull @Size(min=14) @QueryParam("url") String urlParam
    ) { 
        if (Security.getToken(urlParam, config).equalsIgnoreCase(tokenParam)) {
            ++ticker;
            try {
                ByteArrayOutputStream byteArrOutput = new ByteArrayOutputStream();
                ImageIO.write(Network.getImageOrDefault(urlParam, config), "png", byteArrOutput);
                return Response.ok(byteArrOutput.toByteArray()).build();
            } catch (RuntimeException | IOException exception) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        return Response.status(Status.FORBIDDEN).build();
    }
    
    @GET
    @Produces("text/html")
    @Path("/status")
    public Response getStatus() {
        return Response.ok("OK").header("count", ProxyResource.ticker).build();
    }
    
}
