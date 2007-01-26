package hudson.plugins.trac;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Property for {@link AbstractProject} that stores the associated Trac website URL.
 *
 * <p>
 * TODO: right now, only the system-wide configuration is implemented, but not
 * project-local configuration. But since the latter would be a natural extension,
 * this class extends {@link JobProperty}.
 *
 * @author Kohsuke Kawaguchi
 */
public final class TracProjectProperty extends JobProperty<AbstractProject<?,?>> {

    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends JobPropertyDescriptor {
        /**
         * Trac website URL that these projects use.
         *
         * This value is normalized and therefore it always ends with '/'.
         * Null if this is not configured yet.
         */
        public String tracWebsite;

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
            // TODO: support per-project override to the system-wide setting
            return new TracProjectProperty();
        }

        public boolean configure(StaplerRequest req) throws FormException {
            req.bindParameters(this,"trac.");
            if(tracWebsite.length()==0) tracWebsite=null;
            else {
                if(!tracWebsite.endsWith("/"))
                    tracWebsite += '/';
            }
            save();
            return true;
        }
    }
}
