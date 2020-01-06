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

package org.projectforge.framework.xstream.converter;

import org.joda.time.DateMidnight;
import org.junit.jupiter.api.Test;
import org.projectforge.framework.persistence.user.api.ThreadLocalUserContext;
import org.projectforge.framework.persistence.user.entities.PFUserDO;
import org.projectforge.framework.time.DateHelper;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JodaDateMidnightConverterTest
{
  @Test
  public void testConverter()
  {
    test(DateHelper.EUROPE_BERLIN);
    test(DateHelper.UTC);
  }

  private void test(final TimeZone timeZone)
  {
    final PFUserDO user = new PFUserDO();
    user.setTimeZone(timeZone);
    ThreadLocalUserContext.setUser(null, user);
    final JodaDateMidnightConverter converter = new JodaDateMidnightConverter();
    final DateMidnight dateMidnight = (DateMidnight) converter.parse("1970-11-21");
    assertEquals(1970, dateMidnight.getYear());
    assertEquals(11, dateMidnight.getMonthOfYear());
    assertEquals(21, dateMidnight.getDayOfMonth());
    assertEquals("1970-11-21", converter.toString(dateMidnight));
  }
}