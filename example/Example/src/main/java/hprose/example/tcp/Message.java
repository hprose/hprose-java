package hprose.example.tcp;

public class Message<T> {
    private int id;
    private T data;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public T getData() {
        return data;
    }
    public void setData(T value) {
        data = value;
    }
}
