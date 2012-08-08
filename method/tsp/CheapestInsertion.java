package method.tsp;

import gui.DemoPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import model.Node;


/**
 * cheapest insertionによる巡回セールスマン問題の構築法です。
 * @author ma38su
 */
public class CheapestInsertion implements TspConstruction {
	public List<Node> method(DemoPanel panel) {
		Set<Node> nodes = new HashSet<Node>(panel.getNodes());
		List<Node> route = new ArrayList<Node>(nodes.size() + 1);
		Iterator<Node> itr = nodes.iterator();
		if (itr.hasNext()) {
			Node node = itr.next();
			int insertion = 0;
			while (!nodes.isEmpty()) {
				route.add(insertion, node);
				if (nodes.remove(node) && nodes.isEmpty()) {
					break;
				}
				panel.set(route);
				Node nearest = null;
				insertion = -1;
				double min = Double.POSITIVE_INFINITY;
				Node n0 = route.get(route.size() - 1);
				for (int i = 0; i < route.size(); i++) {
					Node n1 = route.get(i);
					for (Node n : nodes) {
						double distance = n0.getDistance(n) + n.getDistance(n1) - n0.getDistance(n1);
						if (min > distance) {
							min = distance;
							insertion = i;
							nearest = n;
						}
					}
					n0 = n1;
				}
				assert insertion != -1 && nearest != null;
				node = nearest;
			}
		}
		return route;
	}

	@Override
	public String toString() {
		return "cheapest insertion";
	}
}
