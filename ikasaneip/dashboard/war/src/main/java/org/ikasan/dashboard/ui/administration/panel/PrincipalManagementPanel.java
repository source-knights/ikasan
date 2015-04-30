/*
 * $Id: EstateViewPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/EstateViewPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.administration.panel;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class PrincipalManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(PrincipalManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private ComboBox rolesCombo;
	private Table principalDropTable = new Table();
	private TextField principalTypeField = new TextField();
	private TextArea descriptionField = new TextArea();
	private Table roleTable = new Table();
	private IkasanPrincipal principal;
	private AutocompleteField<IkasanPrincipal> principalNameField;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public PrincipalManagementPanel(UserService userService, SecurityService securityService)
	{
		super();
		this.userService = userService;
		if (this.userService == null)
		{
			throw new IllegalArgumentException("userService cannot be null!");
		}
		this.securityService = securityService;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"securityService cannot be null!");
		}

		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel("Principal Management");
		securityAdministrationPanel.setStyleName("dashboard");
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(2, 3);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();

		Label principalNameLabel = new Label("Principal Name");

		principalNameField = new AutocompleteField<IkasanPrincipal>();
		principalNameField.setWidth("40%");

		final DragAndDropWrapper principalNameFieldWrap = new DragAndDropWrapper(
				principalNameField);
		principalNameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);

		principalTypeField.setWidth("40%");
		descriptionField.setWidth("40%");
		descriptionField.setHeight("60px");
		
		roleTable.addContainerProperty("Role", String.class, null);
		roleTable.addContainerProperty("", Button.class, null);
		roleTable.setHeight("400px");
		roleTable.setWidth("300px");
		
		principalDropTable.addContainerProperty("Members", String.class, null);
		principalDropTable.addContainerProperty("", Button.class, null);
		principalDropTable.setHeight("400px");
		principalDropTable.setWidth("300px");

		principalNameField.setQueryListener(new AutocompleteQueryListener<IkasanPrincipal>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<IkasanPrincipal> field,
					String query)
			{
				for (IkasanPrincipal principal : securityService.getPrincipalByNameLike(query))
				{
					field.addSuggestion(principal, principal.getName());
				}
			}
		});

		principalNameField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<IkasanPrincipal>()
		{
			@Override
			public void onSuggestionPicked(final IkasanPrincipal principal)
			{
				PrincipalManagementPanel.this.principal = principal;
				PrincipalManagementPanel.this.setValues();
			}
		});
		
		GridLayout formLayout = new GridLayout(2, 3);
		formLayout.setWidth("100%");
		formLayout.setHeight("135px");
		
		formLayout.setColumnExpandRatio(0, 1);
		formLayout.setColumnExpandRatio(1, 5);

		formLayout.addComponent(principalNameLabel, 0, 0);
		formLayout.addComponent(principalNameFieldWrap, 1, 0);

		Label principalTypeLabel = new Label("Principal Type");
		formLayout.addComponent(principalTypeLabel, 0, 1);
		formLayout.addComponent(principalTypeField, 1, 1);

		Label descriptionLabel = new Label("Description");
		formLayout.addComponent(descriptionLabel, 0, 2);
		formLayout.addComponent(descriptionField, 1, 2);
		
		gridLayout.addComponent(formLayout, 0, 0, 1, 0);
		
		gridLayout.addComponent(new Label("<hr />",ContentMode.HTML),0, 1, 1, 1);

		principalDropTable.setDragMode(TableDragMode.ROW);
		principalDropTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				if(rolesCombo.getValue() == null)
				{
					// Do nothing if there is no role selected
					logger.info("Ignoring drop: " + dropEvent);
					return;
				}

				final WrapperTransferable t = (WrapperTransferable) dropEvent
						.getTransferable();

				final AutocompleteField sourceContainer = (AutocompleteField) t
						.getDraggedComponent();
				logger.info("sourceContainer.getText(): "
						+ sourceContainer.getText());

				Button deleteButton = new Button();
				ThemeResource deleteIcon = new ThemeResource(
						"images/remove-icon.png");
				deleteButton.setIcon(deleteIcon);
				deleteButton.setStyleName(Reindeer.BUTTON_LINK);
				
				final IkasanPrincipal principal = securityService.findPrincipalByName(sourceContainer.getText());
				final Role roleToRemove = (Role)rolesCombo.getValue();
				
				deleteButton.addClickListener(new Button.ClickListener() 
		        {
		            public void buttonClick(ClickEvent event) 
		            {
		            	principalDropTable.removeItem(principal.getName());
		            	
		            	principal.getRoles().remove(roleToRemove);
		            	
		            	securityService.savePrincipal(principal);
		            	
		            	if(principalNameField.getText().equals(principal.getName()))
		            	{
		            		roleTable.removeItem(roleToRemove);
		            	}
		            }
		        });
				
				principalDropTable.addItem(new Object[]
						{ sourceContainer.getText(), deleteButton}, sourceContainer.getText());

				principal.getRoles().add((Role)rolesCombo.getValue());
				
				securityService.savePrincipal(principal);

				roleTable.removeAllItems();

				for (final Role role : principal.getRoles())
				{
					Button roleDeleteButton = new Button();
					roleDeleteButton.setIcon(deleteIcon);
					roleDeleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					roleDeleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	principalDropTable.removeItem(principal.getName());
			            }
			        }); 
					
					roleTable.addItem(new Object[]
					{ role.getName(), roleDeleteButton }, role);
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		gridLayout.addComponent(roleTable, 0, 2, 1, 2);
					
		this.rolesCombo = new ComboBox();
		this.rolesCombo.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final Role role = (Role)event.getProperty().getValue();
		       
		        if(role != null)
		        {
			        logger.info("Value changed got Role: " + role);
			        
			        List<IkasanPrincipal> principals = securityService.getAllPrincipalsWithRole(role.getName());
					
					principalDropTable.removeAllItems();
					
					
					for(final IkasanPrincipal principal: principals)
					{
						Button deleteButton = new Button();
						ThemeResource deleteIcon = new ThemeResource(
								"images/remove-icon.png");
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {
				            	principalDropTable.removeItem(principal.getName());
				            	
				            	principal.getRoles().remove(role);
				            	
				            	securityService.savePrincipal(principal);
				            	
				            	if(principalNameField.getText().equals(principal.getName()))
				            	{
				            		roleTable.removeItem(role);
				            	}
				            }
				        });
						
						
						principalDropTable.addItem(new Object[]
								{ principal.getName(), deleteButton }, principal.getName());
					}
		        }
		    }
		});
			
		Panel roleMemberPanel = new Panel("Role/Member Associations");
		
		roleMemberPanel.setStyleName("dashboard");
		roleMemberPanel.setHeight("100%");
		roleMemberPanel.setWidth("100%");

		VerticalLayout roleMemberLayout = new VerticalLayout();
		roleMemberLayout.setMargin(true);
		roleMemberLayout.setWidth("100%");
		roleMemberLayout.setHeight("100%");
		roleMemberLayout.addComponent(this.rolesCombo);
		roleMemberLayout.setExpandRatio(this.rolesCombo, 0.05f);
		roleMemberLayout.addComponent(this.principalDropTable);
		roleMemberLayout.setExpandRatio(this.principalDropTable, 0.95f);
		
		roleMemberPanel.setContent(roleMemberLayout);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);
		
		VerticalLayout roleMemberPanelLayout = new VerticalLayout();
		roleMemberPanelLayout.setWidth("100%");
		roleMemberPanelLayout.setHeight("100%");
		roleMemberPanelLayout.setMargin(true);
		roleMemberPanelLayout.addComponent(roleMemberPanel);
		roleMemberPanelLayout.setSizeFull();
		
		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
		hsplit.setFirstComponent(layout);
		hsplit.setSecondComponent(roleMemberPanelLayout);


		// Set the position of the splitter as percentage
		hsplit.setSplitPosition(65, Unit.PERCENTAGE);
		hsplit.setLocked(true);
		
		this.setContent(hsplit);
	}

	/**
	 * 
	 */
	protected void setValues()
	{
		this.principal = this.securityService.findPrincipalByName(this.principal.getName());
		this.principalNameField.setText(this.principal.getName());
		this.principalTypeField.setValue(this.principal.getType());
		this.descriptionField.setValue(this.principal.getDescription());

		this.roleTable.removeAllItems();

		for (final Role role : principal.getRoles())
		{
			Button deleteButton = new Button();
			ThemeResource deleteIcon = new ThemeResource(
					"images/remove-icon.png");
			deleteButton.setIcon(deleteIcon);
			deleteButton.setStyleName(Reindeer.BUTTON_LINK);
			
			deleteButton.addClickListener(new Button.ClickListener() 
	        {
	            public void buttonClick(ClickEvent event) 
	            {
	            	PrincipalManagementPanel.this.roleTable.removeItem(role);
	            	
	            	PrincipalManagementPanel.this.principal.getRoles().remove(role);
	            	
	            	PrincipalManagementPanel.this.securityService.savePrincipal(principal);
	            	
	            	PrincipalManagementPanel.this.principalDropTable.removeItem(principal.getName());
	            }
	        });
			
			roleTable.addItem(new Object[]
					{ role.getName(), deleteButton }, role);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		List<Role> roles = this.securityService.getAllRoles();
		
		this.principalNameField.clearChoices();
		this.rolesCombo.removeAllItems();
		this.principalDropTable.removeAllItems();
		
		for(Role role: roles)
		{
			this.rolesCombo.addItem(role);
			this.rolesCombo.setItemCaption(role, role.getName());
		}
		
		if(this.principal != null)
		{
			this.setValues();
		}
		
	}
	
	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(IkasanPrincipal principal)
	{
		this.principal = principal;
	}
}