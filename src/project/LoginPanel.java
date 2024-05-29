package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPanel extends JPanel {

	private Image backgroundImage;
	private JPanel backgroundPanel;

	// main Panel
	private JPanel mainPanel;

	// Host IP
	private JPanel ipPanel;
	private JLabel ipLabel;
	private JTextField inputIP;

	// Port
	private JPanel portPanel;
	private JLabel portLabel;
	private JTextField inputPort;

	// ID
	private JPanel idPanel;
	private JLabel idLabel;
	private JTextField inputId;

	// connect 버튼
	private JButton connectBtn;

	private ClientService clientService;

	public LoginPanel(ClientService clientService) {
		this.clientService = clientService;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("images/background.png").getImage();
		backgroundPanel = new JPanel();

		// 메인 컴포넌트
		mainPanel = new JPanel();

		// IP
		ipPanel = new JPanel();
		ipLabel = new JLabel("HOST IP");
		inputIP = new JTextField(10);

		// Port
		portPanel = new JPanel();
		portLabel = new JLabel("PORT NUMBER");
		inputPort = new JTextField(10);

		// Id
		idPanel = new JPanel();
		idLabel = new JLabel("ID");
		inputId = new JTextField(10);

		connectBtn = new JButton("Connect");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);

		backgroundPanel.setSize(getWidth(), getHeight());
		backgroundPanel.setLayout(null);
		add(backgroundPanel);

		// mainPanel
		mainPanel.setBounds(100, 25, 190, 380);
		mainPanel.setLayout(null);
		mainPanel.setBackground(Color.white);
		mainPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 5)));
		add(mainPanel);

		// IP
		ipPanel.setBounds(30, 40, 120, 100);
		ipPanel.setBackground(new Color(0, 0, 0, 0));
		ipPanel.add(ipLabel);
		ipPanel.add(inputIP);
		mainPanel.add(ipPanel);

		// Port
		portPanel.setBounds(30, 140, 120, 100);
		portPanel.setBackground(new Color(0, 0, 0, 0));
		portPanel.add(portLabel);
		portPanel.add(inputPort);
		mainPanel.add(portPanel);

		// Id
		idPanel.setBounds(30, 240, 120, 100);
		idPanel.setBackground(new Color(0, 0, 0, 0));
		idPanel.add(idLabel);
		idPanel.add(inputId);
		mainPanel.add(idPanel);

		// connectBtn
		connectBtn.setBackground(Color.white);
		connectBtn.setBounds(30, 340, 120, 20);
		mainPanel.add(connectBtn);

		// 테스트
		inputIP.setText("127.0.0.1");
		inputPort.setText("5000");
	}

	private void addEventListener() {
		connectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clickconnectBtn();
			}
		});
	}

	private void clickconnectBtn() {
		if (inputIP.getText() != null && inputPort.getText() != null && inputId.getText() != null) {
			String ip = inputIP.getText();
			String StringPort = inputPort.getText();
			int port = Integer.parseInt(StringPort);
			String id = inputId.getText();

			clientService.clickConnectServerBtn(ip, port, id);
		} else {
			JOptionPane.showMessageDialog(null, "값을 입력하시거나 입력한 정보가 올바르지 않을 수도 있으니 다시 확인해 주세요.", "알림",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void printComponents(Graphics g) {
		super.printComponents(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
	}
}
