/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bf.pd;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Rename {

    static Logger log = LoggerFactory.getLogger(Rename.class);

    public static void main(String[] args) throws Exception {
        String path = "c:\\media\\podcast\\相声";
        ArrayList<Path> list = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Path p : list) {
            String test = p.toFile().getName();
            String simple = ZhConverterUtil.toSimple(test);
            if (!test.equals(simple)) {
                log.debug(p.toString());
                Files.move(p, p.resolveSibling(simple));
            }
/*            String arabic = ChineseNumberUtil.convertString(test);
            if (!test.equals(arabic)) {
                log.debug(p.toString());
                log.debug(arabic);
            }*/
        }
    }
}
