package hudson.plugins.trac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSet.Path;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for TracGitRepositoryBrowser with
 * strip and append.
 * See JENKINS-13366.
 * 
 * @author Gerd Zanker (gerd.zanker@web.de)
 * 
 * Based on the ViewGetWeb code from
 * @author Paul Nyheim (paul.nyheim@gmail.com)
 */
public class TracGitRepositoryBrowserStripAndAppendTest {

	/** 
	 * URLs used for testing
	 */
	private static final String TRAC_URL = "https://trac";
	private static final String STRIP_FROM_PATH = "src/";
	private static final String APPEND_TO_URL = "myRepo/";

	/**
     * TracGitRepositoryBrowser instance used for testing.
     * The getTracWebURL function is mocked to easily return the testing URL. 
     */
	private final TracGitRepositoryBrowser tracGitBrowser = new TracGitRepositoryBrowserMock();
    
    /**
     * Mock implementation to return the test URL.
     */
    private class TracGitRepositoryBrowserMock extends TracGitRepositoryBrowser {
        private static final long serialVersionUID = 1L;

		@Override
		protected TracProjectProperty getTracProjectProperty(GitChangeSet changeSet) {
			return new TracProjectProperty(TRAC_URL, STRIP_FROM_PATH, APPEND_TO_URL, null);
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
    public void testGetChangeSetLinkGitChangeSet() throws IOException, SAXException {
        final URL changeSetLink = tracGitBrowser.getChangeSetLink(TracGitHelper.createChangeSet("rawchangelog"));
        assertEquals(TRAC_URL+"/changeset/396fc230a3db05c427737aa5c2eb7856ba72b05d", changeSetLink.toString());
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getDiffLink(hudson.plugins.git.GitChangeSet.Path)}.
     * Test cases where links are tested, leading to the diff of the file from the same changeset page as above.
     * 
     * The STRIP_FROM_PATH string must not be part of the link.
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetDiffLinkPath() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = TracGitHelper.createPathMap("rawchangelog");
        final Path path1 = pathMap.get("src/main/java/hudson/plugins/git/browser/GithubWeb.java");
        assertEquals(TRAC_URL + "/changeset/396fc230a3db05c427737aa5c2eb7856ba72b05d/main/java/hudson/plugins/git/browser/GithubWeb.java", tracGitBrowser.getDiffLink(path1).toString());
        final Path path2 = pathMap.get("src/test/java/hudson/plugins/git/browser/GithubWebTest.java");
        assertEquals(TRAC_URL + "/changeset/396fc230a3db05c427737aa5c2eb7856ba72b05d/test/java/hudson/plugins/git/browser/GithubWebTest.java", tracGitBrowser.getDiffLink(path2).toString());
        final Path path3 = pathMap.get("src/test/resources/hudson/plugins/git/browser/rawchangelog-with-deleted-file");
        assertNull("Do not return a diff link for added files.", tracGitBrowser.getDiffLink(path3));
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
        final HashMap<String, Path> pathMap = TracGitHelper.createPathMap("rawchangelog-with-deleted-file");
        final Path path = pathMap.get("bar");
        assertNull("Do not return a diff link for deleted files.", tracGitBrowser.getDiffLink(path));

    }
    
    /**
     * Test method for
     * {@link hudson.plugins.git.browser.TracGitBrowser#getFileLink(hudson.plugins.git.GitChangeSet.Path)}.
     * Test case for a link to a file of one dedicated revision, derived from changeset-
     * 
     * The STRIP_FROM_PATH string must not be part of the link.
     * The APPEND_TO_URL string must be part of the link.
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testGetFileLinkPath() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = TracGitHelper.createPathMap("rawchangelog");
        final Path path = pathMap.get("src/main/java/hudson/plugins/git/browser/GithubWeb.java");
        final URL fileLink = tracGitBrowser.getFileLink(path);
        assertEquals(TRAC_URL + "/browser/" + APPEND_TO_URL + "main/java/hudson/plugins/git/browser/GithubWeb.java?rev=396fc230a3db05c427737aa5c2eb7856ba72b05d",
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
        final HashMap<String, Path> pathMap = TracGitHelper.createPathMap("rawchangelog-with-deleted-file");
        final Path path = pathMap.get("bar");
        final URL fileLink = tracGitBrowser.getFileLink(path);
        assertEquals(TRAC_URL + "/browser/" + APPEND_TO_URL + "bar?rev=b547aa10c3f06710c6fdfcdb2a9149c81662923b", String.valueOf(fileLink));
    }

}
