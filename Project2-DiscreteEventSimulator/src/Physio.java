import java.util.PriorityQueue;

public class Physio implements Comparable<Physio> {

    private final int ID;
    public double serviceDuration;

    public static PriorityQueue<Physio> physios = new PriorityQueue<>();

    public Physio(int ID, double serviceDuration) {
        this.ID = ID;
        this.serviceDuration = serviceDuration;
        Physio.physios.add(this);
    }

    @Override
    public int compareTo(Physio o) {
        if(this.ID < o.ID) {
            return -1;
        } else {
            return 1;
        }
    }

}
