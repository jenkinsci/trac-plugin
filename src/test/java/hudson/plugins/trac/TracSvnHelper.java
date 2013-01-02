package hudson.plugins.trac;

import static org.junit.Assert.fail;
import hudson.scm.SubversionChangeLogParser;
import hudson.scm.SubversionChangeLogSet.LogEntry;
import hudson.scm.SubversionChangeLogSet.Path;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.SAXException;

public class TracSvnHelper {

    /**
     * Helper to create a LogEntry for testing.
     * 
     * @param rawchangelogpath
     * @return
     * @throws IOException
     * @throws SAXException
     */
	static public LogEntry createChangeSet(String rawchangelogpath) throws IOException, SAXException {
		File rawchangelog = null;
		try {
			rawchangelog = new File(TracSvnHelper.class.getResource(rawchangelogpath).toURI());
		} catch (URISyntaxException e) {
			fail("Resource '"+rawchangelogpath+"' not found in test");
		}
        final SubversionChangeLogParser logParser = new SubversionChangeLogParser();
        final List<LogEntry> changeSetList = logParser.parse(null, rawchangelog).getLogs();
        return changeSetList.get(0);
    }


    /**
     * Helper to create a map of paths.
     * 
     * @param changelog
     * @return
     * @throws IOException
     * @throws SAXException
     */
	static public HashMap<String, Path> createPathMap(final String changelog) throws IOException, SAXException {
        final HashMap<String, Path> pathMap = new HashMap<String, Path>();
        final Collection<Path> changeSet = createChangeSet(changelog).getPaths();
        for (final Path path : changeSet) {
            pathMap.put(path.getPath(), path);
        }
        return pathMap;
    }
}
