import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;


//class for position objects with x and y
class Posn {
  int x;
  int y;

  Posn (int x, int y) {
    this.x = x;
    this.y = y;
  }
}


//class representing a user of the carpool app
class User {
  int role;
  int id;
  String name;

  User(int role, int id, String name) {
    this.role = role;
    this.id = id;
    this.name = name;
  }
}

//representing a request made in the carpool app
class Request {
  int rider;
  int driver;
  boolean accepted;

  Request(int rider, int driver, boolean accepted) {
    this.rider = rider;
    this.driver = driver;
    this.accepted = accepted;
  }

  //EFFECT: processes this request by adding to an existing group or making a new one
  void processReqs (ArrayList<Group> groups) {
    if (this.accepted) {
      boolean driverGroupExisted = false;
      for (Group existingGroup : groups) {
        if (existingGroup.driverId == this.driver) {
          driverGroupExisted = true;
          existingGroup.riderIds.add(this.rider);
        }
      }
      if (!driverGroupExisted) {
        groups.add(new Group (this.driver, new ArrayList<Integer>(Arrays.asList(this.rider)), 
            new Posn(0,0), new Posn(0,0)));
      }
    }
  }
}

//represents a formed carpool group
class Group {
  int driverId;
  ArrayList<Integer> riderIds;
  Posn averagePickup;
  Posn averageDropoff;

  Group (int driverId, ArrayList<Integer> riderIds, Posn averagePickup, Posn averageDropoff) {
    this.driverId = driverId;
    this.riderIds = riderIds;
    this.averagePickup = averagePickup;
    this.averageDropoff = averageDropoff;
  }

  //EFFECT: calculates and sets this groups' average pickup location
  void processAvgPickup(LocationGrid pickupLocations) {
    ArrayList<Posn> LoPosn = new ArrayList<Posn>();

    LoPosn.add(pickupLocations.findLocation(this.driverId));

    for (int rider : this.riderIds) {
      LoPosn.add(pickupLocations.findLocation(rider));
    }

    int xSum = 0;
    int ySum = 0;
    for (Posn pos : LoPosn) {
      xSum = xSum + pos.x;
      ySum = ySum + pos.y;
    }

    this.averagePickup = new Posn (
        (xSum / (this.riderIds.size() + 1)),
        (ySum / (this.riderIds.size() + 1)));
  }

  //EFFECT: calculates and sets this groups' average dropoff location
  void processAvgDropoff(LocationGrid dropoffLocations) {
    ArrayList<Posn> LoPosn = new ArrayList<Posn>();

    LoPosn.add(dropoffLocations.findLocation(this.driverId));

    for (int rider : riderIds) {
      LoPosn.add(dropoffLocations.findLocation(rider));
    }

    int xSum = 0;
    int ySum = 0;
    for (Posn pos : LoPosn) {
      xSum = xSum + pos.x;
      ySum = ySum + pos.y;
    }

    this.averageDropoff = new Posn (
        (xSum / (this.riderIds.size() + 1)),
        (ySum / (this.riderIds.size() + 1)));
  }

}

//represents a 2d arraylist representing pickup and dropoff locations of users
class LocationGrid {
  ArrayList<ArrayList<Integer>> locationGrid;

  LocationGrid(ArrayList<ArrayList<Integer>> locationGrid) {
    this.locationGrid = locationGrid;
  }

  //returns the xy posn of the location of a user's id in this location grid
  Posn findLocation(int userId) {
    int y = 0;

    for (ArrayList<Integer> row : this.locationGrid) {
      if (row.contains(userId)) {
        return new Posn (row.indexOf(userId), y);
      }
      else {y++;
      }
    }
    return new Posn (0,0);
  }
}

//utility class for methods of lists of objects
class Utils {

  //EFFECT: processes each requests in a list of requests
  void processLoRequests(ArrayList<Request> requests, ArrayList<Group> groups) {
    for (Request req : requests) {
      req.processReqs(groups);
    }

  }

  //EFFECT: calculates and sets each average pickup in a list of groups
  void averagePickup (LocationGrid pickupLocations, ArrayList<Group> groups) {
    for (Group group : groups) {
      group.processAvgPickup(pickupLocations);
    }
  }

  //EFFECT: calculates and sets each average dropoff in a list of groups
  void averageDropoff (LocationGrid dropoffLocations, ArrayList<Group> groups) {
    for (Group group : groups) {
      group.processAvgDropoff(dropoffLocations);
    }
  }
}

//utility class for processing and printing the solution to the challenge
class ProcessingSolution {

  //main class, used for building the imported data, processing it, and printing the resulting groups
  public static void main(String[] args) {

    //list of given requests
    ArrayList<Request> requestsList = new ArrayList<Request> (Arrays.asList(
        new Request(21, 14, true),
        new Request(22, 27, false),
        new Request(37,14,true),
        new Request(20,38,true),
        new Request(5,9,true),
        new Request(17,9,false),
        new Request(2,15,true),
        new Request(34,40,true),
        new Request(11,1,true),
        new Request(26,1,true),
        new Request(13,10,true),
        new Request(8,27,true),
        new Request(29,38,true),
        new Request(16,9,true),
        new Request(39,3,true),
        new Request(28,38,false),
        new Request(30,14,true),
        new Request(12,9,true),
        new Request(36,27,true),
        new Request(33,10,true),
        new Request(25,38,false),
        new Request(18,15,true),
        new Request(16,40,false),
        new Request(17,40,true),
        new Request(28,27,true),
        new Request(22,38,true),
        new Request(7,1,true),
        new Request(23,10,true),
        new Request(29,4,false),
        new Request(6,14,true),
        new Request(24,9,true),
        new Request(35,10,true),
        new Request(32,3,true),
        new Request(31,3,true),
        new Request(34,9,false),
        new Request(19,4,true),
        new Request(25,4,true),
        new Request(29,27,false)
        ));

    //empty list of groups to start out with
    ArrayList<Group> groupsList = new ArrayList<Group>();

    //2d array of pickup locations
    LocationGrid pickupLocationsGrid = new LocationGrid ( new ArrayList<ArrayList<Integer>> (Arrays.asList (
        new ArrayList<Integer> (Arrays.asList(-1,-1,-1,-1,-1,-1,5,-1,-1,-1,-1,-1,2,-1,-1)),
        new ArrayList<Integer> (Arrays.asList(-1,21,30,-1,-1,9,-1,-1,-1,-1,-1,-1,18,15,-1)),
        new ArrayList<Integer> (Arrays.asList(6,
            14,
            37,
            -1,
            -1,
            24,
            17,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            16,
            12,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            40,
            34,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            7,
            26,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            1,
            11,
            -1,
            -1,
            31,
            32)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            19,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            39,
            3)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            4,
            25,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList( -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            27,
            8,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            38,
            28,
            36,
            -1,
            -1,
            -1,
            23,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            29,
            22,
            20,
            -1,
            -1,
            10,
            33,
            13,
            -1,
            35)))));





    //2d array of dropoff locations
    LocationGrid dropoffLocationsGrid = new LocationGrid ( new ArrayList<ArrayList<Integer>> (Arrays.asList (
        new ArrayList<Integer> (Arrays.asList(1,
            -1,
            -1,
            -1,
            13,
            33,
            35,
            23,
            -1,
            -1,
            -1,
            -1,
            4,
            29,
            25)),
        new ArrayList<Integer> (Arrays.asList(-1,
            26,
            -1,
            -1,
            -1,
            -1,
            10,
            -1,
            -1,
            -1,
            -1,
            -1,
            38,
            22,
            19)),
        new ArrayList<Integer> (Arrays.asList(11,
            -1,
            39,
            7,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            20,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            3,
            32,
            -1,
            31,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            30,
            21,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            6,
            37,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            14,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            28)),
        new ArrayList<Integer> (Arrays.asList(-1,
            2,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            5,
            -1,
            -1,
            -1,
            36,
            8,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            12,
            -1,
            -1,
            -1,
            -1,
            -1,
            17)),
        new ArrayList<Integer> (Arrays.asList(-1,
            15,
            18,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            24,
            -1,
            -1,
            34,
            27,
            -1)),
        new ArrayList<Integer> (Arrays.asList(-1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            9,
            16,
            -1,
            -1,
            -1,
            40)))));

    


    
    //running the methods to form groups and find average pickup/dropoff locations
    new Utils().processLoRequests(requestsList, groupsList);
    new Utils().averagePickup(pickupLocationsGrid, groupsList);
    new Utils().averageDropoff(dropoffLocationsGrid, groupsList);

    /*
    //printing the mutated list of groups with the final solution, in approximate json formatting
    for(Group group1 : groupsList) {
      System.out.println("{ \n \"driverId\": " + String.valueOf(group1.driverId) +
          ", \n \"riderIds\": [\n");
      for(int riderId: group1.riderIds) {
        System.out.println(String.valueOf(riderId) + ",\n");
      }
      System.out.println("], \n \"averagePickup\": {\n\"x\": " + String.valueOf(group1.averagePickup.x) +
          ",\n\"y\": " + String.valueOf(group1.averagePickup.y) + "\n},\n\"averageDropoff\": {\n\"x\": " +
          String.valueOf(group1.averageDropoff.x) + ",\n\"y\": " + String.valueOf(group1.averageDropoff.y) +
          "\n}\n},");
    }
    */
    
    //prints the json version of the final groups - note however that it is 
    //without the line break formattingp
    Gson gson = new Gson();
    String json = gson.toJson(groupsList);
    System.out.println(json);

  }


}

