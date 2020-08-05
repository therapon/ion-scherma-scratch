import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ionschema.IonSchemaSystemBuilder.Companion.standard
import com.amazon.ionschema.Type


object IonSchemaGettingStarted {
    private val ION = IonSystemBuilder.standard().build()
    @JvmStatic
    fun main(args: Array<String>) {
        val iss = standard()
            .build()

        val schema = iss.newSchema(
            """
            type::{
              name: SingleKey1,
              type: struct,
              content: closed,
              fields: {
                key1: { type: string, occurs: required },
              },
            }
            
            type::{
              name: SingleKey2,
              type: struct,
              content: closed,
              fields: {
                key2: { type: string, occurs: required },
              },
            } 
              
            type::{
              name: BothKey1Key2,
              type: struct,
              content: closed,
              fields: {
                key1: { type: string, occurs: required },
                key2: { type: string, occurs: required },
              },
            }
            
            type::{ 
                name: ValidKeys, 
                one_of: [ 
                    SingleKey1, 
                    SingleKey2, 
                    BothKey1Key2,
                ],
            }
        """
        )

        // key1 only
        println("key1 type")
        val type1 = schema.getType("SingleKey1")
        checkValue(type1, """ { key1: "Susie"} """)
        checkValue(type1, """ { key2: "Smith" } """)
        checkValue(type1, """ { key1: "Susie", key2: "Smith"} """)

        // key2 only
        println("key2 type")
        val type2 = schema.getType("SingleKey2")
        checkValue(type2, """ { key1: "Susie"} """)
        checkValue(type2, """ { key2: "Smith" } """)
        checkValue(type2, """ { key1: "Susie", key2: "Smith"} """)

        // key1&key2 only
        println("key1 & key2 type")
        val type3 = schema.getType("BothKey1Key2")
        checkValue(type3, """ { key1: "Susie"} """)
        checkValue(type3, """ { key2: "Smith" } """)
        checkValue(type3, """ { key1: "Susie", key2: "Smith"} """)
        checkValue(type3, """ { key1: "Susie", key2: "Smith", key3: "foo"} """)

        // one of
        println("Union of key1, or key2 or key1&key2 types")
        val type4 = schema.getType("ValidKeys")
        checkValue(type4, """ { key1: "Susie"} """)
        checkValue(type4, """ { key2: "Smith" } """)
        checkValue(type4, """ { key1: "Susie", key2: "Smith"} """)
        checkValue(type4, """ { key1: "Susie", key2: "Smith", key3: "foo"} """)

    }

    private fun checkValue(type: Type?, str: String) {
        val value = ION.singleValue(str)
        val violations = type!!.validate(value)
        if (!violations.isValid()) {
            println("Validation failed for $str at type $type")
            println(violations)
        } else {
            println("**** Validation succeeded for $str at type $type")
        }
    }
}