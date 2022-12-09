package com.epam.rd.autotask.thread;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConcurrentThreadsTest {

    @ParameterizedTest
    @CsvSource({"5"})
    void testValues(int count) {
        double sum = IntStream.range(0, count).map(i -> (int) ConcurrentThreads.test()).sum();
        System.out.println(sum);
        assertNotEquals(0, sum);
    }

    @Test
    void testConcurrency() {
        Thread main = Thread.currentThread();
        main.setPriority(8);
        Thread runner = new Thread(ConcurrentThreads::test, "runner");
        runner.setPriority(1);
        Set<Thread> initial = Thread.getAllStackTraces().keySet();
//        System.out.println(initial);
        List<Set<Thread>> allTreads = new ArrayList<>(50);
        runner.start();
        while (!runner.isAlive()) {
            // nothing
        }
        while (runner.getState() != Thread.State.TERMINATED) {
            allTreads.add(Thread.getAllStackTraces().keySet());
        }
        Set<Thread> threads = allTreads.stream()
                .peek(s -> s.removeAll(initial))
//                .peek(System.out::println)
                .map(s -> s.stream().filter(t -> !t.getName().startsWith("runner")).toList())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertNotEquals(Optional.empty(), threads,
                "It looks like you don't starts threads correctly");
        assertEquals(2, threads.size());
    }

    ArchRule ruleNoSync = noClasses()
            .should()
            .callMethodWhere(target(describe("Methods Tread#setName() should not be used",
                    target -> (
                            "join".equals(target.getName()) ||
                            "sleep".equals(target.getName()) ||
                            "interrupt".equals(target.getName())
                    ) &&
                            target.getOwner().isAssignableTo(Thread.class)
            )));

    ArchRule ruleNoSetNames = noClasses()
            .should()
            .callMethodWhere(target(describe("Methods Tread#setName() should not be used",
                    target -> (
                            "setName".equals(target.getName())
                    ) &&
                            target.getOwner().isAssignableTo(Thread.class)
            )));

    DescribedPredicate<JavaAccess<?>> isForeignClassPredicate =
            new DescribedPredicate<>("target is a foreign message class") {

                @Override
                public boolean test(JavaAccess<?> access) {
                    JavaClass targetClass = access.getTarget().getOwner();
                    JavaClass callerClass = access.getOwner().getOwner();
                    String targetPackage = targetClass.getPackageName();
                    String callerPackage = callerClass.getPackageName();
                    boolean equals = (targetPackage.equals(callerPackage) ||
                            targetPackage.equals("java.lang") ||
                            targetPackage.equals("java.io") ) &&
                            !targetPackage.startsWith("java.util");
                    return !equals;
                }
            };

    ArchRule ruleNoForeignClasses =
            noClasses().should().accessTargetWhere(isForeignClassPredicate);

    @Test
    void testCompliance() {
        testRules();
    }

    void testRules() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeJars())
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .withImportOption(new ImportOption.DoNotIncludeArchives())
                .importPackages("com.epam.rd.autotask");

//        System.out.println(classes);
        ruleNoSync.check(classes);
        ruleNoSetNames.check(classes);
        ruleNoForeignClasses.check(classes);
    }
}