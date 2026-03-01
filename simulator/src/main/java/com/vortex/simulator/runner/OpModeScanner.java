package com.vortex.simulator.runner;

import com.vortex.simulator.api.annotations.Autonomous;
import com.vortex.simulator.api.annotations.Disabled;
import com.vortex.simulator.api.annotations.TeleOp;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Scans the entire classpath for classes annotated with @TeleOp or @Autonomous
 * (excluding @Disabled) using ClassGraph.
 */
public class OpModeScanner {

    /**
     * Immutable record representing a discovered OpMode.
     */
    public record OpModeEntry(Class<?> opModeClass, String displayName, String group, boolean isTeleOp) {
        @Override
        public String toString() {
            return (isTeleOp ? "[TeleOp] " : "[Auto]   ") + displayName;
        }
    }

    public static List<OpModeEntry> scan() {
        List<OpModeEntry> result = new ArrayList<>();

        try (ScanResult scan = new ClassGraph()
                .enableAllInfo()
                .scan()) {

            for (ClassInfo ci : scan.getClassesWithAnnotation(TeleOp.class.getName())) {
                if (ci.hasAnnotation(Disabled.class.getName())) continue;
                try {
                    Class<?> cls = ci.loadClass();
                    TeleOp ann  = cls.getAnnotation(TeleOp.class);
                    String name = ann.name().isBlank() ? cls.getSimpleName() : ann.name();
                    result.add(new OpModeEntry(cls, name, ann.group(), true));
                } catch (Exception e) {
                    System.err.println("Could not load @TeleOp class " + ci.getName() + ": " + e.getMessage());
                }
            }

            for (ClassInfo ci : scan.getClassesWithAnnotation(Autonomous.class.getName())) {
                if (ci.hasAnnotation(Disabled.class.getName())) continue;
                try {
                    Class<?> cls = ci.loadClass();
                    Autonomous ann = cls.getAnnotation(Autonomous.class);
                    String name    = ann.name().isBlank() ? cls.getSimpleName() : ann.name();
                    result.add(new OpModeEntry(cls, name, ann.group(), false));
                } catch (Exception e) {
                    System.err.println("Could not load @Autonomous class " + ci.getName() + ": " + e.getMessage());
                }
            }
        }

        result.sort((a, b) -> {
            if (a.isTeleOp() != b.isTeleOp()) return a.isTeleOp() ? -1 : 1;
            return a.displayName().compareToIgnoreCase(b.displayName());
        });

        return result;
    }
}
