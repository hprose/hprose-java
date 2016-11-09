package benchmark.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IService extends Remote {
    String hello(String name) throws RemoteException;
}
