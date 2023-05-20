package onetoone.Friends;

public class FriendRequestDto {
    private int senderId;
    private int receiverId;

    public FriendRequestDto() {
    }

    public FriendRequestDto(int senderId, int receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
}