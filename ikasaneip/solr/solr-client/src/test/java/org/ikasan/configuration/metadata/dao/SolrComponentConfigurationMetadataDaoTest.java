package org.ikasan.configuration.metadata.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.configuration.metadata.model.SolrConfigurationMetaData;
import org.ikasan.configuration.metadata.model.SolrConfigurationParameterMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolrComponentConfigurationMetadataDaoTest extends SolrTestCaseJ4
{
    @Test
    @DirtiesContext
    public void test_save_component_metadata_list() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);
            dao.save(solrConfigurationMetaData);

            dao.save(solrConfigurationMetaData);

            assertEquals(2, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(2, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao ();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);

            dao.save(solrConfigurationMetaData);

            SolrConfigurationMetaData configurationMetaData = (SolrConfigurationMetaData)dao.findById("configurationId");

            Assert.assertEquals("id equals","configurationId", configurationMetaData.getConfigurationId());
            Assert.assertEquals("description equals","description", configurationMetaData.getDescription());
            Assert.assertEquals("implementingClass equals","implementingClass", configurationMetaData.getImplementingClass());
            Assert.assertEquals("1 configuration parameter", 1, configurationMetaData.getParameters().size());

            server.close();
        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
