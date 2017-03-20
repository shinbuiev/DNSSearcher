import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * Created by Victor.S on 15-Mar-17.
 */
public class Util {
    public static void main(String[] args) throws Exception {
        Record[] records = new Lookup("google.com", Type.ANY).run();
        //lookup.setResolver(new SimpleResolver("localhost"));
        System.out.println(records.length);

        for(Record record : records){
            System.out.println(record.toString());
        }

        System.out.println();
        System.out.println();

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        env.put(Context.PROVIDER_URL, "dns:");

        DirContext ctx = new InitialDirContext(env);
        Attributes atts = ctx.getAttributes("google.com", new String[] {"NS", "MX", "A", "AAAA"});
        NamingEnumeration<? extends Attribute> e = atts.getAll();
        while(e.hasMore()) {
            System.out.println(e.next().get());
        }

//        for (Record record : lookup.getAnswers()) {
//            System.out.println(record);
//            System.out.println(lookup.getAliases());
//            System.out.println(lookup.getResult());
//            System.out.println(lookup.getErrorString());
////            System.out.println(lookup.);
//        }
    }
}
