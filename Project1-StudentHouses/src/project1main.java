import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class project1main {
    public static void main(String[] args) {

        // Initialization of the Scanner and the PrintStream
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);

        Scanner scanner = null;
        try {
            scanner = new Scanner(inFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        PrintStream printStream = null;
        try {
            printStream = new PrintStream(outFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        // Initialization of the containers for students and houses
        ArrayList<Student> students = new ArrayList<Student>();
        ArrayList<House> houses = new ArrayList<House>();

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split(" ");
            String identifier = tokens[0];
            if(identifier.equals("h")) {  // House
                int id = Integer.parseInt(tokens[1]);
                int duration = Integer.parseInt(tokens[2]);
                double rating = Double.parseDouble(tokens[3]);
                House house = new House(id, duration, rating);
                houses.add(house);
            } else if(identifier.equals("s")) {  // Student
                int id = Integer.parseInt(tokens[1]);
                String name = tokens[2];
                int duration = Integer.parseInt(tokens[3]);
                double rating = Double.parseDouble(tokens[4]);
                Student student = new Student(id, name, duration, rating);
                students.add(student);
            }


        }

        // Houses and students are sorted by their ids.
        Collections.sort(houses);
        Collections.sort(students);

        ArrayList<Student> out = new ArrayList<Student>();
        for(int i = 0; i < 8; i++) {  // The simulation will run for 8 semesters.
            for(Student student : students) {
                if(student.duration == -1) {  // If the student has already been placed in a house and/or graduated
                    continue;
                } else if(student.duration == 0) {  // If it has become certain that the student will graduate without a house
                    out.add(student);
                    student.duration = -1;
                } else {
                    boolean placed = false;
                    for(House house : houses) {
                        if(house.rating >= student.rating && house.duration == 0) {  // If the house is available and its rating is appropriate
                            house.duration = student.duration;
                            student.duration = -1;
                            placed = true;
                            break;
                        }
                    }
                    if(!placed) {
                        student.duration -= 1;
                    }
                }
            }

            for(House house : houses) {
                house.duration = Math.max(0, house.duration - 1);
            }
        }

        // If there are still students that aren't placed to any house after the simulation has been completed, we add
        // those students to our output list as well.
        for(Student student : students) {
            if(student.duration == 0) {
                out.add(student);
            }
        }

        // The students are sorted by their ids and printed.
        Collections.sort(out);
        for(Student student : out) {
            printStream.println(student.name);
        }
    }
}
