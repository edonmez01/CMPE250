import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

class Node implements Comparable<Node> {

    String ID;
    int distance;
    String backtrack;
    HashMap<Node, Integer> inlandNeighbors;
    HashMap<Node, Integer> bridges;

    Node(String ID) {
        this.ID = ID;
        this.distance = Integer.MAX_VALUE;
        this.backtrack = "";
        this.inlandNeighbors = new HashMap<>();
        this.bridges = new HashMap<>();
    }

    @Override
    public int compareTo(Node o) {
        if(this.distance - o.distance == 0) {
            if(this.ID.charAt(0) == o.ID.charAt(0)) {
                return Integer.parseInt(this.ID.substring(1)) - Integer.parseInt(o.ID.substring(1));
            }
            return ((int) this.ID.charAt(0)) - ((int) o.ID.charAt(0));
        }
        return this.distance - o.distance;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Node)) {
            return false;
        }
        return this.ID.equals(((Node) o).ID);
    }

    @Override
    public int hashCode() {
        return ((int) this.ID.charAt(0)) + 37 * Integer.parseInt(this.ID.substring(1));
    }

}

public class project3main {

    public static void main(String[] args) throws FileNotFoundException {

        // Initialization of the scanner and the printstream
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        Scanner scanner = new Scanner(inFile);
        PrintStream printStream = new PrintStream(outFile);

        // Input
        int timeLimit = Integer.parseInt(scanner.nextLine());
        int numOfCities = Integer.parseInt(scanner.nextLine());
        String[] tokens = scanner.nextLine().split(" ");
        int cStartNode = Integer.parseInt(tokens[0].substring(1));
        int cEndNode = Integer.parseInt(tokens[1].substring(1));

        Node[] nodes = new Node[numOfCities];
        int numOfC = 0;

        HashMap<String, ArrayList<String>> draftMap = new HashMap<>();

        // Initialization of the nodes
        for(int i = 0; i < numOfCities; i++) {
            tokens = scanner.nextLine().split(" ");
            char startSide = tokens[0].charAt(0);
            int startNode = Integer.parseInt(tokens[0].substring(1));

            if(startSide == 'c') {
                numOfC++;
                nodes[startNode - 1] = new Node(tokens[0]);
            } else if(startSide == 'd') {
                nodes[numOfC + startNode - 1] = new Node(tokens[0]);
            }

            draftMap.put(tokens[0], new ArrayList<>());

            for(int j = 1; j < tokens.length; j += 2) {
                draftMap.get(tokens[0]).add(tokens[j].concat(" ").concat(tokens[j + 1]));
            }
        }

        // Associating all nodes with the relevant edges
        for(String start : draftMap.keySet()) {
            char startSide = start.charAt(0);
            int startNodeIndex = Integer.parseInt(start.substring(1)) - 1;
            if(startSide == 'd') {
                startNodeIndex += numOfC;
            }
            for(String rawStr : draftMap.get(start)) {
                String[] strTokens = rawStr.split(" ");
                char endSide = strTokens[0].charAt(0);
                int endNodeIndex = Integer.parseInt(strTokens[0].substring(1)) - 1;
                if(endSide == 'd') {
                    endNodeIndex += numOfC;
                }
                int distance = Integer.parseInt(strTokens[1]);
                if(startSide == 'c' && endSide == 'd') {
                    nodes[startNodeIndex].bridges.put(nodes[endNodeIndex], Math.min(distance, nodes[startNodeIndex].bridges.getOrDefault(nodes[endNodeIndex], Integer.MAX_VALUE)));
                    nodes[endNodeIndex].bridges.put(nodes[startNodeIndex], Math.min(distance, nodes[endNodeIndex].bridges.getOrDefault(nodes[startNodeIndex], Integer.MAX_VALUE)));
                } else {
                    nodes[startNodeIndex].inlandNeighbors.put(nodes[endNodeIndex], Math.min(distance, nodes[startNodeIndex].inlandNeighbors.getOrDefault(nodes[endNodeIndex], Integer.MAX_VALUE)));
                    if(startSide == 'd' && endSide == 'd') {
                        nodes[endNodeIndex].inlandNeighbors.put(nodes[startNodeIndex], Math.min(distance, nodes[endNodeIndex].inlandNeighbors.getOrDefault(nodes[startNodeIndex], Integer.MAX_VALUE)));
                    }
                }
            }
        }

        // Dijkstra
        PriorityQueue<Node> pq = new PriorityQueue<>();
        nodes[cStartNode - 1].distance = 0;
        pq.add(nodes[cStartNode - 1]);
        while(!pq.isEmpty()) {
            Node startNode = pq.poll();
            for(Node endNode : startNode.inlandNeighbors.keySet()) {
                int roadLength = startNode.inlandNeighbors.get(endNode);
                if(startNode.distance + roadLength < endNode.distance) {
                    endNode.distance = startNode.distance + roadLength;
                    endNode.backtrack = startNode.backtrack.concat(" ").concat(startNode.ID);
                    pq.add(endNode);
                }
            }
        }

        // Conversion of the Dijkstra output to a printable string, also deciding whether Mecnun married Leyla
        boolean married = false;
        int dijkstraDistance = nodes[cEndNode - 1].distance;
        String dijkstraPath = "-1";
        if(cStartNode == cEndNode) {
            dijkstraPath = "c".concat(String.valueOf(cStartNode));
        } else if(dijkstraDistance < Integer.MAX_VALUE) {
            dijkstraPath = nodes[cEndNode - 1].backtrack.substring(1).concat(" c").concat(String.valueOf(cEndNode));
            if(dijkstraDistance <= timeLimit) {
                married = true;
            }
        }

        // Prim
        int mstLength = 0;
        if(married) {
            HashSet<Node> seen = new HashSet<>();
            nodes[cEndNode - 1].distance = 0;
            TreeSet<Node> pq2 = new TreeSet<>();
            pq2.add(nodes[cEndNode - 1]);
            for(int i = numOfC; i < numOfCities; i++) {
                pq2.add(nodes[i]);
            }
            while(!pq2.isEmpty()) {
                Node currentNode = pq2.pollFirst();
                if(currentNode.distance == Integer.MAX_VALUE) {
                    break;
                }
                mstLength += currentNode.distance;
                seen.add(currentNode);
                for(Node nextNode : currentNode.bridges.keySet()) {
                    if(seen.contains(nextNode)) {
                        continue;
                    }
                    int roadLength = currentNode.bridges.get(nextNode);
                    if(roadLength < nextNode.distance) {
                        pq2.remove(nextNode);
                        nextNode.distance = roadLength;
                        pq2.add(nextNode);
                    }
                }
                if(currentNode.ID.charAt(0) == 'd') {
                    for(Node nextNode : currentNode.inlandNeighbors.keySet()) {
                        if(seen.contains(nextNode)) {
                            continue;
                        }
                        int roadLength = currentNode.inlandNeighbors.get(nextNode);
                        if(roadLength < nextNode.distance) {
                            pq2.remove(nextNode);
                            nextNode.distance = roadLength;
                            pq2.add(nextNode);
                        }
                    }
                }

            }
            if(seen.size() == numOfCities - numOfC + 1) {
                mstLength *= 2;
            } else {
                mstLength = -2;
            }

        } else {
            mstLength = -1;
        }

        // Output
        printStream.println(dijkstraPath);
        printStream.println(mstLength);

        scanner.close();
        printStream.close();

    }

}
