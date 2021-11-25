import java.util.Comparator;
import java.util.PriorityQueue;

class EventQueueComparator implements Comparator<Event> {

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compare(Event o1, Event o2) {
        if(Math.abs(o1.time - o2.time) < 1e-9) {
            if(o1.player.ID < o2.player.ID) {
                return -1;
            } else {
                return 1;
            }
        } else if(o1.time <= o2.time) {
            return -1;
        } else {
            return 1;
        }
    }

}

public class EventQueue extends PriorityQueue<Event> {

    public static EventQueue eventQueue = new EventQueue();
    public static double time = .0; // Simulation time

    public EventQueue() {
        super(new EventQueueComparator());
    }

}
