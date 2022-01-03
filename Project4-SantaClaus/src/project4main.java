import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class project4main {

    static void addNode2(Node node, Node sink, int capacity, ArrayList<Node> nodes2) {
        // This method adds a vehicle to the graph.
        nodes2.add(node);
        Edge edge1 = new Edge(capacity, sink);
        Edge edge2 = new Edge(0, node);
        edge1.reverseEdge = edge2;
        edge2.reverseEdge = edge1;
        node.edges.add(edge1);
        sink.edges.add(edge2);
    }

    public static void main(String[] args) throws FileNotFoundException {

        File inFile = new File(args[0]);
        Scanner scanner = new Scanner(inFile);

        ArrayList<Integer> vehicleCounts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> vehicleLists = new ArrayList<>();  // GT, RT, GR, RR

        // Reading the vehicle types and capacities from the input file.
        for(int i = 0; i < 4; i++) {
            vehicleCounts.add(Integer.parseInt(scanner.nextLine()));
            ArrayList<Integer> vehicleList = new ArrayList<>();
            vehicleLists.add(vehicleList);
            String[] tokens = scanner.nextLine().split(" ");
            for(int j = 0; j < vehicleCounts.get(i); j++) {
                int parsedToken = Integer.parseInt(tokens[j]);
                if(parsedToken > 0) {
                    vehicleList.add(parsedToken);
                }
            }
        }

        // Reading the bag types and capacities from the input file.
        int bagCount = Integer.parseInt(scanner.nextLine());
        int totalGiftCount = 0;
        HashMap<String, ArrayList<Integer>> bags = new HashMap<>();
        String[] tokens = new String[0];
        if(bagCount > 0) {
            tokens = scanner.nextLine().split(" ");
        }
        for(int i = 0; i < bagCount * 2; i += 2) {
            String bagType = tokens[i];
            int bagSize = Integer.parseInt(tokens[i + 1]);

            if(bagSize == 0) {
                continue;
            }

            totalGiftCount += bagSize;

            if(bags.containsKey(bagType)) {
                if(bagType.contains("a")) {
                    bags.get(bagType).add(bagSize);
                } else {
                    bags.get(bagType).set(0, bags.get(bagType).get(0) + bagSize);
                }
            } else {
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(bagSize);
                bags.put(bagType, temp);
            }
        }


        // Adding the bags to the graph.
        ArrayList<Node> nodes1 = new ArrayList<>();
        Node source = new Node("s");

        for(String bagType : bags.keySet()) {
            for(int capacity : bags.get(bagType)) {
                Node node = new Node("1" + bagType);
                node.capacity = capacity;
                nodes1.add(node);
                Edge edge1 = new Edge(capacity, node);
                Edge edge2 = new Edge(0, source);
                edge1.reverseEdge = edge2;
                edge2.reverseEdge = edge1;
                source.edges.add(edge1);
                node.edges.add(edge2);
            }
        }

        // Adding the vehicles to the graph.
        ArrayList<Node> nodes2 = new ArrayList<>();
        Node sink = new Node("t");

        for(int capacity : vehicleLists.get(0)) {
            Node node = new Node("2gt");
            addNode2(node, sink, capacity, nodes2);

        }
        for(int capacity : vehicleLists.get(1)) {
            Node node = new Node("2rt");
            addNode2(node, sink, capacity, nodes2);
        }
        for(int capacity : vehicleLists.get(2)) {
            Node node = new Node("2gr");
            addNode2(node, sink, capacity, nodes2);
        }
        for(int capacity : vehicleLists.get(3)) {
            Node node = new Node("2rr");
            addNode2(node, sink, capacity, nodes2);
        }

        // Creating the appropriate edges between bags and vehicles.
        for(Node node1 : nodes1) {
            String type = node1.type;
            for(Node node2 : nodes2) {
                if((node2.type.equals("2gt") && !(type.contains("c") || type.contains("e"))) ||
                        (node2.type.equals("2rt") && !(type.contains("b") || type.contains("e"))) ||
                        (node2.type.equals("2gr") && !(type.contains("c") || type.contains("d"))) ||
                        (node2.type.equals("2rr") && !(type.contains("b") || type.contains("d")))) {
                    Edge edge1;
                    if(type.contains("a")) {
                        edge1 = new Edge(1, node2);
                    } else {
                        edge1 = new Edge(node1.capacity, node2);
                    }
                    Edge edge2 = new Edge(0, node1);
                    edge1.reverseEdge = edge2;
                    edge2.reverseEdge = edge1;
                    node1.edges.add(edge1);
                    node2.edges.add(edge2);
                }
            }
        }

        // Running Dinic's Algorithm in order to find the maximum number of gifts that can be delivered.
        int deliveredGifts = source.dinic(nodes1, nodes2, sink);

        // Printing the result to the output file.
        File outFile = new File(args[1]);
        PrintStream printStream = new PrintStream(outFile);
        printStream.println(totalGiftCount - deliveredGifts);

    }

}
