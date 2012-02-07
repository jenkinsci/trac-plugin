package hudson.plugins.trac;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSet.Path;
import hudson.plugins.git.browser.GitRepositoryBrowser;
import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import hudson.scm.browsers.QueryBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * {@link GitRepositoryBrowser} that produces Trac links.
 * 
 *  @author Gerd Zanker (gerd.zanker@web.de)
 */
public class TracGitRepositoryBrowser extends GitRepositoryBrowser {
    
	private static final long serialVersionUID = 1L;

    @DataBoundConstructor
    public TracGitRepositoryBrowser() {
    }

    /**
     * Gets a URL for the {@link TracProjectProperty#tracWebsite} value
     * configured for the current project.
     * 
     * This function is protected to allow the test to override it
     * and implement a Mock getTracWebURL function to make tests easy.
     */
    protected URL getTracWebURL(GitChangeSet changeSet) throws MalformedURLException {
    	ChangeLogSet<?> cs = changeSet.getParent();
        AbstractProject<?,?> p = (AbstractProject<?,?>)cs.build.getProject();
        TracProjectProperty tpp = p.getProperty(TracProjectProperty.class);
        if(tpp==null)   
        	return null;
        else
        	return new URL(tpp.tracWebsite);
    }

    @Override
    public URL getDiffLink(Path path) throws IOException {
    	// Normally the diffs of a changeset are shown on one single Trac HTML page 
    	// and use the pattern <url>"/changeset/"<changesetID>"#file"<NoOfFileInChangeset>
    	// But because the git changeset doesn't return an order list (only a HashSet)
    	// it is not possible to get correct index for the file inside the changeset.
    	// see https://github.com/jenkinsci/git-plugin/blob/master/src/main/java/hudson/plugins/git/GitChangeSet.java#L57
    	    	
    	// Therefore a different URL pattern is used to show only the diff of a single file. 
    	// returns <url>"/changeset/"<changesetID>/<file>
    	// The drawback is 'only' that the user has to navigate and not only to scroll to see other diffs.
    	
    	// Instead of https://fedorahosted.org/eclipse-fedorapackager/changeset/0956859f7db2656cae445488689a214c104bf1b3#file3
    	// e.g.       https://fedorahosted.org/eclipse-fedorapackager/changeset/0956859f7db2656cae445488689a214c104bf1b3/org.fedoraproject.eclipse.packager.rpm/src/org/fedoraproject/eclipse/packager/rpm/internal/handlers/SRPMImportHandler.java
        if (path.getEditType() == EditType.EDIT) {
        	return new URL(getTracWebURL(path.getChangeSet()), getChangeSetLink(path.getChangeSet()).toString() + "/" + path.getPath() );            
        }
        return null;
    }

	@Override
    public URL getFileLink(Path path) throws IOException {
    	// returns <url>"/browser/"<file>"$rev="<changsetID>
    	// e.g. https://fedorahosted.org/eclipse-fedorapackager/browser/org.fedoraproject.eclipse.packager.rpm/src/org/fedoraproject/eclipse/packager/rpm/RpmText.java?rev=0956859f7db2656cae445488689a214c104bf1b3
        String spec;
        URL url = getTracWebURL(path.getChangeSet());
        if (path.getEditType() == EditType.DELETE) {
        	spec = new QueryBuilder(url.getQuery()).add("rev="+path.getChangeSet().getParentCommit()).toString();
        } else {
        	spec = new QueryBuilder(url.getQuery()).add("rev="+path.getChangeSet().getId()).toString();
        }
        return new URL(url, url.getPath() + "browser/" + path.getPath() + spec);
    }

    @Override
    public URL getChangeSetLink(GitChangeSet changeSet) throws IOException {
    	// returns <url>"/changeset/"<changsetID>
    	// e.g. https://fedorahosted.org/eclipse-fedorapackager/changeset/0956859f7db2656cae445488689a214c104bf1b3
        URL url = getTracWebURL(changeSet);
        return new URL(url, url.getPath() + "changeset/" + changeSet.getId());
    }

    
    
    @Extension(optional = true)
    public static final class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public DescriptorImpl() {
        	super();
        }

        public String getDisplayName() {
            return "TracGit";
        }
    }

}
