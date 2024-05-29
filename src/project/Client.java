package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client implements Messengerfunction, ClientService {

	// 프레임
	private ClientFrame clientFrame;

	// 프레임에서 사용하는 멤버변수
	private JTextArea chatingBox;
	private JList<String> userList;
	private JList<String> roomList;
	private JList<String> roomUserList;
	private JButton makeRoomBtn;
	private JButton enterRoomBtn;
	private JButton deleteRoomBtn;
	private JButton whisperBtn;
	private JButton sendMsgRoomBtn;
	private JButton exitRoomBtn;
	private JButton kickVoteBtn;

	// 소켓
	private Socket socket;
	private String ip;
	private int port;

	// 유저 정보
	private String id;
	private String roomName;
	private int roomNumber;
	private Vector<String> userIDList = new Vector<>();
	private Vector<String> roomNameList = new Vector<>();

	// Tokenizer 변수
	private String protocol;
	private String from;
	private String message;

	// 입출력
	private BufferedReader reader;
	private BufferedWriter writer;

	// 에러 아이콘
	private ImageIcon icon = new ImageIcon("images/error_icon.png");

	// 타임스탬프
	private SimpleDateFormat dataFormat;

	public Client() {
		clientFrame = new ClientFrame(this);
		chatingBox = clientFrame.getChattingRoomPanel().getChatingBox();
		userList = clientFrame.getWattingRoomPanel().getUserList();
		roomList = clientFrame.getWattingRoomPanel().getRoomList();
		makeRoomBtn = clientFrame.getWattingRoomPanel().getMakeRoomBtn();
		enterRoomBtn = clientFrame.getWattingRoomPanel().getEnterRoomBtn();
		deleteRoomBtn = clientFrame.getWattingRoomPanel().getDeleteRoomBtn();
		whisperBtn = clientFrame.getWattingRoomPanel().getWhisperBtn();
		roomUserList = clientFrame.getChattingRoomPanel().getRoomUserList();
		sendMsgRoomBtn = clientFrame.getChattingRoomPanel().getSendBtn();
		exitRoomBtn = clientFrame.getChattingRoomPanel().getExitBtn();
		kickVoteBtn = clientFrame.getChattingRoomPanel().getKickBtn();

		this.dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public void clickConnectServerBtn(String ip, int port, String id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
		try {
			connectPort();
			connectIO();

			writer(id.trim());

			clientFrame.setTitle(id);

			clientFrame.getLoginPanel().getConnectBtn().setEnabled(false);
			makeRoomBtn.setEnabled(true);
			enterRoomBtn.setEnabled(false);
			deleteRoomBtn.setEnabled(false);
			whisperBtn.setEnabled(false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "접속 에러 !", "알림", JOptionPane.ERROR_MESSAGE, icon);
		}
	}

	// 포트 연결
	private void connectPort() {
		try {
			socket = new Socket(ip, port);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "접속 실패 !!", "알림", JOptionPane.ERROR_MESSAGE, icon);
		}
	}

	// 입출력 초기화
	private void connectIO() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			readThread();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "클라이언트 입출력 장치 에러", "알림", JOptionPane.ERROR_MESSAGE, icon);
		}
	}

	private void readThread() {
		new Thread(() -> {
			while (true) {
				try {
					String msg = reader.readLine();
					checkProtocol(msg);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "클라이언트 입력 장치 에러 !", "알림", JOptionPane.ERROR_MESSAGE, icon);
					break;
				}
			}
		}).start();
	}

	private void writer(String str) {
		try {
			writer.write(str + "\n");
			writer.flush();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "클라이언트 출력 장치 에러 !", "알림", JOptionPane.ERROR_MESSAGE, icon);
		}
	}

	private void checkProtocol(String msg) {
		StringTokenizer tokenizer = new StringTokenizer(msg, "/");

		if (tokenizer.hasMoreTokens()) {
			protocol = tokenizer.nextToken();
		} else {
			return;
		}

		if (tokenizer.hasMoreTokens()) {
			from = tokenizer.nextToken();
		} else {
			return;
		}

		if (protocol.equals("Chatting")) {
			message = tokenizer.nextToken();
			chatting();
		} else if (protocol.equals("Whisper")) {
			message = tokenizer.nextToken();
			whisper();
		} else if (protocol.equals("MakeRoom")) {
			makeRoom();
		} else if (protocol.equals("EnterRoom")) {
			enterRoom();
		} else if (protocol.equals("DeleteRoom")) {
			deleteRoom();
		} else if (protocol.equals("NewRoom")) {
			newRoom();
		} else if (protocol.equals("NewUser")) {
			newUser();
		} else if (protocol.equals("ConnetingUserList")) {
			connetingUserList();
		} else if (protocol.equals("EmptyRoom")) {
			roomNameList.remove(from);
			roomList.setListData(roomNameList);
			makeRoomBtn.setEnabled(true);
			enterRoomBtn.setEnabled(true);
			deleteRoomBtn.setEnabled(false);
		} else if (protocol.equals("FailMakeRoom")) {
			JOptionPane.showMessageDialog(null, from + "님의 메세지\n\"" + message + "\"", "[비밀메세지]",
					JOptionPane.PLAIN_MESSAGE);
		} else if (protocol.equals("UserExit")) {
			userIDList.remove(from);
			userList.setListData(userIDList);
		}

	}

	@Override
	public void chatting() {
		if (id.equals(from)) {
			chatingBox.append("[ 나 ]\n" + message + "\n");
		} else if (from.equals("입장")) {
			chatingBox.append("▶" + from + "◀" + message + "\n");
		} else if (from.equals("퇴장")) {
			chatingBox.append("▷" + from + "◁" + message + "\n");
		} else {
			chatingBox.append("[" + from + "] \n" + message + "\n");
		}
	}

	@Override
	public void whisper() {
		JOptionPane.showMessageDialog(null, from + "님의 메세지" + message + "\n");
	}

	@Override
	public void makeRoom() {
		roomName = from;
		makeRoomBtn.setEnabled(false);
		enterRoomBtn.setEnabled(false);
		deleteRoomBtn.setEnabled(false);
	}

	@Override
	public void newRoom() {
		roomNameList.add(from);
		if (roomNameList.size() != 0) {
			roomList.setListData(roomNameList);
		}
	}

	@Override
	public void enterRoom() {
		roomName = from;
		makeRoomBtn.setEnabled(false);
		enterRoomBtn.setEnabled(false);
		deleteRoomBtn.setEnabled(false);
		userIDList.add(from);
		roomUserList.setListData(userIDList);
	}

	@Override
	public void deleteRoom() {
		if (clientFrame.getChattingRoomPanel().getRoomUserList() != null) {
			roomList = null;
			makeRoomBtn.setEnabled(true);
			enterRoomBtn.setEnabled(true);
			deleteRoomBtn.setEnabled(false);
		}
	}

	@Override
	public void exitRoom() {

	}

	@Override
	public void newUser() {
		if (!from.equals(this.id)) {
			userIDList.add(from);
			userList.setListData(userIDList);
		}
	}

	@Override
	public void connetingUserList() {
		userIDList.add(from);
		userList.setListData(userIDList);
	}

	@Override
	public void kickVote() {

	}

	@Override
	public void clickSendMessageBtn(String messageText) {
		writer("Chatting/" + roomName + "/" + messageText);
	}

	@Override
	public void clickSendwhisperBtn(String msg) {
		String user = (String) clientFrame.getWattingRoomPanel().getUserList().getSelectedValue();
		writer("Whisper/" + user + "/" + msg);
	}

	@Override
	public void clickMakeRoomBtn(String roomName) {
		writer("MakeRoom/" + roomName);
	}

	@Override
	public void clickExitRoomBtn() {
		String thisRoomName = (String) clientFrame.getWattingRoomPanel().getRoomList().getSelectedValue();
		writer("ExitRoom/" + thisRoomName);
	}

	@Override
	public void clickEnterRoomBtn(String roomName) {
		writer("EnterRoom/" + roomName);
	}

	@Override
	public void clickDeleteRoomBtn(String roomName) {
		writer("DeleteRoom/" + roomName);
	}

	@Override
	public void clickKickVoteBtn(String id) {
		writer("kickVote/" + id);
	}

	public static void main(String[] args) {
		new Client();
	}
}
