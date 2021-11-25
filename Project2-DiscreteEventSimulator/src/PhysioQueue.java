import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

class PhysioQueueComparator implements Comparator<Player> {

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compare(Player o1, Player o2) {
        if(Math.abs(o1.trainingDuration - o2.trainingDuration) < 1e-9) {
            if(Math.abs(o1.arrivalTime - o2.arrivalTime) < 1e-9) {
                if(o1.ID < o2.ID) {
                    return -1;
                } else {
                    return 1;
                }
            } else if(o1.arrivalTime < o2.arrivalTime) {
                return -1;
            } else {
                return 1;
            }
        } else if(o1.trainingDuration > o2.trainingDuration) {
            return -1;
        } else {
            return 1;
        }
    }

}

public class PhysioQueue extends PriorityQueue<Player> implements ExcelFedQueue {

    public int maxLength;
    private int totalEntrances;
    private double totalWaitingTime;
    public int totalActivities;  // Total physiotherapy sessions given
    public double totalActivityTime;  // Total time spent in physiotherapy sessions
    public double maxTimeSpent;  // Maximum time a player has spent in the physiotherapy queue
    public int maxTimeSpentID;  // ID of the player that has spent the most time in the physiotherapy queue

    public static PhysioQueue physioQueue = new PhysioQueue();

    public PhysioQueue() {
        super(new PhysioQueueComparator());
        this.maxLength = 0;
        this.totalEntrances = 0;
        this.totalWaitingTime = .0;
        this.totalActivities = 0;
        this.totalActivityTime = .0;
        this.maxTimeSpent = .0;
        this.maxTimeSpentID = 0;
    }

    @Override
    public boolean add(Player player) {
        super.add(player);
        this.totalEntrances++;
        if(!Physio.physios.isEmpty()) {
            this.sendToPhysio();
            return true;
        }
        this.maxLength = Math.max(this.maxLength, this.size());
        return true;
    }

    public void sendToPhysio() {
        Player player = Objects.requireNonNull(this.poll());
        this.totalWaitingTime += EventQueue.time - player.arrivalTime;
        player.goToPhysio();
    }

    @Override
    public double getAvgWaitingTime() {
        if(this.totalEntrances == 0) {
            return .0;
        }
        return this.totalWaitingTime / this.totalEntrances;
    }

    @Override
    public double getAvgActivityTime() {
        if(this.totalActivities == 0) {
            return .0;
        }
        return this.totalActivityTime / this.totalActivities;
    }

}
