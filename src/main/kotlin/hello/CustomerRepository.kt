package hello;

import org.springframework.data.jpa.repository.JpaRepository

import java.util.List

interface CustomerRepository : JpaRepository<Customer, Long> {

	fun findByLastNameStartsWithIgnoreCase(lastName: String?) : List<Customer>
}
