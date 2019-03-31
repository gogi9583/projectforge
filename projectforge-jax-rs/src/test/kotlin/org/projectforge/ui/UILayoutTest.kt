package org.projectforge.ui

import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.projectforge.business.book.BookDO
import org.projectforge.rest.BookRest
import org.projectforge.rest.json.JsonValidator
import org.projectforge.test.AbstractTestBase
import org.springframework.beans.factory.annotation.Autowired

class UILayoutTest : AbstractTestBase() {
    @Autowired
    lateinit var bookRest: BookRest

    @Test
    fun testEditBookActionButtons() {
        val gson = GsonBuilder().create()

        val book = BookDO()
        var jsonString = gson.toJson(bookRest.createEditLayout(book))
        var jsonValidator = JsonValidator(jsonString)
        assertEquals("cancel", jsonValidator.get("actions[0].id"))
        assertEquals("create", jsonValidator.get("actions[1].id"))
        assertEquals(2, jsonValidator.getList("actions")?.size)

        book.pk = 42
        jsonString = gson.toJson(bookRest.createEditLayout(book))
        jsonValidator = JsonValidator(jsonString)
        assertEquals("cancel", jsonValidator.get("actions[0].id"))
        assertEquals("markAsDeleted", jsonValidator.get("actions[1].id"))
        assertEquals("update", jsonValidator.get("actions[2].id"))
        assertEquals(3, jsonValidator.getList("actions")?.size)

        book.isDeleted = true
        jsonString = gson.toJson(bookRest.createEditLayout(book))
        jsonValidator = JsonValidator(jsonString)
        assertEquals("cancel", jsonValidator.get("actions[0].id"))
        assertEquals("undelete", jsonValidator.get("actions[1].id"))
        assertEquals(2, jsonValidator.getList("actions")?.size)
    }

    @Test
    fun testEditBookLayout() {
        val gson = GsonBuilder().create()
        var jsonString = gson.toJson(bookRest.createEditLayout(BookDO()))
        var jsonValidator = JsonValidator(jsonString)

        assertEquals("???book.title.add???", jsonValidator.get("title")) // translations not available in test.
        assertField(jsonValidator.getMap("layout[0]"), "title", 255.0, "STRING", "???book.title???", type = "INPUT", key = "el-1")
        assertEquals(true, jsonValidator.getBoolean("layout[0].focus"))
        assertField(jsonValidator.getMap("layout[1]"), "authors", 1000.0, null, "???book.authors???", type = "TEXTAREA", key = "el-2")
        assertNull(jsonValidator.getBoolean("layout[1].focus"))

        assertEquals("ROW", jsonValidator.get("layout[2].type"))
        assertEquals("el-3", jsonValidator.get("layout[2].key"))

        assertEquals(6.0, jsonValidator.getDouble("layout[2].content[0].length"))
        assertEquals("COL", jsonValidator.get("layout[2].content[0].type"))
        assertEquals("el-4", jsonValidator.get("layout[2].content[0].key"))

        assertEquals("type", jsonValidator.get("layout[2].content[0].content[0].id"))
        assertEquals("???book.type???", jsonValidator.get("layout[2].content[0].content[0].label"))
        assertEquals("SELECT", jsonValidator.get("layout[2].content[0].content[0].type"))
        assertEquals("el-5", jsonValidator.get("layout[2].content[0].content[0].key"))

        assertEquals(8, jsonValidator.getList("layout[2].content[0].content[0].values")?.size)
        assertEquals("BOOK", jsonValidator.get("layout[2].content[0].content[0].values[0].value"))
        assertEquals("???book.type.book???", jsonValidator.get("layout[2].content[0].content[0].values[0].title"))

        assertEquals("GROUP", jsonValidator.get("layout[3].type"))
        assertEquals("lendOutComponent", jsonValidator.get("layout[3].content[1].id"))
        assertEquals("CUSTOMIZED", jsonValidator.get("layout[3].content[1].type"))
        assertEquals("el-15", jsonValidator.get("layout[3].content[0].key"))
        assertEquals("???book.lending???", jsonValidator.get("layout[3].content[0].label"))

    }

    @Test
    fun testBookListLayout() {
        val gson = GsonBuilder().create()
        var jsonString = gson.toJson(bookRest.createListLayout())
        var jsonValidator = JsonValidator(jsonString)

        assertEquals("resultSet", jsonValidator.get("layout[0].id"))
        assertEquals("TABLE", jsonValidator.get("layout[0].type"))
        assertEquals("el-1", jsonValidator.get("layout[0].key"))

        assertEquals(7, jsonValidator.getList("layout[0].columns")?.size)

        assertEquals("created", jsonValidator.get("layout[0].columns[0].id"))
        assertEquals("???created???", jsonValidator.get("layout[0].columns[0].title"))
        assertEquals("DATE", jsonValidator.get("layout[0].columns[0].dataType"))
        assertEquals(true, jsonValidator.getBoolean("layout[0].columns[0].sortable"))
        assertEquals("TIMESTAMP_MINUTES", jsonValidator.get("layout[0].columns[0].formatter"))
        assertEquals("TABLE_COLUMN", jsonValidator.get("layout[0].columns[0].type"))
        assertEquals("el-2", jsonValidator.get("layout[0].columns[0].key"))

        assertEquals("yearOfPublishing", jsonValidator.get("layout[0].columns[1].id"))
        assertEquals("???book.yearOfPublishing???", jsonValidator.get("layout[0].columns[1].title"))
        assertEquals("STRING", jsonValidator.get("layout[0].columns[1].dataType"))
        assertEquals(true, jsonValidator.getBoolean("layout[0].columns[1].sortable"))
        assertNull( jsonValidator.get("layout[0].columns[1].formatter"))
        assertEquals("TABLE_COLUMN", jsonValidator.get("layout[0].columns[1].type"))
        assertEquals("el-3", jsonValidator.get("layout[0].columns[1].key"))

        assertEquals(1, jsonValidator.getList("namedContainers")?.size)
        assertEquals("filterOptions", jsonValidator.get("namedContainers[0].id"))
        assertEquals("NAMED_CONTAINER", jsonValidator.get("namedContainers[0].type"))
        assertEquals("nc-1", jsonValidator.get("namedContainers[0].key"))

        assertEquals(1, jsonValidator.getList("namedContainers[0].content")?.size)

        assertEquals(5, jsonValidator.getList("namedContainers[0].content[0].content")?.size)
        assertEquals("present", jsonValidator.get("namedContainers[0].content[0].content[0].id"))
        assertEquals("???book.status.present???", jsonValidator.get("namedContainers[0].content[0].content[0].label"))
        assertEquals("CHECKBOX", jsonValidator.get("namedContainers[0].content[0].content[0].type"))
        assertEquals("el-10", jsonValidator.get("namedContainers[0].content[0].content[0].key"))

        assertEquals("onlyDeleted", jsonValidator.get("namedContainers[0].content[0].content[3].id"))
        assertEquals("???onlyDeleted.tooltip???", jsonValidator.get("namedContainers[0].content[0].content[3].tooltip"))

        assertEquals(2, jsonValidator.getList("actions")?.size)
        assertEquals("reset", jsonValidator.get("actions[0].id"))
        assertEquals("???reset???", jsonValidator.get("actions[0].title"))
        assertEquals("danger", jsonValidator.get("actions[0].style"))
        assertEquals("BUTTON", jsonValidator.get("actions[0].type"))
        assertEquals("el-15", jsonValidator.get("actions[0].key"))

        assertEquals("primary", jsonValidator.get("actions[1].style"))
    }

    private fun assertField(element: Map<String, *>?, id: String, maxLength: Double, dataType: String?, label: String, type: String, key: String) {
        assertNotNull(element)
        if (element == null) return // Only for compiler: shouldn't occur due to previous assertNotNull statement.
        assertEquals(id, element.get("id"))
        assertEquals(maxLength, element.get("maxLength"))
        if (dataType != null)
            assertEquals(dataType, element.get("dataType"))
        else
            assertNull(dataType)
        assertEquals(label, element.get("label"))
        assertEquals(type, element.get("type"))
        assertEquals(key, element.get("key"))
    }
}