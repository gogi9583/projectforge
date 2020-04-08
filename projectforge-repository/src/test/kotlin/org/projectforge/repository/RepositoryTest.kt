/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2020 Micromata GmbH, Germany (www.micromata.com)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.repository

import mu.KotlinLogging
import org.apache.jackrabbit.commons.JcrUtils
import org.junit.jupiter.api.*
import java.io.File

private val log = KotlinLogging.logger {}

class RepositoryTest {

    companion object {
        private lateinit var repoService: RepositoryService
        private val repoDir = createTempDir()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            repoService = RepositoryService()
            repoService.init(mapOf(JcrUtils.REPOSITORY_URI to repoDir.toURI().toString()))
            // repoDir.deleteOnExit() // Doesn't work reliable.
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            log.info { "Deleting JackRabbit test repo: $repoDir." }
            Assertions.assertTrue(repoDir.deleteRecursively(), "Couldn't delte JackRabbit test repo: $repoDir.")
        }
    }

    @Test
    fun test() {
        try {
            repoService.ensureNode("world/europe", "germany")
            fail("Exception expected, because node 'world/europe' doesn't exist.")
        } catch (ex: Exception) {
            // OK, hello/world doesn't exist.
        }
        Assertions.assertEquals("/world/europe", repoService.ensureNode(null, "world/europe"))
        repoService.storeProperty("world/europe", "germany", "key", "value")
        Assertions.assertEquals("value", repoService.retrievePropertyString("world/europe/", "germany", "key"))

        val file = FileObject()
        file.fileName = "pom.xml"
        file.parentNodePath = "/world/europe"
        file.relPath = "germany"
        file.content = File(file.fileName).readBytes()
        repoService.storeFile(file)

        checkFile(file, null, file.fileName)
        checkFile(file, file.id, null)
        checkFile(file, file.id, "unkown")
        checkFile(file, "unkown", file.fileName)

        val unknownFile = FileObject()
        unknownFile.id = "unknown id"
        unknownFile.fileName = "unknown filename"
        unknownFile.parentNodePath = file.parentNodePath
        unknownFile.relPath = file.relPath
        Assertions.assertFalse(repoService.retrieveFile(unknownFile))
        unknownFile.id = file.id
        unknownFile.relPath = "unknown"
        Assertions.assertFalse(repoService.retrieveFile(unknownFile))
        unknownFile.parentNodePath = "unknown"
        try {
            Assertions.assertFalse(repoService.retrieveFile(unknownFile))
            fail("Exception expected, because parent path not found.")
        } catch (ex: Exception) {
            // OK
        }

        /* val path = repoService.ensureNode("world/europe", "germany/id")
         println(path)
         repoService.store(path)
         repoService.retrieve("world/europe/germany/id")*/
    }

    private fun checkFile(expected: FileObject, id: String?, fileName: String?) {
        val file = FileObject()
        file.id = id
        file.fileName = fileName
        file.parentNodePath = expected.parentNodePath
        file.relPath = expected.relPath
        Assertions.assertTrue(repoService.retrieveFile(file))
        Assertions.assertEquals(expected.size, file.size)
        Assertions.assertEquals(expected.id, file.id)
        Assertions.assertEquals(expected.fileName, file.fileName)
        Assertions.assertEquals(expected.content!!.size, file.content!!.size)
        for (idx in expected.content!!.indices) {
            Assertions.assertEquals(expected.content!![idx], file.content!![idx])
        }
    }

}