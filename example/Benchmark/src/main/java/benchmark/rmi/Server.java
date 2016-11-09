package benchmark.rmi;

import javax.naming.Context;
import javax.naming.InitialContext;

public class Server {
    public static void main(String[] args) {
        try {
          IService serviceRMI = new ServiceImpl();
          Context namingContext = new InitialContext();
          namingContext.bind("rmi://localhost/serviceRMI", serviceRMI);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}