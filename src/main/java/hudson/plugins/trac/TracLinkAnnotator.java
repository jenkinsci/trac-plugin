package hudson.plugins.trac;

import hudson.MarkupText;
import hudson.MarkupText.SubText;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

import java.util.regex.Pattern;

/**
 * Annotates <a href="http://trac.edgewall.org/wiki/TracLinks">TracLink</a>
 * and <a href="http://trac.edgewall.org/wiki/InterTrac">InterTrac</a>
 * notation in changelog messages.
 *
 * @author Kohsuke Kawaguchi
 * @author Rick Riemer
 */
public class TracLinkAnnotator extends ChangeLogAnnotator {
    @Override
    public void annotate(AbstractBuild<?,?> build, Entry change, MarkupText text) {
        TracProjectProperty tpp = build.getProject().getProperty(TracProjectProperty.class);
        if(tpp==null || tpp.tracWebsite==null)
            return; // not configured

        annotate(tpp.tracWebsite, text);
    }

    void annotate(String url, MarkupText text) {
        for (LinkMarkup markup : MARKUPS) {
            markup.process(text, url);
        }
    }

    private static final class LinkMarkup {
        private final Pattern pattern;
        private final String href;

        LinkMarkup(String pattern, String href) {
            pattern = NUM_PATTERN.matcher(pattern).replaceAll("(\\\\d+)"); // \\\\d becomes \\d when in the expanded text.
            pattern = ANYWORD_PATTERN.matcher(pattern).replaceAll("((?:\\\\w|[._-])+)");
            this.pattern = Pattern.compile(pattern);
            this.href = href;
        }

        void process(MarkupText text, String url) {
            for(SubText st : text.findTokens(pattern)) {
                st.surroundWith(
                    "<a href='"+url+href+"'>",
                    "</a>");
            }
        }

        private static final Pattern NUM_PATTERN = Pattern.compile("NUM");
        private static final Pattern ANYWORD_PATTERN = Pattern.compile("ANYWORD");
    }

    private static final LinkMarkup[] MARKUPS = new LinkMarkup[] {
        new LinkMarkup(
            "(?<!\\:)(?:#|ticket:)NUM",  // "#123" or "ticket:123" but not ":#123" or ":ticket:123"
            "ticket/$1"),
        new LinkMarkup(
            "comment:ticket:NUM:NUM",
            "ticket/$1#comment:$2"),
        new LinkMarkup(
            "\\{NUM\\}|report:NUM",
            "report/$1$2"),  // only $1 or $2 matches, and the other will expand to ""
        new LinkMarkup(
            "rNUM:NUM|\\[NUM:NUM\\]|(?<!\\:)log:@NUM:NUM",
            "log/?rev=$2$4$6&stop_ver=$1$3$5"),
        new LinkMarkup(
            "rNUM(?!:)|\\[NUM\\]|(?<!\\:)changeset:NUM", // (?!:) is a position match with negative look ahead, so that "r5" portion of "r5:6" won't match.
            "changeset/$1$2$3"),
        // TODO: log:trunk@1:3 format
        // TODO: diffs
        new LinkMarkup(
            "(?<!\\:)(?:((?:[A-Z][a-z]+){2,})|wiki:ANYWORD)",
            "wiki/$1$2"),
        new LinkMarkup(
            "milestone:ANYWORD",
            "milestone/$1"),
        // TODO: attachment and file.
        new LinkMarkup( // InterTrac ticket links (short: #T123)
            "#([a-zA-Z])NUM",
            "search?q=%23$1$2"),
        new LinkMarkup( // InterTrac changeset links (short: [T123])
             "\\[([a-zA-Z])NUM\\]",
             "search?q=%5B$1$2%5D"),
        new LinkMarkup( // InterTrac ticket links (medium: trac:#123)
            "ANYWORD\\:#NUM",
            "search?q=$1%3A%23$2"),
        new LinkMarkup( // InterTrac ticket or changeset links (full: trac:ticket:123)
            "ANYWORD\\:((?:ticket)|(?:changeset))\\:NUM",
            "search?q=$1%3A$2%3A$3"),
        new LinkMarkup( // InterTrac wiki links (full: trac:wiki:PageName)
            "ANYWORD\\:wiki\\:ANYWORD",
            "search?q=$1%3Awiki%3A$2")
    };
}
