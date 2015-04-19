/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * HttpContext.java                                       *
 *                                                        *
 * http context class for Java.                           *
 *                                                        *
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpContext extends HproseContext {
    private final ServletContext application;
    private final ServletConfig config;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpContext(HttpServletRequest request,
                       HttpServletResponse response,
                       ServletConfig config,
                       ServletContext application) {
        this.request = request;
        this.response = response;
        this.config = config;
        this.application = application;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    public ServletConfig getConfig() {
        return config;
    }

    public ServletContext getApplication() {
        return application;
    }
}