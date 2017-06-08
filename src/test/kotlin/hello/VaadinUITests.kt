package hello

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner

import org.assertj.core.api.BDDAssertions.*

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.boot.VaadinAutoConfiguration
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(VaadinUITests.Companion.Config::class), webEnvironment = SpringBootTest.WebEnvironment.NONE)
class VaadinUITests {

	@Autowired lateinit var repository: CustomerRepository

	val vaadinRequest = Mockito.mock(VaadinRequest::class.java)

    lateinit var editor: CustomerEditor

	lateinit var vaadinUI: VaadinUI

	@Before
	fun setup() {
		editor = CustomerEditor(repository)
		vaadinUI = VaadinUI(repository, editor)
	}

	@Test
	fun shouldInitializeTheGridWithCustomerRepositoryData() {
		val customerCount = repository.count().toInt()

		vaadinUI.init(vaadinRequest)

		then(vaadinUI.grid.getColumns()).hasSize(3)
		then(getCustomersInGrid()).hasSize(customerCount)
	}

	private fun getCustomersInGrid() : List<Customer> {
		val ldp = vaadinUI.grid.dataProvider as ListDataProvider
		return ldp.items.toList()
	}

	@Test
	fun shouldFillOutTheGridWithNewData() {
		val initialCustomerCount = repository.count().toInt()
		vaadinUI.init(vaadinRequest)
		customerDataWasFilled(editor, "Marcin", "Grzejszczak")

		editor.save.click()

		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1)

		then(getCustomersInGrid().get(getCustomersInGrid().size - 1))
			.extracting("firstName", "lastName")
			.containsExactly("Marcin", "Grzejszczak");

	}

	@Test
	fun shouldFilterOutTheGridWithTheProvidedLastName() {
		vaadinUI.init(vaadinRequest);
		repository.save(Customer("Josh", "Long"))

		vaadinUI.listCustomers("Long")

		then(getCustomersInGrid()).hasSize(1)
		then(getCustomersInGrid().get(getCustomersInGrid().size - 1))
			.extracting("firstName", "lastName")
			.containsExactly("Josh", "Long")
	}

	@Test
	fun shouldInitializeWithInvisibleEditor() {
		vaadinUI.init(vaadinRequest)

		then(editor.isVisible).isFalse()
	}

	@Test
	fun shouldMakeEditorVisible() {
		vaadinUI.init(vaadinRequest);
		val first = getCustomersInGrid().get(0)
		vaadinUI.grid.select(first)

		then(editor.isVisible).isTrue()
	}

	private fun customerDataWasFilled(editor: CustomerEditor, firstName: String, lastName: String) {
		editor.firstName.value = firstName
		editor.lastName.value = lastName
		editor.editCustomer(Customer(firstName, lastName))
	}

    companion object {

        @Configuration
        @EnableAutoConfiguration(exclude = arrayOf(VaadinAutoConfiguration::class))
        class Config {

            @Autowired
            lateinit var repository: CustomerRepository

            @PostConstruct
            fun initializeData() {
                repository.save(Customer("Jack", "Bauer"));
                repository.save(Customer("Chloe", "O'Brian"));
                repository.save(Customer("Kim", "Bauer"));
                repository.save(Customer("David", "Palmer"));
                repository.save(Customer("Michelle", "Dessler"));
            }
        }


    }
}


