package hello

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import org.assertj.core.api.BDDAssertions.*
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class), webEnvironment = NONE)
class ApplicationTests {

	@Autowired
	private lateinit var repository: CustomerRepository

	@Test
	fun shouldFillOutComponentsWithDataWhenTheApplicationIsStarted() {
		then(this.repository.count()).isEqualTo(5)
	}

	@Test
	fun shouldFindTwoBauerCustomers() {
		then(this.repository.findByLastNameStartsWithIgnoreCase("Bauer")).hasSize(2)
	}
}
