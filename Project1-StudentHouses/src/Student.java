public class Student implements Comparable<Student> {

    int id;
    String name;
    int duration;
    double rating;

    public Student(int id, String name, int duration, double rating) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.rating = rating;
    }

    @Override
    public int compareTo(Student other) {
        if(this.id < other.id) {
            return -1;
        } else {
            return 1;
        }
    }
}
