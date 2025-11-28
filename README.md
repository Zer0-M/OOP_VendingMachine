# 🤖 Vending Machine Simulation (Java OOP Project)

โปรเจกต์นี้คือโปรแกรมจำลอง **ตู้ขายของอัตโนมัติ** โดยยึดหลักการออกแบบเชิงวัตถุ (Object-Oriented Programming - OOP) ที่แข็งแกร่งและเป็นสัดส่วน

ผู้ใช้สามารถโต้ตอบกับตู้ผ่าน GUI สุดโมเดิร์น เพื่อเลือกสินค้า, หยิบใส่ตะกร้า, เลือกวิธีชำระเงิน (QR/เงินสด), รับสินค้า และรับเงินทอน นอกจากนี้ยังมี "โหมดแอดมิน" ที่ซ่อนไว้สำหรับจัดการสต็อกและเก็บเงินอีกด้วย

## ✨ ฟีเจอร์หลัก (Features)

  * **NEW! Modern GUI:** เปลี่ยนมาใช้หน้าตาแบบกราฟิก (`VendingUI.java`) พร้อม UX/UI ที่สวยงามกว่าเดิม
  * **แสดงรายการสินค้า:** แสดงสินค้าทั้งหมดในตู้ พร้อมราคาและจำนวนคงเหลือ
  * **ตะกร้าสินค้า (Shopping Cart):** ผู้ใช้สามารถเลือกสินค้าหลายชิ้นใส่ตะกร้าก่อนชำระเงิน
  * **ชำระเงิน (Polymorphism):** รองรับการจ่ายเงินหลายรูปแบบ (Strategy Pattern):
      * `💳` **QR Code** (จำลองการจ่ายสำเร็จ)
      * `💰` **เงินสด** (รับเหรียญและธนบัตร 1, 2, 5, 10, 20, 50, 100, 500, 1000)
  * **การจัดการเงินทอนสุดฉลาด:** ระบบใช้ Greedy Algorithm ในการคำนวณเงินทอนเป็นหน่วยย่อย และตรวจสอบเงินทอนในตู้ (Internal Cash) ก่อนรับเงินสด ถ้าเงินทอนไม่พอ จะยกเลิกการซื้อ
  * **ระบบสมาชิก (Member System):**
      * สามารถกรอกเบอร์โทรศัพท์เพื่อสะสมแต้มได้ (1 บาท = 1 แต้ม)
      * **NEW! Data Persistence:** โหลดและบันทึกข้อมูลสมาชิกไปยังไฟล์ `member_data.txt` โดยอัตโนมัติ
  * **จัดการ Error (Exception Handling):** มีการดักจับข้อผิดพลาดอย่างชัดเจน:
      * `❌ OutOfStockException`: ของหมด
      * `❌ InsufficientFundsException`: เงินไม่พอ (หรือผู้ใช้กดยกเลิก)
      * `❌ ChangeNotAvailableException`: ตู้มีเงินทอนไม่พอ

## 🧑‍💼 ฟีเจอร์แอดมิน (Admin Mode)

โปรเจกต์นี้ใช้ `AdminService` เพื่อ **ห่อหุ้ม (Encapsulate)** เมธอดที่อันตรายไว้ ทำให้ View (หน้าตู้ปกติ) ไม่สามารถเรียกใช้ได้

  * **เติมของ (Restock):** เติมสินค้าเข้าช่องที่กำหนด
  * **ปรับราคา/ชื่อ (Set Price/Name):** เปลี่ยนราคาสินค้าและชื่อสินค้าในช่อง
  * **เพิ่มสินค้าใหม่:** สามารถเพิ่มสินค้าใหม่ (Snack/Drink) เข้าตู้ได้ทันทีผ่าน Admin UI
  * **ถอนเงินแบบละเอียด:** สามารถระบุจำนวนเงินที่ต้องการถอนออกมาจากตู้ได้ โดยระบบจะคำนวณและแสดงรายละเอียดแบงค์/เหรียญที่ถูกถอนอย่างชัดเจน (ใช้ Greedy Algorithm)
  * **ดูข้อมูลสมาชิก:** ดูรายการสมาชิกทั้งหมด

-----

## 🏗️ สถาปัตยกรรมและโครงสร้างโปรเจกต์

โปรเจกต์นี้แบ่งความรับผิดชอบ (Separation of Concerns) อย่างชัดเจน โดยใช้โครงสร้างที่คล้ายกับ **Model-View-Controller (MVC)**

  * **View (หน้าจอ):** `VendingUI.java` (GUI)
  * **Controller (สมอง):** `VendingMachineController.java`
  * **Model (ตรรกะ/ข้อมูล):** ทุกคลาสใน Package อื่นๆ (Products, Payment, Users)

```
vendingmachine/
├── VendingMachine.java                         (View - รับ Input/แสดงผล Console)
├── VendingMachineController.java               (Controller - ตัวกลางประสานงาน)
├── VendingUI.java                              (View - หน้าจอ GUI)
│
├── admin/
│   └── AdminService.java                       (Service - รวมเมธอดสำหรับแอดมิน)
│   └── AdminUI.java                            (View - หน้าจอ Admin GUI)
│
├── exceptions/
│   ├── VendingMachineException.java            (คลาสแม่ของ Error)
│   ├── ChangeNotAvailableException.java
│   ├── InsufficientFundsException.java
│   └── OutOfStockException.java
│
├── payment/
│   ├── PaymentMethod.java                      (Interface - ข้อตกลงการจ่ายเงิน)
│   ├── CashReceiver.java                       (Concrete - รับ cash)
│   ├── QRReceiver.java                         (Concrete - รับ QR)
│   ├── MoneyManager.java                       (Service - จัดการเงินทอน, เลือกวิธีจ่าย)
│   └── PaymentResult.java                      (Format - ผลลัพธ์การจ่ายเงิน)
│
├── products/
│   ├── Product.java                            (Abstract - คลาสแม่สินค้า)
│   ├── Drink.java                              (Concrete - สินค้าเครื่องดื่ม)
│   ├── Snack.java                              (Concrete - สินค้าขนม)
│   ├── ItemSlot.java                           (Format - ช่องวางสินค้า 1 ช่อง)
│   ├── InventoryManager.java                   (Service - จัดการคลังสินค้า/สต็อก)
│   └── inventory_data.txt                      (ข้อมูลสินค้าทั้งหมดในตู้)
│
└── users/
    ├── Member.java                             (Format - ข้อมูลสมาชิก)
    ├── MemberDatabase.java                     (Service - จัดการฐานข้อมูลสมาชิก)
    └── member_data.txt                         (ข้อมูลสมาชิก)
```

## 🌟 หลักการ OOP ที่สำคัญในโปรเจกต์นี้

นี่คือหัวใจของการออกแบบโปรเจกต์นี้:

### 1\. 📦 Encapsulation (การห่อหุ้ม)

  * **Why?** เพื่อซ่อนความซับซ้อนและป้องกันข้อมูล (Data Hiding)
  * **Example:** `MoneyManager` ซ่อนตัวแปร `machineMoney` (เงินทอนในตู้) ไว้เป็น `private` คลาสอื่นต้องสั่งงานผ่านเมธอดที่อนุญาตเท่านั้น

### 2\. 🏛️ Inheritance & Abstraction (การสืบทอดและการเป็นนามธรรม)

  * **Why?** เพื่อลดความซ้ำซ้อน และสร้าง "พิมพ์เขียว" (Blueprint)
  * **Example:** `Product` เป็น `abstract class` กำหนดคุณสมบัติพื้นฐาน โดยมี `Drink` และ `Snack` สืบทอดคุณสมบัติไป

### 3\. 🎨 Polymorphism (การมีหลายรูปแบบ)

  * **Why?** เพื่อความยืดหยุ่น โค้ดหลัก (Controller) ไม่ต้องผูกมัดกับวิธีใดวิธีหนึ่ง
  * **Example (Strategy Pattern):** `MoneyManager` เรียกใช้เมธอด `receivePayment()` จาก `PaymentMethod` interface โดยไม่ต้องรู้ว่ากำลังรับเงินจาก `QRReceiver` หรือ `CashReceiver`

### 4\. 🧩 Composition (การประกอบร่าง)

  * **Why?** เพื่อสร้าง Object ที่ซับซ้อนจาก Object ที่เล็กกว่า (หลักการ "Has-A")
  * **Example:** `VendingMachineController` "มี" (`HAS-A`) `InventoryManager`, `MoneyManager`, และ `AdminService` เพื่อรวมความสามารถทั้งหมดไว้ด้วยกัน

-----

## ▶️ วิธีการรัน (How to Run)

1.  เปิดโปรเจกต์ด้วย IDE (เช่น VS Code)
2.  **รัน GUI (แนะนำ):** ไปที่ไฟล์ `vendingmachine/VendingUI.java` และกดปุ่ม "Run"

-----

## ❌ ขีดจำกัดของงาน (Scope)
- จ่ายเงินได้สองระบบ คือ เงินสด กับ QRCode
- ไม่รองรับการทอนเงินสตางค์

-----
Report : https://docs.google.com/document/d/1HKbb4TnQa1G5QExfCQk4326rQAjhPK5ad2Eu5Tft6L4/edit?usp=sharing
