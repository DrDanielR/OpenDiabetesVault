/*
 * Copyright (C) 2018 tiweGH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.processing.filter.options;

import de.opendiabetes.vault.processing.filter.Filter;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author tiweGH
 */
public class TimeSpanFilterOption extends FilterOption {

    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeSpanFilterOption(LocalTime startTime, LocalTime endTime) {
        super(new LinkedHashMap<>(), null);
        super.getParameterNameAndType().put("StartTime", LocalTime.class);
        super.getParameterNameAndType().put("EndTime", LocalTime.class);

        this.endTime = endTime;
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

}
