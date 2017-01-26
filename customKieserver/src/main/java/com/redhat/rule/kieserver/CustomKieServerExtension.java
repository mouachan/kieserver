package com.redhat.rule.kieserver;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.kie.server.api.KieServerConstants;
import org.kie.server.services.api.KieContainerInstance;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerExtension;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;
import org.kie.server.services.drools.DroolsKieSessionLookupHandler;
import org.kie.server.services.drools.RulesExecutionService;
import org.kie.server.services.impl.KieServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomKieServerExtension implements KieServerExtension {

    private static final Logger logger = LoggerFactory.getLogger(CustomKieServerExtension.class);

    public static final String EXTENSION_NAME = "Custom";

    private static final Boolean disabled = Boolean.parseBoolean(System.getProperty(KieServerConstants.KIE_DROOLS_SERVER_EXT_DISABLED, "false"));
    private static final Boolean filterRemoteable = Boolean.parseBoolean(System.getProperty(KieServerConstants.KIE_DROOLS_FILTER_REMOTEABLE_CLASSES, "false"));

    private RulesExecutionService rulesExecutionService;
    private CustomKieContainerCommandServiceImpl batchCommandService;
    private KieServerRegistry registry;

    private List<Object> services = new ArrayList<Object>();
    private boolean initialized = false;

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isActive() {
        return disabled == false;
    }

    @Override
    public void init(KieServerImpl kieServer, KieServerRegistry registry) {
        this.rulesExecutionService = new RulesExecutionService(registry);
        this.batchCommandService = new CustomKieContainerCommandServiceImpl(kieServer, registry, this.rulesExecutionService);
        this.registry = registry;
        if (registry.getKieSessionLookupManager() != null) {
            registry.getKieSessionLookupManager().addHandler(new DroolsKieSessionLookupHandler());
        }
        services.add(batchCommandService);
        services.add(rulesExecutionService);

        initialized = true;
    }

    @Override
    public void destroy(KieServerImpl kieServer, KieServerRegistry registry) {
        // no-op?
    }

    @Override
    public void createContainer(String id, KieContainerInstance kieContainerInstance, Map<String, Object> parameters) {
    }

    @Override
    public void updateContainer(String id, KieContainerInstance kieContainerInstance, Map<String, Object> parameters) {
        disposeContainer(id, kieContainerInstance, parameters);
        // just do the same as when creating container to make sure all is up to date
        createContainer(id, kieContainerInstance, parameters);
    }

    @Override
    public boolean isUpdateContainerAllowed(String id, KieContainerInstance kieContainerInstance, Map<String, Object> parameters) {
        return true;
    }

    @Override
    public void disposeContainer(String id, KieContainerInstance kieContainerInstance, Map<String, Object> parameters) {

    }

    @Override
    public List<Object> getAppComponents(SupportedTransports type) {
        ServiceLoader<KieServerApplicationComponentsService> appComponentsServices
            = ServiceLoader.load(KieServerApplicationComponentsService.class);
        List<Object> appComponentsList =  new ArrayList<Object>();
        Object [] services = { 
                batchCommandService,
                rulesExecutionService,
                registry
        };
        for( KieServerApplicationComponentsService appComponentsService : appComponentsServices ) { 
            appComponentsList.addAll(appComponentsService.getAppComponents(EXTENSION_NAME, type, services));
        }
        return appComponentsList;
    }

    @Override
    public <T> T getAppComponents(Class<T> serviceType) {
       if (serviceType.isAssignableFrom(batchCommandService.getClass())) {
            return (T) this.batchCommandService;
        }
        return null;
    }

    @Override
    public String getImplementedCapability() {
        return KieServerConstants.CAPABILITY_BRM;
    }

    @Override
    public List<Object> getServices() {
        return services;
    }

    @Override
    public String getExtensionName() {
        return EXTENSION_NAME;
    }

    @Override
    public Integer getStartOrder() {
        return 0;
    }

    @Override
    public String toString() {
        return EXTENSION_NAME + " KIE Server extension";
    }

   
}
