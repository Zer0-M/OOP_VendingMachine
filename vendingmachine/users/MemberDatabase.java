package vendingmachine.users;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MemberDatabase {
    private Map<String, Member> activeMember = new HashMap<>();

    public boolean hasMember(String phoneNumber) {
        for (Member Member : activeMember) {
            if (Member.getCustomerId().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    public void deleteMember(String phoneNumber) {
        int indexMember = 0;
        for (Member Member : activeMember) {
            if (Member.getCustomerId().equals(phoneNumber)) {
                break;
            }
            indexMember++;
        }
        activeMember.remove(indexMember);
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
