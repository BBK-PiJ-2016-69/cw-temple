package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;
import game.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class Explorer {

  Set<Node> visitedNodes = new HashSet<Node>();
  Set<Node> unvisitedNodes = new HashSet<Node>();
  Map<Node, Integer> distance = new HashMap<Node, Integer>();
  Map<Node, Node> prev = new HashMap<Node, Node>();
  List<Node> nodes = null;

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

    
    for (NodeStatus neighbour : state.getNeighbours()) {
      if(neighbour.getDistanceToTarget() < state.getDistanceToTarget() && !visited.contains(neighbour.getId())){
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
    
    int startTime = state.getTimeRemaining();

     /* Dijkstra implementation  */
     nodes = new ArrayList<Node>(state.getVertices());
     distance.put(state.getExit(), 0);
     unvisitedNodes.add(state.getExit());
     while (unvisitedNodes.size() > 0) {
       Node node = getMin(unvisitedNodes);
       visitedNodes.add(node);
       unvisitedNodes.remove(node);
       findShortestRoute(node);
    }

    List<Node> path = new ArrayList();
    Node step = state.getCurrentNode();
    path.add(step);
    while (prev.get(step) != null) {
      step = prev.get(step);
      path.add(step);
    }

    /*
    Node maxGold = null;
     for(Node goldNode : nodes){
        if(maxGold == null || goldNode.getTile().getGold() > maxGold.getTile().getGold()){
            maxGold = goldNode;
          }
        }
        System.out.println("Max Gold: " + maxGold.getTile().getGold());
  */



    for (Node node : path) {
      if(!(node == state.getCurrentNode())){
        state.moveTo(node);
        if(node.getTile().getGold() > 0){
          state.pickUpGold();
        }

        // TO DO: Calculate which nodes have the most gold and optimise to visit these? Optimise edge length vs gold ratio?
        // ADD running of multiple paths to see which gives the best result?
        // Search for all gold within range and calculate route?
        // Calculate gold within X squares of grid path, weight by distance from path (in Length()) and visit in order after optimising for time available
        // Calc all paths below TimeRemaining()?
        // BFS for any path under TimeRemaining() ordered by gold on route?

        // Walk to highest gold (or second, or third) then return to nearest point on path


        // DO BELOW VIA RECURSIVE CALL AFTER TESTING
         for (Node child : state.getCurrentNode().getNeighbours()) {
              if(child.getTile().getGold() > 0 && state.getTimeRemaining() > (lengthRemaining(path, node) + (child.getEdge(node).length()*2)) && !path.contains(child)){
               state.moveTo(child);
               state.pickUpGold();
               
               for (Node subChild : state.getCurrentNode().getNeighbours()) {
                 if(subChild.getTile().getGold() > 0 && state.getTimeRemaining() > (lengthRemaining(path, node) + (child.getEdge(node).length()*2) + (child.getEdge(subChild).length()*2)) && !path.contains(subChild)){
                   state.moveTo(subChild);
                   state.pickUpGold();     
                   state.moveTo(child);
                  }
                }
                
               state.moveTo(node);
           }
         }

      }
    }    

    int gold = 0;
     for (Node node : nodes) {
       gold += node.getTile().getOriginalGold();   
      }

      System.out.println("--- Efficiency --- ");
      System.out.println(">> Total Gold: " + gold);
      System.out.println(">> Initial Time: " + startTime);
      System.out.println(">> Time Remaining: " + state.getTimeRemaining());

   }



private Node getMin(Set<Node> nodes) {
  Node min= null;
  for (Node node : nodes) {
    if (min == null) {
      min = node;
    } else {
      if (getDistance(node) < getDistance(min)) {
        min = node;
      }
    }
  }
  return min;
 }

private void findShortestRoute(Node node) {
   Set<Node> neighbours = node.getNeighbours();
   for (Node neighbour : neighbours) {

      //System.out.println(node.getTile().getGold());
      if (getDistance(neighbour) > (getDistance(node) + (3000 - node.getTile().getGold()))) { 
         distance.put(neighbour, getDistance(node) + (3000 - node.getTile().getGold()));
         prev.put(neighbour, node);
         unvisitedNodes.add(neighbour);
      }
   }
  }

 private int getDistance(Node destination) {
  return (distance.get(destination) == null) ? Integer.MAX_VALUE : distance.get(destination);
}

 private int lengthRemaining(List<Node> path, Node currentNode) {
  boolean countNode = false;
  int length = 0;
  for (Node node : path) {
    if (countNode == true){
      for (Node neighbour : node.getNeighbours()){
        if(path.contains(neighbour)){
          length += node.getEdge(neighbour).length();
        }
      }
      
    }
    if(node == currentNode){
      countNode = true;
    }
  }
  return length;
 }
}

