package fi.muni.pa165.web.pages;

import fi.muni.pa165.dto.EmployeeDto;
import fi.muni.pa165.dto.ServiceDto;
import fi.muni.pa165.dto.UserRole;
import fi.muni.pa165.service.EmployeeService;
import fi.muni.pa165.service.ServiceService;
import fi.muni.pa165.web.DogBarberShopApplication;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.validation.validator.StringValidator;

/**
 *
 * @author Oliver Pentek
 */
public final class EmployeePage extends TemplatePage {
    private final int TABLE_RELOAD_INTERVAL = 5;
    private boolean isUpdateButton;
    private Label whatToDoLabel;
    private final EmployeeService service;
    private final ServiceService serviceService;
    private List<ServiceDto> selectedServices;
    private List<ServiceDto> unselectedServices;
    private Palette<ServiceDto> palette = null;

    public EmployeePage() {
        super();
        service = DogBarberShopApplication.get().getEmployeeService();
        serviceService = DogBarberShopApplication.get().getServiceService();
        this.initComponents();
    }

    private void initComponents() {

        /*
         * Panel pre zobrazenie sprav, napriklad ak sa nepodari validacia
         */
        final FeedbackPanel feedback = new FeedbackPanel(ComponentIDs.FEEDBACK_PANEL);
        this.add(feedback);

        final RequiredTextField name = new RequiredTextField<>(ComponentIDs.NAME);
        final RequiredTextField surname = new RequiredTextField<>(ComponentIDs.SURNAME);
        final RequiredTextField address = new RequiredTextField<>(ComponentIDs.ADDRESS);
        final RequiredTextField phone = new RequiredTextField<>(ComponentIDs.PHONE);
        final RequiredTextField salary = new RequiredTextField<>(ComponentIDs.SALARY);
        final RequiredTextField login = new RequiredTextField<>(ComponentIDs.LOGIN);
        final RequiredTextField password = new RequiredTextField<>(ComponentIDs.PASSWORD);
        final CheckBox role = new CheckBox(ComponentIDs.ROLE, Model.of(Boolean.FALSE));
            

        /*
         * Kontrola vstupnych dat - ak sa nepodari zobrazi sa FeedBackPanel
         */
        phone.add(new StringValidator(6, 15));

        final Form<EmployeeDto> editableForm = new Form<EmployeeDto>(ComponentIDs.ADD_EDIT_FORM, new CompoundPropertyModel<>(new EmployeeDto())) {
            
             @Override
            public void onSubmit() {
                super.onSubmit();
                 final EmployeeDto employee = this.getModelObject();
                employee.setServices(new ArrayList<>(palette.getModelCollection()));
                employee.setRole(role.getModelObject() ? UserRole.ADMIN : UserRole.USER);
                if (!EmployeePage.this.isUpdateButton) {                        //adding
                    service.addEmployee(employee);
                } else {                                                        //updating   
                    service.updateEmloyee(employee);
                    setNewCustomerLabel();
                    isUpdateButton = false;
                }
                role.setModelObject(Boolean.FALSE);
                this.setModel(new CompoundPropertyModel<>(new EmployeeDto()));
                palette.setModel(new ListModel(new ArrayList<ServiceDto>()));
            }
        };

        final Button submit = new Button(ComponentIDs.SUBMIT_BUTTON);
        
        // paleta
        selectedServices = new ArrayList<>();
        unselectedServices = serviceService.getAllServices();
        IChoiceRenderer<ServiceDto> renderer = new ChoiceRenderer<>("name", "id");

        palette = new Palette<ServiceDto>(ComponentIDs.PALETTE,
                new ListModel<>(selectedServices),
                new CollectionModel<>(unselectedServices),
                renderer, 10, Boolean.FALSE) {
        };

        editableForm.add(palette);
        editableForm.add(submit);
        editableForm.setDefaultButton(submit);
        editableForm.add(name);
        editableForm.add(surname);
        editableForm.add(address);
        editableForm.add(phone);
        editableForm.add(salary);
        editableForm.add(login);
        editableForm.add(password);
        editableForm.add(role);
        add(editableForm);

        
        //tabulka
        final Form tableForm = new Form<EmployeeDto>(ComponentIDs.TABLE_FORM, new Model<EmployeeDto>()) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                final EmployeeDto employee = this.getModelObject();
                if (!EmployeePage.this.isUpdateButton) {                         //delete
                    service.deleteEmployee(employee);
                    editableForm.setModelObject(new EmployeeDto());
                    setNewCustomerLabel();
                    role.setModelObject(Boolean.FALSE);
                    palette.setModel(new ListModel(new ArrayList<>()));
                } else {                                                        //update
                    editableForm.setModelObject(employee);
                    setUpdateCustomerlabel();
                    role.setModelObject(UserRole.ADMIN.equals(employee.getRole()));
                    palette.setModel(new ListModel(employee.getServices()));
                }
            }
        };

        final IModel<? extends List<? extends EmployeeDto>> employeeModel = new InnerLoadableCustomerModel();
        final ListView<EmployeeDto> listView = new ListView<EmployeeDto>(ComponentIDs.EMPLOYEES_LIST, employeeModel) {
            @Override
            protected void populateItem(ListItem<EmployeeDto> li) {
                final EmployeeDto employee = li.getModelObject();
                li.add(new Label(ComponentIDs.NAME_LIST_ITEM, employee.getName()));
                li.add(new Label(ComponentIDs.SURNAME_LIST_ITEM, employee.getSurname()));
                li.add(new Label(ComponentIDs.ADDRESS_LIST_ITEM, employee.getAddress()));
                li.add(new Label(ComponentIDs.PHONE_LIST_ITEM, employee.getPhone()));
                li.add(new Label(ComponentIDs.SALARY_LIST_ITEM, employee.getSalary()));
                li.add(new Button(ComponentIDs.DELETE_BUTTON) {
                    @Override
                    public void onSubmit() {
                        super.onSubmit();
                        isUpdateButton = false;
                        tableForm.setModelObject(employee);          
                    }
                });

                li.add(new Button(ComponentIDs.EDIT_BUTTON) {
                    @Override
                    public void onSubmit() {
                        super.onSubmit();
                        isUpdateButton = true;
                        tableForm.setModelObject(employee);   
                    }
                });
            }
        };

//        tableForm.add(new AjaxLink(ComponentIDs.REFRESH_LINK){
//
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                target.add(tableForm);
////                tableForm.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(TABLE_RELOAD_INTERVAL)));
//            }
//     
//        });

        tableForm.setOutputMarkupId(true);
        tableForm.add(listView);
        //       tableForm.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(TABLE_RELOAD_INTERVAL)));
        this.add(tableForm);
        this.add(whatToDoLabel = new Label(ComponentIDs.ADD_EDIT_LABEL, new Model()));
        setNewCustomerLabel();

    }

    private void setNewCustomerLabel() {
        whatToDoLabel.setDefaultModelObject(this.getLocalizer().getString("EmployeePage.label.newEmployee", this));
    }

    private void setUpdateCustomerlabel() {
        whatToDoLabel.setDefaultModelObject(this.getLocalizer().getString("EmployeePage.label.editEmployee", this));
    }

    private class InnerLoadableCustomerModel extends LoadableDetachableModel<List<EmployeeDto>> {

        @Override
        protected List<EmployeeDto> load() {
            return EmployeePage.this.service.getAllEmployee();
        }
    }

    /**
     * Zoznam komponent vramci tejto
     */
    private static class ComponentIDs {

        private static final String FEEDBACK_PANEL = "feedback";
        private static final String NAME = "name";
        private static final String SURNAME = "surname";
        private static final String ADDRESS = "address";
        private static final String PHONE = "phone";
        private static final String SALARY = "salary";
        private static final String LOGIN = "login";
        private static final String PASSWORD = "password";
        private static final String ROLE = "role";
        private static final String ADD_EDIT_FORM = "addEmployeeForm";
        private static final String SUBMIT_BUTTON = "submitButton";
        private static final String EDIT_BUTTON = "editButton";
        private static final String DELETE_BUTTON = "deleteButton";
        private static final String TABLE_FORM = "employeesTable";
        private static final String EMPLOYEES_LIST = "employees";
        private static final String NAME_LIST_ITEM = "listName";
        private static final String SURNAME_LIST_ITEM = "listSurname";
        private static final String ADDRESS_LIST_ITEM = "listAddress";
        private static final String PHONE_LIST_ITEM = "listPhone";
        private static final String SALARY_LIST_ITEM = "listSalary";
        private static final String REFRESH_LINK = "refresh";
        private static final String ADD_EDIT_LABEL = "whatToDo";
        public static final String PALETTE = "palette";
    }
}
