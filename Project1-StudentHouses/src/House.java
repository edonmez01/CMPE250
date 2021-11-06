public class House implements Comparable<House> {
    int id;
    int duration;
    double rating;

    public House(int id, int duration, double rating) {
        this.id = id;
        this.duration = duration;
        this.rating = rating;
    }

    @Override
    public int compareTo(House other) {
        if(this.id < other.id) {
            return -1;
        } else {
            return 1;
        }
    }
}
