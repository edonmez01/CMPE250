import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class project5main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(args[0]));

        String[] types = scanner.nextLine().split(" ");
        String[] solidDurationsRaw = scanner.nextLine().split(" ");
        String[] liquidDurationsRaw = scanner.nextLine().split(" ");
        String[] profitsRaw = scanner.nextLine().split(" ");
        String[] arrivalsRaw = scanner.nextLine().split(" ");

        int totalOffers = types.length;
        int[][] offers = new int[totalOffers][3];

        for(int i = 0; i < totalOffers; i++) {
            int duration = Integer.parseInt(solidDurationsRaw[i]);
            if(types[i].equals("l")) {
                duration = Integer.parseInt(liquidDurationsRaw[i]);
            }
            int profit = Integer.parseInt(profitsRaw[i]);
            int startTime = Integer.parseInt(arrivalsRaw[i]);

            offers[i][0] = startTime;
            offers[i][1] = startTime + duration;
            offers[i][2] = profit;
        }

        Arrays.sort(offers, Comparator.comparingInt(o -> o[1]));

        int[] maxProfit = new int[totalOffers];
        maxProfit[0] = offers[0][2];
        for(int i = 1; i < totalOffers; i++) {
            maxProfit[i] = 0;
        }

        for(int i = 1; i < totalOffers; i++) {
            int j = i - 1;
            for(; j >= 0 && offers[j][1] > offers[i][0]; j--){};
            int prevProfit = 0;
            if(j >= 0) {
                prevProfit = maxProfit[j];
            }
            maxProfit[i] = Math.max(maxProfit[i - 1], prevProfit + offers[i][2]);
        }

        int out = maxProfit[totalOffers - 1];

        PrintStream printStream = new PrintStream(args[1]);
        printStream.println(out);

    }
}
