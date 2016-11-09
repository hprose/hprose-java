package benchmark.hprose;

public class ServiceImpl implements IService {
    @Override
    public String hello(String name) {
        return "server >> " + name;
    }
}