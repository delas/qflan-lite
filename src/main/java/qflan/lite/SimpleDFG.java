package qflan.lite;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import qflan.lite.graphviz.Dot;
import qflan.lite.graphviz.DotEdge;
import qflan.lite.graphviz.DotNode;
import qflan.lite.utils.XLogHelper;

public class SimpleDFG {

	private XLog log = null;
	private Map<String, Map<String, Integer>> dfg = null;
	
	public SimpleDFG(XLog log) {
		this.log = log;
	}
	
	public void compute() {
		dfg = new HashMap<>();
		for (XTrace trace : log) {
			String source = null;
			for (XEvent event : trace) {
				String target = XLogHelper.getName(event);
				if (source != null) {
					if (!dfg.containsKey(source)) {
						dfg.put(source, new HashMap<>());
					}
					if (!dfg.get(source).containsKey(target)) {
						dfg.get(source).put(target, 1);
					} else {
						dfg.get(source).put(target, dfg.get(source).get(target) + 1);
					}
				}
				source = target;
			}
		}
	}
	
	public Dot toDot(int minFrequency) {
		Dot dot = new Dot();
		dot.setOption("rankdir", "TB");
		dot.setOption("outputorder", "edgesfirst");

		if (dfg == null) {
			return dot;
		}
		
		Map<String, DotNode> nodes = new HashMap<>();
		for (String source : dfg.keySet()) {
			for (String target : dfg.get(source).keySet()) {
				int freq = dfg.get(source).get(target);
				if (freq >= minFrequency) {
					if (!nodes.containsKey(source)) {
						nodes.put(source, makeActivityNode(dot, source));
					}
					if (!nodes.containsKey(target)) {
						nodes.put(target, makeActivityNode(dot, target));
					}
					makeEdge(dot, nodes.get(source), nodes.get(target), freq);
				}
			}
		}
		
		return dot;
	}
	
	private static DotNode makeActivityNode(Dot dot, String name) {
		DotNode dotNode = dot.addNode(name);
		dotNode.setOption("shape", "box");
		dotNode.setOption("style", "rounded,filled");
		dotNode.setOption("fontname", "Arial");
		return dotNode;
	}
	
	private static DotEdge makeEdge(Dot dot, DotNode source, DotNode target, int frequency) {
		DotEdge edge = dot.addEdge(source, target);
		edge.setLabel("" + frequency);
		edge.setOption("fontname", "Arial");
		return edge;

	}
}
