import org.xbill.DNS.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class Collector implements Runnable {

    private static ArrayList<String> domains = new ArrayList<>();
    private static ArrayList<String> results = new ArrayList<>();
    private static List<String> synchronizedList =  Collections.synchronizedList(results);

    public static boolean  mx;
    public static boolean ns;
    public static boolean a;
    public static boolean aaaa;

    private AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws IOException {

        long started = System.currentTimeMillis();

        if(System.getProperty("mx") == null)
            mx = true;
        else
            mx = Boolean.parseBoolean(System.getProperty("mx"));

        if(System.getProperty("ns") == null)
            ns = true;
        else
            ns = Boolean.parseBoolean(System.getProperty("ns"));

        if(System.getProperty("a") == null)
            a = true;
        else
            a = Boolean.parseBoolean(System.getProperty("a"));

        if(System.getProperty("aaaa") == null)
            aaaa = true;
        else
            aaaa = Boolean.parseBoolean(System.getProperty("aaaa"));

        int nThreads = 200;
        if(System.getProperty("thread") != null)
            try {
                nThreads = Integer.parseInt(System.getProperty("thread"));
            }
            catch (NumberFormatException e){
                System.out.println(e.getMessage());
            }

        String prefixSetup = "domain";

        if(mx) prefixSetup = prefixSetup + ";MX";
        if(ns) prefixSetup = prefixSetup + ";NS";
        if(a) prefixSetup = prefixSetup + ";A";
        if(aaaa) prefixSetup = prefixSetup + ";AAAA";

        synchronizedList.add("sep=;");
        synchronizedList.add(prefixSetup);


        String fileName = "domains.txt";

        try {
            fileName = args[0];
        }
        catch (Exception e){
            System.out.println("no such file");
        }

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                domains.add(line);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < nThreads; i++){
            Runnable collector = new Collector();
            executor.execute(collector);
        }
        executor.shutdown();

        while (!executor.isTerminated()) {}

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out.csv", true)));
            for(String s : synchronizedList) {
                out.println(s);
            }
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

        System.out.println("Finished all threads");
        System.out.println("Took " + ((System.currentTimeMillis() - started) / 1000) + " seconds" );
    }

    public void run() {
        while (domains.size() != 0){
            String domain = getDomain(domains);
            count.getAndIncrement();
            StringBuilder result = new StringBuilder(domain + ";");
            if(mx){
                result.append(getMx(domain));
            }
            if(ns){
                result.append(getNs(domain));
            }
            if (a){
                result.append(getA(domain));
            }
            if (aaaa){
                result.append(getAAAA(domain));
            }

            if (result.toString().endsWith(", "))
                result.setLength(result.length() - 2);
//            SingletonFileWriter.getInstance().writeToFile(result);
            synchronizedList.add(result.toString());
            System.out.println(result);
        }

        System.out.println("Thread " + Thread.currentThread().getName() + " done and made " + count.get() + " domains!");
    }

    public static synchronized String  getDomain(ArrayList<String> domains){
        if(domains.size() > 0)
            return domains.remove(0);
        return "";
    }

    public String getMx(String domain) {

        StringBuilder builder = new StringBuilder();

        try {
            Record[] mxRecords = new Lookup(domain, Type.MX).run();

            if (mxRecords != null) {

                for (int i = 0; i < mxRecords.length; i++) {
                    MXRecord mx = (MXRecord) mxRecords[i];
                    builder.append(mx.getTarget()).append(":" + mx.getPriority());
                    if(i < mxRecords.length - 1)
                        builder.append(",");
                    if(i == mxRecords.length - 1)
                        builder.append(";");
                }
            }
            else {
                builder.append("null;");
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
                    builder.append(mx.getTarget());
                    if(i < nsRecords.length - 1)
                        builder.append(",");
                    if(i == nsRecords.length - 1)
                        builder.append(";");
                }
            }
            else {
                builder.append("null;");
            }
        } catch (TextParseException e) {
            System.out.println("Error for " + domain + " : " + e.getMessage());
        }
        return builder.toString();
    }

    public String getA(String domain) {

        StringBuilder builder = new StringBuilder();

        try {
            Record[] records = new Lookup(domain, Type.A).run();

            if (records != null) {

                for (int i = 0; i < records.length; i++) {
                    ARecord aRecord = (ARecord) records[i];
                    builder.append(aRecord.getAddress());
                    if(i < records.length - 1)
                        builder.append(",");
                    if(i == records.length - 1)
                        builder.append(";");
                }
            }
            else {
                builder.append("null;");
            }
        } catch (TextParseException e) {
            System.out.println("Error for " + domain + " : " + e.getMessage());
        }

        return builder.toString();
    }

    public String getAAAA(String domain) {

        StringBuilder builder = new StringBuilder();

        try {
            Record[] records = new Lookup(domain, Type.AAAA).run();

            if (records != null) {

                for (int i = 0; i < records.length; i++) {
                    AAAARecord aRecord = (AAAARecord) records[i];
                    builder.append(aRecord.rdataToString());
                    if(i < records.length - 1)
                        builder.append(",");
                    if(i == records.length - 1)
                        builder.append(";");
                }
            }
            else {
                builder.append("null");
            }
        } catch (TextParseException e) {
            System.out.println("Error for " + domain + " : " + e.getMessage());
        }

        return builder.toString();
    }

}
