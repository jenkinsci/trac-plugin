package hudson.plugins.trac;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.AbstractProject;
import hudson.plugins.git.GitChangeSet;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SubversionChangeLogSet.LogEntry;
import hudson.scm.SubversionChangeLogSet.Path;
import hudson.scm.SubversionRepositoryBrowser;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link SubversionRepositoryBrowser} that produces Trac links.
 */
public class TracRepositoryBrowser extends SubversionRepositoryBrowser {
    @DataBoundConstructor
    public TracRepositoryBrowser() {
    }


    protected TracProjectProperty getTracProjectProperty(LogEntry changeSet) {
    	AbstractProject<?,?> p = (AbstractProject<?,?>)changeSet.getParent().build.getProject();
    	return p.getProperty(TracProjectProperty.class);
    }

    
    /**
     * Gets a URL for the {@link TracProjectProperty#tracWebsite} value
     * configured for the current project.
     */
    private URL getTracWebURL(LogEntry changeSet) throws MalformedURLException {
    	TracProjectProperty tpp = getTracProjectProperty(changeSet);
        if(tpp==null)   
        	return null;
        else
        	return new URL(tpp.tracWebsite);
    }

    /**
     * Gets the String from {@link TracProjectProperty#tracWebsite} 
	 * which will be appended to the browser URL.
     * See JENKINS-13366
     */
    private String getTracAppendToBrowserURL(LogEntry changeSet)  {
    	TracProjectProperty tpp = getTracProjectProperty(changeSet);
        if(tpp==null || tpp.tracAppendedToBrowserURL==null)   
        	return "";
        else {
        	// remove ending slash, because SVN paths always start with a slash
        	String appendStr = tpp.tracAppendedToBrowserURL;
        	if(appendStr.endsWith("/"))
        		return "/" + appendStr.substring(0, appendStr.length()-1);
        	else 
        		return "/" + appendStr;
        }
    }
    
    private String getPath(Path path) {
        String pathValue = path.getValue();
        TracProjectProperty tpp = getTracProjectProperty(path.getLogEntry());
        if(tpp != null && tpp.tracStrippedFromChangesetPath != null && pathValue != null
            && pathValue.startsWith(tpp.tracStrippedFromChangesetPath))
            return pathValue.substring(tpp.tracStrippedFromChangesetPath.length());
        else
            return pathValue;
    }

    @Override
    public URL getDiffLink(Path path) throws IOException {
        if(path.getEditType()!= EditType.EDIT)
            return null;    // no diff if this is not an edit change
        URL baseUrl = getTracWebURL(path.getLogEntry());
        int revision = path.getLogEntry().getRevision();
        return new URL(baseUrl, "changeset/" + revision + getPath(path) + "#file0");
    }

    @Override
    public URL getFileLink(Path path) throws IOException {
        URL baseUrl = getTracWebURL(path.getLogEntry());
        return baseUrl == null ? null : new URL(baseUrl, "browser" + getTracAppendToBrowserURL(path.getLogEntry()) + getPath(path) + "#L1");
    }

    @Override
    public URL getChangeSetLink(LogEntry changeSet) throws IOException {
        URL baseUrl = getTracWebURL(changeSet);
        return baseUrl == null ? null : new URL(baseUrl, "changeset/" + changeSet.getRevision());
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public DescriptorImpl() {
            super(TracRepositoryBrowser.class);
        }

        public String getDisplayName() {
            return "Trac";
        }
    }
}
