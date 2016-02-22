package com.danidemi.test;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class Testo {

    Logger log = LoggerFactory.getLogger(Testo.class);

    @Test
    public void shouldStartATimer() throws InterruptedException {

        ProcessEngine processEngine = buildInMemoryEngine();

        deployResource(processEngine, "spike-timeout.bpmn");

        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(
                processEngine.getRepositoryService()
                        .createProcessDefinitionQuery()
                        .singleResult().getId()
        );

        ProcessInstanceQuery processInstanceQuery = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstance.getId());
        ProcessInstance processInstance1;
        do {
            Thread.sleep(1000);
            processInstance1 = processInstanceQuery.singleResult();
        }while(processInstance1!=null);



    }

    @Test
    public void shouldExecuteJavaTask() {

        // given

        // ...the resource
        String resource = "spike-services-task.bpmn";

        // ...the engine and the services
        ProcessEngine processEngine = buildInMemoryEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        HistoryService historyService = processEngine.getHistoryService();

        // let's deploy
        deployResource(processEngine, resource);

        // let's start the service
        ProcessDefinition def = repositoryService
                .createProcessDefinitionQuery()
                .singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(def.getId());

        assertThat( processInstance, notNullValue());
        assertThat( processInstance.getId(), notNullValue());

        // let's wait until ended.
        assertThat( processInstance.isEnded(), is(true) );

        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .list();

        System.out.println(list);

        Map<String, HistoricVariableInstance> vars = new LinkedHashMap<>();
        for(HistoricVariableInstance hvi : list){
            vars.put( hvi.getName(), hvi );
        }

        System.out.println(vars);

        assertThat( vars.get("SetVariablesDelegate.isMale").getValue(), is(true) );
        assertThat( vars.get("SetVariablesDelegate.income").getValue(), is(12300.45) );
        assertThat( vars.get("SetVariablesDelegate.name").getValue(), is("I'm John") );

//          is probably not possible to retrieve an execution of a terminated project.
//        List<Execution> list = runtimeService.createExecutionQuery().list();
//        System.out.println(list);
//
//        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).singleResult();
//        assertThat( execution, notNullValue());
//        assertThat( runtimeService, notNullValue());
//
//        Map<String, Object> variables = runtimeService.getVariables(execution.getId());
//        System.out.println(variables);
//
//        Boolean b = (Boolean) runtimeService.getVariableTyped(execution.getId(), "boolean").getValue();
//        String s = (String) runtimeService.getVariableTyped(execution.getId(), "String").getValue();
//        Double d1 = (Double) runtimeService.getVariableTyped(execution.getId(), "double").getValue();
//        Double d2 = (Double) runtimeService.getVariableTyped(execution.getId(), "Double").getValue();
//
//        assertThat( b, is(true) );
//        assertThat( s, is("CAZ") );
//        assertThat( d1, equalTo(12.2) );
//        assertThat( d2, equalTo(12.2) );

    }

    private void deployResource(ProcessEngine processEngine, String resource) {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .name(resource.substring(1, resource.length()-".bpmn".length()) + "-deploy")
                .addClasspathResource(resource)
                .deploy();
    }

    @Test
    public void allBpmnShouldBeOk() {

        // given
        ProcessEngine processEngine = buildInMemoryEngine();

        List<String> bpmnsToLoad = Arrays.asList(
                "pdta.bpmn",
                "pdta-fumatori.bpmn",
                "pdta-fumatori-swimlane.bpmn",
                "swimlanes-simple.bpmn",
                "spike-services-task.bpmn",
                "pdta-fumatori-swimlane.bpmn"
        );
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        LinkedHashMap<String, ProcessEngineException> wrongOnes = new LinkedHashMap<>();

        // when
        for(String resource : bpmnsToLoad){

            try{
                Deployment deployment = repositoryService
                        .createDeployment()
                        .name(resource + "-deploy")
                        .addClasspathResource(resource)
                        .deploy();



            }catch(ProcessEngineException pee){
                wrongOnes.put(resource, pee);
            }

        }

        // this is probably too much, because engine does not know which variables should be used.
//        List<Deployment> deps = repositoryService.createDeploymentQuery().list();
//
//        List<ProcessDefinition> defs = repositoryService
//                .createProcessDefinitionQuery()
//                .list();
//
//        for(Deployment dep : deps) {
//            for(ProcessDefinition def : defs){
//                try{
//                    runtimeService.startProcessInstanceById( def.getId() );
//                }catch (ProcessEngineException pee){
//                    throw new ProcessEngineException("An error occurred in deployment " + dep.getName() + ":" + pee.getMessage(), pee);
//                }
//            }
//        }




        // then
        assertThat( wrongOnes.toString(), wrongOnes.isEmpty(), is(true) );

    }

    @Test
    public void shouldThrowExceptionWhenBpmnIsWrong() {

        // given
        ProcessEngine processEngine = buildInMemoryEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        // when
        ProcessEngineException expectedException = null;
        try{
            Deployment deployment = repositoryService
                    .createDeployment()
                    .addClasspathResource("invalid-bpmn.bpmn")
                    .deploy();
            fail();
        }catch(ProcessEngineException pee){
            expectedException = pee;
        }catch(Exception e){
            fail();
        }

    }

    private ProcessEngine buildInMemoryEngine() {
        return ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .setJobExecutorActivate(true)
                .buildProcessEngine();
    }

    @Test
    public void shouldInitializeAndDestroyAProcessEngine2() {

        log.info("Test start");


        InputStream resourceAsStream = getClass().getResourceAsStream("pdta.bpmn");
        assertThat( resourceAsStream, notNullValue() );


        ProcessEngine processEngine = buildInMemoryEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService
                .createDeployment()
                .addClasspathResource("pdta.bpmn")
                .deploy();




//        ProcessEngines.init();
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        ProcessEngines.destroy();
        assertThat( processEngine, notNullValue() );


        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        IdentityService identityService = processEngine.getIdentityService();
        FormService formService = processEngine.getFormService();
        HistoryService historyService = processEngine.getHistoryService();
        ManagementService managementService = processEngine.getManagementService();
        FilterService filterService = processEngine.getFilterService();
        ExternalTaskService externalTaskService = processEngine.getExternalTaskService();
        CaseService caseService = processEngine.getCaseService();
        DecisionService decisionService = processEngine.getDecisionService();



    }

    @Test
    public void shouldInitializeAndDestroyAProcessEngine() {

        // given
        ProcessEngine processEngine = buildInMemoryEngine();

        // then
        assertThat( processEngine, notNullValue() );

    }

}
