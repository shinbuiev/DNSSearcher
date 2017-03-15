import java.io.*;

/**
 * Created by Victor.S on 14-Mar-17.
 */
public class SingletonFileWriter {
    private static final SingletonFileWriter inst= new SingletonFileWriter();

    private SingletonFileWriter() {
        super();
    }

    public synchronized void writeToFile(String str) {

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out.txt", true)));
            out.println(str);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public static SingletonFileWriter getInstance() {
        return inst;
    }

}
