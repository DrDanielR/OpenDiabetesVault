/*
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.exporter.slicelayoutcsv;

import de.opendiabetes.vault.container.SliceEntry;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.container.csv.SliceCSVEntry;
import de.opendiabetes.vault.plugin.exporter.CSVFileExporter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Wrapper class for the SliceLayoutCSVExporter plugin.
 *
 * @author Lucas Buschlinger
 */
public class SliceLayoutCSVExporter extends Plugin {

    /**
     * Constructor used by the {@link org.pf4j.PluginManager} to instantiate.
     *
     * @param wrapper The {@link PluginWrapper}.
     */
    public SliceLayoutCSVExporter(final PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Actual implementation of the SliceLayoutCSVExporter.
     */
    @Extension
    public static final class SliceLayoutCSVExporterImplementation extends CSVFileExporter<ExportEntry, SliceEntry> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected List<ExportEntry> prepareData(final List<SliceEntry> data) throws IllegalArgumentException {
            if (data == null || data.isEmpty()) {
                LOG.log(Level.SEVERE, "Data cannot be empty");
                throw new IllegalArgumentException("Data cannot be empty");
            }
            List<ExportEntry> retVal = new ArrayList<>();
            for (SliceEntry item : data) {
                retVal.add(new SliceCSVEntry(item));
            }
            return retVal;
        }

    }
}
