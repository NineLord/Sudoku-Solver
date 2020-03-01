package interfaces;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import net.miginfocom.swing.MigLayout;

/*
 * To do list:
 * 1. implement isUserInputing in Solve button by creating different method to disable/enable all text fields and then it can be used there & at the Paste from excel button.
 * 2. Delete the method get() from the object Cell and then fix Solve button (will make a it a bit more effective).
 * 3. Pizza~
 */

public class MainWin extends JFrame {

	private static final long serialVersionUID = -1546289961029722056L;
	private JPanel contentPane;
	private JPanel pNumbers;
	private JTextField[][] tfArray = new JTextField[9][9];
	private ExecutorService threadPool = Executors.newFixedThreadPool(1000);
	private JMenuItem mntmAbout;
	private JMenuItem mntmHelp;
	private JMenuItem mntmClearFilds;
	private JMenuItem mntmExample;
	private JMenuItem mntmExit;
	private JMenuItem mntmNew;
	private JMenuItem mntmCopyToExcel;
	private JMenuItem mntmPasteFromExcel;
	private JProgressBar progressBar;
	private int iProgress = 0;
	private JButton btnSolve;
	
	private Cell[][] cMatrix = new Cell[9][9];
	
	private static final Color SOLVER_COLOR = new Color(0, 100, 0);
	private static final Color BACKTRACKING_COLOR = new Color(194, 162, 0);
	private static final Color ERROR_COLOR = Color.RED;
	private static final Color START_COLOR = Color.BLUE;
	
	private volatile boolean isUserInputing = true; 

	/** Launch the application */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWin frame = new MainWin();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Create the frame */
	public MainWin() {
		initiateComponents();
		initiateEvents();
	}

	private void initiateComponents() {
		// Frame
		setTitle("Sudoku Solver");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWin.class.getResource("/resources/sudoku_icon_16.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 531, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow,center]", "[][500px:500px:500px,grow][grow]"));
		
		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmNew = new JMenuItem("New Sudoku");
		mntmNew.setIcon(new ImageIcon(MainWin.class.getResource("/resources/mathematical-addition-sign_16.png")));
		mnFile.add(mntmNew);
		
		JSeparator sepTop = new JSeparator();
		mnFile.add(sepTop);
		
		mntmCopyToExcel = new JMenuItem("Copy to Excel...");
		mnFile.add(mntmCopyToExcel);
		
		mntmPasteFromExcel = new JMenuItem("Paste from Excel...");
		mnFile.add(mntmPasteFromExcel);
		
		JSeparator sepBottom = new JSeparator();
		mnFile.add(sepBottom);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(new ImageIcon(MainWin.class.getResource("/resources/open-exit-door_16.png")));
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		mntmExample = new JMenuItem("Insert example");
		mntmExample.setIcon(new ImageIcon(MainWin.class.getResource("/resources/favorites_16.png")));
		mnEdit.add(mntmExample);
		
		mntmClearFilds = new JMenuItem("Clear fields");
		mntmClearFilds.setIcon(new ImageIcon(MainWin.class.getResource("/resources/clear-button_16.png")));
		mnEdit.add(mntmClearFilds);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmHelp = new JMenuItem("How to use");
		mntmHelp.setIcon(new ImageIcon(MainWin.class.getResource("/resources/question-mark_16.png")));
		mnHelp.add(mntmHelp);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.setIcon(new ImageIcon(MainWin.class.getResource("/resources/sudoku_icon_16.png")));
		mnHelp.add(mntmAbout);

		// Title
		JLabel lblSudokuSolver = new JLabel("Sudoku - Solver");
		lblSudokuSolver.setFont(new Font("Tahoma", Font.BOLD, 22));
		lblSudokuSolver.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblSudokuSolver, "cell 0 0");

		// Sudoku's numbers panel
		pNumbers = new JPanel();
		pNumbers.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(pNumbers, "cell 0 1,grow");
		pNumbers.setLayout(new MigLayout("",
				"[48:48:48][48:48:48][48:48:48][5:5:5][48:48:48][48:48:48][48:48:48][5:5:5][48:48:48][48:48:48][48:48:48]",
				"[48:48:48][48:48:48][48:48:48][5:5:5][48:48:48][48:48:48][48:48:48][5:5:5][48:48:48][48:48:48][48:48:48]"));

		// Create the text fields and separators in pNumbers
		JSeparator[][] sepArray = new JSeparator[2][2];
		int iCountSepVer = 0, iCountSepHor = 0;
		int iRow = 0,iCol = 0, iRowTemp = 0, iColTemp = 0;
		int iRowReal = 0, iColReal = 0, iRowRealTemp = 0, iColRealTemp = 0;
		for (int iRowBox = 0; iRowBox < 3; iRowBox++) {
			for (int iColBox = 0; iColBox < 3; iColBox++) {
				//Create the box
				for (int iRowSmall = 0; iRowSmall < 3; iRowSmall++) {
					for (int iColSmall = 0; iColSmall < 3; iColSmall++) {
						tfArray[iRow][iCol] = new JTextField(2);
						//tfArray[iRow][iCol].setText(Integer.toString(iRow) + Integer.toString(iCol));
						tfArray[iRow][iCol].setFont(new Font("Tahoma", Font.PLAIN, 14));
						tfArray[iRow][iCol].setHorizontalAlignment(SwingConstants.CENTER);
						pNumbers.add(tfArray[iRow][iCol], String.format("cell %d %d, grow", iColReal, iRowReal));
						iCol++;
						iColReal++;
					}
					iCol = iColTemp;
					iColReal = iColRealTemp;
					iRow++;
					iRowReal++;
				}
				if (iRowBox == 0 && iColBox < 2) {
					sepArray[0][iCountSepVer] = new JSeparator(JSeparator.VERTICAL);
					sepArray[0][iCountSepVer].setBorder(new LineBorder(new Color(0, 0, 0), 5));
					pNumbers.add(sepArray[0][iCountSepVer], String.format("cell %d 0 1 11, grow", (iColBox * 4 + 3)));
					iCountSepVer++;
				}
				iColTemp += 3;
				iCol = iColTemp;
				iColRealTemp += 4;
				iColReal = iColRealTemp;
				iRow = iRowTemp;
				iRowReal = iRowRealTemp;
			}
			if (iRowBox == 2)
				break;
			else {
				sepArray[1][iCountSepHor] = new JSeparator(JSeparator.HORIZONTAL);
				sepArray[1][iCountSepHor].setBorder(new LineBorder(new Color(0, 0, 0), 5));
				pNumbers.add(sepArray[1][iCountSepHor], String.format("cell 0 %d 11 1, grow", (iRowBox * 4 + 3)));
				iCountSepHor++;
				iRowTemp += 3;
				iRow = iRowTemp;
				iRowRealTemp += 4;
				iRowReal = iRowRealTemp;
				
				iCol = 0; iColTemp = 0; iColReal = 0; iColRealTemp = 0;
			}
		}

		// Options buttons panel
		JPanel pButtons = new JPanel();
		contentPane.add(pButtons, "cell 0 2,grow");
		pButtons.setLayout(null);
		
		btnSolve = new JButton("Solve it!");
		btnSolve.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSolve.setBounds(10, 20, 480, 50);
		pButtons.add(btnSolve);
		
		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setValue(45);
		progressBar.setBounds(10, 85, 481, 50);
		progressBar.setVisible(false);
		pButtons.add(progressBar);

	}

	private void initiateEvents() {
		// Add DocumentFilter to the textFields
		// & ActionListener to the ENTER key
		// & KeyListener to the DELETE or BACKSPACE keys
		ActionListener alEnterKey = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.getFocusOwner().transferFocus();
			}
		};
		PlainDocument doc;
		Action beep;
		for (int iRow = 0; iRow < 9; iRow++) {
			for (int iCol = 0; iCol < 9; iCol++) {
				tfArray[iRow][iCol].addActionListener(alEnterKey);
				tfArray[iRow][iCol].addKeyListener(new klDelete(tfArray[iRow][iCol]));
				beep = tfArray[iRow][iCol].getActionMap().get(DefaultEditorKit.deletePrevCharAction);
				tfArray[iRow][iCol].getInputMap().put(KeyStroke.getKeyStroke("DELETE"), new DelActionWrapper(tfArray[iRow][iCol], beep));
				tfArray[iRow][iCol].getActionMap().put(DefaultEditorKit.deletePrevCharAction, new DelActionWrapper(tfArray[iRow][iCol], beep));
				doc = (PlainDocument) tfArray[iRow][iCol].getDocument();
				doc.setDocumentFilter(new IntFilter( tfArray[iRow][iCol] ));
			}
		}
		
		// Give Action to the Edit>Clear Fields button
		// Give Action to the File>New button
		ActionListener alClear = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Publisher> swClear = new SwingWorker<Void, Publisher>() {

					@Override
					protected Void doInBackground() throws Exception {
						Publisher peTemp;
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								peTemp = new Publisher(iRow, iCol, "", Color.BLACK);
								publish(peTemp);
							}
						}
						return null;
					}

					@Override
					protected void process(List<Publisher> chunks) {
						int[] iIndex = new int[2];
						for (Publisher peNow : chunks) {
							iIndex[0] = peNow.getRow();
							iIndex[1] = peNow.getCol();
							tfArray[iIndex[0]][iIndex[1]].setText(peNow.getString());
							tfArray[iIndex[0]][iIndex[1]].setDisabledTextColor(peNow.getColor());
							tfArray[iIndex[0]][iIndex[1]].setForeground(peNow.getColor());
						}
					}

				};
				threadPool.submit(swClear);
			}
		};
		mntmClearFilds.addActionListener(alClear);
		mntmNew.addActionListener(alClear);
		
		// Give Action to the Edit>Insert Example button
		mntmExample.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Publisher> swExample = new SwingWorker<Void, Publisher>() {

					@Override
					protected Void doInBackground() throws Exception {
						Publisher peTemp;
						int iIndex = 0;
						String strExample = "";
						String[] strOptions = new String[] { "Solve Row", "Solve Column", "Solve Box", "Solve Backtracking", "Solve Full Game", "Solved Game", "Defective Game", "Cancel" };
						int iResponse = JOptionPane.showOptionDialog(null, "Which example should the program import?", "Choose Example...", JOptionPane.DEFAULT_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, strOptions, strOptions[0]);
						switch ( iResponse ) {
						case 0: // Row
							strExample = "806937152319625847752184963285713694463859271971246385127598436638471529594362718";
							break;
						case 1: // Col
							strExample = "806907152319625847752184963285713694463859271971246385127598436638471529594362718";
							break;
						case 2: // Box
							strExample = "806907152319625847752184963205713694463859271971246385127598436638471529594362718";
							break;
						case 3: // BackTracking
							strExample = "000000000000000000000000000285713694463859271971246385127598436638471529594362718";
							break;
						case 4: // Full Game
							strExample = "800930002009000040702100960200000090060000070070006005027008406030000500500062008";
							break;
						case 5: // Already solved game
							strExample = "846937152319625847752184963285713694463859271971246385127598436638471529594362718";
							break;
						case 6: // Game with errors
							strExample = "808596201400200930090048000100905060000060000050702009000410070046009003302657004";
							break;
						case 7: // cancel
							return null;
							//break;
						}
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								String strNum = Character.toString( strExample.charAt(iIndex));
								if ( Integer.parseInt(strNum) == 0 )
									strNum = "";
								peTemp = new Publisher(iRow, iCol, strNum, Color.BLACK);
								publish(peTemp);
								iIndex++;
							}
						}
						return null;
					}

					@Override
					protected void process(List<Publisher> chunks) {
						int[] iIndex = new int[2];
						for (Publisher peNow : chunks) {
							iIndex[0] = peNow.getRow();
							iIndex[1] = peNow.getCol();
							tfArray[iIndex[0]][iIndex[1]].setText(peNow.getString());
							tfArray[iIndex[0]][iIndex[1]].setDisabledTextColor(peNow.getColor());
							tfArray[iIndex[0]][iIndex[1]].setForeground(peNow.getColor());
						}
					}
					
				};
				threadPool.submit(swExample);
			}
		});
		
		// Give Action to the File>Exit button
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int iAnswer = JOptionPane.showConfirmDialog(MainWin.this, "Are you sure you want to quit?", "Exit...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (iAnswer == JOptionPane.YES_OPTION)
					dispose();
			}
		});
		
		// Give Action to the Help>About button
		mntmAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				About fAbout = new About();
				fAbout.setModal(true);
				fAbout.setLocation((int)(getLocationOnScreen().getX()+(getSize().getWidth()-fAbout.getSize().getWidth())/2),
						(int)(getLocationOnScreen().getY()+(getSize().getHeight()-fAbout.getSize().getHeight())/2));
				fAbout.setVisible(true);
			}
		});
		
		// Give Action to the Help>How to use button
		mntmHelp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				Tutorial fTut = new Tutorial();
				fTut.setModal(true);
				fTut.setLocation((int)(getLocationOnScreen().getX()+(getSize().getWidth()-fTut.getSize().getWidth())/2),
						(int)(getLocationOnScreen().getY()+(getSize().getHeight()-fTut.getSize().getHeight())/2));
				fTut.setVisible(true);
			}
		});
		
		// Give Action to the File>Copy To Excel button
		mntmCopyToExcel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Integer, Publisher> swCopy = new SwingWorker<Integer, Publisher>() {

					@Override
					protected Integer doInBackground() throws Exception {
						String[] strOptions = new String[] { "Stay empty", "be filled with field's options" };
						int iResponse = JOptionPane.showOptionDialog(null, "Empty fields should...", "Choose copy method...", JOptionPane.DEFAULT_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, strOptions, strOptions[0]);
						switch ( iResponse ) {
						case 0:
							copyFromField();
							break;
						case 1:
							if ( fillOptions() )
								copyFromMatrix();
							else
								return 1;
							break;
						}
						return 0;
					}
					
					@Override
					protected void process(List<Publisher> chunks) {
						int[] iInfo = new int[2];
						for (Publisher peNow : chunks) {
							switch ( peNow.getType() ) {
							case Publisher.UPDATE_COLOR:
								iInfo[0] = peNow.getRow();
								iInfo[1] = peNow.getCol();
								tfArray[iInfo[0]][iInfo[1]].setForeground(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setDisabledTextColor(peNow.getColor());
								break;
								default:
									System.err.println("---ERROR 4---");
									break;
							}
						}
					}

					@Override
					protected void done() {
						try {
							int iRes = get();
							switch (iRes) {
							case 0: // Sudoku has been copied
								JOptionPane.showMessageDialog(MainWin.this, "Data copied!\n Go to your excel file and paste it.");
								break;
							case 1: // Duplicate numbers
								JOptionPane.showMessageDialog(MainWin.this, "There are duplicate numbers in the red fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
								break;
							case 3:
								System.err.println("---ERROR MESSAGE: RETURN 3---");
								break;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						super.done();
					}

					private void copyFromField() {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						StringBuilder stringBuilder = new StringBuilder();
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								stringBuilder.append( tfArray[iRow][iCol].getText().equals("0") ? "" : tfArray[iRow][iCol].getText() );
								stringBuilder.append( "\t" );
							}
							stringBuilder.setLength( stringBuilder.length() - 1 );
							stringBuilder.append( "\n" );
						}
						clipboard.setContents(new StringSelection( stringBuilder.toString() ), null);
					}
					
					private void copyFromMatrix() {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						Iterator<Integer> iIterator;
						int iValue;
						StringBuilder stringBuilder = new StringBuilder();
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								iIterator = cMatrix[iRow][iCol].iterator();
								while ( iIterator.hasNext() ) {
									iValue = iIterator.next();
									if ( iValue != 0 )
										stringBuilder.append( Integer.toString(iValue) );
								}
								stringBuilder.append( "\t" );
							}
							stringBuilder.setLength( stringBuilder.length() - 1 );
							stringBuilder.append( "\n" );
						}
						clipboard.setContents(new StringSelection( stringBuilder.toString() ), null);
					}
					
					private boolean fillOptions() {
						// Initiate matrix
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								if ( tfArray[iRow][iCol].getText().equals("") || tfArray[iRow][iCol].getText().equals("0") )
									cMatrix[iRow][iCol] = new Cell();
								else 
									cMatrix[iRow][iCol] = new Cell( Integer.valueOf( tfArray[iRow][iCol].getText() ) );
							}
						}
						// Looking for dublicates (mistakes in the sudoku)
						int[] iTemp = new int[3];
						boolean isSolvable = true;
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								// Looking for duplicates in the same row or col
								for (int iCheckRowCol = 0; iCheckRowCol < 9; iCheckRowCol++) {
									iTemp[0] = cMatrix[iRow][iCol].get();
									iTemp[1] = cMatrix[iCheckRowCol][iCol].get();
									iTemp[2] = cMatrix[iRow][iCheckRowCol].get();
									if ( (iTemp[0] != 0) && (iTemp[0] == iTemp[1] && iRow != iCheckRowCol) ) {
										publish(new Publisher(iRow, iCol, ERROR_COLOR));
										publish(new Publisher(iCheckRowCol, iCol, ERROR_COLOR));
										isSolvable = false;
									}
									if ( (iTemp[0] != 0) && (iTemp[0] == iTemp[2] && iCol != iCheckRowCol) ) {
										publish(new Publisher(iRow, iCol, ERROR_COLOR));
										publish(new Publisher(iRow, iCheckRowCol, ERROR_COLOR));
										isSolvable = false;
									}
								}
								// Looking for duplicates in the same box
								for (int iCheckRow = (iRow/3)*3; iCheckRow < (iRow/3)*3+3; iCheckRow++) {
									for (int iCheckCol = (iCol/3)*3; iCheckCol < (iCol/3)*3+3; iCheckCol++) {
										iTemp[0] = cMatrix[iRow][iCol].get();
										iTemp[1] = cMatrix[iCheckRow][iCheckCol].get();
										if ( iTemp[0] != 0 && iTemp[0] == iTemp[1] && !(iRow == iCheckRow && iCol == iCheckCol) ) {
											publish(new Publisher(iRow, iCol, ERROR_COLOR));
											isSolvable = false;
										}
									}
								}
							}
						}
						if (!isSolvable)
							return false;
						
						// Fill empty indexes with options
						int iLoopOverFlow = 0, iTempIndex, iFinalIndex = 0, iTrueCounter;
						boolean bSudokuUpdated, bMatrixFilledUp;
						Boolean[][] bOptions;
						Boolean bTemp;
						ListIterator<Boolean> bIterator;
						Iterator<Integer> iIterator;
						bSudokuUpdated = false;
						bMatrixFilledUp = true;
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								bOptions = new Boolean[][] { { true, true, true, true, true, true, true, true, true }, // Row
										{ true, true, true, true, true, true, true, true, true }, // Col
										{ true, true, true, true, true, true, true, true, true } // Box
								};
								if (cMatrix[iRow][iCol].size() != 1) {
									for (;;) {
										// Check Options in the row
										for (int iRowIterate = 0; iRowIterate < 9; iRowIterate++) {
											if (cMatrix[iRowIterate][iCol].size() == 1) {
												iIterator = cMatrix[iRowIterate][iCol].iterator();
												bOptions[0][iIterator.next() - 1] = false;
											} else
												bMatrixFilledUp = false;
										}
										// Check Options in the col
										for (int iColIterate = 0; iColIterate < 9; iColIterate++) {
											if (cMatrix[iRow][iColIterate].size() == 1) {
												iIterator = cMatrix[iRow][iColIterate].iterator();
												bOptions[1][iIterator.next() - 1] = false;
											} else
												bMatrixFilledUp = false;
										}
										// Check Options in the box
										for (int iCheckRow = (iRow / 3) * 3; iCheckRow < (iRow / 3) * 3
												+ 3; iCheckRow++) {
											for (int iCheckCol = (iCol / 3) * 3; iCheckCol < (iCol / 3) * 3
													+ 3; iCheckCol++) {
												if (cMatrix[iCheckRow][iCheckCol].size() == 1) {
													iIterator = cMatrix[iCheckRow][iCheckCol].iterator();
													bOptions[2][iIterator.next() - 1] = false;
												} else
													bMatrixFilledUp = false;
											}
										}
										// Sync Row & Col & Box results to one
										cMatrix[iRow][iCol].clear();
										for (int iOptions = 0; iOptions < 9; iOptions++) {
											if (bOptions[0][iOptions] && bOptions[1][iOptions]
													&& bOptions[2][iOptions]) {
												cMatrix[iRow][iCol].add(iOptions + 1);
											}
										}
										break;
									}
								}
							}
						}
						return true;
					}
					
				};
				threadPool.submit(swCopy);
			}
		});
		
		// Give Action to the File>Paste From Excel button
		mntmPasteFromExcel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Boolean, Publisher> swPaste = new SwingWorker<Boolean, Publisher>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						String strResult = (String) clipboard.getData(DataFlavor.stringFlavor);
						String[] strCells = strResult.split("[\t\n]");
						if (strCells.length != 81)
							return false;
						int iIndex = 0;
						isUserInputing = false;
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								publish(new Publisher(iRow, iCol, strCells[iIndex].length() == 1 ? strCells[iIndex] : "", Color.BLACK));
								iIndex++;
							}
						}
						isUserInputing = true;
						return true;
					}

					@Override
					protected void process(List<Publisher> chunks) {
						int[] iInfo = new int[2];
						for (Publisher peNow : chunks) {
							switch ( peNow.getType() ) {
							case Publisher.UPDATE_FIELD:
								iInfo[0] = peNow.getRow();
								iInfo[1] = peNow.getCol();
								tfArray[iInfo[0]][iInfo[1]].setForeground(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setDisabledTextColor(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setText(peNow.getString());
								break;
								default:
									System.err.println("---ERROR 4---");
									break;
							}
						}
					}
					
					@Override
					protected void done() {
						try {
							if (get()) {
								JOptionPane.showMessageDialog(MainWin.this, "Data pasted!\nNote: fields that had bad input, wasn't inserted.");
							} else
								JOptionPane.showMessageDialog(MainWin.this, "The software couldn't paste the copy data.\n Please copy 9 x 9 cells.", "Input Error", JOptionPane.ERROR_MESSAGE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						super.done();
					}
				};
				threadPool.submit(swPaste);
			}
		});
		
		// Give Action to the Solve it! button
		btnSolve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Integer, Publisher> swSolver = new SwingWorker<Integer, Publisher>() {

					@Override
					protected Integer doInBackground() throws Exception {
						boolean isSolvable = true;
						
						// Convert all the text fields into one array of integers
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								if ( tfArray[iRow][iCol].getText().equals("") || tfArray[iRow][iCol].getText().equals("0") ) {
									cMatrix[iRow][iCol] = new Cell();
									publish(new Publisher(iRow, iCol, "", Color.BLACK));
								}
								else 
									cMatrix[iRow][iCol] = new Cell( Integer.valueOf( tfArray[iRow][iCol].getText() ) );
							}
						}
						
						// Check if the inserted Sudoku has any errors (duplicate numbers in the same row, col, box)
						//iSudoku[iRow][iCol]
						int[] iTemp = new int[3];
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								// Looking for duplicates in the same row or col
								for (int iCheckRowCol = 0; iCheckRowCol < 9; iCheckRowCol++) {
									iTemp[0] = cMatrix[iRow][iCol].get();
									iTemp[1] = cMatrix[iCheckRowCol][iCol].get();
									iTemp[2] = cMatrix[iRow][iCheckRowCol].get();
									if ( (iTemp[0] != 0) && (iTemp[0] == iTemp[1] && iRow != iCheckRowCol) ) {
										publish(new Publisher(iRow, iCol, ERROR_COLOR));
										publish(new Publisher(iCheckRowCol, iCol, ERROR_COLOR));
										isSolvable = false;
									}
									if ( (iTemp[0] != 0) && (iTemp[0] == iTemp[2] && iCol != iCheckRowCol) ) {
										publish(new Publisher(iRow, iCol, ERROR_COLOR));
										publish(new Publisher(iRow, iCheckRowCol, ERROR_COLOR));
										isSolvable = false;
									}
								}
								// Looking for duplicates in the same box
								for (int iCheckRow = (iRow/3)*3; iCheckRow < (iRow/3)*3+3; iCheckRow++) {
									for (int iCheckCol = (iCol/3)*3; iCheckCol < (iCol/3)*3+3; iCheckCol++) {
										iTemp[0] = cMatrix[iRow][iCol].get();
										iTemp[1] = cMatrix[iCheckRow][iCheckCol].get();
										if ( iTemp[0] != 0 && iTemp[0] == iTemp[1] && !(iRow == iCheckRow && iCol == iCheckCol) ) {
											publish(new Publisher(iRow, iCol, ERROR_COLOR));
											isSolvable = false;
										}
									}
								}
							}
						}
						if (!isSolvable)
							return 1;
						
						// Disable all text fields
						// & Change the color of all of the original fields to BLUE
						// & Show progress bar and increment it after done this part
						publish(new Publisher(true));
						iProgress = 19;
						publish(new Publisher(iProgress));
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								if ( (cMatrix[iRow][iCol].get() == 0) )
									publish(new Publisher(iRow, iCol, Color.BLACK, false));
								else {
									publish(new Publisher(iRow, iCol, START_COLOR, false));
									iProgress++;
									publish(new Publisher(iProgress));
								}
							}
						}
						
						// Loop through the Sudoku, check each field option
						// Field that has only one option to be will be updated
						// & Increment progress bar
						// & Image of the current Sudoku will be added the the history
						// If looped through the Sudoku 3 time without changes, will end the loop with error message of failure
						int iLoopOverFlow = 0, iTempIndex, iFinalIndex = 0, iTrueCounter;
						boolean bSudokuUpdated, bMatrixFilledUp;
						Boolean[][] bOptions;
						Boolean bTemp;
						ListIterator<Boolean> bIterator;
						Iterator<Integer> iIterator;
						do {
							bSudokuUpdated = false;
							bMatrixFilledUp = true;
							for (int iRow = 0; iRow < 9; iRow++) {
								for (int iCol = 0; iCol < 9; iCol++) {
									bOptions = new Boolean[][] {
										{true, true, true, true, true, true, true, true, true}, // Row
										{true, true, true, true, true, true, true, true, true}, // Col
										{true, true, true, true, true, true, true, true, true}  // Box
									};
									if ( cMatrix[iRow][iCol].size() != 1 ) {
										for(;;) {
											// Check Options in the row
											for (int iRowIterate = 0; iRowIterate < 9; iRowIterate++) {
												if ( cMatrix[iRowIterate][iCol].size() == 1 ) {
													iIterator = cMatrix[iRowIterate][iCol].iterator();
													bOptions[0][iIterator.next()-1] = false;
												} else
													bMatrixFilledUp = false;
											}
											// Check if there is only false option in the row
											bIterator = Arrays.asList(bOptions[0]).listIterator();
											iTrueCounter = 0;
											while (bIterator.hasNext()) {
												iTempIndex = bIterator.nextIndex();
												bTemp = bIterator.next();
												if (bTemp) {
													iTrueCounter++;
													iFinalIndex = iTempIndex+1;
												}
												if (iTrueCounter >= 2)
													break;
											}
											// Skip Col & Box checks & sync if final choice has been made
											if (iTrueCounter == 1) {
												cMatrix[iRow][iCol].clear();
												cMatrix[iRow][iCol].add(iFinalIndex);
												iProgress++;
												publish(new Publisher(iRow, iCol, Integer.toString( iFinalIndex ), SOLVER_COLOR, iProgress));
												break;
											}
											// Check Options in the col
											for (int iColIterate = 0; iColIterate < 9; iColIterate++) {
												if ( cMatrix[iRow][iColIterate].size() == 1 ) {
													iIterator = cMatrix[iRow][iColIterate].iterator();
													bOptions[1][iIterator.next()-1] = false;
												} else
													bMatrixFilledUp = false;
											}
											// Check if there is only false option in the col
											bIterator = Arrays.asList(bOptions[1]).listIterator();
											iTrueCounter = 0;
											while (bIterator.hasNext()) {
												iTempIndex = bIterator.nextIndex();
												bTemp = bIterator.next();
												if (bTemp) {
													iTrueCounter++;
													iFinalIndex = iTempIndex+1;
												}
												if (iTrueCounter >= 2)
													break;
											}
											// Skip Box check & sync if final choice has been made
											if (iTrueCounter == 1) {
												cMatrix[iRow][iCol].clear();
												cMatrix[iRow][iCol].add(iFinalIndex);
												iProgress++;
												publish(new Publisher(iRow, iCol, Integer.toString( iFinalIndex ), SOLVER_COLOR, iProgress));
												break;
											}
											// Check Options in the box
											for (int iCheckRow = (iRow/3)*3; iCheckRow < (iRow/3)*3+3; iCheckRow++) {
												for (int iCheckCol = (iCol/3)*3; iCheckCol < (iCol/3)*3+3; iCheckCol++) {
													if ( cMatrix[iCheckRow][iCheckCol].size() == 1 ) {
														iIterator = cMatrix[iCheckRow][iCheckCol].iterator();
														bOptions[2][iIterator.next()-1] = false;
													} else
														bMatrixFilledUp = false;
												}
											}
											// Check if there is only false option in the box
											bIterator = Arrays.asList(bOptions[2]).listIterator();
											iTrueCounter = 0;
											while (bIterator.hasNext()) {
												iTempIndex = bIterator.nextIndex();
												bTemp = bIterator.next();
												if (bTemp) {
													iTrueCounter++;
													iFinalIndex = iTempIndex+1;
												}
												if (iTrueCounter >= 2)
													break;
											}
											// Skip sync if final choice has been made
											if (iTrueCounter == 1) {
												cMatrix[iRow][iCol].clear();
												cMatrix[iRow][iCol].add(iFinalIndex);
												iProgress++;
												publish(new Publisher(iRow, iCol, Integer.toString( iFinalIndex ), SOLVER_COLOR, iProgress));
												break;
											}
											// Sync Row & Col & Box results to one
											cMatrix[iRow][iCol].clear();
											for (int iOptions = 0; iOptions < 9; iOptions++) {
												if ( bOptions[0][iOptions] && bOptions[1][iOptions] && bOptions[2][iOptions] ) {
													cMatrix[iRow][iCol].add(iOptions+1);
												}
											}
											// Check if final choice has been made
											if (cMatrix[iRow][iCol].size() == 1) {
												iProgress++;
												iIterator = cMatrix[iRow][iCol].iterator();
												publish(new Publisher(iRow, iCol, Integer.toString( iIterator.next() ), SOLVER_COLOR, iProgress));
											}
											break;
										}
									}
								}
							}
							if ( bSudokuUpdated )
								iLoopOverFlow = 0;
							else
								iLoopOverFlow++;
						} while ( iLoopOverFlow < 3 && !bMatrixFilledUp );
						
						if ( iLoopOverFlow < 3 ) { // Tell the user the Sudoku has been solved
							allowInput();
							return 0;
						}
						else if ( solveIt(0,0,cMatrix) ) {// Using backtracking method to solve the SudoKu
							printMissingFields();
							allowInput();
							return 0;
						} else {
							printMissingFields();
							allowInput();
							return 2; // The Sudoku is Unsolvable
						}
					}

					private void allowInput() {
						publish(new Publisher(false));
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								publish(new Publisher(iRow, iCol, true));
							}
						}
					}
					
					private void printMissingFields() {
						Iterator<Integer> iIterator;
						for (int iRow = 0; iRow < 9; iRow++) {
							for (int iCol = 0; iCol < 9; iCol++) {
								if ( tfArray[iRow][iCol].getText().equals("") && cMatrix[iRow][iCol].size() == 1 ) {
									iIterator = cMatrix[iRow][iCol].iterator();
									publish(new Publisher(iRow, iCol, Integer.toString( iIterator.next() ), BACKTRACKING_COLOR));
								}
							}
						}
					}
					
					private boolean solveIt(int iRow, int iCol, Cell[][] cMatrix) {
						if (iRow == 9) {
							iRow = 0;
							if (++iCol == 9) {
								iProgress++;
								publish(new Publisher(iProgress));
								return true;
							}
						}
						if (cMatrix[iRow][iCol].size() == 1) // skip filled cells
							return solveIt(iRow + 1, iCol, cMatrix);
						
						Set<Integer> sTemp = cMatrix[iRow][iCol].getSet();
						for (int iValue : sTemp ) {
							if (validator(iRow, iCol, iValue, cMatrix)) {
								cMatrix[iRow][iCol].clear();
								cMatrix[iRow][iCol].add(iValue);
								iProgress++;
								publish(new Publisher(iProgress));
								if (solveIt(iRow + 1, iCol, cMatrix))
									return true;
							}
						}
						cMatrix[iRow][iCol].addAll(sTemp); // reset on backtrack
						iProgress--;
						publish(new Publisher(iProgress));
						return false;
					}
					
					private boolean validator(int iRow, int iCol, int iValue, Cell[][] cMatrix) {
						for (int iRowCheck = 0; iRowCheck < 9; iRowCheck++) {
							if ( cMatrix[iRowCheck][iCol].size() == 1 ) {
								if ( cMatrix[iRowCheck][iCol].get() == iValue )
									return false;
							}
						}

						for (int iColCheck = 0; iColCheck < 9; iColCheck++) {
							if ( cMatrix[iRow][iColCheck].size() == 1 ) {
								if ( cMatrix[iRow][iColCheck].get() == iValue )
									return false;
							}
						}

						for (int iRowCheck = (iRow / 3) * 3; iRowCheck < (iRow / 3) * 3 + 3; iRowCheck++) {
							for (int iColCheck = (iCol / 3) * 3; iColCheck < (iCol / 3) * 3 + 3; iColCheck++) {
								if ( cMatrix[iRowCheck][iColCheck].size() == 1 ) {
									if ( cMatrix[iRowCheck][iColCheck].get() == iValue )
										return false;
								}
							}
						}
						
						return true; // no violations
					}

					@Override
					protected void process(List<Publisher> chunks) {
						int[] iInfo = new int[2];
						for (Publisher peNow : chunks) {
							switch ( peNow.getType() ) {
							case Publisher.UPDATE_FIELD_PROGRESS:
								progressBar.setValue(peNow.getProgress());
							case Publisher.UPDATE_FIELD:
								iInfo[0] = peNow.getRow();
								iInfo[1] = peNow.getCol();
								tfArray[iInfo[0]][iInfo[1]].setForeground(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setDisabledTextColor(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setText(peNow.getString());
								break;
							case Publisher.UPDATE_PROGRESS:
								progressBar.setValue(peNow.getProgress());
								break;
							case Publisher.UPDATE_COLOR:
								iInfo[0] = peNow.getRow();
								iInfo[1] = peNow.getCol();
								tfArray[iInfo[0]][iInfo[1]].setForeground(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setDisabledTextColor(peNow.getColor());
								break;
							case Publisher.UPDATE_STATUS:
								iInfo[0] = peNow.getRow();
								iInfo[1] = peNow.getCol();
								tfArray[iInfo[0]][iInfo[1]].setEnabled(peNow.getStatus());
								break;
							case Publisher.UPDATE_STATUS_COLOR:
								iInfo[0] = peNow.getRow();
								iInfo[1] = peNow.getCol();
								tfArray[iInfo[0]][iInfo[1]].setEnabled(peNow.getStatus());
								tfArray[iInfo[0]][iInfo[1]].setForeground(peNow.getColor());
								tfArray[iInfo[0]][iInfo[1]].setDisabledTextColor(peNow.getColor());
								break;
							case Publisher.SHOW_PROGRESS:
								progressBar.setValue(0);
								progressBar.setVisible(true);
								btnSolve.setEnabled(false);
								break;
							case Publisher.HIDE_PROGRESS:
								progressBar.setVisible(false);
								btnSolve.setEnabled(true);
								break;
								default:
									System.err.println("---ERROR 4---");
									break;
							}
						}
					}
					
					@Override
					protected void done() {
						try {
							int iRes = get();
							switch (iRes) {
							case 0: // Sudoku solved
								JOptionPane.showMessageDialog(MainWin.this, "The Sudoku has been solved!", "End Result", JOptionPane.INFORMATION_MESSAGE);
								break;
							case 1: // Duplicate numbers
								JOptionPane.showMessageDialog(MainWin.this, "There are duplicate numbers in the red fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
								break;
							case 2: // Couldn't solve
								JOptionPane.showMessageDialog(MainWin.this, "We are sorry, this Sudoku couldn't be solved.\n Please report this error to the administrator.", "End Result", JOptionPane.INFORMATION_MESSAGE);
								break;
							case 3:
								System.err.println("---ERROR MESSAGE: RETURN 3---");
								break;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						super.done();
					}
				};
				threadPool.submit(swSolver);
			}
		});
	}
	
	class SwingListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			System.out.println("Name:" + event.getPropertyName() + " ; NewValue:" + event.getNewValue() + " ; OldValue:" + event.getOldValue());
		}
		
	}
	
	class klDelete implements KeyListener {
		private JTextField textfield;
		
		public klDelete(JTextField textfield) {
			this.textfield = textfield;
		}
		
		@Override
		public void keyPressed(KeyEvent ke) {
			if ( (ke.getKeyCode() == KeyEvent.VK_DELETE || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) && this.textfield.getText().equals("") ) {
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.getFocusOwner().transferFocusBackward();
			}
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			
		}

		@Override
		public void keyTyped(KeyEvent ke) {
			
		}
		
	}
	
	private class IntFilter extends DocumentFilter {
		private JTextField textField;
		
		public IntFilter(JTextField textField) {
			this.textField = textField;
		}
		
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);
			
			int iRes = test(sb.toString());
			if (iRes == 0) {
				super.insertString(fb, offset, string, attr);
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.getFocusOwner().transferFocus();
			} else if (iRes == 2) {
				super.insertString(fb, offset, string, attr);
			} else if ( isUserInputing ){ // iRes == 1
				JOptionPane.showMessageDialog(null, "Please insert numbers between 0 - 9 or leave the text field empty.\nZero will be considered as empty field.");
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);
			
			int iRes = test(sb.toString());
			if (iRes == 0) {
				if (this.textField.isEnabled()) {
					KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			        manager.getFocusOwner().transferFocus();
				}
				super.replace(fb, offset, length, text, attr);
			} else if (iRes == 2) {
				super.replace(fb, offset, length, text, attr);
			} else { // iRes == 1
				JOptionPane.showMessageDialog(null, "Please insert numbers between 0 - 9 or leave the text field empty.\nZero will be considered as empty field.");
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);
	
			int iRes = test(sb.toString());
			if (iRes == 0) {
				super.remove(fb, offset, length);
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.getFocusOwner().transferFocus();
			} else if (iRes == 2) {
				super.remove(fb, offset, length);
			} else { // iRes == 1
				JOptionPane.showMessageDialog(null, "Please insert numbers between 0 - 9 or leave the text field empty.\nZero will be considered as empty field.");
			}

		}
		
		private int test(String text) {
			try {
				int iNum = Integer.parseInt(text);
				if (iNum >= 0 && iNum <= 9) {
//					textField.setForeground(Color.BLACK);
//					textField.setDisabledTextColor(Color.BLACK);
					return 0; // Update text field & move to the next field
				} else
					return 1; // Do NOT update the text field
			} catch (NumberFormatException e) {
				if (text.equals(" "))
					return 1; // Do NOT update the text field
				else if (text.trim().isEmpty()) {
//					textField.setForeground(Color.BLACK);
//					textField.setDisabledTextColor(Color.BLACK);
					return 2; // Update text field & Do NOT move to the next field
				} else
					return 1; // Do NOT update the text field
			}
		}
	}
	
	private class Publisher {
		private int iRow;
		private int iCol;
		private String strNum;
		private Color cAttr;
		private int iProgress;
		private boolean bStatus;
		private int iType;
		
		public final static int UPDATE_PROGRESS = 1;
		public final static int UPDATE_FIELD = 2;
		public final static int UPDATE_FIELD_PROGRESS = 3;
		public final static int SHOW_PROGRESS = 4;
		public final static int HIDE_PROGRESS = 5;
		public final static int UPDATE_COLOR = 6;
		public final static int UPDATE_STATUS_COLOR = 7;
		public final static int UPDATE_STATUS = 8;
		
		// Constructor methods
		Publisher(int iRow, int iCol, String strNum, Color cAttr) {
			this.iRow = iRow;
			this.iCol = iCol;
			this.strNum = strNum;
			this.cAttr = cAttr;
			this.iType = Publisher.UPDATE_FIELD;
		}
		
		Publisher(int iRow, int iCol, String strNum, Color cAttr, int iProgress) {
			this.iRow = iRow;
			this.iCol = iCol;
			this.strNum = strNum;
			this.cAttr = cAttr;
			this.iProgress = iProgress;
			this.iType = Publisher.UPDATE_FIELD_PROGRESS;
		}
		
		Publisher(int iProgress) {
			this.iProgress = iProgress;
			this.iType = Publisher.UPDATE_PROGRESS;
		}
		
		public Publisher(boolean bProgress) {
			this.iType = (bProgress) ? Publisher.SHOW_PROGRESS : Publisher.HIDE_PROGRESS;
		}
		
		public Publisher(int iRow, int iCol, Color cAttr) {
			this.iRow = iRow;
			this.iCol = iCol;
			this.cAttr = cAttr;
			this.iType = Publisher.UPDATE_COLOR;
		}
		
		public Publisher(int iRow, int iCol, Color cAttr, boolean bStatus) {
			this.iRow = iRow;
			this.iCol = iCol;
			this.cAttr = cAttr;
			this.bStatus = bStatus;
			this.iType = Publisher.UPDATE_STATUS_COLOR;
		}
		
		public Publisher(int iRow, int iCol, boolean bStatus) {
			this.iRow = iRow;
			this.iCol = iCol;
			this.bStatus = bStatus;
			this.iType = Publisher.UPDATE_STATUS;
		}
		
		// Get methods
		public int getRow() {
			return iRow;
		}

		public int getCol() {
			return iCol;
		}

		public String getString() {
			return strNum;
		}

		public Color getColor() {
			return this.cAttr;
		}
		
		public int getProgress() {
			return this.iProgress;
		}
		
		public int getType() {
			return this.iType;
		}
		
		public boolean getStatus() {
			return this.bStatus;
		}

		@Override
		public String toString() {
			StringBuilder sbTemp = new StringBuilder();
			sbTemp.append("Publisher[");
			sbTemp.append("iRow:");
			sbTemp.append(iRow);
			sbTemp.append("; iCol:");
			sbTemp.append(iCol);
			sbTemp.append("; strNum:");
			sbTemp.append(strNum);
			sbTemp.append("; cAttr:");
			sbTemp.append(cAttr);
			sbTemp.append("; iProgress:");
			sbTemp.append(iProgress);
			sbTemp.append("; iType:");
			sbTemp.append(iType);
			sbTemp.append("]");
			return sbTemp.toString();
		}
	}
	
	private class DelActionWrapper extends AbstractAction {
		private static final long serialVersionUID = -8202570796686458310L;
		private JTextField textField;
		private Action beep;
		
		public DelActionWrapper(JTextField textField, Action beep) {
			this.textField = textField;
			this.beep = beep;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.textField.setText("");
			this.beep.setEnabled(false);
		}
	}
	
	private class Cell {
		private Set<Integer> sOptions = new HashSet<Integer>();

		public Cell() {
		}
		
		public Cell(int i) {
			this.sOptions.add(i);
		}
		
		public boolean add(int i) {
			return this.sOptions.add(i);
		}
		
		public boolean remove(int i) {
			return this.sOptions.remove(i);
		}
		
		public boolean contains(int i) {
			return this.sOptions.contains(i);
		}
		
		public int size() {
			return this.sOptions.size();
		}
		
		public Iterator<Integer> iterator() {
			return this.sOptions.iterator();
		}
		
		public void clear() {
			this.sOptions = new HashSet<Integer>();
		}
		
		public boolean isEmpty() {
			return this.sOptions.isEmpty();
		}
		
		public boolean addAll(Set<Integer> sOptions) {
			return this.sOptions.addAll(sOptions);
		}
		
		public int get() {
			/**
			 * Will be used ONLY when there is one element in the set
			 */
			if ( this.sOptions.size() == 1 ) {
				Iterator<Integer> iterator = this.sOptions.iterator();
				return iterator.next();
			} else
				return 0;
		}
		
		public Set<Integer> getSet() {
			return this.sOptions;
		}

		@Override
		public String toString() {
			return this.sOptions.toString();
		}
		
	}

	static void printSudoKu(Cell[][] cMatrix) {
		for (int iRow = 0; iRow < cMatrix.length; iRow++) {
			for (int iCol = 0; iCol < cMatrix[iRow].length; iCol++) {
				System.out.print(cMatrix[iRow][iCol].toString().replaceAll(" ", ""));
				if (iCol%3 == 2)
					System.out.print("\t");
			}
			System.out.println();
			if (iRow%3 == 2)
				System.out.println();
		}
		System.out.println();
	}
}
