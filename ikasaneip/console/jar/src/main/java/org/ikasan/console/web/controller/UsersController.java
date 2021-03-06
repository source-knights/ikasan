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
package org.ikasan.console.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ikasan.console.web.command.UserCriteriaValidator;
import org.ikasan.console.web.command.UserCriteria;
import org.ikasan.security.model.Authority;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller for the various user views
 * 
 * @author Ikasan Development Team
 */
@Controller
//@RequestMapping("/admin/users/*.htm")
@RequestMapping("/admin/users")
@SessionAttributes("user")
public class UsersController
{
    /** username parameter */
    private static final String USERNAME_PARAMETER_NAME = "username";

    /** authority parameter */
    private static final String AUTHORITY_PARAMETER_NAME = "authority";

    /** The user service to use */
    private UserService userService;

    /** The user criteria validator to use */
    private UserCriteriaValidator validator = new UserCriteriaValidator();

    /** Logger for this class */
    private Logger logger = Logger.getLogger(UsersController.class);

    /**
     * Constructor
     * 
     * @param userService - The user service to use
     */
    @Autowired
    public UsersController(UserService userService)
    {
        super();
        if (userService == null)
        {
            throw new IllegalArgumentException("UserService cannot be NULL");
        }
        this.userService = userService;
    }

    /**
     * List the users known to the system
     * 
     * @param model - The model (map)
     * @return "modules/modules"
     */
    @RequestMapping("list.htm")
    public ModelAndView listUsers(ModelMap model)
    {
        if (model != null)
        {
            if (model.get("user") == null)
            {
                model.addAttribute("user", new User(null, null, null, true));
            }
            model.addAttribute("users", this.userService.getUsers());
        }
        return new ModelAndView("admin/users/users");
    }

    /**
     * Navigate to the create user page
     * 
     * @param request - Standard HTTP Request
     * @param response - Standard HTTP Response
     * @return - Model and View for createUser
     */
    @RequestMapping(value = "createUser.htm", method = RequestMethod.GET)
    public ModelAndView createUser(@SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response)
    {
        return new ModelAndView("admin/users/createUser");
    }
    
    /**
     * Accepts submission of the createUser form
     * 
     * @param model - The model (map)
     * @param username - The name of the user we're trying to create
     * @param password - The password of the user we're trying to create
     * @param email - The email address of the user we're trying to create
     * @param enabled - Whether or not the user starts off as enabled in the system
     * @return view the user
     */
    @RequestMapping(value = "saveUser.htm", method = RequestMethod.POST)
    public ModelAndView saveUser(ModelMap model, @RequestParam(required = false) String username, @RequestParam(required = false) String password, @RequestParam(required = false) String email, @RequestParam(required = false) Boolean enabled)
    {
        List<String> errors = new ArrayList<String>();
        
        UserCriteria userCriteria = new UserCriteria(username, password, email);
        this.validator.validate(userCriteria, errors);
        
        if (errors.isEmpty() && this.userService.userExists(username))
        {
            errors.add("User with this username already exists");
        }

        if (!errors.isEmpty())
        {
        	model.addAttribute("errors", errors);
            return createUser(null, null);
        }

        User user = new User(username, password, email, MasterDetailControllerUtil.defaultFalse(enabled));
        this.userService.createUser(user);
        this.logger.info("Created new user, with id:" + user.getId());
        return maintainUser(user.getUsername(), model);
    }

    /**
     * Maintain a user known to the system
     * 
     * @param username - The name of the user we're trying to view
     * @param model - The model (map)
     * @return path to user view
     */
    @RequestMapping(value = "maintainUser.htm", method = RequestMethod.GET)
    public ModelAndView maintainUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        User user = this.userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("allAuthorities", this.userService.getAuthorities());
        model.addAttribute("nonGrantedAuthorities", getNonGrantedAuthorities(user.getAuthorities()));
        return new ModelAndView("admin/users/maintainUser");
    }

    /**
     * Accepts submission of the changePassword form
     * 
     * @param user - The user we're changing the password for
     * @param confirmNewPassword - The password again, for confirmation 
     * @param model - The model (map)
     * @return view the user
     */
    @RequestMapping(value = "changePassword.htm", method = RequestMethod.POST)
    public ModelAndView changePassword(@ModelAttribute("user") User user, @RequestParam("confirm_password") String confirmNewPassword, ModelMap model)
    {
        List<String> errors = new ArrayList<String>();
        try 
        {
            this.userService.changeUsersPassword(user.getUsername(), user.getPassword(), confirmNewPassword);
        }
        catch (IllegalArgumentException e)
        {
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty())
        {
            model.addAttribute("errors", errors);
        }
        return maintainUser(user.getUsername(), model);
    }

    /**
     * Accepts submission of the changeEmail form
     * 
     * @param user - The user we're changing the email for
     * @param model - The model (map)
     * @return view the user
     */
    @RequestMapping(value = "changeEmail.htm", method = RequestMethod.POST)
    public ModelAndView changeEmail(@ModelAttribute("user") User user, ModelMap model)
    {
        List<String> errors = new ArrayList<String>();
        try 
        {
            this.userService.changeUsersEmail(user.getUsername(), user.getEmail());
        }
        catch (IllegalArgumentException e)
        {
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty())
        {
            model.addAttribute("errors", errors);
        }
        return maintainUser(user.getUsername(), model);
    }
    
    
    /**
     * Grant a new authority to an existing user
     * 
     * @param username - The name of the user we're granting authority to 
     * @param authority - The authority we're granting the user
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="grantAuthority.htm", method = RequestMethod.POST)
    public ModelAndView grantAuthority(@RequestParam(USERNAME_PARAMETER_NAME) String username,
            @RequestParam(AUTHORITY_PARAMETER_NAME) String authority, ModelMap model)
    {
        this.userService.grantAuthority(username, authority);
        return maintainUser(username, model);
    }

    /**
     * Revoke an authority from a existing user
     * 
     * @param username - The name of the user we're revoking authority from 
     * @param authority - The authority we're revoking from the user
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="revokeAuthority.htm", method = RequestMethod.POST)
    public ModelAndView revokeAuthority(@RequestParam(USERNAME_PARAMETER_NAME) String username,
            @RequestParam(AUTHORITY_PARAMETER_NAME) String authority, ModelMap model)
    {
        this.userService.revokeAuthority(username, authority);
        return maintainUser(username, model);
    }

    /**
     * Delete a existing user
     * 
     * @param username - The name of the user we're deleting
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="delete.htm", method = RequestMethod.POST)
    public ModelAndView deleteUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        this.userService.deleteUser(username);
        return listUsers(model);
    }

    /**
     * Disable an existing user
     * 
     * @param username - The name of the user we're disabling 
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="disable.htm", method = RequestMethod.POST)
    public ModelAndView disableUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        this.userService.disableUser(username);
        return maintainUser(username, model);
    }

    /**
     * Enable an existing user
     * 
     * @param username - The name of the user we're enabling
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="enable.htm", method = RequestMethod.POST)
    public ModelAndView enableUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        this.userService.enableUser(username);
        return maintainUser(username, model);
    }

    /**
     * Get the list of non granted authorities
     * 
     * @param authorities - Full list of authorities
     * @return list of non granted authorities
     */
    private List<Authority> getNonGrantedAuthorities(Collection<? extends GrantedAuthority> authorities)
    {
        // start with a list of all the authorities
        List<Authority> nonGrantedAuthorities = new ArrayList<Authority>(this.userService.getAuthorities());
        
        // remove all that are granted
        for (GrantedAuthority authority : authorities)
        {
            nonGrantedAuthorities.remove(authority);
        }
        return nonGrantedAuthorities;
    }
}
