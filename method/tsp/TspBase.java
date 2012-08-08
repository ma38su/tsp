package method.tsp;

import model.Node;

public class TspBase {
	public double[][] getCostTable(Node[] nodes) {
		double[][] table = new double[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				table[i][j] = nodes[i].getDistance(nodes[j]);
			}
		}
		return table;
	}
}
