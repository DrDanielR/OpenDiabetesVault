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
package de.opendiabetes.vault.plugin.importer.libretext;

import com.csvreader.CsvReader;
import de.opendiabetes.vault.plugin.importer.fileimporter.validator.CSVValidator;
import de.opendiabetes.vault.plugin.util.TimestampUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a validator for LibreText based CSV data.
 *
 * @author Lucas Buschlinger
 * @author Magnus Gärtner
 */
public class LibreTextCSVValidator extends CSVValidator {

    /**
     * The header field of German LibreText CSV data which contains the scanned glucose values.
     */
    private static final String LIBRE_HEADER_DE_SCAN     = "Gescannte Glukose (mg/dL)";
    /**
     * The header field of German LibreText CSV data which contains the historic glucose values.
     */
    private static final String LIBRE_HEADER_DE_HISTORIC =  "Historische Glukose (mg/dL)";
    /**
     * The header field of German LibreText CSV data which contains the blood glucose values.
     */
    private static final String LIBRE_HEADER_DE_BLOOD    = "Teststreifen-Blutzucker (mg/dL)";
    /**
     * The header field of German LibreText CSV data which contains the timestamp.
     */
    private static final String LIBRE_HEADER_DE_TIME     = "Uhrzeit";
    /**
     * The header field of German LibreText CSV data which contains the type of the entry.
     */
    private static final String LIBRE_HEADER_DE_TYPE     = "Art des Eintrags";
    /**
     * The header field of German LibreTextCSV data which contains the updated time after a time synchronization.
     */
    private static final String LIBRE_HEADER_DE_TIMESYNC = "Neue Uhrzeit";
    /**
     * The header field of German LibreTextCSV data which contains the bolus normal.
     */
    private static final String LIBRE_HEADER_DE_BOLUS_NORMAL = "Schnell wirkendes Insulin (Einheiten)";
    /**
     * The header field of German LibreTextCSV data which contains the amount of consumed carbohydrates.
     */
    private static final String LIBRE_HEADER_DE_CARBS = "Kohlenhydrate (Gramm)";
    /**
     * The time format used in German LibreText CSV data.
     */
    private static final String TIME_FORMAT_LIBRE_DE    = "yyyy.MM.dd HH:mm";

    /**
     * The composed German header used in LibreText CSV data.
     */
    private static final String[] LIBRE_HEADER_DE = {
            LIBRE_HEADER_DE_SCAN,
            LIBRE_HEADER_DE_HISTORIC,
            LIBRE_HEADER_DE_BLOOD,
            LIBRE_HEADER_DE_TIME,
            LIBRE_HEADER_DE_TYPE
    };

    /**
     * The header field of English LibreText CSV data which contains the scanned glucose values.
     */
    private static final String LIBRE_HEADER_EN_SCAN     = "Scan Glucose (mg/dL)";
    /**
     * The header field of English LibreText CSV data which contains the historic glucose values.
     */
    private static final String LIBRE_HEADER_EN_HISTORIC =  "Historic Glucose (mg/dL)";
    /**
     * The header field of English LibreText CSV data which contains the blood glucose values.
     */
    private static final String LIBRE_HEADER_EN_BLOOD    = "Strip Glucose (mg/dL)";
    /**
     * The header field of English LibreText CSV data which contains the timestamp.
     */
    private static final String LIBRE_HEADER_EN_TIME     = "Time";
    /**
     * The header field of English LibreTextCSV data which contains the type of the entry.
     */
    private static final String LIBRE_HEADER_EN_TYPE     = "Record Type";
    /**
     * The header field of English LibreTextCSV data which contains the updated time after a time synchronization.
     */
    private static final String LIBRE_HEADER_EN_TIMESYNC = "Updated Time";
    /**
     * The header field of English LibreTextCSV data which contains the bolus normal.
     */
    private static final String LIBRE_HEADER_EN_BOLUS_NORMAL = "Rapid-Acting Insulin (units)";
    /**
     * The header field of English LibreTextCSV data which contains the amount of consumed carbohydrates.
     */
    private static final String LIBRE_HEADER_EN_CARBS = "Carbohydrates (grams)";
    /**
     * The time format used in English LibreText CSV data.
     */
    private static final String TIME_FORMAT_LIBRE_EN    = "yyyy/MM/dd HH:mm";

    /**
     * The composed English header used in LibreText CSV data.
     */
    private static final String[] LIBRE_HEADER_EN = {
            LIBRE_HEADER_EN_TIME,
            LIBRE_HEADER_EN_TYPE,
            LIBRE_HEADER_EN_HISTORIC,
            LIBRE_HEADER_EN_SCAN,
            LIBRE_HEADER_EN_BLOOD
    };


    /**
     * Constructor for a LibreTextCSV validator.
     */
    public LibreTextCSVValidator() {
        super(LIBRE_HEADER_DE, LIBRE_HEADER_EN);
    }

    /**
     * Gets the type of the entry currently getting parsed by reading the appropriate field in the LibreText CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The type of the entry, it is either of {@link TYPE}.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public TYPE getType(final CsvReader reader) throws IOException, AssertionError {
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                return TYPE.fromInt(Integer.parseInt(reader.get(LIBRE_HEADER_DE_TYPE)));
            case EN:
                return TYPE.fromInt(Integer.parseInt(reader.get(LIBRE_HEADER_EN_TYPE)));
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the timestamp of the entry currently getting parsed by
     * reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The timestamp of the entry.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws ParseException Thrown if the String containing the data can not be parsed
     * in {@link TimestampUtils#createCleanTimestamp(String, String)}.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public Date getTimestamp(final CsvReader reader) throws  IOException, ParseException, AssertionError {
        String timeString;
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                timeString = reader.get(LIBRE_HEADER_DE_TIME);
                return TimestampUtils.createCleanTimestamp(timeString, TIME_FORMAT_LIBRE_DE);
            case EN:
                timeString = reader.get(LIBRE_HEADER_EN_TIME);
                return TimestampUtils.createCleanTimestamp(timeString, TIME_FORMAT_LIBRE_EN);
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the value of the historic glucose of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the historic glucose.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     *
     */
    public double getHistoricGlucose(final CsvReader reader) throws IOException, AssertionError {
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                return Double.parseDouble(reader.get(LIBRE_HEADER_DE_HISTORIC));
            case EN:
                return Double.parseDouble(reader.get(LIBRE_HEADER_EN_HISTORIC));
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the value of the scanned glucose of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the scanned glucose.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public double getScanGlucose(final CsvReader reader) throws IOException, AssertionError {
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                return Double.parseDouble(reader.get(LIBRE_HEADER_DE_SCAN));
            case EN:
                return Double.parseDouble(reader.get(LIBRE_HEADER_EN_SCAN));
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the value of the blood glucose of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the blood glucose.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public double getBloodGlucose(final CsvReader reader) throws IOException, AssertionError {
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                return Double.parseDouble(reader.get(LIBRE_HEADER_DE_BLOOD));
            case EN:
                return Double.parseDouble(reader.get(LIBRE_HEADER_EN_BLOOD));
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the value of the consumed carbohydrates of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the consumed carbohydrates.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public double getCarbohydrates(final CsvReader reader) throws IOException, AssertionError {
        Language language = getLanguageSelection();
        final double factor = 12.0;
        switch (language) {
            case DE:
                return Double.parseDouble(reader.get(LIBRE_HEADER_DE_CARBS)) / factor;
            case EN:
                return Double.parseDouble(reader.get(LIBRE_HEADER_EN_CARBS)) / factor;
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the value of the bolus normal of the entry currently getting parsed
     * by reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The value of the bolus normal.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public double getBolusNormal(final CsvReader reader) throws IOException, AssertionError {
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                return Double.parseDouble(reader.get(LIBRE_HEADER_DE_BOLUS_NORMAL));
            case EN:
                return Double.parseDouble(reader.get(LIBRE_HEADER_EN_BOLUS_NORMAL));
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * Gets the timestamp time synchronization of the entry currently getting parsed by
     * reading the appropriate field in the Libre Text CSV data.
     *
     * @param reader The {@link CsvReader} used to read the file.
     * @return The new time of the entry after the synchronization.
     * @throws IOException Thrown if the CSV file can not be read successfully.
     * @throws ParseException Thrown if the String containing the data can not be parsed
     * in {@link TimestampUtils#createCleanTimestamp(String, String)}.
     * @throws AssertionError Thrown if the language received by {@link CSVValidator#getLanguageSelection()}
     * is neither German nor English.
     */
    public Date getTimeSync(final CsvReader reader) throws  IOException, ParseException, AssertionError {
        String timeString;
        Language language = getLanguageSelection();
        switch (language) {
            case DE:
                timeString = reader.get(LIBRE_HEADER_DE_TIMESYNC);
                return TimestampUtils.createCleanTimestamp(timeString, TIME_FORMAT_LIBRE_DE);
            case EN:
                timeString = reader.get(LIBRE_HEADER_EN_TIMESYNC);
                return TimestampUtils.createCleanTimestamp(timeString, TIME_FORMAT_LIBRE_EN);
            default:
                Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                throw new AssertionError();
        }
    }

    /**
     * This enum lists the different types of data present in LibreText CSv data.
     */
    public enum TYPE {
        /**
         * Indicates that the value is that of scanned glucose.
         */
        SCAN_GLUCOSE(1),
        /**
         * Indicates that the value is that of historic glucose.
         */
        HISTORIC_GLUCOSE(0),
        /**
         * Indicates that the value is that of blood glucose.
         */
        BLOOD_GLUCOSE(2),
        /**
         * Indicates that the value is that of carbohydrate intake.
         */
        CARBOHYDRATES(5),
        /**
         * Indicates that the value is the timestamp.
         */
        TIME_CHANGED(6);
//        /**
//        * Indicates that the value is that of a bolus normal.
//        */
//        BOLUS_NORMAL; TODO with file that contains data about this

        /**
         * The numeric representation of the types of data.
         */
        private final int index;

        /**
         * Constructor for types used in LibreText CSV data.
         * @param index The numeric representation of the type.
         */
        TYPE(final int index) {
            this.index = index;
        }

        /**
         * Gets the type which is bound to the numeric representation specified in the argument.
         *
         * @param type The numeric representation of the type.
         * @return The type bound to the index.
         */
        static TYPE fromInt(final int type) {
            final int timeSync = 6;
            final int carbs = 5;
            switch (type) {
                case 1:
                    return SCAN_GLUCOSE;
                case 0:
                    return HISTORIC_GLUCOSE;
                case 2:
                    return BLOOD_GLUCOSE;
                case carbs:
                    return CARBOHYDRATES;
                case timeSync:
                    return TIME_CHANGED;
                default:
                    LOG.log(Level.SEVERE, "Error while type checking!");
                    throw new IllegalArgumentException("No such type for LibreText data.");
            }
        }
    }
}
