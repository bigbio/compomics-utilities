package com.compomics.util.experiment.mass_spectrometry;

import com.compomics.util.experiment.mass_spectrometry.spectra.Precursor;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import com.compomics.util.experiment.mass_spectrometry.spectra.Peak;
import com.compomics.util.experiment.io.mass_spectrometry.mgf.MgfIndex;
import com.compomics.util.experiment.io.mass_spectrometry.mgf.MgfReader;
import com.compomics.util.experiment.io.mass_spectrometry.msp.MspReader;
import com.compomics.util.waiting.WaitingHandler;
import com.compomics.util.io.file.SerializationUtils;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.PrecursorList;
import uk.ac.ebi.jmzml.model.mzml.ScanList;
import uk.ac.ebi.jmzml.model.mzml.SelectedIonList;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * This factory will provide the spectra when needed.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class SpectrumFactory {

    /**
     * The instance of the factory.
     */
    private static SpectrumFactory instance = null;
    /**
     * Map of already loaded spectra.
     */
    private final HashMap<String, HashMap<String, Spectrum>> currentSpectrumMap = new HashMap<>();
    /**
     * Map of already loaded precursors.
     */
    private final HashMap<String, HashMap<String, Precursor>> loadedPrecursorsMap = new HashMap<>();
    /**
     * Maximal number of spectra in cache. By default 1000000, which corresponds
     * to approx. 110MB.
     */
    private static int nSpectraCache = 1000000;
    /**
     * List of the loaded spectra.
     */
    private final LinkedBlockingDeque<String> loadedSpectra = new LinkedBlockingDeque<>();
    /**
     * Map to the different files.
     */
    private final HashMap<String, File> filesMap = new HashMap<>();
    /**
     * Map of the random access files of the loaded mgf files (filename &gt;
     * random access file).
     */
    private final HashMap<String, BufferedRandomAccessFile> mgfRandomAccessFilesMap = new HashMap<>();
    /**
     * Map of the mgf indexes (fileName &gt; mgf index).
     */
    private final HashMap<String, MgfIndex> mgfIndexesMap = new HashMap<>();
    /**
     * Map of the mzML unmarshallers (fileName &gt; unmarshaller).
     */
    private final HashMap<String, MzMLUnmarshaller> mzMLUnmarshallers = new HashMap<>();
    /**
     * Map of the spectrum file mapped according to the name used by the search
     * engine.
     */
    private final HashMap<String, File> idToSpectrumName = new HashMap<>();
    /**
     * The time out in milliseconds when querying the file.
     */
    public final static long timeOut = 10000;

    /**
     * Constructor.
     */
    private SpectrumFactory() {
    }

    /**
     * Static method returning the instance of the factory.
     *
     * @return the instance of the factory
     */
    public static SpectrumFactory getInstance() {
        if (instance == null) {
            instance = new SpectrumFactory();
        }
        return instance;
    }

    /**
     * Static method returning the instance of the factory with a new cache
     * size.
     *
     * @param nCache the cache size
     * @return the instance of the factory with a new cache size
     */
    public static SpectrumFactory getInstance(int nCache) {
        if (instance == null) {
            instance = new SpectrumFactory();
        }
        instance.setCacheSize(nCache);
        return instance;
    }

    /**
     * Clears the factory getInstance() needs to be called afterwards.
     */
    public void clearFactory() {
        currentSpectrumMap.clear();
        loadedPrecursorsMap.clear();
        loadedSpectra.clear();
        filesMap.clear();
        mgfRandomAccessFilesMap.clear();
        mgfIndexesMap.clear();
        mzMLUnmarshallers.clear();
        idToSpectrumName.clear();
    }

    /**
     * Empties the cache.
     */
    public void emptyCache() {
        currentSpectrumMap.clear();
        loadedPrecursorsMap.clear();
        loadedSpectra.clear();
    }

    /**
     * Sets the spectrum cache size.
     *
     * @param nCache the new cache size
     */
    public void setCacheSize(int nCache) {
        SpectrumFactory.nSpectraCache = nCache;
    }

    /**
     * Returns the spectrum cache size.
     *
     * @return the cache size
     */
    public int getCacheSize() {
        return nSpectraCache;
    }

    /**
     * Add spectra to the factory.
     *
     * @param spectrumFile The spectrum file, can be mgf or mzML
     *
     * @throws IOException Exception thrown whenever an error occurred while
     * reading the file
     *
     */
    public void addSpectra(File spectrumFile) throws IOException {
        addSpectra(spectrumFile, null);
    }

    /**
     * Add spectra to the factory.
     *
     * @param spectrumFile the spectrum file, can be mgf or mzML
     * @param waitingHandler the waiting handler
     *
     * @throws IOException Exception thrown whenever an error occurred while
     * reading the file
     */
    public void addSpectra(File spectrumFile, WaitingHandler waitingHandler) throws IOException {

        String fileName = spectrumFile.getName();
        filesMap.put(fileName, spectrumFile);

        if (fileName.toLowerCase().endsWith(".mgf") || fileName.toLowerCase().endsWith(".msp")) {

            File indexFile = new File(spectrumFile.getParent(), getIndexName(fileName));
            MgfIndex mgfIndex = null;

            if (indexFile.exists()) {
                try {
                    MgfIndex tempIndex = getIndex(indexFile);
                    Long indexLastModified = tempIndex.getLastModified();

                    if (indexLastModified != null) {
                        long fileLastModified = spectrumFile.lastModified();

                        if (indexLastModified == fileLastModified) {
                            mgfIndex = tempIndex;
                        } else {
                            System.err.println("Reindexing: " + fileName + ". (changes in the file detected)");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Reindexing: " + fileName + ". (Reason: " + e.getLocalizedMessage() + ")");
                }
            }

            if (mgfIndex == null) {
                if (fileName.endsWith(".mgf")) {
                    mgfIndex = MgfReader.getIndexMap(spectrumFile, waitingHandler);
                } else {
                    mgfIndex = MspReader.getIndexMap(spectrumFile, waitingHandler);
                }

                if (waitingHandler != null && waitingHandler.isRunCanceled()) {
                    return; // return without saving the partial index
                }

                writeIndex(mgfIndex, spectrumFile.getParentFile());
            }

            if (mgfIndex == null) {
                throw new IllegalArgumentException("An error occurred while indexing " + spectrumFile.getAbsolutePath());
            }

            mgfRandomAccessFilesMap.put(fileName, new BufferedRandomAccessFile(spectrumFile, "r", 1024 * 100));
            mgfIndexesMap.put(fileName, mgfIndex);

        } else if (fileName.toLowerCase().endsWith(".mzml")) {
            MzMLUnmarshaller mzMLUnmarshaller = new MzMLUnmarshaller(spectrumFile);
            mzMLUnmarshallers.put(fileName, mzMLUnmarshaller);
        } else {
            throw new IllegalArgumentException("Spectrum file format not supported.");
        }
    }

    /**
     * Returns the name of an index file based on the given spectrum file name.
     *
     * @param spectrumFileName the name of the spectrum file
     *
     * @return the corresponding name of the index file
     */
    public static String getIndexName(String spectrumFileName) {
        return spectrumFileName + ".cui";
    }

    /**
     * Returns the precursor of the desired spectrum. The value will be saved in
     * cache.
     *
     * @param fileName the name of the spectrum file
     * @param spectrumTitle the title of the spectrum
     *
     * @return the corresponding precursor
     */
    public Precursor getPrecursor(String fileName, String spectrumTitle) {
        return getPrecursor(fileName, spectrumTitle, true);
    }

    /**
     * Returns the precursor of the desired spectrum.
     *
     * @param fileName the name of the spectrum file
     * @param spectrumTitle the title of the spectrum
     * @param save if true the precursor will be saved in cache
     *
     * @return the corresponding precursor
     */
    public Precursor getPrecursor(String fileName, String spectrumTitle, boolean save) {
        
        HashMap<String, Spectrum> fileSpectrumMap = currentSpectrumMap.get(fileName);
        
        if (fileSpectrumMap != null) {
            
            Spectrum spectrum = fileSpectrumMap.get(spectrumTitle);
            
            if (spectrum != null) {
                
                return spectrum.getPrecursor();
                
            }
        }
        
        HashMap<String, Precursor> filePrecursorMap = loadedPrecursorsMap.get(fileName);
        
        if (filePrecursorMap != null) {
            
            Precursor currentPrecursor = filePrecursorMap.get(spectrumTitle);
            
            if (currentPrecursor != null) {
                
                return currentPrecursor;
                
            }
        }
        
        try {
        
        return getPrecursor(fileName, spectrumTitle, save, 1);
        
        } catch (Exception e) {
            
            throw new RuntimeException(e);
            
        }
    }

    /**
     * Returns the precursor of the desired spectrum. The value will be saved in
     * cache.
     *
     * @param spectrumKey the key of the spectrum
     * 
     * @return the corresponding precursor
     */
    public Precursor getPrecursor(String spectrumKey) {
        return getPrecursor(spectrumKey, true);
    }

    /**
     * Returns the precursor mz of the desired spectrum. The value will be saved
     * in cache.
     *
     * @param spectrumKey the key of the spectrum
     * 
     * @return the corresponding precursor mz
     */
    public double getPrecursorMz(String spectrumKey) {

        // get the spectrum title and file name
        String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
        String spectrumFileName = Spectrum.getSpectrumFile(spectrumKey);

        // a special fix for mgf files with strange titles...
        spectrumTitle = fixMgfTitle(spectrumTitle, spectrumFileName);

        // see if the have the precursor mz in the mgf index
        int spectrumIndex = mgfIndexesMap.get(spectrumFileName).getSpectrumIndex(spectrumTitle);
        Double precursorMz = mgfIndexesMap.get(spectrumFileName).getPrecursorMz(spectrumIndex);

        if (precursorMz != null) {
            return precursorMz;
        } else {
            Precursor precursor = getPrecursor(spectrumKey);
            precursorMz = precursor.getMz();
            return precursorMz;
        }
    }

    /**
     * Returns the maximum m/z for the desired file.
     *
     * @param fileName the file of interest
     * 
     * @return the max m/z
     */
    public Double getMaxMz(String fileName) {
        return mgfIndexesMap.get(fileName).getMaxMz();
    }

    /**
     * Returns the maximum m/z for the whole project.
     *
     * @return the max m/z
     */
    public Double getMaxMz() {

        double maxMz = 0;

        for (MgfIndex mgfIndex : mgfIndexesMap.values()) {
            if (maxMz < mgfIndex.getMaxMz()) {
                maxMz = mgfIndex.getMaxMz();
            }
        }

        return maxMz;
    }

    /**
     * Returns the max precursor charge encountered for the given mgf file.
     *
     * @param fileName the name of the mgf file
     * 
     * @return the max precursor charge encountered
     */
    public Integer getMaxCharge(String fileName) {
        return mgfIndexesMap.get(fileName).getMaxCharge();
    }

    /**
     * Returns the max precursor charge encountered among all loaded mgf files.
     *
     * @return the max precursor charge encountered among all loaded mgf files
     */
    public Integer getMaxCharge() {
        int maxCharge = 0;
        for (MgfIndex mgfIndex : mgfIndexesMap.values()) {
            if (mgfIndex.getMaxCharge() > maxCharge) {
                maxCharge = mgfIndex.getMaxCharge();
            }
        }
        return maxCharge;
    }

    /**
     * Returns the max peak count encountered for the given mgf file.
     *
     * @param fileName the name of the mgf file
     * @return the max peak count encountered
     */
    public Integer getMaxPeakCount(String fileName) {
        return mgfIndexesMap.get(fileName).getMaxPeakCount();
    }

    /**
     * Returns the max peak count encountered among all loaded mgf files.
     *
     * @return the max peak count encountered among all loaded mgf files
     */
    public Integer getMaxPeakCount() {
        int maxPeakCount = 0;
        for (MgfIndex mgfIndex : mgfIndexesMap.values()) {
            if (mgfIndex.getMaxPeakCount() > maxPeakCount) {
                maxPeakCount = mgfIndex.getMaxPeakCount();
            }
        }
        return maxPeakCount;
    }

    /**
     * Returns the maximum precursor intensity for the desired file.
     *
     * @param fileName the file of interest
     * @return the max precursor intensity
     */
    public Double getMaxIntensity(String fileName) {
        return mgfIndexesMap.get(fileName).getMaxIntensity();
    }

    /**
     * Returns the maximum precursor intensity for the whole project.
     *
     * @return the max precursor intensity
     */
    public Double getMaxIntensity() {

        double maxIntensity = 0;

        for (MgfIndex mgfIndex : mgfIndexesMap.values()) {
            if (maxIntensity < mgfIndex.getMaxIntensity()) {
                maxIntensity = mgfIndex.getMaxIntensity();
            }
        }

        return maxIntensity;
    }

    /**
     * Returns the maximum retention time in seconds for the desired file.
     *
     * @param fileName the file of interest
     * @return the max RT
     */
    public Double getMaxRT(String fileName) {
        return mgfIndexesMap.get(fileName).getMaxRT();
    }

    /**
     * Returns the minimum retention time in seconds for the desired file.
     *
     * @param fileName the file of interest
     * @return the min RT
     */
    public Double getMinRT(String fileName) {
        return mgfIndexesMap.get(fileName).getMinRT();
    }

    /**
     * Returns the maximum retention time in seconds for the whole project.
     *
     * @return the max RT
     */
    public Double getMaxRT() {

        double maxRT = 0;

        for (MgfIndex mgfIndex : mgfIndexesMap.values()) {
            if (maxRT < mgfIndex.getMaxRT()) {
                maxRT = mgfIndex.getMaxRT();
            }
        }

        return maxRT;
    }

    /**
     * Returns the minimum retention time in seconds for the whole project.
     *
     * @return the min RT
     */
    public Double getMinRT() {

        double minRT = Double.MAX_VALUE;

        for (MgfIndex mgfIndex : mgfIndexesMap.values()) {
            if (minRT > mgfIndex.getMinRT()) {
                minRT = mgfIndex.getMinRT();
            }
        }

        if (minRT == Double.MAX_VALUE) {
            minRT = 0;
        }

        return minRT;
    }

    /**
     * Returns the number of spectra in the desired file.
     *
     * @param fileName the file of interest
     * @return the number of spectra
     */
    public int getNSpectra(String fileName) {
        return mgfIndexesMap.get(fileName).getNSpectra();
    }

    /**
     * Returns the total number of spectra in all files.
     *
     * @return the total number of spectra in all files
     */
    public int getNSpectra() {

        int totalSpectrumCount = 0;

        for (String fileName : mgfIndexesMap.keySet()) {
            totalSpectrumCount += getNSpectra(fileName);
        }

        return totalSpectrumCount;
    }

    /**
     * Returns the precursor of the desired spectrum.
     *
     * @param spectrumKey the key of the spectrum
     * @param save boolean indicating whether the loaded precursor should be
     * stored in the factory. False by default
     * 
     * @return the corresponding precursor
     */
    public Precursor getPrecursor(String spectrumKey, boolean save) {
        String fileName = Spectrum.getSpectrumFile(spectrumKey);
        String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
        return getPrecursor(fileName, spectrumTitle, save);
    }

    /**
     * Returns a boolean indicating whether the spectrum file has been loaded.
     *
     * @param fileName the file name
     * @return a boolean indicating whether the spectrum file has been loaded
     */
    public boolean fileLoaded(String fileName) {
        return mgfIndexesMap.containsKey(fileName);
    }

    /**
     * Returns a boolean indicating whether the spectrum is contained in the
     * given spectrum file.
     *
     * @param fileName the name of the spectrum file
     * @param spectrumTitle the title of the spectrum
     * @return a boolean indicating whether the spectrum is contained in the
     * given spectrum file
     */
    public boolean spectrumLoaded(String fileName, String spectrumTitle) {
        // a special fix for mgf files with strange titles...
        spectrumTitle = fixMgfTitle(spectrumTitle, fileName);
        return mgfIndexesMap.containsKey(fileName) && mgfIndexesMap.get(fileName).containsSpectrum(spectrumTitle);
    }

    /**
     * A boolean indicating whether the spectrum is loaded in the factory.
     *
     * @param spectrumKey the spectrum key
     * @return a boolean indicating whether the spectrum is loaded in the
     * factory
     */
    public boolean spectrumLoaded(String spectrumKey) {
        String fileName = Spectrum.getSpectrumFile(spectrumKey);
        String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
        return spectrumLoaded(fileName, spectrumTitle);
    }

    /**
     * Returns the precursor of the desired spectrum. It can be that the IO is
     * busy (especially when working on distant servers) thus returning an
     * error. The method will then retry after waiting waitingTime milliseconds.
     * The waitingTime is doubled for the next try. The method throws an
     * exception after timeout (see timeOut attribute).
     *
     * @param spectrumKey the key of the spectrum
     * @param save boolean indicating whether the loaded precursor should be
     * stored in the factory
     * @param waitingTime the waiting time before retry
     *
     * @return the corresponding precursor
     *
     * @throws IOException exception thrown whenever the file was not parsed
     * correctly
     */
    private synchronized Precursor getPrecursor(String fileName, String spectrumTitle, boolean save, long waitingTime) throws IOException, MzMLUnmarshallerException {

        if (waitingTime <= 0) {
            throw new IllegalArgumentException("Waiting time should be a positive number.");
        }

        Precursor currentPrecursor = null;

        if (fileName.toLowerCase().endsWith(".mgf")) {

            // a special fix for mgf files with strange titles...
            spectrumTitle = fixMgfTitle(spectrumTitle, fileName);

            if (mgfIndexesMap.get(fileName) == null) {
                throw new IOException("Mgf file not found: \'" + fileName + "\'.");
            }
            if (mgfIndexesMap.get(fileName).getIndex(spectrumTitle) == null) {
                throw new IOException("Spectrum \'" + spectrumTitle + "\' in mgf file \'" + fileName + "\' not found.");
            }
            try {
                currentPrecursor = MgfReader.getPrecursor(mgfRandomAccessFilesMap.get(fileName), mgfIndexesMap.get(fileName).getIndex(spectrumTitle), fileName);
            } catch (Exception e) {
                if (waitingTime < timeOut) {
                    try {
                        wait(waitingTime);
                    } catch (InterruptedException ie) {
                    }
                    return getPrecursor(fileName, spectrumTitle, save, 2 * waitingTime);
                } else {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Error while loading precursor of spectrum " + spectrumTitle + " of file " + fileName + ".");
                }
            }
        } else if (fileName.toLowerCase().endsWith(".msp")) {

            // a special fix for mgf files with strange titles...
            spectrumTitle = fixMgfTitle(spectrumTitle, fileName);

            if (mgfIndexesMap.get(fileName) == null) {
                throw new IOException("msp file not found: \'" + fileName + "\'.");
            }
            if (mgfIndexesMap.get(fileName).getIndex(spectrumTitle) == null) {
                throw new IOException("Spectrum \'" + spectrumTitle + "\' in msp file \'" + fileName + "\' not found.");
            }
            try {

                currentPrecursor = MspReader.getPrecursor(mgfRandomAccessFilesMap.get(fileName), mgfIndexesMap.get(fileName).getIndex(spectrumTitle), fileName);
            } catch (Exception e) {
                if (waitingTime < timeOut) {
                    try {
                        wait(waitingTime);
                    } catch (InterruptedException ie) {
                    }
                    return getPrecursor(fileName, spectrumTitle, save, 2 * waitingTime);
                } else {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Error while loading precursor of spectrum " + spectrumTitle + " of file " + fileName + ".");
                }
            }
        } else if (fileName.toLowerCase().endsWith(".mzml")) {
            uk.ac.ebi.jmzml.model.mzml.Spectrum mzMLSpectrum = mzMLUnmarshallers.get(fileName).getSpectrumById(spectrumTitle);
            int level = 2;
            double mzPrec = 0.0;
            double scanTime = -1.0;
            int chargePrec = 0;
            for (CVParam cvParam : mzMLSpectrum.getCvParam()) {
                if (cvParam.getAccession().equals("MS:1000511")) {
                    level = new Integer(cvParam.getValue());
                    break;
                }
            }
            ScanList scanList = mzMLSpectrum.getScanList();
            if (scanList != null) {
                for (CVParam cvParam : scanList.getScan().get(scanList.getScan().size() - 1).getCvParam()) {
                    if (cvParam.getAccession().equals("MS:1000016")) {
                        scanTime = new Double(cvParam.getValue());
                        break;
                    }
                }
            }
            PrecursorList precursorList = mzMLSpectrum.getPrecursorList();
            if (precursorList != null) {
                if (precursorList.getCount().intValue() == 1) {
                    SelectedIonList sIonList = precursorList.getPrecursor().get(0).getSelectedIonList();
                    if (sIonList != null) {
                        for (CVParam cvParam : sIonList.getSelectedIon().get(0).getCvParam()) {
                            if (cvParam.getAccession().equals("MS:1000744")
                                    || cvParam.getAccession().equals("MS:1000040")) {
                                mzPrec = new Double(cvParam.getValue());
                            } else if (cvParam.getAccession().equals("MS:1000041")) {
                                chargePrec = new Integer(cvParam.getValue());
                            }
                        }
                    }
                }
            }
            if (level == 1) {
                throw new IllegalArgumentException("MS1 spectrum");
            } else {
                //@TODO: is this correct..?
                ArrayList<Integer> charges = new ArrayList<>();
                charges.add(chargePrec);
                currentPrecursor = new Precursor(scanTime, mzPrec, charges);
            }
        } else {
            throw new IllegalArgumentException("Spectrum file format not supported.");
        }
        if (save) {
            HashMap<String, Precursor> fileMap = loadedPrecursorsMap.get(fileName);
            if (fileMap == null) {
                fileMap = new HashMap<>();
                loadedPrecursorsMap.put(fileName, fileMap);
            }
            fileMap.put(spectrumTitle, currentPrecursor);
        }

        return currentPrecursor;
    }

    /**
     * Returns the desired spectrum.
     *
     * @param spectrumFile name of the spectrum file
     * @param spectrumTitle title of the spectrum
     *
     * @return the desired spectrum
     */
    public Spectrum getSpectrum(String spectrumFile, String spectrumTitle) {
        
        HashMap<String, Spectrum> fileMap = currentSpectrumMap.get(spectrumFile);
        
        if (fileMap != null) {
            
            Spectrum currentSpectrum = fileMap.get(spectrumTitle);
            
            if (currentSpectrum != null) {
                
                return currentSpectrum;
                
            }
        }
        
        try {
        
            return getSpectrum(spectrumFile, spectrumTitle, 1);
        
        } catch (Exception e) {
            
            throw new RuntimeException(e);
            
        }
    }

    /**
     * Returns the desired spectrum.
     *
     * @param spectrumKey key of the spectrum
     * 
     * @return the desired spectrum
     */
    public Spectrum getSpectrum(String spectrumKey) {
        String fileName = Spectrum.getSpectrumFile(spectrumKey);
        String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
        return getSpectrum(fileName, spectrumTitle);
    }

    /**
     * Returns the desired spectrum. It can be that the IO is busy (especially
     * when working on distant servers) thus returning an error. The method will
     * then retry after waiting waitingTime milliseconds. The waitingTime is
     * doubled for the next try. The method throws an exception after timeout
     * (see timeOut attribute).
     *
     * @param spectrumFile the name of the file containing the spectrum
     * @param spectrumTitle the title of the desired spectrum
     * @param waitingTime the waiting time before retry
     *
     * @return the desired spectrum
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading the file
     */
    private synchronized Spectrum getSpectrum(String spectrumFile, String spectrumTitle, long waitingTime) throws IOException, MzMLUnmarshallerException {

        if (waitingTime <= 0) {
            throw new IllegalArgumentException("Waiting time should be a positive number.");
        }

        Spectrum currentSpectrum = null;

        if (spectrumFile.toLowerCase().endsWith(".mgf")) {

            // a special fix for mgf files with strange titles...
            spectrumTitle = fixMgfTitle(spectrumTitle, spectrumFile);

            if (mgfIndexesMap.get(spectrumFile) == null) {
                throw new FileNotFoundException("Mgf file not found: \'" + spectrumFile + "\'!");
            }
            if (mgfIndexesMap.get(spectrumFile).getIndex(spectrumTitle) == null) {
                throw new IOException("Spectrum \'" + spectrumTitle + "\' in mgf file \'" + spectrumFile + "\' not found!");
            }
            try {
                currentSpectrum = MgfReader.getSpectrum(mgfRandomAccessFilesMap.get(spectrumFile), mgfIndexesMap.get(spectrumFile).getIndex(spectrumTitle), spectrumFile);
            } catch (Exception e) {
                if (waitingTime < timeOut) {
                    try {
                        wait(waitingTime);
                    } catch (InterruptedException ie) {
                    }
                    return getSpectrum(spectrumFile, spectrumTitle, 2 * waitingTime);
                } else {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Error while loading spectrum " + spectrumTitle + " of file " + spectrumFile + ".");
                }
            }
        } else if (spectrumFile.toLowerCase().endsWith(".msp")) {

            // a special fix for mgf files with strange titles...
            spectrumTitle = fixMgfTitle(spectrumTitle, spectrumFile);

            if (mgfIndexesMap.get(spectrumFile) == null) {
                throw new FileNotFoundException("msp file not found: \'" + spectrumFile + "\'!");
            }
            if (mgfIndexesMap.get(spectrumFile).getIndex(spectrumTitle) == null) {
                throw new IOException("Spectrum \'" + spectrumTitle + "\' in msp file \'" + spectrumFile + "\' not found!");
            }
            try {
                currentSpectrum = MspReader.getSpectrum(mgfRandomAccessFilesMap.get(spectrumFile), mgfIndexesMap.get(spectrumFile).getIndex(spectrumTitle), spectrumFile);
            } catch (Exception e) {
                if (waitingTime < timeOut) {
                    try {
                        wait(waitingTime);
                    } catch (InterruptedException ie) {
                    }
                    return getSpectrum(spectrumFile, spectrumTitle, 2 * waitingTime);
                } else {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Error while loading spectrum " + spectrumTitle + " of file " + spectrumFile + ".");
                }
            }
        } else if (spectrumFile.toLowerCase().endsWith(".mzml")) {

            if (mzMLUnmarshallers.get(spectrumFile) == null) {
                throw new IOException("mzML file not found: \'" + spectrumFile + "\'!");
            }
            if (mzMLUnmarshallers.get(spectrumFile).getSpectrumById(spectrumTitle) == null) {
                throw new IOException("Spectrum \'" + spectrumTitle + "\' in mzML file \'" + spectrumFile + "\' not found!");
            }

            uk.ac.ebi.jmzml.model.mzml.Spectrum mzMLSpectrum = mzMLUnmarshallers.get(spectrumFile).getSpectrumById(spectrumTitle);
            int level = 2;
            double mzPrec = 0.0;
            double scanTime = -1.0;
            int chargePrec = 0;
            for (CVParam cvParam : mzMLSpectrum.getCvParam()) {
                if (cvParam.getAccession().equals("MS:1000511")) {
                    level = new Integer(cvParam.getValue());
                    break;
                }
            }
            ScanList scanList = mzMLSpectrum.getScanList();
            if (scanList != null) {
                for (CVParam cvParam : scanList.getScan().get(scanList.getScan().size() - 1).getCvParam()) {
                    if (cvParam.getAccession().equals("MS:1000016")) {
                        scanTime = new Double(cvParam.getValue());
                        break;
                    }
                }
            }
            PrecursorList precursorList = mzMLSpectrum.getPrecursorList();
            if (precursorList != null) {
                if (precursorList.getCount().intValue() == 1) {
                    SelectedIonList sIonList = precursorList.getPrecursor().get(0).getSelectedIonList();
                    if (sIonList != null) {
                        for (CVParam cvParam : sIonList.getSelectedIon().get(0).getCvParam()) {
                            if (cvParam.getAccession().equals("MS:1000744")
                                    || cvParam.getAccession().equals("MS:1000040")) {
                                mzPrec = new Double(cvParam.getValue());
                            } else if (cvParam.getAccession().equals("MS:1000041")) {
                                chargePrec = new Integer(cvParam.getValue());
                            }
                        }
                    }
                }
            }
            List<BinaryDataArray> bdal = mzMLSpectrum.getBinaryDataArrayList().getBinaryDataArray();
            BinaryDataArray mzBinaryDataArray = (BinaryDataArray) bdal.get(0);
            Number[] mzNumbers = mzBinaryDataArray.getBinaryDataAsNumberArray();
            BinaryDataArray intBinaryDataArray = (BinaryDataArray) bdal.get(1);
            Number[] intNumbers = intBinaryDataArray.getBinaryDataAsNumberArray();
            HashMap<Double, Peak> peakList = new HashMap<>();
            for (int i = 0; i < mzNumbers.length; i++) {
                peakList.put(mzNumbers[i].doubleValue(), new Peak(mzNumbers[i].doubleValue(), intNumbers[i].doubleValue(), scanTime));
            }
            ArrayList<Integer> charges = new ArrayList<>();
            charges.add(chargePrec);
            Precursor precursor = level == 1 ? null : new Precursor(scanTime, mzPrec, charges);
            currentSpectrum = new Spectrum(level, precursor, spectrumTitle, peakList, spectrumFile, scanTime);
            
        } else {
            throw new IllegalArgumentException("Spectrum file format not supported.");
        }
        if (loadedSpectra.size() == nSpectraCache) {
            String tempKey = loadedSpectra.pollFirst();
            String tempFile = Spectrum.getSpectrumFile(tempKey);
            HashMap<String, Spectrum> fileMap = currentSpectrumMap.get(tempFile);
            if (fileMap != null) {
                String tempTitle = Spectrum.getSpectrumTitle(tempKey);
                fileMap.remove(tempTitle);
            }
        }
        HashMap<String, Spectrum> fileMap = currentSpectrumMap.get(spectrumFile);
        if (fileMap == null) {
            fileMap = new HashMap<>();
            currentSpectrumMap.put(spectrumFile, fileMap);
        }
        fileMap.put(spectrumTitle, currentSpectrum);
        String spectrumKey = Spectrum.getSpectrumKey(spectrumFile, spectrumTitle);
        loadedSpectra.add(spectrumKey);
        return currentSpectrum;
    }

    /**
     * Writes the given mgf file index in the given directory.
     *
     * @param mgfIndex the mgf file index
     * @param directory the destination directory
     * 
     * @throws IOException exception thrown whenever an error is encountered
     * while writing the file
     */
    public void writeIndex(MgfIndex mgfIndex, File directory) throws IOException {
        File indexFile = new File(directory, getIndexName(mgfIndex.getFileName()));
        SerializationUtils.writeObject(mgfIndex, indexFile);
    }

    /**
     * Deserializes the index of an mgf file.
     *
     * @param mgfIndex the mgf index cui file
     * 
     * @return the corresponding mgf index object
     * 
     * @throws IOException exception thrown whenever an error was encountered
     * while reading the file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing the object
     */
    public MgfIndex getIndex(File mgfIndex) throws IOException, ClassNotFoundException {
        return (MgfIndex) SerializationUtils.readObject(mgfIndex);
    }

    /**
     * Closes all opened files.
     *
     * @throws IOException exception thrown whenever an error occurred while
     * closing the files
     */
    public void closeFiles() throws IOException {
        for (BufferedRandomAccessFile randomAccessFile : mgfRandomAccessFilesMap.values()) {
            randomAccessFile.close();
        }
    }

    /**
     * Returns a list of loaded mgf files.
     *
     * @return a list of loaded mgf files
     */
    public ArrayList<String> getMgfFileNames() {
        return new ArrayList<>(mgfRandomAccessFilesMap.keySet());
    }

    /**
     * Returns a list of loaded mzML files.
     *
     * @return a list of loaded mzML files
     */
    public ArrayList<String> getMzMLFileNames() {
        return new ArrayList<>(mzMLUnmarshallers.keySet());
    }

    /**
     * Returns a list of titles from indexed spectra in the given file.
     *
     * @param mgfFile the name of the mgf file
     * @return a list of titles from indexed spectra in the given file
     */
    public ArrayList<String> getSpectrumTitles(String mgfFile) {
        MgfIndex index = mgfIndexesMap.get(mgfFile);
        if (index == null) {
            return null;
        }
        return index.getSpectrumTitles();
    }

    /**
     * Returns the spectrum index of the given spectrum in the given file. If
     * the same spectrum title is used more than ones the last index is
     * returned. Null if not found.
     *
     * @param spectrumTitle the spectrum title
     * @param mgfFile the name of the mgf file
     * @return the spectrum index of the given spectrum
     */
    public Integer getSpectrumIndex(String spectrumTitle, String mgfFile) {
        MgfIndex mgfIndex = mgfIndexesMap.get(mgfFile);
        if (mgfIndex == null) {
            return null;
        }
        return mgfIndex.getSpectrumIndex(spectrumTitle);
    }

    /**
     * Returns the spectrum title of the spectrum of the given number in the
     * given file. 1 is the first spectrum. Null if not found.
     *
     * @param mgfFile the name of the mgf file of interest
     * @param spectrumNumber the number of the spectrum in the file
     *
     * @return the title of the spectrum of interest
     */
    public String getSpectrumTitle(String mgfFile, int spectrumNumber) {
        MgfIndex mgfIndex = mgfIndexesMap.get(mgfFile);
        if (mgfIndex == null) {
            return null;
        }
        return mgfIndex.getSpectrumTitle(spectrumNumber - 1);
    }

    /**
     * Returns the fixed mgf title.
     *
     * @param spectrumTitle the spectrum title
     * @param fileName the spectrum file name
     * 
     * @return the fixed mgf title
     */
    private String fixMgfTitle(String spectrumTitle, String fileName) {

        // a special fix for mgf files with titles containing url encoding, e.g.: %3b instead of ;
        if (mgfIndexesMap.get(fileName).getIndex(spectrumTitle) == null) {
            try {
                spectrumTitle = URLDecoder.decode(spectrumTitle, "utf-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("An exception was thrown when trying to decode an mgf title: " + spectrumTitle);
                e.printStackTrace();
            }
        }

        // a special fix for mgf files with titles containing \\ instead \
        if (mgfIndexesMap.get(fileName).getIndex(spectrumTitle) == null) {
            spectrumTitle = spectrumTitle.replaceAll("\\\\\\\\", "\\\\"); // @TODO: only required for omssa???
        }

        return spectrumTitle;
    }

    /**
     * Adds an id to spectrum name in the mapping.
     *
     * @param idName name according to the id file
     * @param spectrumFile the spectrum file
     */
    public void addIdNameMapping(String idName, File spectrumFile) {
        idToSpectrumName.put(idName, spectrumFile);
    }

    /**
     * Returns the spectrum file corresponding to the name of the file used for
     * identification
     *
     * @param idName the name of the spectrum file according to the
     * identification file
     * @return the spectrum file
     */
    public File getSpectrumFileFromIdName(String idName) {
        return idToSpectrumName.get(idName);
    }

    /**
     * Returns the file associated to the given name.
     *
     * @param fileName the name of the file
     *
     * @return the file
     */
    public File getMgfFileFromName(String fileName) {
        return filesMap.get(fileName);
    }

    /**
     * Returns a map containing all the precursors of a gven file indexed by
     * spectrum title.
     *
     * @param fileName the name of the file
     *
     * @return a map containing all the precursors of a gven file
     */
    public HashMap<String, Precursor> getPrecursorMap(String fileName) {
        HashMap<String, Precursor> precursorMap = new HashMap<>(getNSpectra(fileName));
        for (String spectrumtitle : getSpectrumTitles(fileName)) {
            Precursor precursor = getPrecursor(fileName, spectrumtitle);
            precursorMap.put(spectrumtitle, precursor);
        }
        return precursorMap;
    }
}
