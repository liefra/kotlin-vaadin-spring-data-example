package hello

import com.github.vok.karibudsl.*
import com.vaadin.data.Binder
import com.vaadin.server.FontAwesome
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.Button
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.annotation.Autowired

/**
 * A simple example to introduce building forms. As your real application is probably much
 * more complicated than this example, you could re-use this form in multiple places. This
 * example component is only used in VaadinUI.
 *
 *
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Viritin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
class CustomerEditor @Autowired constructor(private val repository: CustomerRepository) : VerticalLayout() {

    /**
     * The currently edited customer
     */
    private var customer: Customer? = null

    /* Fields to edit properties in Customer entity */
    val firstName: TextField
    val lastName: TextField

    /* Action buttons */
    lateinit var save: Button
    lateinit var cancel: Button
    lateinit var delete: Button
    var binder = Binder(Customer::class.java)

    init {
        isSpacing = true

        firstName = textField("First name")
        lastName = textField("Last name")
        cssLayout {
            styleName = ValoTheme.LAYOUT_COMPONENT_GROUP
            save = button("Save") {
                setPrimary()
                icon = FontAwesome.SAVE
                onLeftClick { repository.save<Customer>(customer) }
            }
            cancel = button("Cancel") {
                onLeftClick { editCustomer(customer) }
            }
            delete = button("Delete") {
                icon = FontAwesome.TRASH_O
                onLeftClick { repository.delete(customer) }
            }
        }

        // bind using naming convention
        binder.bindInstanceFields(this)

        isVisible = false
    }

    fun editCustomer(c: Customer?) {
        if (c == null) {
            isVisible = false
            return
        }
        val persisted = c.id != null
        if (persisted) {
            // Find fresh entity for editing
            customer = repository.findOne(c.id)
        } else {
            customer = c
        }
        cancel.isVisible = persisted

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.bean = customer

        isVisible = true

        // A hack to ensure the whole form is visible
        save.focus()
        // Select all text in firstName field automatically
        firstName.selectAll()
    }

    fun setChangeHandler(h: ()->Unit) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        save.addClickListener { h() }
        delete.addClickListener { h() }
    }
}
