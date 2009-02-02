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
