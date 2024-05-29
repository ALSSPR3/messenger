package project;

public interface ClientService {
	void clickConnectServerBtn(String ip, int port, String id);

	void clickSendMessageBtn(String messageText);

	void clickSendwhisperBtn(String msg);

	void clickMakeRoomBtn(String roomName);

	void clickExitRoomBtn();

	void clickEnterRoomBtn(String roomName);

	void clickDeleteRoomBtn(String roomName);

	void clickKickVoteBtn(String id);
}
