package interfaces;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.SystemColor;

public class Tutorial extends JDialog {
	private JButton okButton;
	private JPanel buttonPane;

	/** Launch the application */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			Tutorial dialog = new Tutorial();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Create the dialog */
	public Tutorial() {
		initiateComponents();
		initiateEvents();
	}
	
	// Create the window
	private void initiateComponents() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Tutorial.class.getResource("/resources/sudoku_icon_16.png")));
		setTitle("Tutorial");
		setResizable(false);
		setBounds(100, 100, 646, 362);
		{
			buttonPane = new JPanel();
			{
				okButton = new JButton("OK");
				okButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(277)
						.addComponent(okButton)
						.addContainerGap(285, Short.MAX_VALUE))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addComponent(okButton)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		
		JLabel lblHowToUse = new JLabel("How to use:");
		lblHowToUse.setFont(new Font("Tahoma", Font.BOLD, 22));
		
		JTextArea txtrInsertThe = new JTextArea();
		txtrInsertThe.setBackground(SystemColor.menu);
		txtrInsertThe.setDisabledTextColor(Color.BLACK);
		txtrInsertThe.setEditable(false);
		txtrInsertThe.setEnabled(false);
		txtrInsertThe.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtrInsertThe.setText("1. Insert the numbers in the text fields.\r\nTip: you can type zero as empty field.\r\n2. Click the solve button to see it go!\r\n\r\nColors:\r\n* RED - Input error: dublicates numbers.\r\n* BLUE - Original numbers.\r\n* YELLOW - fields that was solved using backtracking method.\r\n* GREEN - fields that was solved using logic method.\r\n\r\nUsing Copy/Paste options:\r\nCopy to excel - After clicking this button, go to the excel file and paste it. it will fill up 9 x 9 cells.\r\nPaste from excel - Copy 9 x 9 cells from the excel file (cells that have bad input will not get inserted,\r\nfor example: 1753 won't be copied). After copying click this button to paste it in the Sudoku Solver.");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblHowToUse)
						.addComponent(txtrInsertThe)
						.addComponent(buttonPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblHowToUse)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtrInsertThe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(buttonPane, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addGap(190))
		);
		getContentPane().setLayout(groupLayout);
	}
	
	// Event methods
	private void initiateEvents() {
		// okButton has been clicked.
		// Will close the window.
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
}
