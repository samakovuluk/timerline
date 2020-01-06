import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class FileLine {


    public static String getRandomLine(String path) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(path));
        List<String> line = new ArrayList<>();
        while (scanner.hasNextLine()) {
            line.add(scanner.nextLine());

        }
        String randomElement="";
        while (randomElement=="") {
            Random rand = new Random();
            randomElement=line.get(rand.nextInt(line.size()));
        }
        scanner.close();
        System.out.println(randomElement);
        return randomElement;

    }


}
