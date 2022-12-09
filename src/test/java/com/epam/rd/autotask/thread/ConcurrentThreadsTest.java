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
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentThreadsTest {

    @Test
    void testValues() {
        Map<Long, Integer> map = new HashMap<>();
        IntStream.iterate(3, i -> i + 2)
                .limit(5)
                .forEach(count ->
                        testValues(count).forEach((k, v) -> map.merge(k, v, Integer::sum)));
        System.out.println(map);
        assertNotNull(map.get(0L));
        assertNotNull(map.get(1L));
    }

    Map<Long, Integer> testValues(int count) {
        ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setPriority(1);
            return t;
        });
        Map<Long, Integer> collect;
        return IntStream.range(0, count).mapToLong(i -> {
                    try {
                        return pool.submit(ConcurrentThreads::test).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .boxed()
                .collect(Collectors.groupingBy(v -> v.equals(0L) ? 0L : 1L))
                .entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().size()));
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
        assertEquals(2, threads.size(),
                "It looks like you don't starts threads correctly");
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
            new DescribedPredicate<>("target is a foreign class") {

                @Override
                public boolean test(JavaAccess<?> access) {
                    JavaClass targetClass = access.getTarget().getOwner();
                    JavaClass callerClass = access.getOwner().getOwner();
                    String targetPackage = targetClass.getPackageName();
                    String callerPackage = callerClass.getPackageName();
                    boolean equals = (targetPackage.equals(callerPackage) ||
                            targetPackage.equals("java.lang") ||
                            targetPackage.equals("java.io")) &&
                            !(targetPackage.startsWith("java.util") ||
                            targetPackage.startsWith("java.util.concurrent"));
                    return !equals;
                }
            };

    ArchRule ruleNoForeignClasses =
            noClasses().should().accessTargetWhere(isForeignClassPredicate);

    @Test
    void testCompliance() {
        testRules();
        testInheritance();
    }

    void testInheritance() {
        assertEquals(Thread.class, ConcurrentThreads.Increment.class.getSuperclass());
        assertEquals(Object.class, ConcurrentThreads.Decrement.class.getSuperclass());
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