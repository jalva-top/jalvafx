package top.jalva.jalvafx.controller.interfaces;

import org.controlsfx.glyphfont.Glyph;

import javafx.util.Pair;

public interface FxmlViewInterface {
	String getTitle();

	String getFxmlFile();

	Pair<Integer, Integer> getSize();

	Glyph getGlyph(String cssStyle);

	boolean hasGlyph();

	boolean hasDetailPane();
}
