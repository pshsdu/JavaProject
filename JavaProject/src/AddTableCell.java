import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class AddTableCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	JButton btn;
	private static int cnt = 0;
	
	public AddTableCell(ActionListener a){
		
		btn = new JButton("선택");
		btn.addActionListener(a);
		btn.setBackground(Color.white);
	}
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		return btn;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column) {
		return btn;
	}
}
