package application;

import java.awt.EventQueue;

import javax.swing.UIManager;

import interfaces.MainWin;

public class App {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWin fWelcome = new MainWin();
					fWelcome.setLocationRelativeTo(null);
					fWelcome.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
