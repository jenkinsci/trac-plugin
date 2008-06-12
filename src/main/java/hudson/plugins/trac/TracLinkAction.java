package hudson.plugins.trac;

import hudson.model.Action;

/**
 * {@link Action} to be added to the job top page to link to Trac.
 * @author Kohsuke Kawaguchi
 */
public class TracLinkAction implements Action {
    private final TracProjectProperty prop;

    public TracLinkAction(TracProjectProperty prop) {
        this.prop = prop;
    }

    public String getIconFileName() {
        return "/plugin/trac/trac.png";
    }

    public String getDisplayName() {
        return "Trac";
    }

    public String getUrlName() {
        return prop.tracWebsite;
    }
}
