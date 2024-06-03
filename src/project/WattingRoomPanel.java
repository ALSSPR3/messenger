package project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Data;

@Data
public class WattingRoomPanel extends JPanel implements ActionListener {

	private Image backgroundImage;
	private JPanel backgroundPanel;

	private JPanel userListPanel;
	private JPanel roomListPanel;
	private JPanel btnPanel;
	private JPanel whisperPanel;

	private JList<String> userList;
	private JList<String> roomList;

	private JButton makeRoomBtn;
	private JButton enterRoomBtn;
	private JButton deleteRoomBtn;

	private JButton whisperBtn;

	private ClientService clientService;
	
	private String roomName;

	public WattingRoomPanel(ClientService clientService) {
		this.clientService = clientService;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("images/background.png").getImage();
		backgroundPanel = new JPanel();

		userListPanel = new JPanel();
		roomListPanel = new JPanel();
		btnPanel = new JPanel();
		whisperPanel = new JPanel();

		userList = new JList<>();
		roomList = new JList<>();

		makeRoomBtn = new JButton("makeRoom");
		enterRoomBtn = new JButton("enterRoom");
		deleteRoomBtn = new JButton("deleteRoom");
		whisperBtn = new JButton("send Message");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);

		userListPanel.setBounds(50, 30, 120, 260);
		userListPanel.setBackground(Color.WHITE);
		userListPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 3), "user List"));

		userListPanel.add(userList);
		add(userListPanel);

		roomListPanel.setBounds(230, 30, 120, 260);
		roomListPanel.setBackground(Color.WHITE);
		roomListPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 3), "room List"));
		roomListPanel.add(roomList);
		add(roomListPanel);

		btnPanel.setBounds(50, 310, 300, 30);
		btnPanel.setBackground(Color.WHITE);
		btnPanel.setLayout(null);

		makeRoomBtn.setBackground(Color.WHITE);
		makeRoomBtn.setBounds(0, 5, 100, 25);
		makeRoomBtn.setEnabled(false);

		enterRoomBtn.setBackground(Color.WHITE);
		enterRoomBtn.setBounds(100, 5, 95, 25);
		enterRoomBtn.setEnabled(false);

		deleteRoomBtn.setBackground(Color.WHITE);
		deleteRoomBtn.setBounds(200, 5, 95, 25);
		deleteRoomBtn.setEnabled(false);

		btnPanel.add(makeRoomBtn);
		btnPanel.add(deleteRoomBtn);
		btnPanel.add(enterRoomBtn);
		add(btnPanel);

		whisperBtn.setBounds(30, 12, 240, 20);
		whisperBtn.setBackground(Color.WHITE);
		whisperBtn.setEnabled(false);

		whisperPanel.setBounds(50, 360, 300, 40);
		whisperPanel.setBackground(Color.WHITE);
		whisperPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2)));
		whisperPanel.setLayout(null);
		whisperPanel.add(whisperBtn);
		add(whisperPanel);
	}

	private void addEventListener() {
		makeRoomBtn.addActionListener(this);
		deleteRoomBtn.addActionListener(this);
		whisperBtn.addActionListener(this);
		enterRoomBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == whisperBtn) {
			String msg = JOptionPane.showInputDialog("[ 귓 속말 ]");
			if (!msg.equals(null)) {
				clientService.clickSendwhisperBtn(msg);
				userList.setSelectedValue(null, false);
			}
		} else if (e.getSource() == makeRoomBtn) {
			roomName = JOptionPane.showInputDialog("[ 방 이름 ]");
			if (!roomName.equals(null)) {
				clientService.clickMakeRoomBtn(roomName);
			}
		} else if (e.getSource() == enterRoomBtn) {
			String roomName = roomList.getSelectedValue();
			clientService.clickEnterRoomBtn(roomName);
			roomList.setSelectedValue(null, false);
		} else if (e.getSource() == deleteRoomBtn) {
			String roomName = roomList.getSelectedValue();
			clientService.clickDeleteRoomBtn(roomName);
			roomList.setSelectedValue(null, false);
		}
	}

	@Override
	protected void printComponent(Graphics g) {
		super.printComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
	}

	public static void main(String[] args) {
		new WattingRoomPanel(null);
	}
}
