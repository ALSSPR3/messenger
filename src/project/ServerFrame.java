package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Getter;

@Getter
public class ServerFrame extends JFrame {

	private Server mContext;

	private BackgroundPanel backgroundPanel;

	// 텍스트 로그
	private JPanel mainPanel;
	private JTextArea mainArea;
	private ScrollPane mainScrollPane;

	// port
	private JPanel portPanel;
	private JTextField inputPort;
	private JLabel portLabel;
	private JButton connectBtn;
	private JButton disConnectBtn;

	public ServerFrame(Server mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundPanel = new BackgroundPanel();

		// 텍스트 로그
		mainPanel = new JPanel();
		mainArea = new JTextArea();
		mainScrollPane = new ScrollPane();

		// port
		portPanel = new JPanel();
		portLabel = new JLabel("PORT NUMBER");
		inputPort = new JTextField(10);
		connectBtn = new JButton("connect");
		disConnectBtn = new JButton("disConnect");
		disConnectBtn.setEnabled(false);

		inputPort.setText("5000"); // 테스트
	}

	private void setInitLayout() {
		setTitle("메신저");
		setSize(500, 500);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setVisible(true);
		// backgroundPanel
		backgroundPanel.setSize(getWidth(), getHeight());
		backgroundPanel.setLayout(null);
		add(backgroundPanel);

		// port
		portPanel.setBounds(50, 30, 400, 50);
		portPanel.setBackground(new Color(0, 0, 0, 0));
		portPanel.add(portLabel);
		portPanel.add(inputPort);
		portPanel.add(connectBtn);
		portPanel.add(disConnectBtn);
		backgroundPanel.add(portPanel);

		// 텍스트 로그
		mainPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 5), "SERVER LOG"));
		mainPanel.setBounds(35, 100, 420, 350);
		mainPanel.setBackground(Color.white);

		mainArea.setEnabled(false);
		mainPanel.add(mainScrollPane);
		mainScrollPane.setBounds(10, 23, 400, 315);
		mainScrollPane.add(mainArea);
		backgroundPanel.add(mainPanel);

	}

	private void addEventListener() {
		connectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mContext.serverStart();
			}
		});

		disConnectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mContext.serverStop();
			}
		});
	}

	private class BackgroundPanel extends JPanel {
		private JPanel backgroundPanel;
		private Image backgroundImage;

		public BackgroundPanel() {
			backgroundImage = new ImageIcon("images/background.png").getImage();
			backgroundPanel = new JPanel();
			add(backgroundPanel);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		}
	}

}
