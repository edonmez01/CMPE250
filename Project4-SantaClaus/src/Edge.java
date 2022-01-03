public class Edge {
    int capacity;
    int flow;
    Node destination;
    Edge reverseEdge;

    public Edge(int capacity, Node destination) {
        this.capacity = capacity;
        this.flow = 0;
        this.destination = destination;
    }
}