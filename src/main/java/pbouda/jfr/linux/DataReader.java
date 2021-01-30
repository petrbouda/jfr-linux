package pbouda.jfr.linux;

public interface DataReader extends AutoCloseable {

    String read() throws Exception;
}
