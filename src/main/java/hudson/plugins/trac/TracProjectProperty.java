package hudson.plugins.trac;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.kohsuke.stapler.StaplerRequest;

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
     * @stapler-constructor
     */
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

        public JobProperty<?> newInstance(StaplerRequest req) throws FormException {
            TracProjectProperty tpp = req.bindParameters(TracProjectProperty.class, "trac.");
            if(tpp.tracWebsite==null)
                tpp = null; // not configured
            return tpp;
        }
    }
}
