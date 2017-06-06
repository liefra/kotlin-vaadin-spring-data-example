package hello

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

import com.vaadin.server.FontAwesome
import com.vaadin.server.Sizeable
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

@SpringUI
class VaadinUI @Autowired
constructor(private val repo: CustomerRepository, private val editor: CustomerEditor) : UI() {

    internal val grid: Grid<Customer>

    internal val filter: TextField

    private val addNewBtn: Button

    init {
        this.grid = Grid<Customer>(Customer::class.java)
        this.filter = TextField()
        this.addNewBtn = Button("New customer", FontAwesome.PLUS)
    }

    public override fun init(request: VaadinRequest) {
        // build layout
        val actions = HorizontalLayout(filter, addNewBtn)
        val mainLayout = VerticalLayout(actions, grid, editor)
        content = mainLayout

        grid.setHeight(300f, Sizeable.Unit.PIXELS)
        grid.setColumns("id", "firstName", "lastName")

        filter.placeholder = "Filter by last name"

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.valueChangeMode = ValueChangeMode.LAZY
        filter.addValueChangeListener { e -> listCustomers(e.value) }

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener { e -> editor.editCustomer(e.value) }

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener { e -> editor.editCustomer(Customer("", "")) }

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler {
            editor.isVisible = false
            listCustomers(filter.value)
        }

        // Initialize listing
        listCustomers(null)
    }

    // tag::listCustomers[]
    internal fun listCustomers(filterText: String?) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll())
        } else {
            grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText))
        }
    }
    // end::listCustomers[]

}
