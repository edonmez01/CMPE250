@SuppressWarnings("ClassCanBeRecord")

public class Event {

    public final Player player;
    public final double time;
    public final double duration;
    public final String type;
    // type can be one of these: "tq", "t", "pq", "p", "mq", "m"
    // In order, these are: entering training queue, finishing training session, entering physiotherapy queue,
    // finishing physiotherapy session, entering massage queue, finishing massage session.

    public static int invalidAttempts = 0;
    public static int canceledAttempts = 0;

    public Event(Player player, double time, double duration, String type) {
        this.player = player;
        this.time = time;
        this.duration = duration;
        this.type = type;
        EventQueue.eventQueue.add(this);
    }

}
