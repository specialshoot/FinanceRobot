package finance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import jdbc.Mysql;
import socket.OnConnectListener;
import socket.SocketServer;

public class MainWindow {

	private JFrame frame;
	private JTable table;
	private JButton deleteButton;
	private static final boolean DEBUG = true;
	private static JLabel connectText;
	private static Mysql mysql = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if (mysql == null) {
			mysql = new Mysql();
		}

		SocketServer socketServer = new SocketServer();
		socketServer.setOnConnectListener(new OnConnectListener() {

			@Override
			public void setSendMessage(String msg) {
				// TODO Auto-generated method stub
				if (mysql != null) {
					try {
						if (!mysql.ddlHasItem(msg)) {
							mysql.addData(msg);
						} else {
							System.out.println("数据库已经存在该项目");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void setConnect(Boolean isConnect) {
				// TODO Auto-generated method stub
				System.out.println("进入setConnect");
				try {
					System.out.println("connectText getText -> " + connectText.getText());
					if (isConnect && connectText != null) {
						connectText.setText("已连接设备");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("try失败");
				}
			}
		});
		socketServer.startServer();
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel jPanelTop = new JPanel();
		jPanelTop.setLayout(new GridLayout(2, 1));
		JLabel title = new JLabel("财务机器人PC客户端");
		connectText = new JLabel("设备未连接");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		connectText.setHorizontalAlignment(SwingConstants.CENTER);
		jPanelTop.add(title);
		jPanelTop.add(connectText);
		frame.getContentPane().add(jPanelTop, BorderLayout.NORTH);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new AbstractTableModel() {

			private String[] columnNames = { "id", "question", "delete" };
			private Object[][] data = { { 1, "Smith", new Boolean(false) }, { 2, "Doe", new Boolean(true) },
					{ 3, "Black", new Boolean(false) }, { 4, "White", new Boolean(true) },
					{ 5, "Brown", new Boolean(false) } };

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
					for (int j = 0; j < numCols; j++) {
						System.out.print("  " + data[i][j]);
					}
					System.out.println();
				}
				System.out.println("--------------------------");
			}
		});
		table.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		deleteButton = new JButton("删除选中项");
		frame.getContentPane().add(deleteButton, BorderLayout.SOUTH);
	}

	public void setConnectTitle(Boolean isConnect) {
		if (isConnect) {
			connectText.setText("设备已连接");
		} else {
			connectText.setText("设备未连接");
		}
	}

}
