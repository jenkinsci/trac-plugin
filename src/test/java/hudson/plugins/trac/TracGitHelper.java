package hudson.plugins.trac;

import static org.junit.Assert.fail;
import hudson.plugins.git.GitChangeLogParser;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSet.Path;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.SAXException;

public class TracGitHelper {

    /**
     * Helper to create a changeset for testing.
     * 
     * @param rawchangelogpath
     * @return
     * @throws IOException
     * @throws SAXException
     */
    static public GitChangeSet createChangeSet(String rawchangelogpath) throws IOException, SAXException {
        File rawchangelog = null;
		try {
			rawchangelog = new File(TracGitHelper.class.getResource(rawchangelogpath).toURI());
		} catch (URISyntaxException e) {
			fail("Resource '"+rawchangelogpath+"' not found in test");
		}
        final GitChangeLogParser logParser = new GitChangeLogParser(false);
        final List<GitChangeSet> changeSetList = logParser.parse(null, rawchangelog).getLogs();
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
