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

package org.projectforge.continuousdb;

import org.projectforge.framework.persistence.user.api.ThreadLocalUserContext;
import org.projectforge.framework.persistence.user.api.UserContext;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Checks whether the database is up-to-date or not.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * @deprecated Since version 6.18.0 please use flyway db migration.
 */
@Deprecated
public class SystemUpdater
{
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SystemUpdater.class);

  private SortedSet<UpdateEntry> updateEntries;

  private final ExecutorService exService = Executors.newSingleThreadExecutor();

  private boolean isUpdating = false;

  public void register(final UpdateEntry... updateEntries)
  {
    if (updateEntries == null) {
      return;
    }
    for (final UpdateEntry updateEntry : updateEntries) {
      if (updateEntry != null) {
        getUpdateEntries().add(updateEntry);
      }
    }
  }

  /**
   * Only for test case (avoids reading of the update-scripts.xml).
   *
   * @param updateEntries
   */
  void testRegister(final UpdateEntry updateEntry)
  {
    if (this.updateEntries == null) {
      this.updateEntries = new TreeSet<>();
    }
    this.updateEntries.add(updateEntry);
  }

  public void register(final Collection<UpdateEntry> updateEntries)
  {
    if (updateEntries == null) {
      return;
    }
    getUpdateEntries().addAll(updateEntries);
  }

  /**
   * Runs the pre-check test of the first update entry in the list.
   *
   * @return true if ALREADY_UPDATED, otherwise false.
   */
  public boolean isUpdated()
  {
    log.info("Checking for database updates...");
    final Set<String> checkedRegions = new HashSet<>();
    for (final UpdateEntry updateEntry : getUpdateEntries()) {
      if (!updateEntry.isInitial() && checkedRegions.contains(updateEntry.getRegionId())) {
        // Check only the newest update entry.
        continue;
      }
      checkedRegions.add(updateEntry.getRegionId());
      updateEntry.setPreCheckStatus(updateEntry.runPreCheckSafely());
      if (updateEntry.getPreCheckStatus() != UpdatePreCheckStatus.ALREADY_UPDATED) {
        log.warn(
            "*** Please note: The database perhaps has to be updated first before running the ProjectForge web app. Please login as administrator. Status '"
                + updateEntry.getPreCheckStatus()
                + "' for update entry '"
                + updateEntry.getRegionId()
                + "' "
                + updateEntry.getVersion());
        for (final String str : DATA_BASE_UPDATES_REQUIRED) {
          log.warn(str);
        }
        //for (final String str : DATA_BASE_UPDATES_REQUIRED) {
        //  System.err.println(str);
        //}
        return false;
      }
    }
    log.info("No database updates found (OK).");
    return true;
  }

  /**
   * Runs all the pre checks of all update entries.
   */
  public void runAllPreChecks()
  {
    for (final UpdateEntry updateEntry : getUpdateEntries()) {
      updateEntry.setPreCheckStatus(updateEntry.runPreCheckSafely());
    }
  }

  /**
   * @return The sorted update entries of the ProjectForge core and all plugins in descendant order (sorted by date).
   */
  public SortedSet<UpdateEntry> getUpdateEntries()
  {
    return updateEntries;
  }

  public void setUpdateEntries(final SortedSet<UpdateEntry> updateEntries)
  {
    this.updateEntries = updateEntries;
  }

  /**
   * Runs the update method of the given update entry.
   *
   * @param updateScript
   */
  public void update(final UpdateEntry updateEntry)
  {
    UserContext uc = ThreadLocalUserContext.getUserContext();
    exService.submit(() -> {
      setUpdating(true);
      try {
        ThreadLocalUserContext.setUserContext(uc);
        updateEntry.setRunningStatus(updateEntry.runUpdate());
        //getDatabaseUpdateDao().writeUpdateEntryLog(updateEntry);
        updateEntry.setPreCheckStatus(updateEntry.runPreCheckSafely());
        runAllPreChecks();
      } catch (Error e) {
        log.error("Error while updating entry: " + updateEntry.getVersion(), e);
      } finally {
        setUpdating(false);
      }
    });
  }

  public boolean isUpdating()
  {
    return isUpdating;
  }

  //write lock 
  private void setUpdating(boolean isUpdating)
  {
    this.isUpdating = isUpdating;
  }

  private static final String[] DATA_BASE_UPDATES_REQUIRED = { //
      "**********************************************************", //
      "***                                                    ***", //
      "*** It seems that there have to be done some database  ***", //
      "*** updates first. Please login as administrator!      ***", //
      "*** Otherwise login of any non-administrator user      ***", //
      "*** isn't possible!                                    ***", //
      "*** Don't forget to restart ProjectForge after update. ***", //
      "*** It's possible that tons of error messages follow.  ***", //
      "***                                                    ***", //
      "**********************************************************" };
}
