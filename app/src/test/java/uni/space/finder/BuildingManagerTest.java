package uni.space.finder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuildingManagerTest {
    @Test
    void testAddBuilding() {
        BuildingManager manager = new BuildingManager();
        Building building = new Building("BuildingA", "123 Main St");
        manager.addBuilding(building);
        assertTrue(manager.getBuildings().contains(building));
    }
}