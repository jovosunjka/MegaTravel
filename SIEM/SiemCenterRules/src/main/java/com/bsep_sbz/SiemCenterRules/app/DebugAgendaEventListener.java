package com.bsep_sbz.SiemCenterRules.app;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
 
public class DebugAgendaEventListener extends DefaultAgendaEventListener {
 
   @Override
   public void afterMatchFired(AfterMatchFiredEvent event) {
      Rule rule = event.getMatch().getRule();
      System.out.println("Rule fired: " + rule.getName());
   }
}