package vendingmachine.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MemberDatabase {
    private static final String MEMBER_FILE = "vendingmachine/users/member_data.txt";
    private final Map<String, Member> activeMember = new HashMap<>();
    
    public MemberDatabase() {
        loadMembersFromFile();
    }

    public Member findMemberByPhone(String phoneNumber) {
        return activeMember.get(phoneNumber);
    }

    public void deleteMember(String phoneNumber) {
        activeMember.remove(phoneNumber);
    }

    public String addPointsToMember(String phoneNumber, int pointsToAdd) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty())
            return "Invalid phone number.";
        if (pointsToAdd <= 0)return "No points to add.";

        // ตรวจสอบว่าสมาชิกใหม่หรือเก่า
        boolean wasNewMember = !activeMember.containsKey(phoneNumber);

        // ดึง Member เดิมมาหรือสร้างใหม่ถ้ายังไม่มี
        Member member = activeMember.computeIfAbsent(phoneNumber, key -> new Member(key));

        // เพิ่มแต้มให้สมาชิก
        member.addPoints(pointsToAdd);

        if (wasNewMember) {
            return "Welcome new member! Added " + pointsToAdd + " points. Total: " + member.getPoints();
        } else {
            return "Points added! Member (" + phoneNumber + ") now has " + member.getPoints() + " points.";
        }
    }



    // บันทึกข้อมูลสมาชิกลงไฟล์
    public void saveMembersToFile() {
        try (FileWriter writer = new FileWriter(MEMBER_FILE)) {
            for (Member member : activeMember.values()) {
                writer.write(member.toString() + "\n");
            }
            System.out.println("[System] Member data saved to " + MEMBER_FILE);
        } catch (IOException e) {
            System.out.println("[System] Error saving member data: " + e.getMessage());
        }
    }

    // โหลดข้อมูลสมาชิกจากไฟล์
    private void loadMembersFromFile() {
        File file = new File(MEMBER_FILE);
        if (!file.exists()) {
            System.out.println("[System] Member data file not found. Starting with empty database.");
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
                    member.addPoints(points);

                    activeMember.put(phone, member);
                }
            }
            System.out.println("[System] Loaded " + activeMember.size() + " members from file.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("[System] Error loading member data: " + e.getMessage());
            activeMember.clear();
        }
    }
    
    // สร้างข้อความแสดงรายการสมาชิกทั้งหมด
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
