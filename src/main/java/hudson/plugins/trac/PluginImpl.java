package hudson.plugins.trac;

import hudson.Plugin;
import hudson.scm.RepositoryBrowsers;
import hudson.model.Jobs;

/**
 * Entry point of the plugin.
 *
 * @author Kohsuke Kawaguchi
 * @plugin
 */
public class PluginImpl extends Plugin {
    private final TracLinkAnnotator annotator = new TracLinkAnnotator();

    @Override
    public void start() throws Exception {
        annotator.register();
        Jobs.PROPERTIES.add(TracProjectProperty.DESCRIPTOR);
        RepositoryBrowsers.LIST.add(TracRepositoryBrowser.DESCRIPTOR);
    }

    public void stop() throws Exception {
        annotator.unregister();
    }
}
