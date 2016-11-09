package benchmark.rmi;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServiceImpl extends UnicastRemoteObject implements IService {
    public ServiceImpl() throws RemoteException {
        super();
    }
    @Override
    public String hello(String name) throws RemoteException {
        return "server >> " + name;
    }
}