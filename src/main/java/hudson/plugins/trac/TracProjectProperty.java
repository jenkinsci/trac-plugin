package hudson.plugins.trac;

import java.util.Collection;
import java.util.Collections;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Action;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;
import net.sf.json.JSONObject;

/**
 * Property for {@link AbstractProject} that stores the associated Trac website URL.
 *
 * @author Kohsuke Kawaguchi
 */
public final class TracProjectProperty extends JobProperty<AbstractProject<?,?>> {

    /**
     * Trac website URL that this project uses.
     *
     * This value is normalized and therefore it always ends with '/'.
     * Null if this is not configured yet.
     */
    public final String tracWebsite;

    /**
     * Stripped from the source code changeset path before the path is appended.
     * Null if this is not configured yet.
     */
    public final String tracStrippedFromChangesetPath;

    /**
     * Appended to the Trac's source code browser URL before the path.
     * Null if this is not configured yet.
     */
    public final String tracAppendedToBrowserURL;

    
    @Deprecated
    public TracProjectProperty(String tracWebsite) {
        this(tracWebsite, null, null);
    }

    @DataBoundConstructor
    public TracProjectProperty(String tracWebsite, String tracStrippedFromChangesetPath, String tracAppendedToBrowserURL) {
        // normalize
        if(tracWebsite==null || tracWebsite.length()==0)
            tracWebsite=null;
        else {
            if(!tracWebsite.endsWith("/"))
                tracWebsite += '/';
        }
        if(tracStrippedFromChangesetPath==null || tracStrippedFromChangesetPath.length()==0)
        	tracStrippedFromChangesetPath=null;
        if(tracAppendedToBrowserURL==null || tracAppendedToBrowserURL.length()==0)
        	tracAppendedToBrowserURL=null;
        else {
            if(!tracAppendedToBrowserURL.endsWith("/"))
            	tracAppendedToBrowserURL += '/';
        }
        this.tracWebsite = tracWebsite;
        this.tracStrippedFromChangesetPath = tracStrippedFromChangesetPath;
        this.tracAppendedToBrowserURL = tracAppendedToBrowserURL;
    }

    @Override
    public Collection<? extends Action> getJobActions(AbstractProject<?,?> job) {
        return Collections.singletonList(new TracLinkAction(this));
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {
        // no longer in use but kept for backward compatibility
        private transient String tracWebsite;

        public DescriptorImpl() {
            super(TracProjectProperty.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }

        public String getDisplayName() {
            return "Associated Trac website";
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            TracProjectProperty tpp = req.bindJSON(TracProjectProperty.class,formData);
            if(tpp.tracWebsite==null)
                tpp = null; // not configured
            return tpp;
        }
    }
}
