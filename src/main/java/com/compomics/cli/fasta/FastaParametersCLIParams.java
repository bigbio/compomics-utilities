package com.compomics.cli.fasta;

import com.compomics.software.cli.CommandLineUtils;
import org.apache.commons.cli.Options;

/**
 * Command line parameters for the FASTA files.
 *
 * @author Marc Vaudel
 */
public enum FastaParametersCLIParams {

    //////////////////////////////////////////////////////////////////////////////////////////
    // Needs to be added to the wiki
    //
    // IMPORTANT: Any change here must be reported in the wiki: 
    // https://github.com/compomics/compomics-utilities/wiki/IdentificationParametersCLI
    //////////////////////////////////////////////////////////////////////////////////////////
    NAME("name", "The name of the database, fasta file name by default.", false, true),
    DESCRIPTION("description", "The description of the database, fasta file name by default.", false, true),
    VERSION("version", "The version of the database, date of creation of the file by default.", false, true),
    DECOY("decoy", "The decoy flag, no argument means target only, inferred by default.", false, true),
    SUFFIX("suffix", "The location of the decoy flag: (1) prefix, (2) suffix. 1 by default.", false, true);

    /**
     * Short Id for the CLI parameter.
     */
    public final String id;
    /**
     * Explanation for the CLI parameter.
     */
    public final String description;
    /**
     * Boolean indicating whether the parameter is mandatory.
     */
    public final boolean mandatory;
    /**
     * Boolean indicating whether this command line option needs an argument.
     */
    public final boolean hasArgument;

    /**
     * Private constructor managing the various variables for the enum
     * instances.
     *
     * @param id the id
     * @param description the description
     * @param mandatory is the parameter mandatory
     * @param hasArgument boolean indicating whether this command line option
     * needs an argument
     */
    private FastaParametersCLIParams(String id, String description, boolean mandatory, boolean hasArgument) {
        this.id = id;
        this.description = description;
        this.mandatory = mandatory;
        this.hasArgument = hasArgument;
    }

    /**
     * Creates the options for the command line interface based on the possible
     * values.
     *
     * @param aOptions the options object where the options will be added
     */
    public static void createOptionsCLI(Options aOptions) {
        for (FastaParametersCLIParams identificationParametersCLIParams : FastaParametersCLIParams.values()) {
            aOptions.addOption(identificationParametersCLIParams.id, identificationParametersCLIParams.hasArgument, identificationParametersCLIParams.description);
        }
    }

    /**
     * Returns the options as a string.
     *
     * @return the options as a string
     */
    public static String getOptionsAsString() {

        String output = "";

        output += "\n\nFasta Parameters:\n\n";

        for (FastaParametersCLIParams value : values()) {

            output += "-" + String.format(CommandLineUtils.formatter, value.id) + " " + value.description + "\n";

        }

        return output;
    }
}
