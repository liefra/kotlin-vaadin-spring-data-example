package hello

import com.github.vok.karibudsl.*
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.UI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

@SpringUI
class VaadinUI @Autowired constructor(private val repo: CustomerRepository, private val editor: CustomerEditor) : UI() {

    lateinit var grid: Grid<Customer>
    private lateinit var filter: TextField

    public override fun init(request: VaadinRequest) {
        verticalLayout {
            horizontalLayout { // actions
                filter = textField {
                    placeholder = "Filter by last name"
                    valueChangeMode = ValueChangeMode.LAZY
                    // Replace listing with filtered content when user changes filter
                    addValueChangeListener { e -> listCustomers(e.value) }
                }
                button("New customer") {
                    icon = VaadinIcons.PLUS
                    // Instantiate and edit new Customer the new button is clicked
                    onLeftClick { editor.editCustomer(Customer("", "")) }
                }
            }
            grid = grid(Customer::class) {
                h = 300.px
                showColumns(Customer::id, Customer::firstName, Customer::lastName)
                // Connect selected Customer to editor or hide if none is selected
                asSingleSelect().addValueChangeListener { e -> this@VaadinUI.editor.editCustomer(e.value) }
            }
            addComponent(editor.apply {
                // Listen changes made by the editor, refresh data from backend
                setChangeHandler {
                    isVisible = false
                    listCustomers(filter.value)
                }
            })
        }
        // Initialize listing
        listCustomers(null)
    }

    // tag::listCustomers[]
    fun listCustomers(filterText: String?) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll())
        } else {
            grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText))
        }
    }
    // end::listCustomers[]

}
