package top.jalva.jalvafx.node;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import top.jalva.jalvafx.style.CssStyle;

public class Glyphs {

	public static Glyph createCheckInfoGlyph() {
		return Glyphs.createGlyph(FontAwesome.Glyph.CHECK, CssStyle.TEXT_FILL_INFO);
	}

	public static Glyph createExclamationDangerGlyph() {
		return Glyphs.createGlyph(FontAwesome.Glyph.EXCLAMATION, CssStyle.TEXT_FILL_DANGER);
	}

	public static Glyph createSearchGrayGlyph() {
		return Glyphs.createGlyph(FontAwesome.Glyph.SEARCH, CssStyle.TEXT_FILL_SHY);
	}

	public static Glyph createGlyph(FontAwesome.Glyph glyph) {
		return Glyphs.createGlyph(glyph, "");
	}

	/**
	 * @param cssStyle
	 *            String or {@link CssStyle} can be used
	 */
	public static Glyph createGlyph(FontAwesome.Glyph glyph, String cssStyle) {

		if (cssStyle == null)
			cssStyle = "";
		GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
		Glyph resultGlyph = fontAwesome.create(glyph);
		resultGlyph.setStyle(CssStyle.FONT_FAMILY_FONTAWESOME);
		if (cssStyle != null && !cssStyle.isEmpty())
			resultGlyph.setStyle(cssStyle);

		return resultGlyph;
	}

	public static Glyph createPasteGlyph() {
		return Glyphs.createGlyph(FontAwesome.Glyph.PASTE, CssStyle.TEXT_FILL_POSTED);
	}
}
