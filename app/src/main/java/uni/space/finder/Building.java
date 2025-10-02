package uni.space.finder;
import java.util.ArrayList;
import java.util.List;

public class Building {
    private static int nextId = 1;

    private int id;
    private String name;
    private List<Room> rooms;

    public Building(String name) {
        this.id = nextId++;
        this.name = name;
        this.rooms = new ArrayList<>();
    }

    

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
