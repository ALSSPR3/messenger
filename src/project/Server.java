package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import lombok.Data;

@Data
public class Server {

	// 서버 소켓
	private ServerSocket serverSocket;
	private Socket socket;

	// 유저, 방 리스트
	private Vector<ConnectingUser> connectingUsers = new Vector<>();
	private Vector<Room> rooms = new Vector<>();

	// 로그 저장을 위한 함수
	private FileWriter logWriter;
	private SimpleDateFormat dateFormat;

	// 서버 프레임
	private ServerFrame serverFrame;
	private JTextArea mainTextArea;

	// 방 만들기 관련
	private boolean roomCheck;

	private String protocol;
	private String from;
	private String message;

	// 서버 온오프
	private boolean serverTurn;

	private String id;
	private String roomName;

	private int roomUsers;
	private Vote vote;

	// 오류 아이콘
	private ImageIcon icon = new ImageIcon("images/error_Icon.png");

	public Server() {
		serverFrame = new ServerFrame(this);
		roomCheck = true;
		mainTextArea = serverFrame.getMainArea();
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	// 포트를 입력받아 서버 시작
	public void serverStart() {
		try {
			serverSocket = new ServerSocket(5000);
			serverLogWriter("--- 서버 시작 ---\n");
			serverFrame.getConnectBtn().setEnabled(false);
			serverFrame.getDisConnectBtn().setEnabled(true);
			connectClient();
			serverTurn = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트 번호이거나, 잘못된 포트 번호입니다.", "알림", JOptionPane.ERROR_MESSAGE,
					icon);
			serverLogWriter("이미 사용중인 포트 번호이거나, 잘못된 포트 번호입니다.\n");
			serverFrame.getConnectBtn().setEnabled(true);
			serverFrame.getDisConnectBtn().setEnabled(false);
		}
	}

	// 서버를 끄는 메서드
	public void serverStop() {
		try {
			if (serverTurn) {
				serverSocket.close();
				serverLogWriter("--- 서버 종료 ---\n");
				serverFrame.getConnectBtn().setEnabled(true);
				serverFrame.getDisConnectBtn().setEnabled(false);
				serverTurn = false;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "서버가 정상적으로 종료되지 않았습니다.", "알림", JOptionPane.ERROR_MESSAGE, icon);
			serverLogWriter("서버가 정상적으로 종료되지 않았습니다.\n");
			serverFrame.getConnectBtn().setEnabled(false);
			serverFrame.getDisConnectBtn().setEnabled(true);
		}
	}

	private void connectClient() {
		new Thread(() -> {
			while (true) {
				try {
					socket = serverSocket.accept();
					serverLogWriter("--- Client 접속 대기 ---\n");
					ConnectingUser user = new ConnectingUser(socket);
					user.start();
				} catch (Exception e) {
					serverLogWriter("--- Client 접속 에러 ---\n");
					break;
				}
			}
		}).start();
	}

	private void broadCast(String msg) {
		for (ConnectingUser user : connectingUsers) {
			user.writer(msg);
		}
		serverLogWriter(msg);
	}

	private void serverLogWriter(String str) {
		try {
			logWriter = new FileWriter("LOG.txt", true);
			String now = dateFormat.format(new Date());
			mainTextArea.append(str);
			logWriter.write(now + " :: " + str);
			logWriter.flush();
		} catch (Exception e) {

		}
	}

	private class ConnectingUser extends Thread implements Messengerfunction {

		// 소켓
		private Socket socket;

		// IO
		private BufferedReader reader;
		private BufferedWriter writer;

		// 유저 정보
		private String id;
		private String RoomName;

		public ConnectingUser(Socket socket) {
			this.socket = socket;
			connetIO();
		}

		private void connetIO() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				sendInfo();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "서버 입출력 장치 에러!", "알림", JOptionPane.ERROR_MESSAGE, icon);
				serverLogWriter("!서버 입출력 장치 에러! \n");
			}
		}

		private void sendInfo() {
			try {
				id = reader.readLine();
				serverLogWriter("[로그인] " + id + "님\n");

				newUser();
				newRoom();
				connetingUserList();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "접속 에러 !", "알림", JOptionPane.ERROR_MESSAGE, icon);
				serverLogWriter("!! 접속 에러 !!\n");
			}

		}

		@Override
		public void run() {
			try {
				while (true) {
					String str = reader.readLine();
					checkProtocol(str);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "유저 접속 끊김 !", "알림", JOptionPane.ERROR_MESSAGE, icon);
				serverLogWriter("! ! 유저 " + id + " 접속 끊김 ! !\n");
				for (Room room : rooms) {
					if (room.roomName.equals(this.RoomName)) {
						room.removeRoom(this);
					}
				}
				connectingUsers.remove(this);
				broadCast("UserOut/" + id);
			}
		}

		private void checkProtocol(String str) {
			StringTokenizer tokenizer = new StringTokenizer(str, "/");

			protocol = tokenizer.nextToken();
			from = tokenizer.nextToken();

			if (protocol.equals("Chatting")) {
				message = tokenizer.nextToken();
				chatting();

			} else if (protocol.equals("Whisper")) {
				message = tokenizer.nextToken();
				whisper();

			} else if (protocol.equals("MakeRoom")) {
				makeRoom();

			} else if (protocol.equals("ExitRoom")) {
				exitRoom();

			} else if (protocol.equals("EnterRoom")) {
				enterRoom();

			} else if (protocol.equals("DeleteRoom")) {
				deleteRoom();

			} else if (protocol.equals("kickVote")) {

			}
		}

		private void writer(String str) {
			try {
				System.out.println(str);
				writer.write(str + "\n");
				writer.flush();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "서버 출력 에러 !", "알림", JOptionPane.ERROR_MESSAGE, icon);
			}
		}

		@Override
		public void chatting() {
			serverLogWriter("[메세지] " + from + " - " + message + "\n");

			for (Room room : rooms) {
				if (room.roomName.equals(from)) {
					room.roomBroadCast("Chatting/" + id + "/" + message);
				}
			}
		}

		@Override
		public void whisper() {
			serverLogWriter("[귓속말] " + id + "->" + from + "-" + message + "\n");

			for (ConnectingUser user : connectingUsers) {
				if (user.id.equals(from)) {
					user.writer("Whisper/" + id + "/" + message + "\n");
				}
			}
		}

		@Override
		public void makeRoom() {
			for (Room room : rooms) {
				if (room.roomName.equals(from)) {
					writer("FailMakeRoom/" + from);
					serverLogWriter("[방 생성 실패]" + id + "-" + from + message + "\n");
					roomCheck = false;
				} else {
					roomCheck = true;
				}
			}
			if (roomCheck) {
				roomName = from;
				Room room = new Room(from, this);
				rooms.add(room);
				serverLogWriter("[방 생성] " + id + "-" + from + "\n");

				newRoom();
				writer("MakeRoom/" + from);
			}
		}

		@Override
		public void enterRoom() {
			for (Room room : rooms) {
				if (room.roomName.equals(from)) {
					roomName = from;
					room.addUser(this);
					room.roomBroadCast("Chatting/입장/" + id + "님 입장");
					serverLogWriter("[입장]" + from + "방_" + id + "\n");
					roomUsers = room.size();
					writer("EnterRoom/" + from + "\n");
				}
			}
		}

		@Override
		public void exitRoom() {
			for (Room room : rooms) {
				if (room.roomName.equals(from)) {
					rooms = null;
					room.roomBroadCast("Chatting/퇴장/" + id + "님 퇴장");
					serverLogWriter("[방 퇴장]" + id + "-" + from + "\n");
					writer("OurRoom/" + from + "\n");
				}
			}
		}

		@Override
		public void deleteRoom() {
			for (Room room : rooms) {
				if (room.roomName.equals(from)) {
					room.removeRoom(this);
				}
			}
		}

		@Override
		public void newRoom() {
			broadCast("newRoom/" + from + "\n");
		}

		@Override
		public void connetingUserList() {
			for (ConnectingUser user : connectingUsers) {
				writer("ConnetingUserList/" + user.id + "\n");
			}
		}

		@Override
		public void newUser() {
			connectingUsers.add(this);
			broadCast("NewUser/" + id + " " + "\n");
		}

		@Override
		public void kickVote() {
		}

	}

	private class Room {
		private String roomName;
		private Vector<ConnectingUser> room = new Vector<>();

		public Room(String roomName, ConnectingUser connectingUser) {
			this.roomName = roomName;
			this.room.add(connectingUser);
			connectingUser.RoomName = roomName;
		}

		public int size() {
			return room.size();
		}

		private void roomBroadCast(String msg) {
			for (ConnectingUser user : room) {
				user.writer(msg);
			}
		}

		private void addUser(ConnectingUser connectingUser) {
			room.add(connectingUser);
		}

		private void removeRoom(ConnectingUser user) {
			room.remove(user);
			boolean empty = room.isEmpty();
			if (empty) {
				for (Room room : rooms) {
					if (room.roomName.equals(roomName)) {
						rooms.remove(this);
						serverLogWriter("[방 제거]" + user.id + "-" + from + "\n");
						roomBroadCast("DeleteRoom/" + from + "\n");
						broadCast("EmptyRoom/" + from + "\n");
						break;
					}
				}

			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}

}
