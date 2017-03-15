
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Victor.S on 14-Mar-17.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        String fileName = args[0];

        ArrayList<String> domains = new ArrayList<>();

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                domains.add(line);
            }
        }

        List<ArrayList<String>> parts = splitList(domains, 200);

        System.out.println("parts size:" + parts.size());

        for (int i = 0; i < parts.size(); i++){
            Collector collector = new Collector(String.valueOf(i), parts.get(i));
            collector.start();
        }

    }

    static <T> List<ArrayList<T>> splitList(List<T> list, final int L) {

        List<ArrayList<T>> parts = new ArrayList<ArrayList<T>>();
        final int N = list.size();

        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

}
