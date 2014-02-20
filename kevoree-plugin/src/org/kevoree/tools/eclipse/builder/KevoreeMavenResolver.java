package org.kevoree.tools.eclipse.builder;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.kevoree.resolver.MavenResolver;


/**
 * Created by gregory.nain on 22/01/2014.
 */
public class KevoreeMavenResolver {

    private static Set<String> urls = new HashSet<String>();
    static {
        urls.add("http://repo1.maven.org/maven2/");
        //urls.add("http://oss.sonatype.org/content/groups/public/");
    }

    protected static final MavenResolver resolver = new MavenResolver();

    public static synchronized SortedSet<String> getAllRuntimeVersions() {

        try {
            SortedSet<String> versions = resolver.listVersion("org.kevoree.platform", "org.kevoree.platform.standalone", "jar", urls);
            return versions;
        }catch(StackOverflowError e){
            e.printStackTrace();
        }
        return null;
    }

    public static File resolve(String group, String name, String versionAsked, String extension) {
    	
        return resolver.resolve(group, name, versionAsked, extension, urls);
    }
    


}