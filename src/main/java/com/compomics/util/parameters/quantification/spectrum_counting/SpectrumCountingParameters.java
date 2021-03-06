package com.compomics.util.parameters.quantification.spectrum_counting;

import com.compomics.util.db.object.DbObject;
import com.compomics.util.experiment.identification.validation.MatchValidationLevel;
import com.compomics.util.experiment.quantification.spectrumcounting.SpectrumCountingMethod;
import com.compomics.util.experiment.units.MetricsPrefix;
import com.compomics.util.experiment.units.StandardUnit;
import com.compomics.util.experiment.units.UnitOfMeasurement;

/**
 * This class contains the spectrum counting parameters.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class SpectrumCountingParameters extends DbObject {

    /**
     * The reference total mass to use for normalization in μg.
     */
    private Double referenceMass = 2.0;
    /**
     * The unit to use for normalization.
     */
    private UnitOfMeasurement unit = new UnitOfMeasurement(StandardUnit.mol, MetricsPrefix.femto);
    /**
     * Indicates whether the spectrum counting index should be normalized.
     */
    private Boolean normalize = true;
    /**
     * The currently selected spectrum counting method.
     */
    private SpectrumCountingMethod selectedMethod;
    /**
     * The minimal match validation level to consider as indexed in the
     * MatchValidationLevel enum.
     */
    private Integer matchValidationLevel;

    /**
     * Default constructor.
     */
    public SpectrumCountingParameters() {
        // Set default preferences
        selectedMethod = SpectrumCountingMethod.NSAF;
        matchValidationLevel = MatchValidationLevel.doubtful.getIndex();
    }
    

    /**
     * Creates new preferences based on other spectrum counting preferences.
     *
     * @param otherSpectrumCountingPreferences the other spectrum counting
     * preferences
     */
    public SpectrumCountingParameters(SpectrumCountingParameters otherSpectrumCountingPreferences) {
        this.selectedMethod = otherSpectrumCountingPreferences.getSelectedMethod();
        this.normalize = otherSpectrumCountingPreferences.getNormalize();
        this.referenceMass = otherSpectrumCountingPreferences.getReferenceMass();
        this.unit = otherSpectrumCountingPreferences.getUnit();

    }

    /**
     * Returns the current spectrum counting method.
     *
     * @return the current spectrum counting method
     */
    public SpectrumCountingMethod getSelectedMethod() {
        readDBMode();
        return selectedMethod;
    }

    /**
     * Set the current spectrum counting method.
     *
     * @param selectedMethod the spectral counting method
     */
    public void setSelectedMethod(SpectrumCountingMethod selectedMethod) {
        writeDBMode();
        this.selectedMethod = selectedMethod;
    }

    /**
     * Returns the lowest validation level considered as an integer as indexed
     * in the MatchValidationLevel enum.
     *
     * @return the lowest validation level considered
     */
    public Integer getMatchValidationLevel() {
        readDBMode();
        return matchValidationLevel;
    }

    /**
     * Sets the lowest validation level to consider as an integer as indexed in
     * the MatchValidationLevel enum.
     *
     * @param matchValidationLevel the lowest validation level to consider
     */
    public void setMatchValidationLevel(Integer matchValidationLevel) {
        writeDBMode();
        this.matchValidationLevel = matchValidationLevel;
    }

    /**
     * Compares two spectrum counting preferences.
     *
     * @param anotherSpectrumCountingPreferences another spectrum counting
     * preferences
     * @return a boolean indicating whether the other spectrum counting
     * preferences is the same as this one
     */
    public boolean isSameAs(SpectrumCountingParameters anotherSpectrumCountingPreferences) {
        readDBMode();
        if (!getNormalize() && anotherSpectrumCountingPreferences.getNormalize()
                || getNormalize() && !anotherSpectrumCountingPreferences.getNormalize()) {
            return false;
        }
        if (getNormalize() && anotherSpectrumCountingPreferences.getNormalize()) {
            if (!getUnit().isSameAs(anotherSpectrumCountingPreferences.getUnit())) {
                return false;
            }
            if (getReferenceMass() != null && anotherSpectrumCountingPreferences.getReferenceMass() == null
                    || getReferenceMass() == null && anotherSpectrumCountingPreferences.getReferenceMass() != null) {
                return false;
            }
            if (getReferenceMass() != null && anotherSpectrumCountingPreferences.getReferenceMass() != null
                    && !getReferenceMass().equals(anotherSpectrumCountingPreferences.getReferenceMass())) {
                return false;
            }
        }
        return anotherSpectrumCountingPreferences.getSelectedMethod() == selectedMethod
                && anotherSpectrumCountingPreferences.getMatchValidationLevel().equals(getMatchValidationLevel());
    }

    /**
     * Returns the reference total mass to use for normalization.
     *
     * @return the reference total mass to use for normalization in μg
     */
    public Double getReferenceMass() {
        readDBMode();
        return referenceMass;
    }

    /**
     * Sets the reference total mass to use for normalization.
     *
     * @param referenceMass the reference total mass to use for normalization in
     * μg
     */
    public void setReferenceMass(Double referenceMass) {
        writeDBMode();
        this.referenceMass = referenceMass;
    }

    /**
     * Returns the unit used for normalization.
     *
     * @return the unit used for normalization
     */
    public UnitOfMeasurement getUnit() {
        readDBMode();
        return unit;
    }

    /**
     * Sets the unit used for normalization.
     *
     * @param unit the unit used for normalization
     */
    public void setUnit(UnitOfMeasurement unit) {
        writeDBMode();
        this.unit = unit;
    }

    /**
     * Indicates whether the spectrum counting index should be normalized.
     *
     * @return true if the spectrum counting index should be normalized
     */
    public Boolean getNormalize() {
        readDBMode();
        return normalize;
    }

    /**
     * Sets whether the spectrum counting index should be normalized.
     *
     * @param normalize a boolean indicating whether the spectrum counting index
     * should be normalized
     */
    public void setNormalize(Boolean normalize) {
        writeDBMode();
        this.normalize = normalize;
    }
}
