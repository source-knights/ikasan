/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.console.pointtopointflow.service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.ikasan.console.module.Module;
import org.ikasan.console.pointtopointflow.PointToPointFlowComparator;
import org.ikasan.console.pointtopointflow.dao.PointToPointFlowProfileDao;
import org.ikasan.spec.management.PointToPointFlow;
import org.ikasan.spec.management.PointToPointFlowProfile;

/**
 * Console implementation of <code>PointToPointFlowProfileService</code>
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlowProfileServiceImpl implements PointToPointFlowProfileService
{
    /** DAO for this service to use */
    private PointToPointFlowProfileDao pointToPointFlowProfileDao = null;

    /**
     * Constructor
     * 
     * @param pointToPointFlowProfileDao - DAO for this service to use
     */
    public PointToPointFlowProfileServiceImpl(PointToPointFlowProfileDao pointToPointFlowProfileDao)
    {
        super();
        if (pointToPointFlowProfileDao == null)
        {
            throw new IllegalArgumentException("PointToPointFlowProfileDao cannot be NULL.");
        }
        this.pointToPointFlowProfileDao = pointToPointFlowProfileDao;
    }

    /**
     * Get a list of all PointToPointFlowProfiles
     * 
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getAllPointToPointFlowProfiles()
     */
    public Set<PointToPointFlowProfile> getAllPointToPointFlowProfiles()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = pointToPointFlowProfileDao.findAllPointToPointFlowProfiles();
        this.orderPointToPointFlows(pointToPointFlowProfiles);
        return pointToPointFlowProfiles;
    }

    /**
     * Helper method which orders the PointToPointFlows within each
     * PointToPointFlowProfile
     * 
     * @param pointToPointFlowProfiles - Profiles to adjust
     */
    private void orderPointToPointFlows(Set<PointToPointFlowProfile> pointToPointFlowProfiles)
    {
        for (PointToPointFlowProfile pointToPointFlowProfile : pointToPointFlowProfiles)
        {
            // By using a TreeSet implementation, we can order the flows
// FIXME            TreeSet<PointToPointFlow> pointToPointFlows = new TreeSet<PointToPointFlow>(new PointToPointFlowComparator());
            TreeSet<PointToPointFlow> pointToPointFlows = new TreeSet(new PointToPointFlowComparator());
            pointToPointFlows.addAll(pointToPointFlowProfile.getPointToPointFlows());
            pointToPointFlowProfile.setPointToPointFlows(pointToPointFlows);
        }
    }

    /**
     * Get a list of all PointToPointFlowProfile Ids
     * 
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getAllPointToPointFlowProfileIds()
     */
    public Set<Long> getAllPointToPointFlowProfileIds()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = this.getAllPointToPointFlowProfiles();
        Set<Long> pointToPointFlowProfileIds = new LinkedHashSet<Long>();
        for (PointToPointFlowProfile pointToPointFlowProfile : pointToPointFlowProfiles)
        {
            pointToPointFlowProfileIds.add(pointToPointFlowProfile.getId());
        }
        return pointToPointFlowProfileIds;
    }

    /**
     * @see org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService#getModuleIdsFromPointToPointFlowProfiles(Set)
     */
    public Set<Long> getModuleIdsFromPointToPointFlowProfiles(Set<Long> pointToPointFlowProfileIds)
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = pointToPointFlowProfileDao.findPointToPointFlowProfiles(pointToPointFlowProfileIds);
        return this.getModuleIdsFromProfiles(pointToPointFlowProfiles);
    }

    /**
     * Helper method, retrieves Modules from the PointToPointFlowProfiles given
     * 
     * @param pointToPointFlowProfiles - PointToPointFlowProfiles to get Modules
     *            from
     * @return Set of Modules
     */
    private Set<Module> getModulesFromProfiles(Set<PointToPointFlowProfile> pointToPointFlowProfiles)
    {
        Set<Module> modules = new LinkedHashSet<Module>();
        for (PointToPointFlowProfile profile : pointToPointFlowProfiles)
        {
            for (PointToPointFlow<Module> pointToPointFlow : profile.getPointToPointFlows())
            {
                Module fromModule = pointToPointFlow.getFromModule();
                if (fromModule != null)
                {
                    modules.add(fromModule);
                }
                Module toModule = pointToPointFlow.getToModule();
                if (toModule != null)
                {
                    modules.add(toModule);
                }
            }
        }
        return modules;
    }

    /**
     * Helper method, retrieves Module ids from the PointToPointFlowProfiles
     * given.
     * 
     * @param pointToPointFlowProfiles - PointToPointFlowProfiles to get Module
     *            ids from
     * @return Set of Module ids
     */
    private Set<Long> getModuleIdsFromProfiles(Set<PointToPointFlowProfile> pointToPointFlowProfiles)
    {
        Set<Long> moduleIds = new LinkedHashSet<Long>();
        // Modules in this Set are never NULL
        Set<Module> modules = this.getModulesFromProfiles(pointToPointFlowProfiles);
        for (Module module : modules)
        {
            moduleIds.add(module.getId());
        }
        return moduleIds;
    }
}
