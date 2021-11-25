import java.util.ArrayList;
import java.util.Objects;

public class Player {

    public final int ID;
    public final int skill;
    public double arrivalTime;
    private double trainingQueueEntranceTime;
    public double trainingDuration;
    public double massageDuration;
    private Physio physio;
    public int massagesReceived;
    public double totalPhysioWaitingTime;
    public double totalMassageWaitingTime;
    private boolean isBusy;

    public static ArrayList<Player> players = new ArrayList<>();

    public Player(int ID, int skill) {
        this.ID = ID;
        this.skill = skill;
        this.massagesReceived = 0;
        this.isBusy = false;
        this.totalPhysioWaitingTime = .0;
        this.totalMassageWaitingTime = .0;
        Player.players.add(this);
    }

    public void enterTrainingQueue(double arrivalTime, double trainingDuration) {
        if(this.isBusy) {
            Event.canceledAttempts++;
            return;
        }
        this.arrivalTime = arrivalTime;
        this.trainingQueueEntranceTime = arrivalTime;
        this.trainingDuration = trainingDuration;
        this.isBusy = true;
        TrainingQueue.trainingQueue.add(this);
    }

    public void goToTraining() {
        Staff.availableCoaches--;
        this.isBusy = true;
        TrainingQueue.trainingQueue.totalActivities++;
        TrainingQueue.trainingQueue.totalActivityTime += this.trainingDuration;
        new Event(this, EventQueue.time + this.trainingDuration, -1., "t");
    }

    public void enterPhysioQueue(double arrivalTime) {
        if(this.isBusy) {
            Event.canceledAttempts++;
            return;
        }
        this.arrivalTime = arrivalTime;
        this.isBusy = true;
        PhysioQueue.physioQueue.add(this);
    }

    public void goToPhysio() {
        this.physio = Objects.requireNonNull(Physio.physios.poll());
        this.isBusy = true;
        PhysioQueue.physioQueue.totalActivities++;
        PhysioQueue.physioQueue.totalActivityTime += this.physio.serviceDuration;
        this.totalPhysioWaitingTime += EventQueue.time - this.arrivalTime;
        if(Math.abs(this.totalPhysioWaitingTime - PhysioQueue.physioQueue.maxTimeSpent) < 1e-9) {
            if(this.ID < PhysioQueue.physioQueue.maxTimeSpentID) {
                PhysioQueue.physioQueue.maxTimeSpent = this.totalPhysioWaitingTime;
                PhysioQueue.physioQueue.maxTimeSpentID = this.ID;
            }
        } else if(this.totalPhysioWaitingTime > PhysioQueue.physioQueue.maxTimeSpent) {
            PhysioQueue.physioQueue.maxTimeSpent = this.totalPhysioWaitingTime;
            PhysioQueue.physioQueue.maxTimeSpentID = this.ID;
        }
        new Event(this, EventQueue.time + this.physio.serviceDuration, -1., "p");
    }

    public void enterMassageQueue(double arrivalTime, double massageDuration) {
        if(this.massagesReceived >= 3) {
            Event.invalidAttempts++;
            return;
        }
        if(this.isBusy) {
            Event.canceledAttempts++;
            return;
        }
        this.massagesReceived++;
        this.arrivalTime = arrivalTime;
        this.massageDuration = massageDuration;
        this.isBusy = true;
        MassageQueue.massageQueue.add(this);
    }

    public void goToMassage() {
        Staff.availableMasseurs--;
        this.isBusy = true;
        this.totalMassageWaitingTime += EventQueue.time - this.arrivalTime;
        MassageQueue.massageQueue.totalActivities++;
        MassageQueue.massageQueue.totalActivityTime += this.massageDuration;
        new Event(this, EventQueue.time + this.massageDuration, -1., "m");
    }

    public void endTraining() {
        Staff.availableCoaches++;
        this.isBusy = false;
        new Event(this, EventQueue.time, -1., "pq");
        if(!TrainingQueue.trainingQueue.isEmpty()) {
            TrainingQueue.trainingQueue.sendToTraining();
        }
    }

    public void endPhysio() {
        Physio.physios.add(this.physio);
        this.isBusy = false;
        TrainingQueue.trainingQueue.totalTurnaroundTime += EventQueue.time - this.trainingQueueEntranceTime;
        if(!PhysioQueue.physioQueue.isEmpty()) {
            PhysioQueue.physioQueue.sendToPhysio();
        }
    }

    public void endMassage() {
        Staff.availableMasseurs++;
        this.isBusy = false;
        if(!MassageQueue.massageQueue.isEmpty()) {
            MassageQueue.massageQueue.sendToMassage();
        }
    }

}
