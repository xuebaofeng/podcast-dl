package bf.pd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TidySplatted {
    public static void main(String[] args) throws IOException {

        ArrayList<Path> list = Files.walk(Paths.get("C:\\media\\tv\\虹猫蓝兔七侠传\\"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Path p : list) {
            boolean keep = p.toString().contains("_2_1");
            boolean delete = p.toString().contains("_2_0") || p.toString().contains("_2_2");
            if (keep)
                System.out.println("keep:" + p);
            if (delete) {
                System.out.println("delete:" + p);
                Files.deleteIfExists(p);
            }
            boolean move = !p.toString().contains("_2_");
            if (move) {
                System.out.println("move:" + p);
                Files.move(p, Path.of("C:\\Users\\I854966\\Downloads\\"+p.toFile().getName()));
            }
        }
    }
}
