package method.tsp;

import gui.DemoPanel;
import java.util.List;
import model.Node;


/**
 * 巡回セールスマン問題の構築法のインターフェースです。
 * 構築法では何もないところから巡回路を求めます。
 * @author ma38su
 */
public interface TspConstruction {
	public List<Node> method(DemoPanel panel);
}
