package vendingmachine.users;

public class Member {
    private String phoneNumber;
    private int points; // ห่อหุ้มไว้

    public Member(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.points = 0; // แต้มเริ่มต้น
    }

    public String getCustomerId() {
        return this.phoneNumber;
    }

    public int getPoints() {
        return this.points;
    }

    public void addPoints(int pointsToAdd) {
        if (pointsToAdd > 0)
            this.points += pointsToAdd;
    }
}