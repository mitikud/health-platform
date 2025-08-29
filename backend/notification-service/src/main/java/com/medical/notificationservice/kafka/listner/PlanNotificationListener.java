package com.medical.notificationservice.kafka.listner;


import com.medical.commonsevents.events.MedicationPlanCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanNotificationListener {

    @KafkaListener(topics = "${topics.planCreated:meds.plan.created}",
            containerFactory = "planListenerFactory")
    public void onPlanCreated(MedicationPlanCreatedEvent evt) {
        log.info("Medication plan created: {} (lang={})", evt.getPlanId(), evt.getLanguage());
        // TODO: send email/SMS via NotificationService#send(...)
    }
}
