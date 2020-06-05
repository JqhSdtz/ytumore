package bean;

public class RoomBean {
    private String roomNo;
    private String roomType;
    private int seatNum;
    private boolean[] available;
    private int secNum;

    public boolean[] getAvailable() {
        return available;
    }

    public void setAvailable(boolean[] available) {
        this.available = available;
    }

    public int getSecNum() {
        return secNum;
    }

    public void setSecNum(int secNum) {
        this.secNum = secNum;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }
}
