/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2019 Micromata GmbH, Germany (www.micromata.com)
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

package org.projectforge.plugins.ffp.model

import java.math.BigDecimal

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

import org.projectforge.common.anots.PropertyInfo
import org.projectforge.framework.persistence.entities.DefaultBaseDO
import org.projectforge.framework.persistence.user.entities.PFUserDO

import de.micromata.genome.db.jpa.history.api.WithHistory

@Entity
@Table(name = "T_PLUGIN_FINANCIALFAIRPLAY_DEBT", uniqueConstraints = [UniqueConstraint(columnNames = ["EVENT_ID", "ATTENDEE_USER_ID_FROM", "ATTENDEE_USER_ID_TO"])], indexes = [javax.persistence.Index(name = "idx_fk_T_PLUGIN_FINANCIALFAIRPLAY_DEBT_event_id", columnList = "EVENT_ID"), javax.persistence.Index(name = "idx_fk_T_PLUGIN_FINANCIALFAIRPLAY_DEBT_from_id", columnList = "ATTENDEE_USER_ID_FROM"), javax.persistence.Index(name = "idx_fk_T_PLUGIN_FINANCIALFAIRPLAY_DEBT_to_id", columnList = "ATTENDEE_USER_ID_TO")])
@WithHistory
class FFPDebtDO : DefaultBaseDO() {

    @get:ManyToOne(fetch = FetchType.EAGER)
    @get:JoinColumn(name = "EVENT_ID")
    var event: FFPEventDO? = null

    @get:ManyToOne(fetch = FetchType.EAGER)
    @get:JoinColumn(name = "ATTENDEE_USER_ID_FROM")
    var from: PFUserDO? = null

    @get:ManyToOne(fetch = FetchType.EAGER)
    @get:JoinColumn(name = "ATTENDEE_USER_ID_TO")
    var to: PFUserDO? = null

    @PropertyInfo(i18nKey = "plugins.ffp.value")
    @get:Column(nullable = false)
    var value: BigDecimal? = null

    @PropertyInfo(i18nKey = "plugins.ffp.approvedByFrom")
    @get:Column(nullable = false)
    var isApprovedByFrom: Boolean = false

    @PropertyInfo(i18nKey = "plugins.ffp.approvedByTo")
    @get:Column(nullable = false)
    var isApprovedByTo: Boolean = false

}