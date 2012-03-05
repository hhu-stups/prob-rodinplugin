package de.prob.core.domainobjects.ltl.unittests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CounterExampleFinallyUnitTest.class,
		CounterExampleGloballyUnitTest.class, CounterExampleNextUnitTest.class,
		CounterExampleUntilUnitTest.class,
		CounterExampleWeakUntilUnitTest.class,
		CounterExampleReleaseUnitTest.class, CounterExampleOnceUnitTest.class,
		CounterExampleHistoryUnitTest.class,
		CounterExampleYesterdayUnitTest.class,
		CounterExampleSinceUnitTest.class, CounterExampleTriggerUnitTest.class,
		CounterExampleNotUnitTest.class, CounterExampleAndUnitTest.class,
		CounterExampleOrUnitTest.class, CounterExampleImplyUnitTest.class })
public final class CounterExampleAllUnitTests {
}
