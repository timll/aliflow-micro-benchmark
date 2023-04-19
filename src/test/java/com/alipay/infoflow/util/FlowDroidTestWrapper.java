package com.alipay.infoflow.util;

import org.junit.Assert;
import soot.jimple.infoflow.AbstractInfoflow;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser;
import soot.jimple.infoflow.cfg.DefaultBiDiICFGFactory;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;
import soot.jimple.infoflow.sourcesSinks.manager.DefaultSourceSinkManager;
import soot.jimple.infoflow.util.DebugFlowFunctionTaintPropagationHandler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

public class FlowDroidTestWrapper {
    protected static String appPath, libPath;

    static void appendWithSeparator(StringBuilder sb, File f) throws IOException {
        if (f.exists()) {
            if (sb.length() > 0)
                sb.append(System.getProperty("path.separator"));
            sb.append(f.getCanonicalPath());
        }
    }

    public static void commonSetup() throws IOException {
        File testSrc = new File("target" + File.separator + "test-classes");
        if (!testSrc.exists()) {
            Assert.fail("Test aborted - none of the test sources are available");
        }
        appPath = testSrc.toString();

        StringBuilder libPathBuilder = new StringBuilder();
        appendWithSeparator(libPathBuilder,
                new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"));
        appendWithSeparator(libPathBuilder, new File("/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar"));
        libPath = libPathBuilder.toString();
    }

    private String entrypoint;
    private AbstractInfoflow infoflow;

    public FlowDroidTestWrapper() throws IOException, URISyntaxException {
        infoflow = new Infoflow("", false, new DefaultBiDiICFGFactory());
        infoflow.setThrowExceptions(true);
        infoflow.setTaintPropagationHandler(new DebugFlowFunctionTaintPropagationHandler());
        infoflow.setTaintWrapper(TaintWrapperFactory.createTaintWrapper());
        this.entrypoint = "";
    }

    public FlowDroidTestWrapper setResultRootDir(String dir) {
        return this;
    }

    public FlowDroidTestWrapper rewriteEntrypoints(String entrypoint) {
        this.entrypoint = entrypoint.replace("\n", "");
        return this;
    }

    public FlowDroidTestWrapper run() {
        try {
            commonSetup();
            infoflow.computeInfoflow(appPath, libPath,
                    new DefaultEntryPointCreator(Collections.singleton(entrypoint)),
                    new DefaultSourceSinkManager(PermissionMethodParser.fromFile("AliSourcesSinks.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public int getResultSize() {
        return infoflow.getResults().size();
    }

    public FlowDroidTestWrapper setDumpCg(boolean b) {
        return this;
    }

    public FlowDroidTestWrapper setDumpJimple(boolean b) {
        return this;
    }

    public FlowDroidTestWrapper setLogLevelDebug(boolean b) {
        return this;
    }
}
