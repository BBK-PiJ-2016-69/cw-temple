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
    
    List<Long> visited = new ArrayList<Long>();
    int max = 0;
    NodeStatus min = null;
    Map<Long, Integer> visitedTimes = new TreeMap<Long, Integer>();
    boolean moved;
    for(int i=0; state.getDistanceToTarget() > 0 && i < 100000; i++){
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

    List<Node> path = new ArrayList<Node>();
    Node step = state.getCurrentNode();
    path.add(step);
    while (prev.get(step) != null) {
      step = prev.get(step);
      path.add(step);
    }

    int capacity = startTime - lengthRemaining(state.getCurrentNode());

    List<Node> goldPath = new ArrayList<Node>();

    Node goldStep = state.getCurrentNode();
    boolean reachedLimit = false;
    goldPath.add(goldStep);
    Node maxGold = null;
    int i = 0;
    while(!reachedLimit && i < 1000){
    for (Node goldSteps : goldStep.getNeighbours()){
      if(!path.contains(goldSteps) && !goldPath.contains(goldSteps)){
        if(maxGold == null){
          maxGold = goldSteps;
        }
        if(maxGold.getTile().getGold() < goldSteps.getTile().getGold()){
          maxGold = goldSteps;
        }
      }
    }
    if(maxGold != null) {
    if(capacity > maxGold.getEdge(goldStep).length()*2){
      capacity -= maxGold.getEdge(goldStep).length()*2;
      goldPath.add(maxGold);
    }
    else
    {
      reachedLimit = true;
    }
    goldStep = maxGold;
    maxGold = null;
  }
  i++;
  }

  for (Node node : goldPath) {
      if(!(node == state.getCurrentNode())){
        state.moveTo(node);
        if(node.getTile().getGold() > 0){
          state.pickUpGold();
        }
      }
  }

  Collections.reverse(goldPath);

  for (Node node : goldPath) {
      if(!(node == state.getCurrentNode())){
        state.moveTo(node);
        if(node.getTile().getGold() > 0){
          state.pickUpGold();
        }
      }
  }

    for (Node node : path) {
      if(!(node == state.getCurrentNode())){
        state.moveTo(node);
        if(node.getTile().getGold() > 0){
          state.pickUpGold();
        }

         for (Node child : state.getCurrentNode().getNeighbours()) {
              if(child.getTile().getGold() > 0 && state.getTimeRemaining() > (lengthRemaining(node) + (child.getEdge(node).length()*2)) && !path.contains(child)){
               state.moveTo(child);
               state.pickUpGold();  
               for (Node subChild : state.getCurrentNode().getNeighbours()) {
                 if(subChild.getTile().getGold() > 0 && state.getTimeRemaining() > (lengthRemaining(node) + (child.getEdge(node).length()*2) + (child.getEdge(subChild).length()*2)) && !path.contains(subChild)){
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
   }

/**
 * Gets the closest node as measured by the getDistance method.
 *
 * @param nodes A set of nodes from which the closest is selected.
 * @return min The closest node
 */
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

/**
 * Finds the closest neighbour node to a given node and adds the path
 * to the List.
 *
 * @param node A node for which to find the nearest neighbour
 */
private void findShortestRoute(Node node) {
   Set<Node> neighbours = node.getNeighbours();
   for (Node neighbour : neighbours) {
      if (getDistance(neighbour) > (getDistance(node))) { 
         distance.put(neighbour, getDistance(node));
         prev.put(neighbour, node);
         unvisitedNodes.add(neighbour);
      }
   }
  }

/**
 * Gets distance from list where calculated or returns a maximum value
 *
 * @param destination A node for which to check the distance.
 * @return The distance or a maximum integer value where not found.
 */
 private int getDistance(Node destination) {
  return (distance.get(destination) == null) ? Integer.MAX_VALUE : distance.get(destination);
}

/**
 * Gets the length of the remainder of the path to the door via Dijkstra's algorithm
 *
 * @param currentNode The length of the path from this node to the exit will be returned.
 * @return length The length of the path from the given node to the exit.
 */
 private int lengthRemaining(Node currentNode) {
  int length = 0;

  while (prev.get(currentNode) != null) {
      length += currentNode.getEdge(prev.get(currentNode)).length();
      currentNode = prev.get(currentNode);
    }
  return length;
 }
}

