package method.tsp;

import java.util.List;
import model.Node;

/**
 * 改善法を適用しないためのダミーのクラス
 * @author ma38su
 */
public class NoImprovement implements TspImprovement {
	public boolean method(List<Node> route) {
		return false;
	}
	public boolean method(int[] route, double[][] table) {
		return false;
	}

	@Override
	public String toString() {
		return "適用しない";
	}
}
