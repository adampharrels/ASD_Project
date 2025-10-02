package uni.space.finder;
public class Facility {
    private static int nextId = 1;

    private int id;
    private String name;

    public Facility(String name) {
        this.id = nextId++;
        this.name = name;

    }
}
