// vendingmachine/users/Member.java
package vendingmachine.users;

public class Member {
    private String phoneNumber;
    private int points; // ห่อหุ้มไว้

    public Member(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.points = 0; // แต้มเริ่มต้น
    }

    public String getCustomerId() { return this.phoneNumber; }

    public int getPoints() { return this.points; }

    public void addPoints(int pointsToAdd) {
        // มี Logic การเพิ่มแต้ม (เช่น 10 บาท = 1 แต้ม)
        this.points += pointsToAdd; 
    }
    
    public boolean isMember() { return true; }
}