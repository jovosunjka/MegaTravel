package com.bsep_sbz.SIEMCenter.util;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.repository.AlarmRepository;
import java.util.List;
import com.bsep_sbz.SIEMCenter.websockets.Producer;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class DebugAgendaEventListener extends DefaultAgendaEventListener
{
   private KieSession kieSession;
   private AlarmRepository alarmRepository;
   private Producer producer;

   public DebugAgendaEventListener(KieSession kieSession, AlarmRepository alarmRepository, Producer producer) {
      this.kieSession = kieSession;
      this.alarmRepository = alarmRepository;
      this.producer = producer;
   }

   @Override
   public void afterMatchFired(AfterMatchFiredEvent event)
   {
      List<Object> objs = event.getMatch().getObjects();
      if(objs.size() > 0) {
         if(objs.get(0) instanceof Alarm) {
            return;
         }
      }
      // after match is fired, rule 'then' part is not executed yet, and it won't until
      // this method is finished, so we need to create separate thread to get alarm
      Thread t = new Thread(new Runnable() {
         @Override
         public void run() {
            QueryResults results = kieSession.getQueryResults("Get all alarms");
            // save new alarms
            for(QueryResultsRow queryResult : results){
               Alarm a = (Alarm) queryResult.get("$a");
               alarmRepository.save(a);
            }
            // prevent alarms from retrieving again
            kieSession.getAgenda().getAgendaGroup("alarm").setFocus();
            kieSession.fireAllRules();
            // revert focus
            kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
            // send on view
            for(QueryResultsRow queryResult : results){
               Alarm a = (Alarm) queryResult.get("$a");
               producer.sendMessage(a);
            }
         }
      });
      t.start();
   }
}