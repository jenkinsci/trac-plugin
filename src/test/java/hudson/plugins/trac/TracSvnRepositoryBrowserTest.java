package hudson.plugins.trac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import hudson.plugins.git.GitChangeSet;
import hudson.scm.SubversionChangeLogSet.LogEntry;
import hudson.scm.SubversionChangeLogSet.Path;
import hudson.scm.SubversionChangeLogParser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for TracRepositoryBrowser 
 * 
 * @author Gerd Zanker (gerd.zanker@web.de)
 * 
 */
public class TracSvnRepositoryBrowserTest {

	/** 
	 * URL used for testing
	 */
	private static final String TRAC_URL = "https://trac";
	
    /**
     * TracGitRepositoryBrowser instance used for testing.
     * The getTracWebURL function is mocked to easily return the testing URL. 
     */
	private final TracRepositoryBrowser tracSvnBrowser = new TracSvnRepositoryBrowserMock();
    
    /**
     * Mock implementation to return the test URL.
     */
    private class TracSvnRepositoryBrowserMock extends TracRepositoryBrowser {
        private static final long serialVersionUID = 1L;

		@Override
		protected TracProjectProperty getTracProjectProperty(LogEntry changeSet) {
			return new TracProjectProperty(TRAC_URL);
	    }
	}


        
    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getChangeSetLink(hudson.plugins.git.GitChangeSet)}.
     * Test case where a simple link to a changeset is build.
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetChangeSetLinkSvnChangeSet() throws IOException, SAXException {
        final URL changeSetLink = tracSvnBrowser.getChangeSetLink(createChangeSet("changelog_unsorted.xml"));
        assertEquals(TRAC_URL+"/changeset/68100", changeSetLink.toString());
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getDiffLink(hudson.plugins.git.GitChangeSet.Path)}.
     * Test cases where links are tested, leading to the diff of the file from the same changeset page as above.
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetDiffLinkPath() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = createPathMap("changelog_unsorted.xml");
        final Path path1 = pathMap.get("/src/main/java/hudson/plugins/git/browser/GithubWeb.java");
        assertEquals(TRAC_URL + "/changeset/68100/src/main/java/hudson/plugins/git/browser/GithubWeb.java#file0", tracSvnBrowser.getDiffLink(path1).toString());
        final Path path2 = pathMap.get("/src/test/java/hudson/plugins/git/browser/GithubWebTest.java");
        assertEquals(TRAC_URL + "/changeset/68100/src/test/java/hudson/plugins/git/browser/GithubWebTest.java#file0", tracSvnBrowser.getDiffLink(path2).toString());
        final Path path3 = pathMap.get("/src/test/resources/hudson/plugins/git/browser/rawchangelog-with-deleted-file");
        assertNull("Do not return a diff link for added files.", tracSvnBrowser.getDiffLink(path3));
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getDiffLink(hudson.plugins.git.GitChangeSet.Path)}.
     * Special test cases for a diff of a deleted file. 
     * Here no diff is available and therefore the test checks for null. 
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetDiffLinkForDeletedFile() throws Exception{
        final HashMap<String, Path> pathMap = createPathMap("changelog-with-deleted-file.xml");
        final Path path = pathMap.get("/bar");
        assertNull("Do not return a diff link for deleted files.", tracSvnBrowser.getDiffLink(path));

    }
    
    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getFileLink(hudson.plugins.git.GitChangeSet.Path)}.
     * Test case for a link to a file of one dedicated revision, derived from changeset
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetFileLinkPath() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = createPathMap("changelog_unsorted.xml");
        final Path path = pathMap.get("/src/main/java/hudson/plugins/git/browser/GithubWeb.java");
        final URL fileLink = tracSvnBrowser.getFileLink(path);
        assertEquals(TRAC_URL + "/browser/src/main/java/hudson/plugins/git/browser/GithubWeb.java#L1",
                String.valueOf(fileLink));
    }
    
    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getFileLink(hudson.plugins.git.GitChangeSet.Path)}.
     * Special test case for a link to a deleted file of one dedicated revision, derived from changeset.
     * Here the changeset doesn't have the right ID, because the parent changeset ID must be used
     * to link to the last possible revision.
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetFileLinkPathForDeletedFile() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = createPathMap("changelog-with-deleted-file.xml");
        final Path path = pathMap.get("/bar");
        final URL fileLink = tracSvnBrowser.getFileLink(path);
        assertEquals(TRAC_URL + "/browser/bar#L1", String.valueOf(fileLink));
    }
    
    
    /**
     * Helper to create a LogEntry for testing.
     * 
     * @param rawchangelogpath
     * @return
     * @throws IOException
     * @throws SAXException
     */
    private LogEntry createChangeSet(String rawchangelogpath) throws IOException, SAXException {
        File rawchangelog = null;
		try {
			rawchangelog = new File(this.getClass().getResource(rawchangelogpath).toURI());
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
    private HashMap<String, Path> createPathMap(final String changelog) throws IOException, SAXException {
        final HashMap<String, Path> pathMap = new HashMap<String, Path>();
        final Collection<Path> changeSet = createChangeSet(changelog).getPaths();
        for (final Path path : changeSet) {
            pathMap.put(path.getPath(), path);
        }
        return pathMap;
    }

}
