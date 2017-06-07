package hello

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Customer(

    @field:Id
    @field:GeneratedValue
    val id: Long? = null,

    var firstName: String? = null,

    var lastName: String? = null
) : Serializable {
    constructor(firstName: String, lastName: String) : this(null, firstName, lastName)
}
