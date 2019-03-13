package distributed.ui.client;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import distributed.core.entities.Blockchain;
import distributed.core.entities.NodeMiner;
import distributed.core.entities.Transaction;
import distributed.core.threads.ServerThread;
import distributed.ui.operations.RoundedBorder;

public class MainWindow {

	private JFrame frame;
	private static JTextField recipient_addr;
	private static JTextField amount;
	private static JTable table;

	private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();
	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class.getName());

	private static NodeMiner node;
	private String lastBlockHash = null;

	/**
	 * Launch the application.
	 *
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		LOG.info("Starting UI...", 5);

		node = NodeMiner.initializeBackEnd(args);
		// should we launch here the backend procedure ?
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/* public static void initializeBackEnd(String args[]) throws IOException {
	 * LOG.info("START initializing backend");
	 *
	 * Security.addProvider(new
	 * org.bouncycastle.jce.provider.BouncyCastleProvider()); // String myAddress =
	 * Inet4Address.getLocalHost().getHostAddress(); // args[0]; // δε χρειάζεται
	 * δημιουργείται εξ ορισμού στην ip της εφαρμογής // το πορτ θα μπορούσε να
	 * τίθεται αυτόματα διαλέγοντας κάποια πόρτα if (args.length < 1) { LOG.
	 * warn("A port number must be provided in order for the node to start. Exiting..."
	 * ); return; } int myPort = Integer.parseInt(args[0]); int numOfNodes = 3; if
	 * (args.length < 2) { LOG.
	 * warn("Number of nodes wasn't specified, procedding with defaults which is {}"
	 * , numOfNodes); } else { numOfNodes = Integer.parseInt(args[1]); }
	 *
	 * node = new NodeMiner(myPort); node.setNumOfNodes(numOfNodes);
	 * node.setBlockchain(new Blockchain()); // Define new server ServerThread
	 * server = new ServerThread(myPort, node);
	 *
	 * LOG.info("About to start server..."); server.start(); // εκκινούμε το thread
	 * του server όπου μας έρχονται μηνύματα
	 *
	 * // connectToBootstrap(myAddress, myPort);
	 * node.initiliazeNetoworkConnections();
	 *
	 * InputStream is = null; BufferedReader br = null;
	 *
	 * //Thread.sleep(12000); // for debug //node.getBlockchain().printBlockChain();
	 * //LOG.info("Size of blockchain={}", node.getBlockchain().getSize());
	 *
	 * //server.getServerSocket().close(); } */

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		LOG.info("START initializion of frontend");

		frame = new JFrame();
		frame.setSize(512, 460);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.getContentPane().setLayout(null);
		frame.setMinimumSize(new Dimension(450, 300));
		// frame.setMaximizedBounds(new Rectangle(100, 100, 720, 600));
		frame.setResizable(false);
		frame.setTitle("Noobcash client - " + node.getId());

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		URL iconURL = loader.getResource("bitcoin.png");
		try {
			ImageIcon icon = new ImageIcon(iconURL);
			frame.setIconImage(icon.getImage());
		} catch (NullPointerException e) {
			LOG.warn("Icon image not found");
		}

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);
		tabbedPane.setBounds(0, 0, 434, 261);
		tabbedPane.addChangeListener(new ChangeListener() {
			// This method is called whenever the selected tab changes
			@Override
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane TabbedPane = (JTabbedPane) evt.getSource();

				// Get current tab
				int tab = TabbedPane.getSelectedIndex();
				JPanel aux = (JPanel) tabbedPane.getComponent(tab);
				switch (tab) {
				case 0:
					// everything happens when submit button is clicked
					break;
				case 1:
					String lastHash = node.getBlockchain().getLastHash();
					if (lastHash.equals(lastBlockHash)) {
						break;
					}
					lastBlockHash = lastHash;
					List<Transaction> tList = node.getBlockchain().getTransLastBlock();
					DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
					if (tableModel.getRowCount() > 0) {
						for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
							tableModel.removeRow(i);
						}
					}
					for (Transaction t : tList) {
						PublicKey senders = t.getSenderAddress();
						String b64Sender = senders == null ? "Genesis"
								: StringUtils.right(Base64.getEncoder().encodeToString(senders.getEncoded()), 10);
						String b64Receiver = Base64.getEncoder().encodeToString(t.getReceiverAddress().getEncoded());
						// we show last digits of keys because only those are different
						// TODO better use ids
						tableModel.addRow(new Object[] { t.getTransactionId(), b64Sender,
								b64Receiver.substring(b64Receiver.length() - 10), t.getAmount() });
					}
					break;
				case 2:
					JLabel lbl = (JLabel) aux.getComponent(0);
					float balance = node.getBalance(); // TODO replace with function
					lbl.setText("Your balance is " + balance + " noobcash.");
					break;
				case 3:
					break;
				}
			}
		});
		frame.getContentPane().add(tabbedPane);

		// New transaction panel
		initializeNewTransPanel(tabbedPane);

		// View transactions of last block panel
		initializeViewLastTransPanel(tabbedPane);

		// Show balance panel
		initializeBalancePanel(tabbedPane);

		// Help panel
		initializeHelpPanel(tabbedPane);

	}

	private static void initializeNewTransPanel(JTabbedPane tabbedPane) {
		LOG.info("START initialize new trans panel");

		JPanel newTransPanel = new JPanel();
		tabbedPane.addTab("New transaction", null, newTransPanel, null);
		newTransPanel.setLayout(null);

		recipient_addr = new JTextField(); // two textfields for user input
		recipient_addr.setBounds(247, 51, 90, 23);
		recipient_addr.setBorder(new RoundedBorder(5));
		newTransPanel.add(recipient_addr);
		recipient_addr.setColumns(10);

		amount = new JTextField();
		amount.setBounds(247, 93, 90, 23);
		amount.setBorder(new RoundedBorder(5));
		newTransPanel.add(amount);
		amount.setColumns(10);

		JButton submitTransBtn = new JButton("Submit Transaction");
		/*		submitTransBtn.addActionListener(new ActionListener() { // we need this ?
					@Override
					public void actionPerformed(ActionEvent e) {
					}
				});*/
		submitTransBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				//LOG.debug("pressed");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				LOG.debug("released");
				if (submitTransBtn.contains(e.getPoint())) {
					mouseClicked(e); // capture a click event in this case also
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				LOG.info("Sumbit transaction was clicked");

				String recipient_addr_value = recipient_addr.getText();
				float amountValue;
				try {
					amountValue = Float.parseFloat(amount.getText());
					if (recipient_addr_value == null || recipient_addr_value == ""
							|| recipient_addr_value.trim().isEmpty() || amountValue < 0) {
						throw new IllegalArgumentException();
					}
					Triple<PublicKey, String, Integer> triple = node.getNode(recipient_addr_value);
					if (triple == null) {
						JOptionPane.showMessageDialog(tabbedPane,
								"There is not a node with address/id: " + recipient_addr_value, "Input Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					int dialogRes = JOptionPane.showConfirmDialog(tabbedPane,
							"Are you sure you want to transfer " + amountValue + " to " + recipient_addr_value + " ?",
							"Confirm Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (dialogRes == 2) {
						LOG.info("Transaction discarted");
						amount.setText("");
						recipient_addr.setText("");
						return;
					}
					LOG.info("Start function to make the transaction here");
					// TODO call function
					Transaction trans = node.sendFunds(triple.getLeft(), amountValue);
					if (trans == null) {
						JOptionPane.showMessageDialog(tabbedPane, "Insufficient funds", "Input Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						node.sendTrans(trans);
						amount.setText("");
						recipient_addr.setText("");
					}
				} catch (IllegalArgumentException e1) {
					JOptionPane.showMessageDialog(tabbedPane,
							"A problem arised with your arguments. Please validate them in order to continue",
							"Input Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		submitTransBtn.setBounds(230, 167, 122, 23);
		submitTransBtn.setMargin(new Insets(1, 1, 1, 1));
		submitTransBtn.setBorder(new RoundedBorder(5));
		newTransPanel.add(submitTransBtn);

		JLabel msgRecpLabel = new JLabel("Recipient label");
		msgRecpLabel.setBounds(89, 55, 148, 14);
		msgRecpLabel.setText("Recipient's address");
		newTransPanel.add(msgRecpLabel);

		JLabel msgAmtLabel = new JLabel("Amount label");
		msgAmtLabel.setBounds(89, 97, 148, 14);
		msgAmtLabel.setText("Amount to transfer");
		newTransPanel.add(msgAmtLabel);

	}

	private static void initializeViewLastTransPanel(JTabbedPane tabbedPane) {
		LOG.info("START initialize view panel");

		JPanel lastTransPanel = new JPanel();
		tabbedPane.addTab("View last transactions", null, lastTransPanel, null);

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		lastTransPanel.add(scrollPane);

		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Id");
		tableModel.addColumn("Sender");
		tableModel.addColumn("Recipient");
		tableModel.addColumn("Amount");

		table.setModel(tableModel);
		table.setEnabled(false); // by this way cells cannot be edited

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		for (int x = 0; x < table.getColumnCount(); x++) {
			table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
	}

	private static void initializeBalancePanel(JTabbedPane tabbedPane) {
		LOG.info("START initialize balance panel");

		JPanel balancePanel = new JPanel();
		tabbedPane.addTab("Show balance", null, balancePanel, null);

		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setBounds(10, 11, 395, 51);
		lblNewLabel_1.setText("");
		balancePanel.add(lblNewLabel_1);
	}

	private static void initializeHelpPanel(JTabbedPane tabbedPane) {
		LOG.info("START initialize help panel");

		JPanel helpPanel = new JPanel();
		tabbedPane.addTab("Help", null, helpPanel, null);
		helpPanel.setLayout(null);
		// helpPanel.setLayout(new GridLayout(3, 1, 40, 40));

		JLabel newTransLabel = new JLabel("New transaction");
		newTransLabel.setBounds(10, 11, 395, 51);
		newTransLabel.setText(
				"<html><b>New transaction:</b> provide recipient's wallet address as well as specify the amount you wish to transfer</html>");
		helpPanel.add(newTransLabel);

		JLabel viewLastTransLabel = new JLabel("View last transaction");
		viewLastTransLabel.setBounds(10, 72, 395, 51);
		viewLastTransLabel.setText(
				"<html><b>View last transaction:</b> returns the transactions that are contained in the last block</html>");
		helpPanel.add(viewLastTransLabel);

		JLabel balanceLabel = new JLabel("Show balance");
		balanceLabel.setBounds(10, 133, 395, 51);
		balanceLabel.setText("<html><b>Show balance:</b> returns the amount of coins in your wallet</html>");
		helpPanel.add(balanceLabel);
	}
}
