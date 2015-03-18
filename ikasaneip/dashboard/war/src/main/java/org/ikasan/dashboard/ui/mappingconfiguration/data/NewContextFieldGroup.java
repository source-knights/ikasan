/*
 * $Id: NewContextFieldGroup.java 40677 2014-11-07 17:14:59Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/data/NewContextFieldGroup.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

/**
 * @author CMI2 Development Team
 *
 */
public class NewContextFieldGroup extends FieldGroup
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(NewContextFieldGroup.class);

    private static final long serialVersionUID = -5167608612810176052L;

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    private RefreshGroup refreshGroup;
    private MappingConfigurationService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewContextFieldGroup(RefreshGroup refreshGroup
            , MappingConfigurationService mappingConfigurationService)
    {
        super();
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
    }

    /**
     * Constructor
     * 
     * @param itemDataSource
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewContextFieldGroup(Item itemDataSource, RefreshGroup refreshGroup
            , MappingConfigurationService mappingConfigurationService)
    {
        super(itemDataSource);
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.fieldgroup.FieldGroup#commit()
     */
    @Override
    public void commit() throws CommitException
    {
        Field<String> name = (Field<String>) this.getField(NAME);
        Field<String> description = (Field<String>) this.getField(DESCRIPTION);

        ConfigurationContext context = new ConfigurationContext();
        context.setDescription(description.getValue());
        context.setName(name.getValue());

        try
        {
            this.mappingConfigurationService.saveConfigurationConext(context);

            UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(MappingConfigurationUISessionValueConstants.USER);

            logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
                + " added a new Mapping Configuration Context:  " 
                    + context);
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Notification.show("Cauget exception trying to save a new Configuration Context!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
        }

        this.refreshGroup.refresh();
    }
}