package com.acertainsupplychain.performance;

import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

import com.acertainsupplychain.business.*;
import com.acertainsupplychain.interfaces.OrderManager.StepStatus;
import com.acertainsupplychain.server.*;
import com.acertainsupplychain.client.*;
import com.acertainsupplychain.interfaces.*;
import com.acertainsupplychain.utils.*;


public class RunResult {
    public int successes;
    public int totalRuns;
    public long deltaNS;

    public RunResult(int successes,
                     int totalRuns,
                     long deltaNS) {
        this.successes = successes;
        this.totalRuns = totalRuns;
        this.deltaNS = deltaNS;
    }

    public String toString() {
        return "<RunResult: " + successes + "/" + totalRuns + " in " + deltaNS + ">";
    }
}
