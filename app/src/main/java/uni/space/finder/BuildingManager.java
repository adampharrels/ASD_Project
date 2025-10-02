package uni.space.finder;
public class BuildingManager {

    private static Building buildingOne; 
    private static Building buildingTwo;
    private static Building buildingThree;

    public static void constructBuildings() {
        
        buildingOne = new Building("Building One");
        buildingTwo = new Building("Building Two");
        buildingThree = new Building("Building Three");
    } 
}
