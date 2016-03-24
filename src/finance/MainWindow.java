package finance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import jdbc.Mysql;
import mina.MyServerHandler;
import mina.MyTextLineFactory;
import mina.OnMinaConnectListener;
import model.MyTableModel;
import model.Question;

public class MainWindow {

	private JFrame frame;
	private static JTable table;
	private JButton deleteButton;
	// private static final boolean DEBUG = true;
	private static JLabel connectText;
	private static Mysql mysql = null;
	private static MyTableModel myTableModel = null;
	private static ArrayList<Question> list;
	private static NioSocketAcceptor acceptor=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		if (mysql == null) {
			mysql = new Mysql();
		}

		list = new ArrayList<>();
		if (mysql != null) {
			System.out.println("mysql != null");
			try {
				list.clear();
				list.addAll(mysql.ddlSelectAll());
				myTableModel = new MyTableModel(list);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

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

		try {
			acceptor = new NioSocketAcceptor();
			MyServerHandler serverHandler = new MyServerHandler();
			serverHandler.setOnMinaConnectListener(new OnMinaConnectListener() {

				@Override
				public void setSendMessage(String msg) {
					// TODO Auto-generated method stub
					if (mysql != null) {
						try {
							if (!mysql.ddlHasItem(msg)) {
								mysql.addData(msg);
								list.clear();
								list.addAll(mysql.ddlSelectAll());
								list = mysql.ddlSelectAll();
								if (myTableModel != null) {
									myTableModel.setList(list);
									table.repaint();
									table.updateUI();
								}
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
						if (connectText != null) {
							if (isConnect) {
								connectText.setText("已连接设备");
								list.clear();
								list.addAll(mysql.ddlSelectAll());
								list = mysql.ddlSelectAll();
								if (myTableModel != null) {
									myTableModel.setList(list);
									table.repaint();
									table.updateUI();
								}
							} else {
								connectText.setText("未连接设备");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("try失败");
					}
				}
			});
			acceptor.setHandler(serverHandler);
//			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextLineFactory()));
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
//			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("GBK"))));
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
			acceptor.bind(new InetSocketAddress(9898));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// SocketServer socketServer = new SocketServer();
		// socketServer.setOnConnectListener(new OnConnectListener() {
		//
		// @Override
		// public void setSendMessage(String msg) {
		// // TODO Auto-generated method stub
		// if (mysql != null) {
		// try {
		// if (!mysql.ddlHasItem(msg)) {
		// mysql.addData(msg);
		// } else {
		// System.out.println("数据库已经存在该项目");
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		//
		// @Override
		// public void setConnect(Boolean isConnect) {
		// // TODO Auto-generated method stub
		// System.out.println("进入setConnect");
		// try {
		// System.out.println("connectText getText -> " +
		// connectText.getText());
		// if (isConnect && connectText != null) {
		// connectText.setText("已连接设备");
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("try失败");
		// }
		// }
		// });
		//
		// socketServer.startServer();
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
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int i = JOptionPane.showConfirmDialog(null, "确定要退出系统吗？", "退出系统", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION) {
					if (acceptor!=null) {
						acceptor.dispose();
						acceptor=null;
					}
					System.exit(0);
				}
			}
		});

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
		table.setModel(myTableModel);
		// table.setModel(new AbstractTableModel() {
		//
		// ArrayList<Question> list = new ArrayList<Question>();
		// private String[] columnNames = { "id", "question", "delete" };
		// private Object[][] data = {};
		//
		// @Override
		// public int getRowCount() {
		// // TODO Auto-generated method stub
		// return data.length;
		// }
		//
		// @Override
		// public int getColumnCount() {
		// // TODO Auto-generated method stub
		// return columnNames.length;
		// }
		//
		// @Override
		// public String getColumnName(int col) {
		// return columnNames[col];
		// }
		//
		// @Override
		// public Object getValueAt(int row, int col) {
		// return data[row][col];
		// }
		//
		// @Override
		// public Class getColumnClass(int c) {
		// return getValueAt(0, c).getClass();
		// }
		//
		// @Override
		// public boolean isCellEditable(int row, int col) {
		// // Note that the data/cell address is constant,
		// // no matter where the cell appears onscreen.
		// if (col < 2) {
		// return false;
		// } else {
		// return true;
		// }
		// }
		//
		// @Override
		// public void setValueAt(Object value, int row, int col) {
		// if (DEBUG) {
		// System.out.println("Setting value at " + row + "," + col + " to " +
		// value + " (an instance of "
		// + value.getClass() + ")");
		// }
		//
		// data[row][col] = value;
		// fireTableCellUpdated(row, col);//
		// 通知所有的Listener在这个表格中的(row,column)字段的内容已经改变了
		//
		// if (DEBUG) {
		// System.out.println("New value of data:");
		// printDebugData();
		// }
		// }
		//
		// private void printDebugData() {
		// int numRows = getRowCount();
		// int numCols = getColumnCount();
		//
		// for (int i = 0; i < numRows; i++) {
		// System.out.print(" row " + i + ":");
		// for (int j = 0; j < numCols; j++) {
		// System.out.print(" " + data[i][j]);
		// }
		// System.out.println();
		// }
		// System.out.println("--------------------------");
		// }
		// });
		table.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		deleteButton = new JButton("删除选中项");
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ArrayList<Integer> listNum = myTableModel.itemNum;
				for (int i = 0; i < listNum.size(); i++) {
					String temp = list.get(listNum.get(i)).getQuestion();
					try {
						mysql.ddlDel(temp);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				myTableModel.itemNum.clear();
				list.clear();
				try {
					list.addAll(mysql.ddlSelectAll());
					list = mysql.ddlSelectAll();
					if (myTableModel != null) {
						myTableModel.setList(list);
						table.repaint();
						table.updateUI();
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(deleteButton, BorderLayout.SOUTH);
	}
}
