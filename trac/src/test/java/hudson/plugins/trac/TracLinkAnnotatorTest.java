package hudson.plugins.trac;

import hudson.MarkupText;

import junit.framework.TestCase;


public class TracLinkAnnotatorTest extends TestCase {

    private static final String TRAC_URL = "http://trac/";

    public void testWikiLinkSyntax() {
        assertAnnotatedTextEquals("Nothing here.", "Nothing here.");
        assertAnnotatedTextEquals("Text with WikiLink.", "Text with <a href='" + TRAC_URL + "wiki/WikiLink'>WikiLink</a>.");
    }

    public void testTicketLinkSyntax() {
        assertAnnotatedTextEquals("Text with ticket #123 link",
                "Text with ticket <a href='" + TRAC_URL + "ticket/123'>#123</a> link");
        assertAnnotatedTextEquals("#123",
                "<a href='" + TRAC_URL + "ticket/123'>#123</a>");
    }

    public void testInterTracShortTicketLinkSyntax() {
        assertAnnotatedTextEquals("Text with #T123 link",
                "Text with <a href='" + TRAC_URL + "search?q=%23T123'>#T123</a> link");
        assertAnnotatedTextEquals("#T123",
                "<a href='" + TRAC_URL + "search?q=%23T123'>#T123</a>");
    }

    public void testInterTracMediumTicketLinkSyntax() {
        assertAnnotatedTextEquals("Text with trac:#123 link",
                "Text with <a href='" + TRAC_URL + "search?q=trac%3A%23123'>trac:#123</a> link");
        assertAnnotatedTextEquals("trac:#123",
                "<a href='" + TRAC_URL + "search?q=trac%3A%23123'>trac:#123</a>");
    }

    public void testInterTracFullTicketLinkSyntax() {
        assertAnnotatedTextEquals("Text with trac:ticket:123 link",
                "Text with <a href='" + TRAC_URL + "search?q=trac%3Aticket%3A123'>trac:ticket:123</a> link");
        assertAnnotatedTextEquals("trac:ticket:123",
                "<a href='" + TRAC_URL + "search?q=trac%3Aticket%3A123'>trac:ticket:123</a>");
    }

    public void testInterTracShortChangesetLinkSyntax() {
        assertAnnotatedTextEquals("Text with [T123] link",
                "Text with <a href='" + TRAC_URL + "search?q=%5BT123%5D'>[T123]</a> link");
        assertAnnotatedTextEquals("[T123]",
                "<a href='" + TRAC_URL + "search?q=%5BT123%5D'>[T123]</a>");
    }

    public void testInterTracFullChangesetLinkSyntax() {
        assertAnnotatedTextEquals("Text with trac:changeset:123 link",
                "Text with <a href='" + TRAC_URL + "search?q=trac%3Achangeset%3A123'>trac:changeset:123</a> link");
        assertAnnotatedTextEquals("trac:changeset:123",
                "<a href='" + TRAC_URL + "search?q=trac%3Achangeset%3A123'>trac:changeset:123</a>");
    }

    public void testInterTracFullWikiLinkSyntax() {
        assertAnnotatedTextEquals("Text with trac:wiki:PageName link",
                "Text with <a href='" + TRAC_URL + "search?q=trac%3Awiki%3APageName'>trac:wiki:PageName</a> link");
        assertAnnotatedTextEquals("trac:wiki:PageName",
                "<a href='" + TRAC_URL + "search?q=trac%3Awiki%3APageName'>trac:wiki:PageName</a>");
    }

    public void testMultipleLinks() {
        String ticketUrl = "<a href='" + TRAC_URL + "ticket/101'>#101</a>";
        String changesetUrl = "<a href='" + TRAC_URL + "changeset/303'>[303]</a>";
        assertAnnotatedTextEquals("#101 for [303]: Text", ticketUrl + " for " + changesetUrl + ": Text");
    }

    private void assertAnnotatedTextEquals(String originalText, String expectedAnnotatedText) {
        MarkupText markupText = new MarkupText(originalText);

        TracLinkAnnotator annotator = new TracLinkAnnotator();
        annotator.annotate(TRAC_URL, markupText);

        assertEquals(expectedAnnotatedText, markupText.toString());
    }
}
