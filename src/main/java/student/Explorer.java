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

  private Set<Node> visitedNodes = new HashSet<Node>();
  private Set<Node> unvisitedNodes = new HashSet<Node>();
  private Map<Node, Integer> distance = new HashMap<Node, Integer>();
  private Map<Node, Node> prev = new HashMap<Node, Node>();
  private Map<Long, Integer> visitedTimes = new TreeMap<Long, Integer>();

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

    boolean moved;
    int max = 0;
    NodeStatus min = null;
    NodeStatus toVisit = null;

    // Enter main loop (until destination is found)
    while (state.getDistanceToTarget() > 0) {
      moved = false;

      // Check all neighbours to find an unvisisted node that is closer to the target
      for (NodeStatus neighbour : state.getNeighbours()) {
        if (neighbour.getDistanceToTarget() < state.getDistanceToTarget() && !visited(neighbour)) {
          moved = true;
          toVisit = neighbour;
          break;
        }
      }

      // Check all neighbours to find an unvisisted node that is the same distance to the target
      if (!moved) { 
        for (NodeStatus neighbour : state.getNeighbours()) {
          if (neighbour.getDistanceToTarget() == state.getDistanceToTarget() && !visited(neighbour)) {
            moved = true;
            toVisit = neighbour;
            break;
          }
        }
      }

      // Check all neighbours to find an unvisited node
      if (!moved) { 
        for (NodeStatus neighbour : state.getNeighbours()) {
          if (!visited(neighbour)) {
            moved = true;
            toVisit = neighbour;
            break;
          }
        }
      }

      // Check all neighbours to find the least visited node
      if (!moved) { 
        for (NodeStatus neighbour : state.getNeighbours()) {
          if (min == null || visitedTimes.get(neighbour.getId()) < visitedTimes.get(min.getId())) {
            min = neighbour;
          }
        }
        moved = true;
        toVisit = min;
        min = null;
      }

      // Move to selected node
      visitedTimes.put(toVisit.getId(),!visited(toVisit) ? 1 : visitedTimes.get(toVisit.getId()) + 1);
      state.moveTo(toVisit.getId()); 
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
    
    // Implements Dijkstra's algorithm to find shortest path to exit from current location
    List<Node> path = dijkstra(state);

    // Calculates the time (distance) it will take to reach the exit. Leave some space for later exploration.
    int capacity = state.getTimeRemaining() - lengthRemaining(state.getCurrentNode()) - 250;

    // Calculates an additional path (leaving time to escape) that explores for gold in the vicinity
    List<Node> goldPath = new ArrayList<Node>();
    Node goldStep = state.getCurrentNode();
    boolean reachedLimit = false;
    goldPath.add(goldStep);
    Node maxGold = null;
    int i = 0;
    while (!reachedLimit && i < 1000) {
      for (Node goldSteps : goldStep.getNeighbours()) {
        if (!path.contains(goldSteps) && !goldPath.contains(goldSteps)) {
          if (maxGold == null || maxGold.getTile().getGold() < goldSteps.getTile().getGold()) {
            maxGold = goldSteps;
          }
        }
      }
      if (maxGold != null) {
        if (capacity > maxGold.getEdge(goldStep).length() * 2) {
          capacity -= maxGold.getEdge(goldStep).length() * 2;
          goldPath.add(maxGold);
        } else {
          reachedLimit = true;
        }

        goldStep = maxGold;
        maxGold = null;
      }
      i++;
    }

    if (state.getCurrentNode().getTile().getGold() > 0) {
      state.pickUpGold();
    }

    // Follow the path to find the gold 
    for (Node node : goldPath) {
      if (!(node == state.getCurrentNode())) {
        state.moveTo(node);
        if (state.getCurrentNode().getTile().getGold() > 0) {
          state.pickUpGold();
        }
      }
    }

    // Calculates new route to the exit
    path = dijkstra(state);

    // Follows path
    for (Node node : path) {
      if (!(node == state.getCurrentNode())) {
        state.moveTo(node);
        if (node.getTile().getGold() > 0) {
          state.pickUpGold();
        }

        // Explores around the target path if there is enough capacity and there is gold to be gathered
        stray(state, path, node);
        
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
    Node min = null;
    for (Node node : nodes) {
      if (min == null || getDistance(node) < getDistance(min)) {
        min = node;
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
   * Gets distance from list where calculated or returns a maximum value.
   *
   * @param destination A node for which to check the distance.
   * @return The distance or a maximum integer value where not found.
   */
  private int getDistance(Node destination) {
    return (distance.get(destination) == null) ? Integer.MAX_VALUE : distance.get(destination);
  }

  /**
   * Gets the length of the remainder of the path to the door via Dijkstra's algorithm.
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

  /**
   * Calculates and returns the path to the exit using Dijkstra's algorithm.
   *
   * @param state the EscapeState to allow the method to calculate relevant locations
   * @return path a List of nodes providing a path to the exit
   */
  private List<Node> dijkstra(EscapeState state) {
    distance.put(state.getExit(), 0);
    unvisitedNodes.add(state.getExit());

    // Calculate route
    while (unvisitedNodes.size() > 0) {
      Node node = getMin(unvisitedNodes);
      visitedNodes.add(node);
      unvisitedNodes.remove(node);
      findShortestRoute(node);
    }

    // Add route to path and return
    List<Node> path = new ArrayList<Node>();
    Node step = state.getCurrentNode();
    path.add(step);
    while (prev.get(step) != null) {
      step = prev.get(step);
      path.add(step);
    }
    return path;
  }

  /**
   * Returns true if the node has been visited during the search phase.
   *
   * @param node The node to check for visiting status.
   * @return true if the node has been visited.
   */
  private boolean visited(NodeStatus node) {
    return visitedTimes.get(node.getId()) != null;
  }

  /**
   * Recursive function to stray from the path in search of gold.
   *
   * @param state The current state of the game.
   * @param path A list of nodes on the route to the exit.
   * @param node The current node on which the character stands.
   */
  private void stray(EscapeState state, List path, Node node) {
    for (Node child : state.getCurrentNode().getNeighbours()) {
      if (child.getTile().getGold() > 0 && state.getTimeRemaining() > (lengthRemaining(node) + (child.getEdge(node).length() * 2)) && !path.contains(child)) {
        state.moveTo(child);
        state.pickUpGold();  
        stray(state, path, child);
        state.moveTo(node);
      }
    }
  }
}	
