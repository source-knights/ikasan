package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Route(value = "policyManagement", layout = IkasanAppLayout.class)
@UIScope
@Component
public class PolicyManagementView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(PolicyManagementView.class);

    @Resource
    private SecurityService securityService;

    private Grid<Policy> policyGrid;

    /**
     * Constructor
     */
    public PolicyManagementView()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setSizeFull();
        this.setSpacing(true);

        H2 policyManagementLabel = new H2("Policy Management");

        Button addPolicyButton = new Button(VaadinIcon.PLUS.create());

        HorizontalLayout leftLayout = new HorizontalLayout();
        leftLayout.setJustifyContentMode(JustifyContentMode.START);
        leftLayout.setWidth("100%");
        leftLayout.add(policyManagementLabel);
        leftLayout.setVerticalComponentAlignment(Alignment.CENTER, policyManagementLabel);

        HorizontalLayout rightLayout = new HorizontalLayout();
        rightLayout.setJustifyContentMode(JustifyContentMode.END);
        rightLayout.setWidth("100%");
        rightLayout.add(addPolicyButton);
        rightLayout.setVerticalComponentAlignment(Alignment.CENTER, addPolicyButton);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");

        layout.add(leftLayout, rightLayout);
        add(layout);

        this.policyGrid = new Grid<>();
        this.policyGrid.setSizeFull();
        this.policyGrid.setClassName("my-grid");

        this.policyGrid.addColumn(Policy::getName).setHeader("Name");
        this.policyGrid.addColumn(Policy::getDescription).setHeader("Type");
        this.policyGrid.addColumn(new ComponentRenderer<>(policy ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            Button trash = new TableButton(VaadinIcon.TRASH.create());
            trash.getStyle().set("width", "30px");
            trash.getStyle().set("height", "30px");
            trash.getStyle().set("font-size", "12pt");

            trash.addClickListener(buttonClickEvent -> this.securityService.deletePolicy(policy));

            horizontalLayout.add(trash);

            return horizontalLayout;
        }));

        add(this.policyGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        List<Policy> policies = this.securityService.getAllPolicies();

        this.policyGrid.setItems(policies);
    }
}
