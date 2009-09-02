package hudson.plugins.trac;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Descriptor;
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

    @DataBoundConstructor
    public TracProjectProperty(String tracWebsite) {
        // normalize
        if(tracWebsite==null || tracWebsite.length()==0)
            tracWebsite=null;
        else {
            if(!tracWebsite.endsWith("/"))
                tracWebsite += '/';
        }
        this.tracWebsite = tracWebsite;
    }

    @Override
    public Action getJobAction(AbstractProject<?,?> job) {
        return new TracLinkAction(this);
    }

    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends JobPropertyDescriptor {
        // no longer in use but kept for backward compatibility
        private transient String tracWebsite;

        public DescriptorImpl() {
            super(TracProjectProperty.class);
            load();
        }

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
