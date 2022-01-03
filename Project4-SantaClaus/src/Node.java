import java.util.ArrayList;
import java.util.LinkedList;

public class Node {
    String type;
    ArrayList<Edge> edges;
    int capacity;
    int level;
    int dinicNextEdge;

    public Node(String type) {
        this.type = type;
        this.edges = new ArrayList<>();
        this.capacity = -1;
        this.level = -1;
        this.dinicNextEdge = 0;  // This variable stores the index of the next edge that will be traversed during the
                                 // DFS part of Dinic's Algorithm.
    }

    public boolean BFS(ArrayList<Node> nodes1, ArrayList<Node> nodes2, Node sink) {
        // This method runs during Dinic's Algorithm in order to determine the levels of the nodes.
        this.level = 0;
        for(Node node : nodes1) {
            node.level = -1;
        }
        for(Node node : nodes2) {
            node.level = -1;
        }
        sink.level = -1;

        LinkedList<Node> bfsQueue = new LinkedList<>();
        bfsQueue.add(this);
        while(!bfsQueue.isEmpty()) {
            Node node = bfsQueue.poll();
            for(Edge edge : node.edges) {
                if(edge.destination.level == -1 && edge.capacity > edge.flow) {
                    edge.destination.level = node.level + 1;
                    bfsQueue.add(edge.destination);
                }
            }
        }

        return sink.level != -1;

    }



    public int pathFlow(int currFlow) {
        // This method runs during Dinic's Algorithm in order to find a flow from the source to the sink, using DFS.
        if(this.type.equals("t")) {
            return currFlow;
        }

        for(; this.dinicNextEdge < this.edges.size(); this.dinicNextEdge++) {
            Edge edge = this.edges.get(this.dinicNextEdge);
            if(edge.destination.level == this.level + 1 && edge.capacity > edge.flow) {
                int nextFlow = edge.destination.pathFlow(Math.min(currFlow, edge.capacity - edge.flow));
                if(nextFlow > 0) {
                    edge.flow += nextFlow;
                    edge.reverseEdge.flow -= nextFlow;
                    return nextFlow;
                }
            }
        }

        return 0;
    }

    public int dinic(ArrayList<Node> nodes1, ArrayList<Node> nodes2, Node sink) {
        // Dinic's Algortihm is utilized while finding the maximum flow of the graph.

        int maxTotalFlow = 0;
        while(this.BFS(nodes1, nodes2, sink)) {
            this.dinicNextEdge = 0;
            for(Node node : nodes1) {
                node.dinicNextEdge = 0;
            }
            for(Node node : nodes2) {
                node.dinicNextEdge = 0;
            }
            sink.dinicNextEdge = 0;

            while(true) {
                int flow = this.pathFlow(Integer.MAX_VALUE);
                if(flow == 0) {
                    break;
                }
                maxTotalFlow += flow;
            }

        }

        return maxTotalFlow;

    }
}