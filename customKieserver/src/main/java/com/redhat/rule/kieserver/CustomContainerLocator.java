package com.redhat.rule.kieserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.kie.server.services.api.ContainerLocator;
import org.kie.server.services.api.KieContainerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomContainerLocator implements ContainerLocator {
    private static final Logger logger = LoggerFactory.getLogger(CustomContainerLocator.class);

    private static CustomContainerLocator INSTANCE = new CustomContainerLocator();

    public static CustomContainerLocator get() {
        return INSTANCE;
    }

    @Override
    public String locateContainer(String alias, List<? extends KieContainerInstance> containerInstances) {
        if (containerInstances.isEmpty()) {
            return alias;
        }
        logger.debug("Searching for latest container for alias {} within available containers {}", alias, containerInstances);
        List<KieRepositoryImpl.ComparableVersion> comparableVersions = new ArrayList<KieRepositoryImpl.ComparableVersion>();
        Map<String, String> versionToIdentifier = new HashMap<String, String>();
        containerInstances.forEach(c ->
                {
                    comparableVersions.add(new KieRepositoryImpl.ComparableVersion(c.getKieContainer().getReleaseId().getVersion()));
                    versionToIdentifier.put(c.getKieContainer().getReleaseId().getVersion(), c.getContainerId());
                }
        );
        KieRepositoryImpl.ComparableVersion latest = Collections.max(comparableVersions);
        logger.debug("Latest version for alias {} is {}", alias, comparableVersions);
        return versionToIdentifier.get(latest.toString());
    }

}


