package hprose.exam.server;

import hprose.server.HproseHttpMethods;
import hprose.server.HproseHttpService;
import hprose.server.HttpContext;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyHproseServlet2 extends HttpServlet {
    private final HproseHttpService service = new HproseHttpService();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        Exam2 exam2 = new Exam2();
        HproseHttpMethods methods = new HproseHttpMethods();
        methods.addInstanceMethods(exam2);
        methods.addInstanceMethods(exam2, Exam1.class);
        service.handle(new HttpContext(service,
                                      request,
                                      response,
                       this.getServletConfig(),
                     this.getServletContext()),
                                      methods);
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
}