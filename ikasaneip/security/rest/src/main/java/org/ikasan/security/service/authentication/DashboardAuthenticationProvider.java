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
package org.ikasan.security.service.authentication;

import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.DashboardUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Ikasan Development Team
 *
 */
public class DashboardAuthenticationProvider implements AuthenticationProvider
{
    private static Logger logger = LoggerFactory.getLogger(DashboardAuthenticationProvider.class);

    private DashboardUserServiceImpl dashboardUserService;

    /**
     * @param dashboardUserService
     */
    public DashboardAuthenticationProvider(DashboardUserServiceImpl dashboardUserService)
    {
        super();
        this.dashboardUserService = dashboardUserService;
        if(this.dashboardUserService == null)
        {
            throw new IllegalArgumentException("dashboardUserService cannot be null!");
        }

    }

    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException
    {
        String userName = authentication.getName();
        String password = ((String)authentication.getCredentials());
        if(dashboardUserService.authenticate(userName,password)){

            User user = dashboardUserService.loadUserByUsername(userName);

            Set<IkasanPrincipal> principals = user.getPrincipals();

            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

            for(IkasanPrincipal principal: principals)
            {
                Set<Role> roles = principal.getRoles();

                for(Role role: roles)
                {
                    Set<Policy> policies = role.getPolicies();

                    for(Policy policy: policies)
                    {
                        if(!authorities.contains(policy))
                        {
                            authorities.add(policy);
                        }
                    }
                }
            }

            IkasanAuthentication ikasanAuthentication = new IkasanAuthentication(true, user, authorities, (String)authentication.getCredentials()
                , user.getPreviousAccessTimestamp());

            return ikasanAuthentication;
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz)
    {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz));
    }


}
