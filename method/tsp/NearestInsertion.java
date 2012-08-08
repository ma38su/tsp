package method.tsp;

import gui.DemoPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import model.Node;


/**
 * nearest insertionによる巡回セールスマン問題の構築法です。
 * @author ma38su
 */
public class NearestInsertion implements TspConstruction {
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
				Node node0 = route.get(route.size() - 1);
				for (int n1 = 0; n1 < route.size(); n1++) {
					Node node1 = route.get(n1);
					for (Node n : nodes) {
						double distance = node1.getDistance(n);
						if (min > distance) {
							min = distance;
							int n2 = (n1 + 1) % route.size();
							if (node0.getDistance(n) > n.getDistance(route.get(n2))) {
								insertion = n2;
							} else {
								insertion = n1;
							}
							nearest = n;
						}
					}
					node0 = node1;
				}
				assert insertion != -1;
				assert nearest != null;
				node = nearest;
			}
		}
		return route;
	}

	@Override
	public String toString() {
		return "nearest insertion";
	}
}
