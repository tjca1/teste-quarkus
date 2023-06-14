package org.br.mineradora.message;


import io.netty.channel.ChannelHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.br.mineradora.dto.QuotationDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaEvents {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("quotation-channel") /* CANAL QUE VAI TER ACESSO AO TOPICO DO KAFKA*/
    Emitter<QuotationDTO> quotationRequestEmitter;

    public void sendNewKafkaEvent(QuotationDTO dto){
        LOG.info("-- Enviando contacao para o topico kafka");
        quotationRequestEmitter.send(dto).toCompletableFuture().join();
    }
}
