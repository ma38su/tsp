package controller;

import gui.DemoPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Panelに対するマウスリスナーの実装クラス
 * @author MASAYASU Fujiwara
 */
public class Controller implements MouseListener {
	private DemoPanel panel;
	public Controller(DemoPanel panel) {
		this.panel = panel;
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		this.panel.add(x, y);
	}

	public void mouseReleased(MouseEvent e) {
	}
}
