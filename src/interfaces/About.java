package interfaces;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class About extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton btnOk;

	/** Launch the application */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Create the dialog */
	public About() {
		initiateComponents();
		initiateEvents();
	}

	private void initiateComponents() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/resources/sudoku_icon_16.png")));
		setTitle("About");
		setBounds(100, 100, 430, 359);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		btnOk = new JButton("OK");
		btnOk.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblTheMailSystem = new JLabel("The Mail System");
		lblTheMailSystem.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JTextPane txtpnVersionCreator = new JTextPane();
		txtpnVersionCreator.setEditable(false);
		txtpnVersionCreator.setEnabled(false);
		txtpnVersionCreator.setBackground(SystemColor.menu);
		txtpnVersionCreator.setText("Version:\t1.100\r\nCreator:\tShaked Fish\r\nEmaill:\tEightLord@gmail.com\r\nCredit:\tDoor, Star icons was made by Freepik:\r\n\thttps://www.flaticon.com/authors/freepik\r\n\tPlus icon was made by Elegant Themes:\r\n\thttps://www.flaticon.com/authors/elegant-themes\r\n\tQuestion mark icon was made by Zurb:\r\n\thttps://www.flaticon.com/authors/zurb\r\n\tCross icon was made by Google:\r\n\thttps://www.flaticon.com/authors/google\r\n\r\nCopyright 2017 Shaked Fish. All rights reserved.");
		txtpnVersionCreator.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblTheMailSystem))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(170)
							.addComponent(btnOk))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(txtpnVersionCreator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTheMailSystem)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtpnVersionCreator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(btnOk)
					.addContainerGap(34, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
	}

	private void initiateEvents() {
		// okButton has been clicked.
		// Will close the dialog window.
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
}
