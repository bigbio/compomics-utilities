package com.compomics.util.parameters.identification.tool_specific;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.gui.parameters.identification.IdentificationAlgorithmParameter;

/**
 * The MS Amanda specific parameters.
 *
 * @author Harald Barsnes
 */
public class MsAmandaParameters implements IdentificationAlgorithmParameter {

    /**
     * Version number for deserialization.
     */
    static final long serialVersionUID = -8458620189315975268L;
    /**
     * Defines whether a decoy database shall be created and searched against.
     * Decoy FASTS files are generated by reverting protein sequences,
     * accessions are marked with the prefix “REV_”.
     */
    private boolean generateDecoy = false;
    /**
     * The MS Amanda instrument ID.
     */
    private String instrumentID = "b, y";
    /**
     * The maximum rank.
     */
    private Integer maxRank = 10;
    /**
     * Defines whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     */
    private boolean monoisotopic = true;
    /**
     * Defines whether the low memory mode is used.
     *
     * @deprecated since MS Amanda 2.0
     */
    private Boolean lowMemoryMode = true;
    /**
     * Defines whether deisotoping is to be performed.
     */
    private Boolean performDeisotoping = true;
    /**
     * Maximum number of occurrences of a specific modification on a peptide
     * (0-10).
     */
    private Integer maxModifications = 3;
    /**
     * Maximum number of variable modifications per peptide (0-10).
     */
    private Integer maxVariableModifications = 4;
    /**
     * Maximum number of potential modification sites per modification per
     * peptide (0-20).
     */
    private Integer maxModificationSites = 6;
    /**
     * Maximum number of water and ammonia losses per peptide (0-5).
     */
    private Integer maxNeutralLosses = 1;
    /**
     * Maximum number identical modification specific losses per peptide (0-5).
     */
    private Integer maxNeutralLossesPerModification = 2;
    /**
     * Minimum peptide length.
     */
    private Integer minPeptideLength = 6;
    /**
     * Maximum number of proteins loaded into memory (1000-500000).
     */
    private Integer maxLoadedProteins = 100000;
    /**
     * Maximum number of spectra loaded into memory (1000-500000).
     */
    private Integer maxLoadedSpectra = 2000;
    /**
     * The maximum allowed length of the FASTA file name.
     */
    public static final int MAX_MS_AMANDA_FASTA_FILE_NAME_LENGTH = 80;
    /**
     * The output format: csv or mzIdentML.
     */
    private String outputFormat = "csv";

    /**
     * Constructor.
     */
    public MsAmandaParameters() {
    }

    @Override
    public Advocate getAlgorithm() {
        return Advocate.msAmanda;
    }

    @Override
    public boolean equals(IdentificationAlgorithmParameter identificationAlgorithmParameter) {

        if (identificationAlgorithmParameter instanceof MsAmandaParameters) {
            MsAmandaParameters msAmandaParameters = (MsAmandaParameters) identificationAlgorithmParameter;
            if (generateDecoy != msAmandaParameters.generateDecoy()) {
                return false;
            }
            if (monoisotopic != msAmandaParameters.isMonoIsotopic()) {
                return false;
            }
            if (!instrumentID.equalsIgnoreCase(msAmandaParameters.getInstrumentID())) {
                return false;
            }
            if (!maxRank.equals(msAmandaParameters.getMaxRank())) {
                return false;
            }
            if (performDeisotoping != msAmandaParameters.isPerformDeisotoping()) {
                return false;
            }
            if (!maxModifications.equals(msAmandaParameters.getMaxModifications())) {
                return false;
            }
            if (!maxVariableModifications.equals(msAmandaParameters.getMaxVariableModifications())) {
                return false;
            }
            if (!maxModificationSites.equals(msAmandaParameters.getMaxModificationSites())) {
                return false;
            }
            if (!maxNeutralLosses.equals(msAmandaParameters.getMaxNeutralLosses())) {
                return false;
            }
            if (!maxNeutralLossesPerModification.equals(msAmandaParameters.getMaxNeutralLossesPerModification())) {
                return false;
            }
            if (!minPeptideLength.equals(msAmandaParameters.getMinPeptideLength())) {
                return false;
            }
            if (!maxLoadedProteins.equals(msAmandaParameters.getMaxLoadedProteins())) {
                return false;
            }
            if (!maxLoadedSpectra.equals(msAmandaParameters.getMaxLoadedSpectra())) {
                return false;
            }
            if (!getOutputFormat().equalsIgnoreCase(msAmandaParameters.getOutputFormat())) {
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    public String toString(boolean html) {
        String newLine = System.getProperty("line.separator");

        if (html) {
            newLine = "<br>";
        }

        StringBuilder output = new StringBuilder();
        Advocate advocate = getAlgorithm();
        output.append("# ------------------------------------------------------------------");
        output.append(newLine);
        output.append("# ").append(advocate.getName()).append(" Specific Parameters");
        output.append(newLine);
        output.append("# ------------------------------------------------------------------");
        output.append(newLine);
        output.append(newLine);

        output.append("SEARCH_DECOY=");
        output.append(generateDecoy);
        output.append(newLine);
        output.append("INSTRUMENT_ID=");
        output.append(instrumentID);
        output.append(newLine);
        output.append("MONOISOTOPIC=");
        output.append(monoisotopic);
        output.append(newLine);
        output.append("MAX_RANK=");
        output.append(maxRank);
        output.append(newLine);
        output.append("PERFORM_DEISOTOPING=");
        output.append(isPerformDeisotoping());
        output.append(newLine);
        output.append("MAX_MODIFICATIONS=");
        output.append(getMaxModifications());
        output.append(newLine);
        output.append("MAX_VARIABLE_MODIFICATIONS=");
        output.append(getMaxVariableModifications());
        output.append(newLine);
        output.append("MAX_MODIFICATIONS_SITES=");
        output.append(getMaxModificationSites());
        output.append(newLine);
        output.append("MAX_NEUTRAL_LOSSES=");
        output.append(getMaxNeutralLosses());
        output.append(newLine);
        output.append("MAX_NEUTRAL_LOSSES_PER_MODIFICATION=");
        output.append(getMaxNeutralLossesPerModification());
        output.append(newLine);
        output.append("MIN_PEPTIDE_LENGTH=");
        output.append(getMinPeptideLength());
        output.append(newLine);
        output.append("MAX_LOADED_PROTEINS=");
        output.append(getMaxLoadedProteins());
        output.append(newLine);
        output.append("MAX_LOADED_SPECTRA=");
        output.append(getMaxLoadedSpectra());
        output.append(newLine);
        output.append("OUTPUT_FORMAT=");
        output.append(outputFormat);
        output.append(newLine);

        return output.toString();
    }

    /**
     * Returns whether a decoy database shall be created and searched against.
     *
     * @return true if a decoy database shall be created and searched against
     */
    public boolean generateDecoy() {
        return generateDecoy;
    }

    /**
     * Set whether a decoy database shall be created and searched against.
     *
     * @param generateDecoy the generateDecoy to set
     */
    public void setGenerateDecoyDatabase(boolean generateDecoy) {
        this.generateDecoy = generateDecoy;
    }

    /**
     * Returns whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     *
     * @return true if mass values shall be used (in contrast to average mass
     * values)
     */
    public boolean isMonoIsotopic() {
        return monoisotopic;
    }

    /**
     * Set whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     *
     * @param monoisotopic the monoisotopic to set
     */
    public void setMonoIsotopic(boolean monoisotopic) {
        this.monoisotopic = monoisotopic;
    }

    /**
     * Return the instrument ID.
     *
     * @return the instrument ID
     */
    public String getInstrumentID() {
        return instrumentID;
    }

    /**
     * Set the instrument ID.
     *
     * @param instrumentID the instrument ID to set
     */
    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    /**
     * Returns the maximum rank.
     *
     * @return the max rank
     */
    public Integer getMaxRank() {
        return maxRank;
    }

    /**
     * Set the maximum rank.
     *
     * @param maxRank the maxRank to set
     */
    public void setMaxRank(Integer maxRank) {
        this.maxRank = maxRank;
    }

    /**
     * Returns whether the low memory mode is used.
     *
     * @deprecated use getMaxLoadedProteins and getMaxLoadedSpectra instead
     * @return true if in low memory mode
     */
    public boolean isLowMemoryMode() {
        if (lowMemoryMode == null) {
            lowMemoryMode = true;
        }
        return lowMemoryMode;
    }

    /**
     * Set whether the low memory mode is used.
     *
     * @deprecated use setMaxLoadedProteins and setMaxLoadedSpectra instead
     * @param lowMemoryMode the low memory mode to set
     */
    public void setLowMemoryMode(boolean lowMemoryMode) {
        this.lowMemoryMode = lowMemoryMode;
    }

    /**
     * Returns whether deisotoping is to be performed.
     *
     * @return true if deisotoping is to be performed
     */
    public boolean isPerformDeisotoping() {
        if (performDeisotoping == null) {
            performDeisotoping = true;
        }
        return performDeisotoping;
    }

    /**
     * Sets if deisotoping is to be performed.
     *
     * @param performDeisotoping the performDeisotoping to set
     */
    public void setPerformDeisotoping(boolean performDeisotoping) {
        this.performDeisotoping = performDeisotoping;
    }

    /**
     * Returns the maximum number of modifications per peptide.
     *
     * @return the maximum number of modifications
     */
    public Integer getMaxModifications() {
        if (maxModifications == null) {
            maxModifications = 3;
        }
        return maxModifications;
    }

    /**
     * Set the maximum number of modifications per peptide.
     *
     * @param maxModifications the maximum number of modifications
     */
    public void setMaxModifications(Integer maxModifications) {
        this.maxModifications = maxModifications;
    }

    /**
     * Returns the maximum number of variable modifications per peptide.
     *
     * @return the maximum number of variable modifications
     */
    public Integer getMaxVariableModifications() {
        if (maxVariableModifications == null) {
            maxVariableModifications = 4;
        }
        return maxVariableModifications;
    }

    /**
     * Set the maximum number of variable modifications per peptide.
     *
     * @param maxVariableModifications the maximum number of variable
     * modifications
     */
    public void setMaxVariableModifications(Integer maxVariableModifications) {
        this.maxVariableModifications = maxVariableModifications;
    }

    /**
     * Returns the maximum number of modifications sites per modification per
     * peptide.
     *
     * @return the maximum number of modifications sites per modification per
     * peptide
     */
    public Integer getMaxModificationSites() {
        if (maxModificationSites == null) {
            maxModificationSites = 6;
        }
        return maxModificationSites;
    }

    /**
     * Set the maximum number of modifications sites per modification per
     * peptide.
     *
     * @param maxModificationSites the maximum number of modifications sites per
     * modification per peptide
     */
    public void setMaxModificationSites(Integer maxModificationSites) {
        this.maxModificationSites = maxModificationSites;
    }

    /**
     * Returns the maximum number of water and ammonia losses per peptide.
     *
     * @return the maximum number of water and ammonia losses per peptide
     */
    public Integer getMaxNeutralLosses() {
        if (maxNeutralLosses == null) {
            maxNeutralLosses = 1;
        }
        return maxNeutralLosses;
    }

    /**
     * Set the maximum number of water and ammonia losses per peptide.
     *
     * @param maxNeutralLosses the maximum number of water and ammonia losses
     * per peptide
     */
    public void setMaxNeutralLosses(Integer maxNeutralLosses) {
        this.maxNeutralLosses = maxNeutralLosses;
    }

    /**
     * Returns the maximum number identical modification specific losses per
     * peptide.
     *
     * @return the the maximum number identical modification specific losses per
     * peptide
     */
    public Integer getMaxNeutralLossesPerModification() {
        if (maxNeutralLossesPerModification == null) {
            maxNeutralLossesPerModification = 2;
        }
        return maxNeutralLossesPerModification;
    }

    /**
     * Set the maximum number identical modification specific losses per
     * peptide.
     *
     * @param maxNeutralLossesPerModification the maximum number identical
     * modification specific losses per peptide
     */
    public void setMaxNeutralLossesPerModification(Integer maxNeutralLossesPerModification) {
        this.maxNeutralLossesPerModification = maxNeutralLossesPerModification;
    }

    /**
     * Returns the minimum peptide length.
     *
     * @return the the minimum peptide length
     */
    public Integer getMinPeptideLength() {
        if (minPeptideLength == null) {
            minPeptideLength = 6;
        }
        return minPeptideLength;
    }

    /**
     * Set the minimum peptide length.
     *
     * @param minPeptideLength the minimum peptide length
     */
    public void setMinPeptideLength(Integer minPeptideLength) {
        this.minPeptideLength = minPeptideLength;
    }

    /**
     * Returns the maximum number of proteins loaded into memory.
     *
     * @return the maximum number of proteins loaded into memory
     */
    public Integer getMaxLoadedProteins() {
        if (maxLoadedProteins == null) {
            maxLoadedProteins = 100000;
        }
        return maxLoadedProteins;
    }

    /**
     * Set the maximum number of proteins loaded into memory.
     *
     * @param maxLoadedProteins the maximum number of proteins loaded into
     * memory
     */
    public void setMaxLoadedProteins(Integer maxLoadedProteins) {
        this.maxLoadedProteins = maxLoadedProteins;
    }

    /**
     * Returns the maximum number of spectra loaded into memory.
     *
     * @return the maximum number of spectra loaded into memory
     */
    public Integer getMaxLoadedSpectra() {
        if (maxLoadedSpectra == null) {
            maxLoadedSpectra = 2000;
        }
        return maxLoadedSpectra;
    }

    /**
     * Set the maximum number of spectra loaded into memory.
     *
     * @param maxLoadedSpectra the maximum number of spectra loaded into memory
     */
    public void setMaxLoadedSpectra(Integer maxLoadedSpectra) {
        this.maxLoadedSpectra = maxLoadedSpectra;
    }
    
    /**
     * Returns the output format.
     *
     * @return the outputFormat
     */
    public String getOutputFormat() {
        if (outputFormat == null) {
            outputFormat = "csv";
        }
        return outputFormat;
    }

    /**
     * Set the output format.
     *
     * @param outputFormat the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
