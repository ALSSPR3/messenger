package project;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientFrame extends JFrame {

	private JTabbedPane tabPane;
	private JPanel mainPanel;

	private LoginPanel loginPanel;
	private WattingRoomPanel wattingRoomPanel;
	private ChattingRoomPanel chattingRoomPanel;

	private ClientService clientService;

	public ClientFrame(ClientService clientService) {
		this.clientService = clientService;
		initData();
		setInitLayout();
	}

	private void initData() {
		loginPanel = new LoginPanel(clientService);
		wattingRoomPanel = new WattingRoomPanel(clientService);
		chattingRoomPanel = new ChattingRoomPanel(clientService);
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		mainPanel = new JPanel();
	}

	private void setInitLayout() {
		setTitle("Chatting");
		setSize(400, 500);
//		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
		setContentPane(mainPanel);

		tabPane.setBounds(0, 0, getWidth(), getHeight());
		mainPanel.add(tabPane);

		tabPane.addTab("로그인", loginPanel);
		tabPane.addTab("대기실", wattingRoomPanel);
		tabPane.addTab("채팅방", chattingRoomPanel);

		setVisible(true);
	}

	public static void main(String[] args) {
		new ClientFrame(null);
	}
}
