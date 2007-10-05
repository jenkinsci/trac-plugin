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

        for (TracLinkAnnotator.LinkMarkup markup : TracLinkAnnotator.MARKUPS) {
            markup.process(markupText, TRAC_URL);
        }

        assertEquals(expectedAnnotatedText, markupText.toString());
    }
}
