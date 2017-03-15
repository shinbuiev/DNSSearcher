import org.xbill.DNS.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Victor.S on 14-Mar-17.
 */
public class Collector implements Runnable {

    private static ArrayList<String> domains = new ArrayList<>();
    private static ArrayList<String> results = new ArrayList<>();
    private static List<String> synchronizedList =  Collections.synchronizedList(results);

    private AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws IOException {

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            while ((line = br.readLine()) != null) {
                domains.add(line);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        ExecutorService executor = Executors.newFixedThreadPool(100);Executors.

        for (int i = 0; i < 100; i++){
            Runnable collector = new Collector();
            executor.execute(collector);
        }
        executor.shutdown();

        while (!executor.isTerminated()) {}

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out.txt", true)));
            for(String s : synchronizedList) {
                out.println(s);
            }
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

        System.out.println("Finished all threads");
    }

    public void run() {
        while (domains.size() != 0){
            String dom = domains.remove(0);
            count.getAndIncrement();
            String result = dom + System.lineSeparator() + getMx(dom) + System.lineSeparator() + getNs(dom);
//            SingletonFileWriter.getInstance().writeToFile(result);
            synchronizedList.add(result);
            System.out.println(result);
        }

        System.out.println("Thread " + Thread.currentThread().getName() + " done and made " + count.get() + " domains!");
    }

    public String getMx(String domain) {

        StringBuilder builder = new StringBuilder();

        try {
            Record[] mxRecords = new Lookup(domain, Type.MX).run();

            if (mxRecords != null) {

                for (int i = 0; i < mxRecords.length; i++) {
                    MXRecord mx = (MXRecord) mxRecords[i];
                    builder.append("MX: ").append(mx.getTarget()).append(" priority: " + mx.getPriority()).append(" ");
                }
            }
            else {
                builder.append("MX: null");
            }
        } catch (TextParseException e) {
            System.out.println("Error for " + domain + " : " + e.getMessage());
        }

        return builder.toString();
    }

    public String getNs(String domain) {

        StringBuilder builder = new StringBuilder();

        try {
            Record[] nsRecords = new Lookup(domain, Type.NS).run();

            if (nsRecords != null) {
                for (int i = 0; i < nsRecords.length; i++) {
                    NSRecord mx = (NSRecord) nsRecords[i];
                    builder.append("NS: ").append(mx.getTarget()).append(" ");
                }
            }
            else {
                builder.append("NS: null");
            }
        } catch (TextParseException e) {
            System.out.println("Error for " + domain + " : " + e.getMessage());
        }
        return builder.toString();
    }
}
