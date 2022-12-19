package com.epam.rd.autotask.thread;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IncDecThreadsTest {
    static final Logger LOG = LoggerFactory.getLogger(IncDecThreadsTest.class);
    static final PrintStream SYS_OUT = System.out;
    static final String EOL = System.lineSeparator();
    static final long TEST_TIMEOUT = 1000 * 10; // 10 seconds
    static final int COUNT = 5000;
    ByteArrayOutputStream bos;
    PrintStream out;

    @BeforeEach
    void setUp() {
        bos = new ByteArrayOutputStream();
        out = new PrintStream(bos);
        System.setOut(out);
    }

    @AfterEach
    void tearDown() {
        System.setOut(SYS_OUT);
        out.close();
    }


    @Test
    @Order(2)
    void testConcurrencyRunner() { // have to run several times to be sure
        int count = 0;
        String msg = "";
        for (int i = 0; i < 5; i++) {
            try {
                setUp();
                testConcurrency();
                tearDown();
            } catch (AssertionError e) {
                ++count;
                msg = e.getMessage();
                System.err.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertTrue(count < 5, msg);
    }

    void testConcurrency() throws IOException {
        pause(100); // let JVM handle threads
        Thread.currentThread().setPriority(9);
        int baseCount = Thread.activeCount();

        Thread thread = new Thread(() -> IncDecThreads.main(null), "runner");
        thread.setPriority(4);
        thread.start();
        boolean flag = wait(ac -> ac == baseCount, ac -> (ac - baseCount) == 3);
        assertTrue(flag, "The threads must run concurrently.");
    }

    private boolean wait(Predicate<Integer> endPredicate, Predicate<Integer> returnPredicate) {
        long before = System.currentTimeMillis();
        boolean flag = false;
        int ac;
        while (!endPredicate.test(ac = Thread.activeCount()) && (System.currentTimeMillis() - before) < TEST_TIMEOUT) {
            if (!flag) {
                flag = returnPredicate.test(ac);
            }
        }
        return flag;
    }

    @Order(1)
    @Test
    void testOutputRunner() { // have to run several times to be sure
        int count = 0;
        String msg = "";
        for (int i = 0; i < 5; i++) {
            try {
                setUp();
                testOutput();
                tearDown();
            } catch (AssertionError e) {
                ++count;
                msg = e.getMessage();
                System.err.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertTrue(count < 5, msg);
    }

    void testOutput() throws IOException {
        pause(100); // let JVM handles threads
        int baseCount = Thread.activeCount();
        LOG.debug("BaseCount: {}", baseCount);
        Thread thread = new Thread(() -> IncDecThreads.main(null));
        thread.setPriority(8);
        thread.start();
        wait(activeCount -> activeCount == baseCount, ac -> true);
        pause(10); // let JVM flushes threads buffers
        out.flush();
        bos.flush();
        String output = bos.toString();

        String[] split = output.split(EOL);
        assertEquals(COUNT * 2, split.length,
                "Each thread should prints exactly " + COUNT + " times.");
        IntSummaryStatistics statistics = Arrays.stream(split)
                .collect(Collectors.summarizingInt(s -> Integer.parseInt(s.split(" : ")[2])));
        int actual = Math.abs(statistics.getMax());
        assertTrue(COUNT > actual,
                "Threads must run concurrently. Expected: max value is less then " + COUNT + ", actual: " + actual);
        actual = Math.abs(statistics.getMin());
        assertTrue(COUNT > actual,
                "Threads must run concurrently. Expected: abs(min value) less then " + COUNT + ", actual: " + actual);
    }

    @Test
    void testCompliance() {
        testIncrementInheritance();
        testDecrementInheritance();
        testRules();
    }

    static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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
                            !(targetPackage.startsWith("java.util.concurrent"));
                    return !equals;
                }
            };

    ArchRule ruleNoForeignClasses =
            noClasses().should().accessTargetWhere(isForeignClassPredicate);

    //    @Test
    void testIncrementInheritance() {
        assertEquals(Thread.class, IncDecThreads.Increment.class.getSuperclass());
    }

    //    @Test
    void testDecrementInheritance() {
        assertEquals(Object.class, IncDecThreads.Decrement.class.getSuperclass());
        Class<?>[] interfaces = IncDecThreads.Decrement.class.getInterfaces();
        assertEquals(1, interfaces.length);
        assertTrue(Arrays.stream(interfaces).anyMatch(i -> i == Runnable.class));
    }

    void testRules() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeJars())
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .withImportOption(new ImportOption.DoNotIncludeArchives())
                .importPackages("com.epam.rd.autotask");

        ruleNoSetNames.check(classes);
        ruleNoForeignClasses.check(classes);
    }
}