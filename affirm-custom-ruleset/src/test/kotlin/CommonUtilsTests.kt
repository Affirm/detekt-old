import com.affirm.central.ruleset.count
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CommonUtilsTests {
    @Test
    fun testSubstringMatchCount() {
        val hay = "abcabcabcd"
        assertEquals(0, hay.count(null))
        assertEquals(0, hay.count(""))
        assertEquals(3, hay.count("a"))
        assertEquals(3, hay.count("abc"))
        assertEquals(1, hay.count("abcd"))
    }
}