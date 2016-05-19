package com.lxdnz.nz.movieproject;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by alex on 19/05/16.
 */
public class FullSuiteTest extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullSuiteTest.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullSuiteTest() {
        super();
    }
}
