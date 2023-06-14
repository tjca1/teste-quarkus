package org.br.mineradora.scheduler;


import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.br.mineradora.service.QuotationService;

@ApplicationScoped
public class QuotationScheduter {

    @Inject
    QuotationService service;

    @Transactional
    @Scheduled(every = "30s" , identity = "task-job")
    void schedule(){
        service.getCurrentPrice();
    }
}
