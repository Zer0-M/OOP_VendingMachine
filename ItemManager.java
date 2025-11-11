import java.util.LinkedHashMap;

class ItemManager {
    
    static int current_id = 001;
    
    public static int getNextId() {
        return current_id++;
    }
    
    public static LinkedHashMap<String, ItemSlot[]> Initialize() {
        LinkedHashMap<String, ItemSlot[]> temp = new LinkedHashMap<>();
        ItemSlot[] slots0, slots1;

        // ตั้งค่าสินค้า
        slots0 = new ItemSlot[4];
        slots0[0] = new ItemSlot(new Snack(getNextId(), "Lays", 15.0, 330), 5);
        slots0[1] = new ItemSlot(new Snack(getNextId(),"Testo", 10.0, 500), 5);
        slots0[2] = new ItemSlot(new Snack(getNextId(),"Chips", 20.0, 100), 3);
        slots0[3] = new ItemSlot(new Snack(getNextId(),"Cookie", 12.0, 50), 4);
        temp.put("Snack", slots0);

        slots1 = new ItemSlot[4];
        slots1[0] = new ItemSlot(new Drink(getNextId(), "Coke", 15.0, 330), 5);
        slots1[1] = new ItemSlot(new Drink(getNextId(), "Water", 10.0, 500), 5);
        slots1[2] = new ItemSlot(new Drink(getNextId(), "Pepsi", 20.0, 100), 3);
        slots1[3] = new ItemSlot(new Drink(getNextId(), "Soda", 12.0, 50), 4);
        temp.put("Drink", slots1);

        return temp;
    }
}