package qflan.lite.graphviz;

import java.io.File;
import java.io.IOException;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class GraphExporter {

	public static void exportSVG(Dot dot, File destination) throws IOException {
		export(dot, Format.SVG, destination);
	}
	
	private static void export(Dot dot, Format format, File destination) throws IOException {
		Graphviz.fromString(dot.toString()).render(Format.SVG).toFile(destination);
	}
}