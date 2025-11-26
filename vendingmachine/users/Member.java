package vendingmachine.users;

public class Member {
    private final String phoneNumber;
    private int points;

    public Member(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.points = 0;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        if (pointsToAdd > 0) {
            points += pointsToAdd;
        }
    }

    @Override public String toString() {
        return phoneNumber + "," + points;
    }
}
