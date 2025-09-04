public class Room {
    int id;
    int building;
    int roomNum;
    int level;
    int type;
    String name;

    

    public Room() {
        id = 0;
        building = 0;
        roomNum = 0;
        level = 0;
        type = null;
        name = "";
    }

    public void constructName() {
        this.name = "Room " + String.valueOf(this.building) + "." + String.valueOf(this.level) + "." + String.valueOf(this.roomNum);
    }
}

