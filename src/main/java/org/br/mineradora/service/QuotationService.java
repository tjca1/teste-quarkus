package org.br.mineradora.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.br.mineradora.client.CurrencyPriceClient;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.br.mineradora.dto.QuotationDTO;
import org.br.mineradora.entity.QuotationEntity;
import org.br.mineradora.message.KafkaEvents;
import org.br.mineradora.repository.QuotationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrentPrice(){
        try{
            CurrencyPriceDTO currencyPriceDTO = currencyPriceClient.getPriceByPair("USD-BRL");

            LinkedHashMap map = currencyPriceClient.getPriceByPairObject("USD-BRL");
            ObjectMapper mapper = new ObjectMapper();
            mapper.convertValue(currencyPriceDTO , CurrencyPriceDTO.class);

            if(updateCurrentInfoPrice(currencyPriceDTO)){

                kafkaEvents.sendNewKafkaEvent(QuotationDTO.builder()
                        .currencyPrice(new BigDecimal(currencyPriceDTO.getUSDBRL().getBid()))
                        .date(new Date())
                        .build()
                );


            }
        }catch(Exception ex) {
            ex.getMessage();


        }
/*
        kafkaEvents.sendNewKafkaEvent(QuotationDTO.builder()
                        .currencyPrice(new BigDecimal("1"))
                        .date(new Date())
                        .build()

           );
*/





    }

    private Boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceDTO){

        QuotationEntity entity = new QuotationEntity();
        BigDecimal currentPrice = new BigDecimal(currencyPriceDTO.getUSDBRL().getBid());
        AtomicBoolean updatePrice = new AtomicBoolean(false);
        List<QuotationEntity> quotationList = quotationRepository.findAll().list();

        if(quotationList.isEmpty()){

            entity.setDate(new Date());
            entity.setCurrencyPrice(currentPrice);
            //entity.setPair(currencyPriceDTO.getUSDBRL().get);
            saveQuotation(currencyPriceDTO);
        }else{
            QuotationEntity lastDollarEntity = quotationList.get(quotationList.size() - 1);
            if(currentPrice.floatValue() > lastDollarEntity.getCurrencyPrice().floatValue()){
                updatePrice.set(true);
                saveQuotation(currencyPriceDTO);
            }

        }
        return updatePrice.get();
    }

    private void saveQuotation(CurrencyPriceDTO dto) {
        QuotationEntity entity = new QuotationEntity();
        entity.setDate(new Date());
        entity.setCurrencyPrice(new BigDecimal(dto.getUSDBRL().getBid()));
        entity.setPctChange(dto.getUSDBRL().getPctChange());
        entity.setPair("USD-BRL");
        quotationRepository.persist(entity);
    }

}
