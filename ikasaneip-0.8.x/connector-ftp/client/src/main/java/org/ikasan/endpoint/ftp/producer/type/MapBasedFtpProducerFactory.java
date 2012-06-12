/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.endpoint.ftp.producer.type;

import javax.resource.cci.ConnectionFactory;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.endpoint.ftp.producer.FtpProducerAlternateConfiguration;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.Producer;

/**
 * SFTP producer factory for creating sftpProducer endpoint implementations.
 * @author Ikasan Development Team
 */
public class MapBasedFtpProducerFactory implements EndpointFactory<Producer<?>, FtpProducerConfiguration>
{
    /** connection factory */
    private ConnectionFactory connectionFactory;

    /**
     * Constructor
     * @param connectionFactory an FTP {@link ConnectionFactory}
     */
    public MapBasedFtpProducerFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointFactory#createEndpoint(java.lang.Object)
     */
    public Producer<?> createEndpoint(FtpProducerConfiguration configuration)
    {
        configuration.validate();

        FTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(configuration.getClientID());
        spec.setRemoteHostname(configuration.getRemoteHost());
        spec.setMaxRetryAttempts(configuration.getMaxRetryAttempts());
        spec.setRemotePort(configuration.getRemotePort());
        spec.setConnectionTimeout(configuration.getConnectionTimeout());
        spec.setUsername(configuration.getUsername());
        spec.setCleanupJournalOnComplete(configuration.getCleanupJournalOnComplete());
        spec.setActive(configuration.getActive());
        spec.setPassword(configuration.getPassword());
        spec.setDataTimeout(configuration.getDataTimeout());
        spec.setSocketTimeout(configuration.getSocketTimeout());
        spec.setSystemKey(configuration.getSystemKey());

        FTPConnectionSpec alternateSpec = null;
        if (configuration instanceof FtpProducerAlternateConfiguration)
        {
            FtpProducerAlternateConfiguration alternteConfig = (FtpProducerAlternateConfiguration)configuration;
            alternateSpec = this.getConnectionSpec();
            alternateSpec.setClientID(alternteConfig.getClientID());
            alternateSpec.setRemoteHostname(alternteConfig.getAlternateRemoteHost());
            alternateSpec.setMaxRetryAttempts(alternteConfig.getAlternateMaxRetryAttempts());
            alternateSpec.setRemotePort(alternteConfig.getAlternateRemotePort());
            alternateSpec.setConnectionTimeout(alternteConfig.getAlternateConnectionTimeout());
            alternateSpec.setUsername(alternteConfig.getAlternateUsername());
            alternateSpec.setCleanupJournalOnComplete(alternteConfig.getCleanupJournalOnComplete());
            alternateSpec.setActive(alternteConfig.getAlternateActive());
            alternateSpec.setPassword(alternteConfig.getAlternatePassword());
            alternateSpec.setDataTimeout(alternteConfig.getAlternateDataTimeout());
            alternateSpec.setSocketTimeout(alternteConfig.getAlternateSocketTimeout());
            alternateSpec.setSystemKey(alternteConfig.getAlternateSystemKey());
        }

        return this.getEndpoint(spec, alternateSpec, configuration);
    }

    /**
     * Internal endpoint creation method allows for easier overriding of the actual endpoint creation and simpler testing.
     * @param fileTransferConnectionTemplate
     * @param ftpProducerConfiguration
     * @return
     */
    protected Producer<?> getEndpoint(FTPConnectionSpec spec, FTPConnectionSpec alternateSpec, FtpProducerConfiguration ftpProducerConfiguration)
    {
        MapBasedFtpProducer producer = new MapBasedFtpProducer(new FileTransferConnectionTemplate(this.connectionFactory, spec), ftpProducerConfiguration);
        if (alternateSpec != null)
        {
            producer.setAlternateFileTransferConnectionTemplate(new FileTransferConnectionTemplate(this.connectionFactory, alternateSpec));
        }
        return producer;
    }
    
    /**
     * Utility method to aid testing of this class
     * @return SFTPConnectionSpec
     */
    protected FTPConnectionSpec getConnectionSpec()
    {
        return new FTPConnectionSpec();
    }
  
}