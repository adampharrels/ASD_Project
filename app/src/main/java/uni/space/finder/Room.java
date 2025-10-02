package uni.space.finder;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private static int nextId = 1;


    private int id;
    private int building;
    private int roomNum;
    private int level;
    private String name = "Building " + building + "Level " + level + "Room " + roomNum;
    private List<Facility> facilities;

    

    public Room(int building, int roomNum, int level, List<Facility> facility) {
        this.id = 0;
        this.building = 0;
        this.roomNum = 0;
        this.level = 0;
        this.name = name;
        this.facilities = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getBuilding() {
        return building;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    
}

