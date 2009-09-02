package hudson.plugins.trac;

import hudson.model.Descriptor;
import hudson.model.AbstractProject;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SubversionChangeLogSet.LogEntry;
import hudson.scm.SubversionChangeLogSet.Path;
import hudson.scm.SubversionRepositoryBrowser;
import org.kohsuke.stapler.StaplerRequest;
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

    /**
     * Gets a URL for the {@link TracProjectProperty#tracWebsite} value
     * configured for the current project.
     */
    private URL getTracWebURL(LogEntry cs) throws MalformedURLException {
        AbstractProject<?,?> p = (AbstractProject<?,?>)cs.getParent().build.getProject();
        TracProjectProperty tpp = p.getProperty(TracProjectProperty.class);
        if(tpp==null)   return null;
        else            return new URL(tpp.tracWebsite);
    }

    @Override
    public URL getDiffLink(Path path) throws IOException {
        if(path.getEditType()!= EditType.EDIT)
            return null;    // no diff if this is not an edit change
        URL baseUrl = getTracWebURL(path.getLogEntry());
        int revision = path.getLogEntry().getRevision();
        return new URL(baseUrl, "changeset/" + revision + path.getValue() + "#file0");
    }

    @Override
    public URL getFileLink(Path path) throws IOException {
        URL baseUrl = getTracWebURL(path.getLogEntry());
        return baseUrl == null ? null : new URL(baseUrl, "browser" + path.getValue() + "#L1");
    }

    @Override
    public URL getChangeSetLink(LogEntry changeSet) throws IOException {
        URL baseUrl = getTracWebURL(changeSet);
        return baseUrl == null ? null : new URL(baseUrl, "changeset/" + changeSet.getRevision());
    }

    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public DescriptorImpl() {
            super(TracRepositoryBrowser.class);
        }

        public String getDisplayName() {
            return "Trac";
        }
    }
}
