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
 * LastModified: Mar 10, 2018                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.FilterHandler;
import hprose.common.HproseFilter;
import hprose.common.HproseMethods;
import hprose.common.InvokeHandler;
import hprose.io.HproseClassManager;
import hprose.io.HproseMode;
import hprose.util.StrUtil;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HproseServlet extends HttpServlet {

    private final static long serialVersionUID = 1716958719284073368L;
    protected final HproseHttpService service = new HproseHttpService();

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
        param = config.getInitParameter("origin");
        if (param != null) {
            String[] origins = StrUtil.split(param, ',', 0);
            for (int i = 0, n = origins.length; i < n; ++i) {
                service.addAccessControlAllowOrigin(origins[i]);
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
                    service.setEvent((HproseServiceEvent) type.getDeclaredConstructor().newInstance());
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("filter");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    Class<?> type = Class.forName(classNames[i]);
                    if (HproseFilter.class.isAssignableFrom(type)) {
                        service.addFilter((HproseFilter) type.getDeclaredConstructor().newInstance());
                    }
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("beforeFilter");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    Class<?> type = Class.forName(classNames[i]);
                    if (FilterHandler.class.isAssignableFrom(type)) {
                        service.beforeFilter.use((FilterHandler) type.getDeclaredConstructor().newInstance());
                    }
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("afterFilter");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    Class<?> type = Class.forName(classNames[i]);
                    if (FilterHandler.class.isAssignableFrom(type)) {
                        service.afterFilter.use((FilterHandler) type.getDeclaredConstructor().newInstance());
                    }
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        param = config.getInitParameter("invoke");
        if (param != null) {
            try {
                String[] classNames = StrUtil.split(param, ',', 0);
                for (int i = 0, n = classNames.length; i < n; ++i) {
                    Class<?> type = Class.forName(classNames[i]);
                    if (InvokeHandler.class.isAssignableFrom(type)) {
                        service.use((InvokeHandler) type.getDeclaredConstructor().newInstance());
                    }
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
                    Object obj = type.getDeclaredConstructor().newInstance();
                    Class<?> ancestorType;
                    switch (name.length) {
                        case 1:
                            methods.addInstanceMethods(obj, type);
                            break;
                        case 2:
                            for (ancestorType = Class.forName(name[1]);
                                    ancestorType.isAssignableFrom(type);
                                    type = type.getSuperclass()) {
                                methods.addInstanceMethods(obj, type);
                            }
                            break;
                        case 3:
                            if (name[1].length() == 0) {
                                methods.addInstanceMethods(obj, type, name[2]);
                            }
                            else {
                                for (ancestorType = Class.forName(name[1]);
                                        ancestorType.isAssignableFrom(type);
                                        type = type.getSuperclass()) {
                                    methods.addInstanceMethods(obj, type, name[2]);
                                }
                            }
                            break;
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
        param = config.getInitParameter("topic");
        if (param != null) {
            try {
                String[] topics = StrUtil.split(param, ',', 0);
                for (int i = 0, n = topics.length; i < n; ++i) {
                    String[] item = StrUtil.split(topics[i], '|', 3);
                    switch (item.length) {
                        case 1:
                            service.publish(item[0]);
                            break;
                        case 2:
                            service.publish(item[0],
                                    Integer.parseInt(item[1], 10));
                            break;
                        case 3:
                            service.publish(item[0],
                                    Integer.parseInt(item[1], 10),
                                    Integer.parseInt(item[1], 10));
                            break;
                    }
                }
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        setGlobalMethods(methods);
    }

    protected void setGlobalMethods(HproseMethods methods) {
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        service.handle(new HttpContext(service,
                                       request,
                                      response,
                       this.getServletConfig(),
                    this.getServletContext()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        processRequest(request, response);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Hprose Servlet 2.0";
    }
}
