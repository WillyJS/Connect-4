package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * test Suite for Connect4 core
 * @author William J Sanchez
 * @version 1.0, Feb 23, 2020
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Connect4ClientTest.class,
        Connect4ServerTest.class,
        Connect4ComputerPlayerTest.class
})
public class TestSuite {
}
