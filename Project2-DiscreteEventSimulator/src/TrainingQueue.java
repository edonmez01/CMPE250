import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

class TrainingQueueComparator implements Comparator<Player> {

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compare(Player o1, Player o2) {
        if(Math.abs(o1.arrivalTime - o2.arrivalTime) <= 1e-9) {
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
    }

}

public class TrainingQueue extends PriorityQueue<Player> implements ExcelFedQueue {

    public int maxLength;
    private int totalEntrances;
    private double totalWaitingTime;
    public int totalActivities;  // Total training sessions given
    public double totalActivityTime;  // Total time spent in training sessions
    public double totalTurnaroundTime;  // Total time spent between a player entering the training queue and the same
                                        // player finishing their subsequent physiotherapy session.

    public static TrainingQueue trainingQueue = new TrainingQueue();

    public TrainingQueue() {
        super(new TrainingQueueComparator());
        this.maxLength = 0;
        this.totalEntrances = 0;
        this.totalWaitingTime = .0;
        this.totalActivities = 0;
        this.totalActivityTime = .0;
        this.totalTurnaroundTime = .0;
    }

    @Override
    public boolean add(Player player) {
        super.add(player);
        this.totalEntrances++;
        if(Staff.availableCoaches > 0) {
            this.sendToTraining();
            return true;
        }
        this.maxLength = Math.max(this.maxLength, this.size());
        return true;
    }

    public void sendToTraining() {
        Player player = Objects.requireNonNull(this.poll());
        this.totalWaitingTime += EventQueue.time - player.arrivalTime;
        player.goToTraining();
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

    public double getAvgTurnaroundTime() {
        if(this.totalEntrances == 0) {
            return .0;
        }
        return this.totalTurnaroundTime / this.totalEntrances;
    }

}
