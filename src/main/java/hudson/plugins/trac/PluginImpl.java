package hudson.plugins.trac;

import hudson.Plugin;

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
    }

    public void stop() throws Exception {
        annotator.unregister();
    }
}
