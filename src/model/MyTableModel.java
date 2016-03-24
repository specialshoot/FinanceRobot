package model;

import java.util.ArrayList;
import java.util.PrimitiveIterator.OfDouble;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {

	private static final boolean DEBUG = true;
	private String[] columnNames = { "id", "question", "delete" };
	private Object[][] data = {};
	public ArrayList<Integer> itemNum;

	public MyTableModel(ArrayList<Question> list) {
		super();
		itemNum = new ArrayList<Integer>();
		data = new Object[list.size()][3];
		for (int i = 0; i < list.size(); i++) {
			data[i][0] = new Integer(i + 1);
			data[i][1] = list.get(i).getQuestion();
			data[i][2] = new Boolean(false);
		}
	}

	public void setList(ArrayList<Question> list) {
		data = new Object[list.size()][3];
		for (int i = 0; i < list.size(); i++) {
			data[i][0] = new Integer(i + 1);
			data[i][1] = list.get(i).getQuestion();
			data[i][2] = new Boolean(false);
		}
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 2) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (DEBUG) {
			System.out.println("Setting value at " + row + "," + col + " to " + value + " (an instance of "
					+ value.getClass() + ")");
		}

		data[row][col] = value;
		fireTableCellUpdated(row, col);// 通知所有的Listener在这个表格中的(row,column)字段的内容已经改变了

		if (DEBUG) {
			System.out.println("New value of data:");
			printDebugData();
		}
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			if ((Boolean) data[i][2] == true) {
				itemNum.add(i);
			} else {
				if (itemNum.contains(i)) {
					itemNum.remove(i);
				}
			}
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}
