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
 * HproseServlet.java                                     *
 *                                                        *
 * hprose servlet class for Java.                         *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseFilter;
import hprose.common.HproseMethods;
import hprose.io.HproseClassManager;
import hprose.io.HproseMode;
import hprose.util.StrUtil;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HproseServlet extends HttpServlet {

    private static final long serialVersionUID = 1716958719284073368L;
    private final HproseHttpService service = new HproseHttpService();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String param = config.getInitParameter("mode");
        if (param != null) {
            param = param.toLowerCase();
            if (param.equals("propertymode")) {
                service.setMode(HproseMode.PropertyMode);
            }
            else if (param.equals("fieldmode")) {
                service.setMode(HproseMode.FieldMode);
            }
            else if (param.equals("membermode")) {
                service.setMode(HproseMode.MemberMode);
            }
        }
        param = config.getInitParameter("debug");
        if (param != null) {
            param = param.toLowerCase();
            if (param.equals("true")) {
                service.setDebugEnabled(true);
            }
        }
        param = config.getInitParameter("crossDomain");
        if (param != null) {
            param = param.toLowerCase();
            if (param.equals("true")) {
                service.setCrossDomainEnabled(true);
            }
        }
        param = config.getInitParameter("p3p");
        if (param != null) {
            param = param.toLowerCase();
            if (param.equals("true")) {
                service.setP3pEnabled(true);
            }
        }
        param = config.getInitParameter("get");
        if (param != null) {
            param = param.toLowerCase();
            if (param.equals("false")) {
                service.setGetEnabled(false);
            }
        }
        param = config.getInitParameter("event");
        if (param != null) {
            try {
                Class<?> type = Class.forName(param);
                if (HproseServiceEvent.class.isAssignableFrom(type)) {
                    service.setEvent((HproseServiceEvent) type.newInstance());
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("filter");
        if (param != null) {
            try {
                Class<?> type = Class.forName(param);
                if (HproseFilter.class.isAssignableFrom(type)) {
                    service.setFilter((HproseFilter) type.newInstance());
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        HproseMethods methods = service.getGlobalMethods();
        param = config.getInitParameter("class");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    String[] name = StrUtil.split(classNames[i], '|', 3);
                    Class<?> type = Class.forName(name[0]);
                    Object obj = type.newInstance();
                    Class<?> ancestorType;
                    if (name.length == 1) {
                        methods.addInstanceMethods(obj, type);
                    }
                    else if (name.length == 2) {
                        for (ancestorType = Class.forName(name[1]);
                             ancestorType.isAssignableFrom(type);
                             type = type.getSuperclass()) {
                            methods.addInstanceMethods(obj, type);
                        }
                    }
                    else if (name.length == 3) {
                        if (name[1].equals("")) {
                            methods.addInstanceMethods(obj, type, name[2]);
                        }
                        else {
                            for (ancestorType = Class.forName(name[1]);
                                 ancestorType.isAssignableFrom(type);
                                 type = type.getSuperclass()) {
                                methods.addInstanceMethods(obj, type, name[2]);
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("staticClass");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    String[] name = StrUtil.split(classNames[i], '|', 2);
                    Class<?> type = Class.forName(name[0]);
                    if (name.length == 1) {
                        methods.addStaticMethods(type);
                    }
                    else {
                        methods.addStaticMethods(type, name[1]);
                    }
                }
            }
            catch (ClassNotFoundException ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("type");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    String[] name = StrUtil.split(classNames[i], '|', 2);
                    HproseClassManager.register(Class.forName(name[0]), name[1]);
                }
            }
            catch (ClassNotFoundException ex) {
                throw new ServletException(ex);
            }
        }
        setGlobalMethods(methods);
    }

    protected void setGlobalMethods(HproseMethods methods) {
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        service.handle(new HttpContext(request,
                                      response,
                       this.getServletConfig(),
                    this.getServletContext()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Hprose Servlet 1.4";
    }
}
