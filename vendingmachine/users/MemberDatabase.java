package vendingmachine.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
// [NEW IMPORTS] สำหรับ File I/O
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MemberDatabase {
    private Map<String, Member> activeMember = new HashMap<>();
    private static final String MEMBER_FILE = "vendingmachine/users/member_data.txt";

    public MemberDatabase() {
        // [UPDATE] ลองโหลดข้อมูลสมาชิกตั้งแต่เริ่มต้นโปรแกรม
        loadMembersFromFile(); //
    }

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

    // [NEW METHOD] บันทึกข้อมูลสมาชิกทั้งหมดลงไฟล์
    public void saveMembersToFile() {
        try (FileWriter writer = new FileWriter(MEMBER_FILE)) { //
            for (Member member : activeMember.values()) {
                writer.write(member.toString() + "\n");
            }
            System.out.println("Member data saved to " + MEMBER_FILE);
        } catch (IOException e) {
            System.out.println("Error saving member data: " + e.getMessage());
        }
    }

    // [NEW METHOD] โหลดข้อมูลสมาชิกจากไฟล์ (ถูกเรียกใน Constructor)
    private void loadMembersFromFile() {
        File file = new File(MEMBER_FILE);
        if (!file.exists()) {
            System.out.println("Member data file not found. Starting with empty database.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String phone = parts[0].trim();
                    int points = Integer.parseInt(parts[1].trim());

                    Member member = new Member(phone);
                    member.addPoints(points); //

                    activeMember.put(phone, member);
                }
            }
            System.out.println("Loaded " + activeMember.size() + " members from file.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading member data: " + e.getMessage());
            activeMember.clear();
        }
    }
    
    // [NEW METHOD] สร้างข้อความแสดงรายการสมาชิกทั้งหมด
    public String getAllMembersDisplay() {
        if (activeMember.isEmpty()) {
            return "No active members found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Member List ---\n");
        sb.append(String.format("%-15s | %s\n", "Phone Number", "Points"));
        sb.append("--------------------------------\n");
        
        for (Map.Entry<String, Member> entry : activeMember.entrySet()) {
            sb.append(String.format("%-15s | %d\n", entry.getKey(), entry.getValue().getPoints()));
        }
        sb.append("-------------------\n");
        return sb.toString();
    }
}
