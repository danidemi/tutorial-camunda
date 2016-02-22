package com.danidemi.tutorial.camunda.firststep.services;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SetVariablesDelegate implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		
		System.out.println( new ToStringBuilder(execution, ToStringStyle.MULTI_LINE_STYLE).toString() );
		
		System.out.println( "waiting" );
		Thread.sleep(1000);
		
		execution.setVariable(key("isMale"), true);
		execution.setVariable(key("name"), "I'm John");
		execution.setVariable(key("income"), 12300.45);
		

	}

	private String key(String key) {
		return getClass().getSimpleName() + "." + key;
	}

}
