package hudson.plugins.trac;

import hudson.MarkupText;

import junit.framework.TestCase;


public class TracLinkAnnotatorTest extends TestCase {

    private static final String TRAC_URL = "http://trac/";

    public void testWikiLinkSyntax() {
        assertAnnotatedTextEquals("Nothing here.", "Nothing here.");
        assertAnnotatedTextEquals("Text with WikiLink.", "Text with <a href='" + TRAC_URL + "wiki/WikiLink'>WikiLink</a>.");
    }

    private void assertAnnotatedTextEquals(String originalText, String expectedAnnotatedText) {
        MarkupText markupText = new MarkupText(originalText);

        TracLinkAnnotator annotator = new TracLinkAnnotator();
        annotator.annotate(TRAC_URL, markupText);

        assertEquals(expectedAnnotatedText, markupText.toString());
    }
}
