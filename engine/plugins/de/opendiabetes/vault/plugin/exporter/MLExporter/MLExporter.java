/*
 * Copyright (C) 2017 Jens Heuschkel
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
package de.opendiabetes.vault.plugin.exporter.MLExporter;

import de.opendiabetes.vault.container.BucketEventTriggers;
import de.opendiabetes.vault.container.FinalBucketEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.plugin.exporter.FileExporter;
import de.opendiabetes.vault.processing.buckets.BucketProcessor;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a CSV file with processed Bucket entries from a given VaultEntry
 * dataset.
 *
 * @author jorg
 */
public class MLExporter extends FileExporter<VaultEntry, VaultEntry> {

    private int wantedBucketSize = 1;
    private String filePath;
    private Properties config;

    /**
     * public MLExporter(int wantedBucketSize, String filePath) {
     * this.wantedBucketSize = wantedBucketSize; this.filePath = filePath; }
     */
    private String deleteComma(VaultEntry entry) {
        String result = entry.toString();
        return result.replace(",", " ");
    }

    /**
     * writes the header for the columns of the csv file
     *
     * @return
     */
    private String createHeader() {

        HashMap<VaultEntryType, Integer> oneHotHeader = BucketEventTriggers.ARRAY_ENTRIES_AFTER_MERGE_TO;
        String[] header = new String[oneHotHeader.size()];

        int i = 0;
        for (Map.Entry<VaultEntryType, Integer> entry : oneHotHeader.entrySet()) {
            VaultEntryType key = entry.getKey();
            int value = entry.getValue();
            header[value] = key.toString();
            i++;
        }

        String result = Arrays.toString(header);
        result = result.replace("[", "");
        result = result.replace("]", "");

        return result;
    }

    /**
     * rounds the Double values of the given bucket to the second decimal place
     *
     * @param bucket
     * @param i
     * @throws ParseException
     */
    private void shortenValues(FinalBucketEntry bucket, int i) throws ParseException {
        if ((bucket.getValues(i) != 0.0) && (bucket.getValues(i) != 1.0)) {
            BigDecimal bd = new BigDecimal(bucket.getValues(i));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            bucket.setValues(i, bd.doubleValue());
        }
    }

    /**
     * writes the processed Buckets to a csv file
     *
     * @param buckets
     * @throws IOException
     * @throws ParseException
     */
    @Override
    protected void writeToFile(final String filePath, final List<VaultEntry> data) throws IOException {
        //private void writeToFile(List<FinalBucketEntry> buckets) throws IOException,  {

        try {
            BucketProcessor processor = new BucketProcessor();
            List<FinalBucketEntry> buckets = processor.runProcess(0, data, wantedBucketSize);
            //int x = buckets.get(1).getFullOnehotInformationArray().length;
            FileWriter fw;
            //System.out.println("writing File to " + filePath + ".csv");
            fw = new FileWriter(filePath + ".csv");
            try {
                fw.write("index, " + createHeader() + "\n");
                //int j = 0;
                for (FinalBucketEntry bucket : buckets) {
                    for (int i = 0; i < bucket.getFullValues().length; i++) {
                        shortenValues(bucket, i);
                    }
                    String line = Arrays.toString(bucket.getFullValues());
                    line = line.replace("[", "");
                    line = line.replace("]", "");
                    fw.write(bucket.getBucketNumber() + ", " + line);
                    fw.write(System.lineSeparator());
                    //j++;
                }
            } catch (IOException ex) {
                Logger.getLogger(MLExporter.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                fw.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean loadPluginSpecificConfiguration(final Properties configuration) {

        if (configuration == null) {
            LOG.log(Level.WARNING, "No configuration given,"
                    + " assuming default values and no period restriction");
            config = new Properties();
            wantedBucketSize = 1;
        } else {
            config = configuration;
            if (configuration.containsKey("wantedbucketsize")) {
                wantedBucketSize = Integer.parseInt(configuration.getProperty("wantedbucketsize"));
            }
        }
        return true;
    }

    @Override
    protected List<VaultEntry> prepareData(List<VaultEntry> data) throws IllegalArgumentException {
        /**
         * nothing to Prepare
         */
        return data;
    }
}
