package hello

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import org.mockito.BDDMockito.*
import org.mockito.Matchers.argThat

@RunWith(MockitoJUnitRunner::class)
class CustomerEditorTests {

	private val FIRST_NAME = "Marcin"
	private val LAST_NAME = "Grzejszczak"

	@Mock lateinit var customerRepository: CustomerRepository
	@InjectMocks lateinit var editor: CustomerEditor

	@Test
	fun shouldStoreCustomerInRepoWhenEditorSaveClicked() {
		editor.firstName.value = FIRST_NAME
		editor.lastName.value = LAST_NAME
		customerDataWasFilled()

		this.editor.save.click();

		then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()))
	}

	@Test
	fun shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		editor.firstName.value = FIRST_NAME
		this.editor.lastName.value = LAST_NAME
		customerDataWasFilled()

		editor.delete.click()

		then(this.customerRepository).should().delete(argThat(customerMatchesEditorFields()))
	}

	fun customerDataWasFilled() {
		this.editor.editCustomer(Customer(FIRST_NAME, LAST_NAME))
	}

	private fun customerMatchesEditorFields() = object : TypeSafeMatcher<Customer>() {
        override fun describeTo(description: Description) {}

        override fun matchesSafely(item: Customer): Boolean {
            return FIRST_NAME == item.firstName && LAST_NAME == item.lastName
        }
	}

}
