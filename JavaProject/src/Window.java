import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Window extends JFrame implements ActionListener{
	
	private String[] labels = {"정렬", "지출 분석", "등록", "수정/삭제", "검색", "홈"};
	private JButton[] buttons;
	
	DBConnection db = new DBConnection();
	private String[] header = new String[]{"No.","태그", "금액(- : 지출)", "비고"};
	private String[][] contents;
	
	PieChart piChart;
	private JPanel chartPanel;
	
	private JPanel mainPanel;
	private JPanel btnPanel;
	private JPanel infoPanel;
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scrollerPane;
	private JLabel titleLabel;
	private JLabel totalAmtLabel;
	
	private JPanel selectionPanel;
	private JButton modifyBtn;
	private JButton deleteBtn;
	
	private JPanel searchPanel;
	private JTextField searchTag;
	private JButton confirmBtn;
	
	private JPanel enrollmentPanel;
	private JPanel typeBtnPanel;
	private JPanel txtFieldPanel;
	private JPanel labelPanel;
	private JTextField tagField;
	private JTextField costField;
	private JLabel tagLabel;
	private JLabel costLabel;
	private JButton selectExpendBtn;
	private JButton selectIncomeBtn;
	
	private JPanel sortTypeSelectionPanel;
	JButton tagSelectBtn;
	JButton costSelectBtn;
	
	private int selectedId = -1;
	private int selectedIdx = -1;
	
	// initiation
	private void initTable() {
		model = new DefaultTableModel(null, header);
		table = new JTable(model);
		table.setRowHeight(20);
		
		scrollerPane = new JScrollPane(table);
		scrollerPane.setPreferredSize(new Dimension(380, 300));
	}
	
	private void initEnrollPanel() {
		enrollmentPanel = new JPanel();
		enrollmentPanel.setLayout(new BorderLayout(10, 10));
		enrollmentPanel.setBackground(Color.WHITE);
		
		tagField = new JTextField(10);
		costField = new JTextField(10);
		
		tagLabel = new JLabel("tag : ");
		tagLabel.setBackground(Color.WHITE);
		tagLabel.setOpaque(true); 
		costLabel = new JLabel("price : ");
		costLabel.setBackground(Color.WHITE);
		costLabel.setOpaque(true); 
		
		selectExpendBtn = new JButton("지출");
		selectExpendBtn.setBackground(new Color(235, 235, 235));
		selectExpendBtn.addActionListener(this);
		
		selectIncomeBtn = new JButton("수입");
		selectIncomeBtn.setBackground(new Color(235, 235, 235));
		selectIncomeBtn.addActionListener(this);
	}
	
	private void initTitleLabel() {
		titleLabel = new JLabel("HOME");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 15));
		try {
			totalAmtLabel = new JLabel("총 금액 : " + db.getTotalAmount());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initBtnPanel() {
		buttons = new JButton[8];
		
		for (int idx = 0; idx < labels.length; idx++) {
			buttons[idx] = new JButton(labels[idx]);
			buttons[idx].addActionListener(this);
			btnPanel.add(buttons[idx]);
			buttons[idx].setBackground(new Color(235, 235, 235));
		}
		
		btnPanel.setBackground(Color.WHITE);
	}
	
	private void initSelectionPanel() {
		selectionPanel = new JPanel();
		
		modifyBtn = new JButton("수정");
		modifyBtn.setBackground(new Color(235, 235, 235));
		deleteBtn = new JButton("삭제");
		deleteBtn.setBackground(new Color(235, 235, 235));
		
		modifyBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		
		selectionPanel.add(modifyBtn);
		selectionPanel.add(deleteBtn);
		selectionPanel.setBackground(Color.WHITE);
	}
	
	private void initSortPanel() {
		sortTypeSelectionPanel = new JPanel();
		
		tagSelectBtn = new JButton("태그를 기준으로 정렬");
		tagSelectBtn.setBackground(new Color(235, 235, 235));
		costSelectBtn = new JButton("금액을 기준으로 정렬");
		costSelectBtn.setBackground(new Color(235, 235, 235));
		
		tagSelectBtn.addActionListener(this);
		costSelectBtn.addActionListener(this);
		
		sortTypeSelectionPanel.add(tagSelectBtn);
		sortTypeSelectionPanel.add(costSelectBtn);
		sortTypeSelectionPanel.setBackground(Color.WHITE);
	}
	
	private void initSearchPanel() {
		searchPanel = new JPanel();
		
		searchTag = new JTextField(20);
		confirmBtn = new JButton("확인");
		confirmBtn.setBackground(new Color(235, 235, 235));
		
		confirmBtn.addActionListener(this);
		searchPanel.add(searchTag);
		searchPanel.add(confirmBtn);
		searchPanel.setBackground(Color.WHITE);
	}
	
	private void addToInfoPanel(JPanel p) {
		infoPanel.add(p, BorderLayout.SOUTH);
		infoPanel.repaint();
	}
	
	private void removeToInfoPanel(JPanel p) {
		infoPanel.remove(p);
		infoPanel.repaint();
	}
	
	private void clearPanel() {
		removeTable();
		removeToInfoPanel(enrollmentPanel);
		removeToInfoPanel(searchPanel);
		removeToInfoPanel(selectionPanel);
		removeToInfoPanel(sortTypeSelectionPanel);
		removeTableBtn();
		
		if(chartPanel != null)
			removeToInfoPanel(chartPanel);
	}
	
	private void addModifyScreen() {
		resetEnrollmentPanel();
		selectExpendBtn.setText("지출 수정");
		selectIncomeBtn.setText("수입 수정");
		addToInfoPanel(enrollmentPanel);
	}
	
	private void resetEnrollmentPanel() {
		selectExpendBtn.setText("지출");
		selectIncomeBtn.setText("수입");
		tagField.setText("");
		costField.setText("");
	}
	
	private void addTable() {
		table.setModel(model);
		infoPanel.add(scrollerPane, BorderLayout.CENTER);
		infoPanel.repaint();
	}
	
	private void removeTable() {
		infoPanel.remove(scrollerPane);
		infoPanel.repaint();
	}
	
	private void addTableBtn() {
		table.getColumnModel().getColumn(3).setCellRenderer(new AddTableCell(this));
		table.getColumnModel().getColumn(3).setCellEditor(new AddTableCell(this));
	}
	
	private void removeTableBtn() {
		table.getColumnModel().getColumn(3).setCellRenderer(new RemoveTableCell());
		table.getColumnModel().getColumn(3).setCellEditor(new RemoveTableCell());
	}
	
	private void setTableModel(String[][] s) {
		DefaultTableModel searchedResultModel = new DefaultTableModel(s, header);
		table.setModel(searchedResultModel);
		infoPanel.add(scrollerPane, BorderLayout.CENTER);
		infoPanel.repaint();
	}
	
	private void addPiChart() {
		try {
			piChart = new PieChart("Spending", db.spendingAnalysis());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chartPanel = piChart.createDemoPanel();
		
		infoPanel.add(chartPanel);
		infoPanel.repaint();
	}
	
	public Window() {
		
		setSize(400, 500);
		setTitle("TellerSystem");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			contents = db.get();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Panel 선언
		mainPanel = new JPanel();
		btnPanel = new JPanel();
		infoPanel = new JPanel();
		txtFieldPanel = new JPanel();
		typeBtnPanel = new JPanel();
		labelPanel = new JPanel();
		
		//Panel Layout 선언
		infoPanel.setLayout(new BorderLayout(10, 10));
		mainPanel.setLayout(null);
		btnPanel.setLayout(new GridLayout(2, 4, 10, 10));
		txtFieldPanel.setLayout(new GridLayout(2, 1));
		labelPanel.setLayout(new GridLayout(2, 1));
		typeBtnPanel.setLayout(new GridLayout(2, 1));
		
		initTable();
		initEnrollPanel();
		initTitleLabel();
		initBtnPanel();
		initSelectionPanel();
		initSearchPanel();
		initSortPanel();

		labelPanel.add(tagLabel);
		labelPanel.add(costLabel);
		
		typeBtnPanel.add(selectExpendBtn);
		typeBtnPanel.add(selectIncomeBtn);
		
		txtFieldPanel.add(tagField);
		txtFieldPanel.add(costField);
		
		enrollmentPanel.add(labelPanel, BorderLayout.WEST);
		enrollmentPanel.add(txtFieldPanel, BorderLayout.CENTER);
		enrollmentPanel.add(typeBtnPanel, BorderLayout.EAST);

		mainPanel.add(titleLabel);
		titleLabel.setBounds(10, 10, 380, 20);
		infoPanel.add(totalAmtLabel, BorderLayout.NORTH);
		
		mainPanel.add(infoPanel);
		infoPanel.setBounds(10, 40, 380, 360);
		infoPanel.setBackground(Color.WHITE);
		 
		mainPanel.add(btnPanel);
		btnPanel.setBounds(10, 420, 380, 60);
		mainPanel.setBackground(Color.WHITE);
		add(mainPanel);
		setVisible(true);
		setResizable(false);
		
		for(String[] strs : contents) {
			model.addRow(new String[]{strs[0], strs[1], strs[2], strs[3]});
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();	// 눌려진 컴포넌트를 JButton에 저장함
		String str = b.getText();	//b의 라벨을 받아옴
		
		clearPanel();
		
		switch(str) {
		
		case "태그를 기준으로 정렬":
			String[][] sortedResult = null;
			addToInfoPanel(sortTypeSelectionPanel);
			
			try {
				sortedResult = db.getSortedResult(true);
			} catch (SQLException e4) {
				e4.printStackTrace();
			}
			
			setTableModel(sortedResult);
			
			break;
			
		case "금액을 기준으로 정렬" :
			sortedResult = null;
			addToInfoPanel(sortTypeSelectionPanel);
			
			try {
				sortedResult = db.getSortedResult(false);
			} catch (SQLException e4) {
				e4.printStackTrace();
			}
			
			setTableModel(sortedResult);
			break;
		
		case "정렬":
			titleLabel.setText("정렬");			
			addToInfoPanel(sortTypeSelectionPanel);
			sortedResult = null;
			
			try {
				sortedResult = db.getSortedResult(true);
			} catch (SQLException e4) {
				e4.printStackTrace();
			}
			
			setTableModel(sortedResult);
			
			break;
			
		case "지출 분석":
			titleLabel.setText("지출 분석");
			addPiChart();
			break;
			
		case "선택":
			titleLabel.setText("선택");
			try {
				selectedIdx = table.getSelectedRow();
				contents = db.get();
				selectedId = Integer.parseInt(contents[selectedIdx][0]);
				System.out.println(selectedId);
				addToInfoPanel(selectionPanel);
				addTable();
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			break;
			
		case "등록":
			titleLabel.setText("등록");
			addToInfoPanel(enrollmentPanel);
			addTable();
			break;
			
		case "지출":
			try {
				try {
					if(db.insert(tagField.getText(),Integer.parseInt("-" + costField.getText())))
						model.addRow(new String[]{Integer.toString(db.getMaxId()), tagField.getText(), "-" + costField.getText(), " "});
					else
						JOptionPane.showMessageDialog(null, "삽입 중 에러 발생!");
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}catch (NullPointerException e2) {
				JOptionPane.showMessageDialog(new JFrame(), 
						"추가할 요소가 없음.");
			}finally {
				resetEnrollmentPanel();
				addTable();
			}
			break;
			
		case "수입":
			try {
				try {
					if(db.insert(tagField.getText(),Integer.parseInt(costField.getText())))
						model.addRow(new String[]{Integer.toString(db.getMaxId()), tagField.getText(), costField.getText(), " "});
					else 
						JOptionPane.showMessageDialog(null, "삽입 중 에러 발생!");
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}catch (NullPointerException e2) {
				JOptionPane.showMessageDialog(new JFrame(), 
						"추가할 요소가 없음.");
			}finally {
				resetEnrollmentPanel();
				addTable();
			}
			break;
			
		case "삭제":
			titleLabel.setText("삭제된 결과");
			try {
				if(db.delete(selectedId))
					model.removeRow(selectedIdx);
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			try {
				totalAmtLabel.setText("총 금액 : " + db.getTotalAmount());
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			addTable();
			break;
		
		case "수정":
			titleLabel.setText("수정");
			addModifyScreen();
			addTable();
			break;
			
		case "수입 수정":
			titleLabel.setText("수정된 결과");
			addTable();
			try {
				if(db.modify(selectedId, tagField.getText(), Integer.parseInt(costField.getText()))) {
					model.setValueAt(tagField.getText(), selectedIdx, 1);
					model.setValueAt(costField.getText(), selectedIdx, 2);
				}
			} catch (NumberFormatException e2) {
				e2.printStackTrace();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}

			resetEnrollmentPanel();
			break;
		
		case "지출 수정":
			titleLabel.setText("수정된 결과");
			addTable();
			try {
				if(db.modify(selectedId, tagField.getText(), Integer.parseInt("-" + costField.getText()))) {
					model.setValueAt(tagField.getText(), selectedIdx, 1);
					model.setValueAt("-" +costField.getText(), selectedIdx, 2);
				}
			} catch (NumberFormatException e2) {
				e2.printStackTrace();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			
			resetEnrollmentPanel();
			break;
			
		case "수정/삭제":
			titleLabel.setText("수정/삭제");
			addTable();
			addTableBtn();
			addTable();
			break;
			
		case "검색":
			titleLabel.setText("검색");
			addToInfoPanel(searchPanel);
			break;
		
		case "확인":
			titleLabel.setText("검색된 결과");
			String[][] searchedResult = null;
			
			try {
				searchedResult = db.getSearchedResult(searchTag.getText());
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			
			setTableModel(searchedResult);
			searchTag.setText("");
			break;
			
		case "홈":
			titleLabel.setText("HOME");
			break;
			
		default:
			System.out.println(str);
			break;
		}
		
		try {
			totalAmtLabel.setText("총 금액 : " + db.getTotalAmount());
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
	}
}
