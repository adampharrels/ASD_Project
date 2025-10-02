package uni.space.finder;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private static Room roomOneOne; 
    private static Room roomOneTwo;
    private static Room roomOneThree;
    private static Room roomTwoOne; 
    private static Room roomTwoTwo;
    private static Room roomTwoThree;
    private static Room roomTwoFour; 
    private static Room roomThreeOne; 
    private static Room roomThreeTwo;
    private static Room roomThreeThree;
    private static Room roomThreeFour; 

    public static void constructRooms() {
        
        List<Room> rooms = new ArrayList<>();

        roomOneOne = new Room(1, 1, 0, List.of(FacilityManager.Table));
        roomOneTwo = new Room(1, 2, 0, List.of(FacilityManager.Table, FacilityManager.TV));
        roomOneThree = new Room(1, 3, 0, List.of(FacilityManager.Table, FacilityManager.TV));
        roomTwoOne = new Room(2, 1, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard));
        roomTwoTwo = new Room(2, 2, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard));
        roomTwoThree = new Room(2, 3, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard));
        roomTwoFour = new Room(2, 4, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard));
        roomThreeOne = new Room(3, 1, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard, FacilityManager.TV));
        roomThreeTwo = new Room(3, 2, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard, FacilityManager.TV));
        roomThreeThree = new Room(3, 3, 0, List.of(FacilityManager.Table, FacilityManager.TV));
        roomThreeFour = new Room(3, 4, 0, List.of(FacilityManager.Table, FacilityManager.Whiteboard));
    } 
}
