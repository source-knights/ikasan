package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource( {

        "classpath:monitor-service-conf.xml",
        "classpath:monitor-conf.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:logger-conf.xml",
        "classpath:exception-conf.xml",
        "classpath:sftp-to-log-component-conf.xml",
        "classpath:filetransfer-service-conf.xml"


} )
public class ModuleConfig {

    @Resource
    private ScheduledConsumer sftpConsumer;

    @Resource
    private ScheduledConsumer fileGeneratorScheduledConsumer;

    @Resource
    private Converter payloadToStringConverter;
    @Resource
    private Converter filePayloadGeneratorConverter;

    @Resource
    private Producer sftpProducer;

    @Resource
    private ApplicationContext context;


    @Bean
    public Module getModule(){

        ModuleBuilder mb = new ModuleBuilder(context,"sample-boot-sftp-module");
        FlowBuilder sftpToLogFlowBuilder = mb.getFlowBuilder("sftpToLogFlow");
        Flow sftpToLogFlow = sftpToLogFlowBuilder
                .withDescription("Sftp to Log")
                .consumer("Sftp Consumer", sftpConsumer)
                .converter("SFTP payload to String Converter",payloadToStringConverter)
                .producer("Log", new DevNull()).build();

        FlowBuilder timeGeneratorToSftpFlowBuilder = mb.getFlowBuilder("timeGeneratorToSftpFlow");
        Flow timeGeneratorToSftpFlow = timeGeneratorToSftpFlowBuilder
                .withDescription("Generates random string and send it to sftp as file")
                .consumer("Scheduled Consumer", fileGeneratorScheduledConsumer)
                .converter("Random String Generator",filePayloadGeneratorConverter)
                .producer("Sftp Producer", sftpProducer).build();

        Module module = mb.withDescription("SFTP Sample Module")
                .addFlow(sftpToLogFlow).addFlow(timeGeneratorToSftpFlow).build();
        return module;
    }





}