import org.xbill.DNS.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Victor.S on 14-Mar-17.
 */
public class Collector extends Thread {

    private ArrayList<String> domains;

    public Collector(String name, ArrayList<String> domains) throws FileNotFoundException {
        super(name);
        this.domains = domains;
    }

    public void run() {
        for (String domain : domains){
            System.out.println();
            String result = domain + System.lineSeparator() + getMx(domain) + System.lineSeparator() + getNs(domain);
            System.out.println(result);
            SingletonFileWriter.getInstance().writeToFile(result);
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " done!");
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
