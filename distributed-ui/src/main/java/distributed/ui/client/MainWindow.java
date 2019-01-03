package distributed.ui.client;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

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

import distributed.backend.trlog.DummyMain;
import distributed.ui.operations.RoundedBorder;

public class MainWindow {

   // TODO add a logger
   private JFrame frame;
   private static JTextField recipient_addr;
   private static JTextField amount;
   private static JTable table;

   /**
    * Launch the application.
    */
   public static void main(String[] args) {
      System.out.println("Starting...");
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

   /**
    * Initialize the contents of the frame.
    */
   private void initialize() {
      frame = new JFrame();
      frame.setSize(512, 460);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //frame.getContentPane().setLayout(null);
      frame.setMinimumSize(new Dimension(450, 300));
      //frame.setMaximizedBounds(new Rectangle(100, 100, 720, 600));
      frame.setResizable(false);
      frame.setTitle("Noobcash client");

      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
      URL iconURL = getClass().getResource("bitcoin.png");
      ImageIcon icon = new ImageIcon(iconURL);
      frame.setIconImage(icon.getImage());

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
               DummyMain.main(null);
               break;
            case 1:
               break;
            case 2:
               JLabel lbl = (JLabel) aux.getComponent(0);
               int balance = 5; // TODO replace with function 
               lbl.setText("Your balance is " + balance + " noobcash.");
               break;
            case 3:
               break;
            }
         }
      });
      frame.getContentPane().add(tabbedPane);

      // New trans panel
      initializeNewTransPanel(tabbedPane);

      // View last trans panel
      initializeViewLastTransPanel(tabbedPane);

      // Show balance panel
      initializeBalancePanel(tabbedPane);

      // Help panel
      initializeHelpPanel(tabbedPane);

   }

   private static void initializeNewTransPanel(JTabbedPane tabbedPane) {
      JPanel newTransPanel = new JPanel();
      tabbedPane.addTab("New transaction", null, newTransPanel, null);
      newTransPanel.setLayout(null);

      recipient_addr = new JTextField();          // two textfields for user input
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
      submitTransBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {}
      });
      submitTransBtn.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            String recipient_addr_value = recipient_addr.getText();
            int amountValue;
            try {
               amountValue = Integer.parseInt(amount.getText());
               if (recipient_addr_value == null || recipient_addr_value == "" || recipient_addr_value.trim().isEmpty()
                     || amountValue < 0) {
                  throw new IllegalArgumentException();
               }
               JOptionPane.showMessageDialog(tabbedPane,
                     "Are you sure you want to transfer " + amountValue + " to " + recipient_addr_value + " ?",
                     "Confirm Transaction", JOptionPane.QUESTION_MESSAGE);
               System.out.println("Start transaction");
               // TODO call function
            } catch (IllegalArgumentException e1) {
               JOptionPane.showMessageDialog(tabbedPane,
                     "A problem arised with your arguments. Please validate them in order to continue",
                     "Input Error", JOptionPane.ERROR_MESSAGE);
               System.out.println("Please confirm that arguments specified are valid");
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
      JPanel lastTransPanel = new JPanel();
      tabbedPane.addTab("View last transactions", null, lastTransPanel, null);

      table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      lastTransPanel.add(scrollPane);
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

      DefaultTableModel tableModel = new DefaultTableModel();
      tableModel.addColumn("Id");
      tableModel.addColumn("Sender");
      tableModel.addColumn("Recipient");
      tableModel.addColumn("Amount");
      for (int i = 0; i < 10; i++) {
         tableModel.addRow(new Object[] {
               i, "sender", "receiver", "5"
         });
      }
      table.setModel(tableModel);
      table.setEnabled(false);   // by this way cells cannot be edited
      for (int x = 0; x < table.getColumnCount(); x++) {
         table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
      }
   }

   private static void initializeBalancePanel(JTabbedPane tabbedPane) {
      JPanel balancePanel = new JPanel();
      tabbedPane.addTab("Show balance", null, balancePanel, null);

      JLabel lblNewLabel_1 = new JLabel("New label");
      lblNewLabel_1.setBounds(10, 11, 395, 51);
      lblNewLabel_1.setText("");
      balancePanel.add(lblNewLabel_1);
   }

   private static void initializeHelpPanel(JTabbedPane tabbedPane) {
      JPanel helpPanel = new JPanel();
      tabbedPane.addTab("Help", null, helpPanel, null);
      helpPanel.setLayout(null);
      //helpPanel.setLayout(new GridLayout(3, 1, 40, 40));

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
      balanceLabel.setText(
            "<html><b>Show balance:</b> returns the amount of coins in your wallet</html>");
      helpPanel.add(balanceLabel);
   }
}
