package hello

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Customer(
    var firstName: String,
    var lastName: String,
    @field:Id @field:GeneratedValue val id: Long? = null
) : Serializable
