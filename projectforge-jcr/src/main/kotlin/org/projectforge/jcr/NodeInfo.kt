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

package org.projectforge.jcr

import javax.jcr.Node

/**
 * For information.
 */
class NodeInfo() {
    internal constructor(node: Node, recursive: Boolean = true): this() {
        name = node.name
        if (recursive) {
            node.nodes?.let {
                val nodes = mutableListOf<NodeInfo>()
                while (it.hasNext()) {
                    nodes.add(NodeInfo(it.nextNode()))
                }
                children = nodes
            }
        }
        if (node.properties?.hasNext() == true) {
            val props = mutableListOf<PropertyInfo>()
            properties = props
            node.properties.let {
                while (it.hasNext()) {
                    props.add(PropertyInfo(it.nextProperty()))
                }
            }
        }
    }

    var name: String? = null
    var children: List<NodeInfo>? = null
    var properties: List<PropertyInfo>? = null

    override fun toString(): String {
        return PFJcrUtils.toJson(this)
    }
}
