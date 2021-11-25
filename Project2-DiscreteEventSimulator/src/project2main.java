import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

@SuppressWarnings("IfCanBeSwitch")

public class project2main {

    public static void main(String[] args){
        // In order to enforce (.) as the decimal separator:
        Locale.setDefault(new Locale("en", "US"));

        // Initialization of the Scanner and the PrintStream
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);

        Scanner scanner;
        try {
            scanner = new Scanner(inFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        PrintStream printStream;
        try {
            printStream = new PrintStream(outFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Parsing the input file
        int N = Integer.parseInt(scanner.nextLine());
        for(int i = 0; i < N; i++) {
            String[] tokens = scanner.nextLine().split(" ");
            int ID = Integer.parseInt(tokens[0]);
            int skill = Integer.parseInt(tokens[1]);
            new Player(ID, skill);
        }

        int A = Integer.parseInt(scanner.nextLine());
        for(int i = 0; i < A; i++) {
            String[] tokens = scanner.nextLine().split(" ");
            int ID = Integer.parseInt(tokens[1]);
            double arrivalTime = Double.parseDouble(tokens[2]);
            double duration = Double.parseDouble(tokens[3]);
            if(tokens[0].equals("m")) {
                // New event: player enters massage queue
                new Event(Player.players.get(ID), arrivalTime, duration, "mq");
            } else if(tokens[0].equals("t")) {
                // New event: player enters training queue
                new Event(Player.players.get(ID), arrivalTime, duration, "tq");
            }
        }

        String[] tokens = scanner.nextLine().split(" ");
        int numOfPhysios = Integer.parseInt(tokens[0]);
        for(int ID = 0; ID < numOfPhysios; ID++) {
            double serviceDuration = Double.parseDouble(tokens[ID + 1]);
            new Physio(ID, serviceDuration);
        }

        tokens = scanner.nextLine().split(" ");
        Staff.availableCoaches = Integer.parseInt(tokens[0]);
        Staff.availableMasseurs = Integer.parseInt(tokens[1]);

        // Simulation
        while(!EventQueue.eventQueue.isEmpty()) {
            Event event = EventQueue.eventQueue.poll();
            EventQueue.time = event.time;  // Setting the simulation time equal to the time of the next event
            if(event.type.equals("tq")) {
                // Player enters training queue
                event.player.enterTrainingQueue(event.time, event.duration);
            } else if(event.type.equals("pq")) {
                // Player enters physiotherapy queue
                event.player.enterPhysioQueue(event.time);
            } else if(event.type.equals("mq")) {
                // Player enters massage queue
                event.player.enterMassageQueue(event.time, event.duration);
            } else if(event.type.equals("t")) {
                // Player finishes training session
                event.player.endTraining();
            } else if(event.type.equals("p")) {
                // Player finishes physiotherapy session
                event.player.endPhysio();
            } else if(event.type.equals("m")) {
                // Player finishes massage session
                event.player.endMassage();
            }
        }

        printStream.println(TrainingQueue.trainingQueue.maxLength);                         // 1
        printStream.println(PhysioQueue.physioQueue.maxLength);                             // 2
        printStream.println(MassageQueue.massageQueue.maxLength);                           // 3
        printStream.printf("%.3f%n", TrainingQueue.trainingQueue.getAvgWaitingTime());      // 4
        printStream.printf("%.3f%n", PhysioQueue.physioQueue.getAvgWaitingTime());          // 5
        printStream.printf("%.3f%n", MassageQueue.massageQueue.getAvgWaitingTime());        // 6
        printStream.printf("%.3f%n", TrainingQueue.trainingQueue.getAvgActivityTime());     // 7
        printStream.printf("%.3f%n", PhysioQueue.physioQueue.getAvgActivityTime());         // 8
        printStream.printf("%.3f%n", MassageQueue.massageQueue.getAvgActivityTime());       // 9
        printStream.printf("%.3f%n", TrainingQueue.trainingQueue.getAvgTurnaroundTime());   // 10
        printStream.println(PhysioQueue.physioQueue.maxTimeSpentID + " " +
                String.format("%.3f", PhysioQueue.physioQueue.maxTimeSpent));               // 11
        printStream.println(MassageQueue.massageQueue.getMinWaitingTime());                 // 12
        printStream.println(Event.invalidAttempts);                                         // 13
        printStream.println(Event.canceledAttempts);                                        // 14
        printStream.printf("%.3f%n", EventQueue.time);                                      // 15
    }

}
