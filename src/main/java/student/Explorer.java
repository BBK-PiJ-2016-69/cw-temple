package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Explorer {

  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   *   
   * <p>There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.</p>
   * 
   * <p>At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).</p>
   * 
   * <p>To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.</p>
   *
   * <p>Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.</p>
   *
   * <p>A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.</p>
   *
   * @param state the information available at the current state
   */
  public void explore(ExplorationState state) {

  //  System.out.println("My Location: "+state.getCurrentLocation());
 //   System.out.println("Distance to Target: "+state.getDistanceToTarget());
    
    List visited = new ArrayList();
    List toVisit = new ArrayList();

    int i = 0;
    int max = 0;
    NodeStatus min = null;
    Map<Long, Integer> visitedTimes = new TreeMap<Long, Integer>();
    boolean moved;
    while(state.getDistanceToTarget() > 0 && i < 100000){
      moved = false;
      i++;

   /*  System.out.println("------------Neighbours-----------");
    for (NodeStatus neighbour : state.getNeighbours()) {
      System.out.println(neighbour.getId() + " \\ "+neighbour.getDistanceToTarget());
    }
    System.out.println("---------------------------------"); */
    
    for (NodeStatus neighbour : state.getNeighbours()) {
      if(neighbour.getDistanceToTarget() < state.getDistanceToTarget() && !visited.contains(neighbour.getId())){
        // System.out.println("Moving based on being closer and no having visisted.");
        // System.out.println("Moving to: " + neighbour.getId() + "// Distance: " + neighbour.getDistanceToTarget());
        moved = true;
        visited.add(neighbour.getId());
        if(visitedTimes.get(neighbour.getId()) == null){
          visitedTimes.put(neighbour.getId(), 1);
        }
        else
        {
          visitedTimes.put(neighbour.getId(), visitedTimes.get(neighbour.getId()) + 1);
        }
          state.moveTo(neighbour.getId());
        
        break;
      }
    }

    if(!moved) { 
      for (NodeStatus neighbour : state.getNeighbours()) {
       if(neighbour.getDistanceToTarget() == state.getDistanceToTarget() && !visited.contains(neighbour.getId())){
       // System.out.println("Moving based on equivalent distance and not visisted");
        // System.out.println("Moving to: " + neighbour.getId() + "// Distance: " + neighbour.getDistanceToTarget());
        moved = true;
        visited.add(neighbour.getId());
        if(visitedTimes.get(neighbour.getId()) == null){
          visitedTimes.put(neighbour.getId(), 1);
        }
        else
        {
          visitedTimes.put(neighbour.getId(), visitedTimes.get(neighbour.getId()) + 1);
        }
        state.moveTo(neighbour.getId());
        break;
      }
    }
  }

    if(!moved) { 
      for (NodeStatus neighbour : state.getNeighbours()) {

       if(!visited.contains(neighbour.getId())){
       // System.out.println("Moving based on not haing visited");
       // System.out.println("Moving to: " + neighbour.getId() + "// Distance: " + neighbour.getDistanceToTarget());
        moved = true;
        visited.add(neighbour.getId());
        if(visitedTimes.get(neighbour.getId()) == null){
          visitedTimes.put(neighbour.getId(), 1);
        }
        else
        {
          visitedTimes.put(neighbour.getId(), visitedTimes.get(neighbour.getId()) + 1);
        }
        state.moveTo(neighbour.getId());
        break;
      }
    }
  }

  if(!moved) { 

      for (NodeStatus neighbour : state.getNeighbours()) {
        if(min == null){ min = neighbour; }
        if (visitedTimes.get(neighbour.getId()) < visitedTimes.get(min.getId())){
          min = neighbour;
        }
      }
       // System.out.println("Moving based on min");
       // System.out.println("Moving to: " + min.getId() + "// Distance: " + min.getDistanceToTarget());
        moved = true;
        visited.add(min.getId());
        if(visitedTimes.get(min.getId()) == null){
          visitedTimes.put(min.getId(), 1);
        }
        else
        {
          if(visitedTimes.get(min.getId()) + 1 > max) { max = visitedTimes.get(min.getId()) + 1; }
          visitedTimes.put(min.getId(), visitedTimes.get(min.getId()) + 1);
        }
        state.moveTo(min.getId());
        min = null;
   }

    
  }
  System.out.println(" -- Moves: " + i + " ---");
  System.out.println(" -- Max: " + max + " ---");
    //TODO:
  }

  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   *
   * <p>You now have access to the entire underlying graph, which can be accessed 
   * through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.</p>
   * 
   * <p>Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.</p>
   * 
   * <p>You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.</p>
   * 
   * <p>You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.</p>
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    //TODO: Escape from the cavern before time runs out
  }
}
