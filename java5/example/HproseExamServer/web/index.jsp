<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<%@page import="hprose.server.*"%>
<%@page import="hprose.exam.*"%>
<jsp:useBean id="service" scope="application" class="hprose.server.HproseHttpService" />
<jsp:setProperty name="service" property="debugEnabled" value="true" />
<%
    Exam2 exam2 = new Exam2();
    HproseHttpMethods methods = new HproseHttpMethods();
    methods.addInstanceMethods(exam2);
    methods.addInstanceMethods(exam2, Exam1.class);
    service.handle(new HttpContext(request, response, config, application), methods);
    out.clear();
    out = pageContext.pushBody();
%>