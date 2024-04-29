package edu.students.genetators;

import java.util.List;
import java.util.stream.Stream;

public class Generator {

//    public static List<Integer> grades() {
//        return List.of(0, 3, 6);
//    }

    public static Stream<Integer> grades() {
        return Stream.of(0, 3, 6);
    }

}
