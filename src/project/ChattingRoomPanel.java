package project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Data;

@Data
public class ChattingRoomPanel extends JPanel implements ActionListener {

	private Image backgroundImage;
	private JPanel backgroundPanel;

	// Panel
	private JPanel mainPanel;
	private JPanel userListPanel;
	private JPanel sendMsgPanel;
	private JPanel btnPanel;
	private ScrollPane scrollPane; // 스크롤

	// 메세지 Field
	private JTextArea chatingBox;
	private JTextField msgBox;

	// 버튼
	private JButton sendBtn;
	private JButton exitBtn;
	private JButton whisperBtn;

	// 유저 List
	private JList<String> roomUserList;

	private ClientService clientService;

	public ChattingRoomPanel(ClientService clientService) {
		this.clientService = clientService;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("images/background.png").getImage();
		backgroundPanel = new JPanel();

		mainPanel = new JPanel();
		userListPanel = new JPanel();
		sendMsgPanel = new JPanel();
		btnPanel = new JPanel();

		scrollPane = new ScrollPane();

		roomUserList = new JList<String>();

		chatingBox = new JTextArea();
		msgBox = new JTextField(17);

		sendBtn = new JButton("전송");
		exitBtn = new JButton("나가기");
		whisperBtn = new JButton("귓속말");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);
		setVisible(true);

		// 배경
		backgroundPanel.setSize(getWidth(), getHeight());
		backgroundPanel.setLayout(null);
		add(backgroundPanel);

		// 채팅 textArea, 유저 List panel
		chatingBox.setEnabled(false);
		mainPanel.setBounds(40, 20, 200, 325);
		mainPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 5), "Message"));
		mainPanel.setBackground(Color.white);
		userListPanel.setBounds(240, 20, 100, 325);
		userListPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 5), "user"));
		userListPanel.setBackground(Color.WHITE);
		userListPanel.add(roomUserList);
		mainPanel.add(scrollPane);
		scrollPane.setBounds(45, 15, 180, 290);
		scrollPane.add(chatingBox);

		add(mainPanel);
		add(userListPanel);

		sendBtn.setPreferredSize(new Dimension(70, 20));
		sendMsgPanel.setBounds(43, 365, 294, 35);
		sendMsgPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 3)));
		sendMsgPanel.setBackground(Color.white);
		sendMsgPanel.add(msgBox);
		sendMsgPanel.add(sendBtn);
		add(sendMsgPanel);

		btnPanel.setBounds(43, 395, 294, 35);
		btnPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 3)));
		btnPanel.setBackground(Color.white);
		exitBtn.setPreferredSize(new Dimension(80, 20));
		whisperBtn.setPreferredSize(new Dimension(80, 20));
		btnPanel.add(exitBtn);
		btnPanel.add(whisperBtn);

		whisperBtn.setEnabled(false);
		sendBtn.setEnabled(false);
		exitBtn.setEnabled(false);

		add(btnPanel);

	}

	private void addEventListener() {
		msgBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		sendBtn.addActionListener(this);
		exitBtn.addActionListener(this);
		whisperBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendBtn) {
			sendMessage();
		}
		if (e.getSource() == exitBtn) {
			exit();
		}
		if (e.getSource() == whisperBtn) {
			Whisper();
		}
	}

	private void sendMessage() {
		if (!msgBox.getText().equals(null)) {
			String msg = msgBox.getText();
			clientService.clickSendMessageBtn(msg);
			msgBox.setText("");
			msgBox.requestFocus();
		}
	}

	private void exit() {
		clientService.clickExitRoomBtn();
		chatingBox.setText("");
		whisperBtn.setEnabled(false);
		sendBtn.setEnabled(false);
		exitBtn.setEnabled(false);
	}

	private void Whisper() {
		String msg = JOptionPane.showInputDialog("[ 귓 속말 ]");
		if (!msg.equals(null)) {
			clientService.clickSendwhisperBtn(msg);
			roomUserList.setSelectedValue(null, false);
		}
	}

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
	}

}
