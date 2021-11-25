import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

class MassageQueueComparator implements Comparator<Player> {

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compare(Player o1, Player o2) {
        if(o1.skill > o2.skill) {
            return -1;
        } else if(o1.skill < o2.skill) {
            return 1;
        } else {
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

}

public class MassageQueue extends PriorityQueue<Player> implements ExcelFedQueue {

    public int maxLength;
    private int totalEntrances;
    private double totalWaitingTime;
    public int totalActivities;  // Total massage sessions given
    public double totalActivityTime;  // Total time spent in massage sessions

    public static MassageQueue massageQueue = new MassageQueue();

    public MassageQueue() {
        super(new MassageQueueComparator());
        this.maxLength = 0;
        this.totalEntrances = 0;
        this.totalWaitingTime = .0;
        this.totalActivities = 0;
        this.totalActivityTime = .0;
    }

    @Override
    public boolean add(Player player) {
        super.add(player);
        this.totalEntrances++;
        if(Staff.availableMasseurs > 0) {
            this.sendToMassage();
            return true;
        }
        this.maxLength = Math.max(this.maxLength, this.size());
        return true;
    }

    public void sendToMassage() {
        Player player = Objects.requireNonNull(this.poll());
        this.totalWaitingTime += EventQueue.time - player.arrivalTime;
        player.goToMassage();
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
        return this.totalActivityTime / totalActivities;
    }

    public String getMinWaitingTime() {
        int minID = -1;
        double minTime = -1.;
        for(Player player : Player.players) {
            if(player.massagesReceived >= 3) {
                if((minID == -1) ||
                        (Math.abs(minTime - player.totalMassageWaitingTime) < 1e-9 && player.ID < minID) ||
                        (player.totalMassageWaitingTime < minTime)) {
                    minID = player.ID;
                    minTime = player.totalMassageWaitingTime;
                }
            }
        }
        if(minID == -1) {
            return "-1 -1";
        }
        return minID + " " + String.format("%.3f", minTime);
    }

}
