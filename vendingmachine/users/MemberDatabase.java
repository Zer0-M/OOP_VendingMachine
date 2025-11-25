package vendingmachine.users;

import java.util.HashMap;
import java.util.Map;

public class MemberDatabase {
    private Map<String, Member> activeMember = new HashMap<>();

    public Member findMemberByPhone(String phoneNumber) {
        return activeMember.get(phoneNumber); // คืนค่า Member หรือ null ถ้าไม่เจอ
    }

    public void deleteMember(String phoneNumber) {
        activeMember.remove(phoneNumber);
    }

    public String addPointsToMember(String phoneNumber, int pointsToAdd) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Invalid phone number.";
        }
        if (pointsToAdd <= 0) {
            return "No points to add."; // ไม่ต้องทำอะไร ถ้าแต้มเป็น 0
        }

        // ตรวจสอบสถานะก่อนว่า "เป็นสมาชิกใหม่หรือไม่"
        boolean wasNewMember = !activeMember.containsKey(phoneNumber);

        Member member = activeMember.computeIfAbsent(phoneNumber, key -> new Member(key));

        member.addPoints(pointsToAdd);

        // คืนค่าข้อความ Status ให้ Controller เอาไปโชว์
        if (wasNewMember) {
            return "Welcome new member! Added " + pointsToAdd + " points. Total: " + member.getPoints();
        } else {
            return "Points added! Member (" + phoneNumber + ") now has " + member.getPoints() + " points.";
        }
    }
}
