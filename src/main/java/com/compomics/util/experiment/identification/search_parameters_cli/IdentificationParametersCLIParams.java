package com.compomics.util.experiment.identification.search_parameters_cli;

/**
 * Enum class specifying the SearchParameter command line option parameters to
 * create a SearchParameters object
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public enum IdentificationParametersCLIParams {

    OUTPUT("out", "The destination Identification Parameters file (.parameters).", false),
    MODS("mods", "Lists the modifications available.", false),
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // IMPORTANT: Any change here must be reported in the wikis: 
    // http://code.google.com/p/denovogui/wiki/DeNovoCLI and
    // http://code.google.com/p/searchgui/wiki/SearchCLI.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////
    // General parameters
    //////////////////////////////////
    PREC_PPM("prec_ppm", "Precursor ion tolerance unit: ppm (1) or Da (2). Default is '1'.", false),
    FRAG_PPM("frag_ppm", "Fragment ion tolerance unit: ppm (1) or Da (2). Default is '1'.", false),
    PREC_TOL("prec_tol", "Precursor ion mass tolerance, default is '10' ppm.", false),
    PREC_TOL_DA("prec_tol", "Precursor ion mass tolerance in Dalton, default is 0.5 Da.", false), // For tools which do not have the ppm option
    FRAG_TOL("frag_tol", "Fragment ion mass tolerance in Dalton, default is '0.5' Da.", false),
    ENZYME("enzyme", "Enzyme, default is 'Trypsin'. The available enzymes are listed in the GUI. (Names are case sensitive.)", false),
    FIXED_MODS("fixed_mods", "The fixed modifications as a comma separated list. Example: '\"oxidation of m\",\"phosphorylation of s\"' (Modifications are configured in the GUI.)", false),
    VARIABLE_MODS("variable_mods", "The variable modifications as a comma separated list. Example: '\"oxidation of m\",\"phosphorylation of s\"' (Modifications are configured in the GUI.)", false),
    MIN_CHARGE("min_charge", "The minimal charge to search for, default is '2'.", false),
    MAX_CHARGE("max_charge", "The maximal charge to search for, default is '4'.", false),
    MC("mc", "The number of allowed missed cleavages, default is '2'.", false),
    FI("fi", "The type of forward ion searched. Default is 'b'.", false),
    RI("ri", "The type of rewind ion searched. Default is 'y'.", false),
    DB("db", "The sequence database in FASTA format.", true),
    //////////////////////////////////
    // OMSSA specific parameters
    //////////////////////////////////
    OMSSA_SEQUENCES_IN_MEMORY("omssa_memory", "OMSSA map sequences in memory option, 1: true, 0: false, default is '1'.", false),
    OMSSA_ISOTOPES("omssa_isotopes", "OMSSA number of isotopes option, integer, 0: monoisotopic, default is '0'.", false),
    OMSSA_NEUTRON("omssa_neutron", "Mass after which OMSSA should consider neutron exact mass, default is '1446.94'.", false),
    OMSSA_LOW_INTENSITY("omssa_low_intensity", "OMSSA low intensity cut-off as percentage of the most intense peak, default is '0.0'.", false),
    OMSSA_HIGH_INTENSITY("omssa_high_intensity", "OMSSA high intensity cut-off as percentage of the most intense peak, default is '0.2'.", false),
    OMSSA_INTENSITY_INCREMENT("omssa_intensity_increment", "OMSSA intensity increment, default is '0.0005'.", false),
    OMSSA_SINGLE_WINDOW_WIDTH("omssa_single_window_width", "OMSSA single charge window width, integer, default is '27'.", false),
    OMSSA_DOUBLE_WINDOW_WIDTH("omssa_double_window_width", "OMSSA double charge window width, integer, default is '14'.", false),
    OMSSA_SINGLE_WINDOW_PEAKS("omssa_single_window_peaks", "OMSSA single charge window number of peaks, integer, default is '2'.", false),
    OMSSA_DOUBLE_WINDOW_PEAKS("omssa_double_window_peaks", "OMSSA double charge window number of peaks, integer, default is '2'.", false),
    OMSSA_MIN_ANNOTATED_INTENSE_PEAKS("omssa_min_annotated_intense_peaks", "OMSSA minimum number of annotated peaks among the most intense ones, integer, default is '6'.", false),
    OMSSA_MIN_ANNOTATED_PEAKS("omssa_min_annotated_peaks", "OMSSA minimum number of annotated peaks, integer, default is '2'.", false),
    OMSSA_MIN_PEAKS("omssa_min_peaks", "OMSSA minimum number of peaks, integer, default is '4'.", false),
    OMSSA_METHIONINE("omssa_methionine", "OMSSA N-terminal methionine cleavage option, 1: true, 0: false, default is '1'.", false),
    OMSSA_MAX_LADDERS("omssa_max_ladders", "OMSSA maximum number of m/z ladders, integer, default is '128'.", false),
    OMSSA_MAX_FRAG_CHARGE("omssa_max_frag_charge", "OMSSA maximum fragment charge, integer, default is '2'.", false),
    OMSSA_MAX_FRACTION("omssa_fraction", "OMSSA fraction of peaks to estimate charge 1, default is '0.95'.", false),
    OMSSA_PLUS_ONE("omssa_plus_one", "OMSSA estimate plus one charge algorithmically option, 1: true, 0: false, default is '1'.", false),
    OMSSA_POSITIVE_IONS("omssa_charge", "OMSSA fragment charge option, 1: plus, 0: minus, default is '1'.", false),
    OMSSA_PREC_PER_SPECTRUM("omssa_prec_per_spectrum", "OMSSA minimum number of precursors per spectrum, integer, default is '1'.", false),
    OMSSA_FORWARD_IONS("omssa_forward", "OMSSA search forward ions first option, 1: true, 0: false, default is '0'.", false),
    OMSSA_REWIND_IONS("omssa_rewind", "OMSSA search rewind (C-terminal) ions option, 1: true, 0: false, default is '1'.", false),
    OMSSA_MAX_FRAG_SERIES("omssa_max_frag_series", "OMSSA maximum fragment per series option, integer, default is '100'.", false),
    OMSSA_CORRELATION_CORRECTION("omssa_corr", "OMSSA use correlation correction score option, 1: true, 0: false, default is '1'.", false),
    OMSSA_CONSECUTIVE_ION_PROBABILITY("omssa_consecutive_p", "OMSSA consecutive ion probability, default is '0.5'.", false),
    OMSSA_ITERATIVE_SEQUENCE_EVALUE("omssa_it_sequence_evalue", "OMSSA e-value cutoff to consider a sequence in the iterative search 0.0 means all, default is '0.0'.", false),
    OMSSA_ITERATIVE_SPECTRUM_EVALUE("omssa_it_spectrum_evalue", "OMSSA e-value cutoff to consider a spectrum in the iterative search 0.0 means all, default is '0.01'.", false),
    OMSSA_ITERATIVE_REPLACE_EVALUE("omssa_it_replace_evalue", "OMSSA e-value cutoff to replace a hit in the iterative search 0.0 means keep best, default is '0.0'.", false),
    OMSSA_REMOVE_PREC("omssa_remove_prec", "OMSSA remove precursor option, 1: true, 0: false, default is '1'.", false),
    OMSSA_SCALE_PREC("omssa_scale_prec", "OMSSA scale precursor mass option, 1: true, 0: false, default is '0'.", false),
    OMSSA_ESTIMATE_CHARGE("omssa_estimate_charge", "OMSSA estimate precursor charge option, 1: true, 0: false, default is '1'.", false),
    OMSSA_MAX_EVALUE("omssa_max_evalue", "OMSSA maximal evalue considered, default is '100'.", false),
    OMSSA_HITLIST_LENGTH("omssa_hitlist_length", "OMSSA hitlist length, default is '25'.", false),
    OMSSA_HITLIST_LENGTH_CHARGE("omssa_hitlist_length_charge", "OMSSA number of hits per spectrum per charge, default is '30'.", false),
    OMSSA_MIN_PEP_LENGTH("omssa_min_pep_length", "OMSSA minumum peptide length (semi-tryptic or no enzyme searches only).", false),
    OMSSA_MAX_PEP_LENGTH("omssa_max_pep_length", "OMSSA maximum peptide length (OMSSA semi-tryptic or no enzyme searches only).", false),
    OMSSA_FORMAT("omssa_format", "OMSSA output format. 0: omx, 1: csv, default is 'omx'.", false),
    //////////////////////////////////
    // X!Tandem specific parameters
    //////////////////////////////////
    XTANDEM_DYNAMIC_RANGE("xtandem_dynamic_range", "X!Tandem 'spectrum, dynamic range' option. Default is '100'.", false),
    XTANDEM_NPEAKS("xtandem_npeaks", "X!Tandem 'spectrum, total peaks' option. Default is '50'.", false),
    XTANDEM_MIN_FRAG_MZ("xtandem_min_frag_mz", "X!Tandem 'spectrum, minimum fragment mz' option. Default is '200'.", false),
    XTANDEM_MIN_PEAKS("xtandem_min_peaks", "X!Tandem 'spectrum, minimum peaks' option. Default is '15'.", false),
    XTANDEM_NOISE_SUPPRESSION("xtandem_noise_suppression", "X!Tandem 'spectrum, use noise suppression' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_MIN_PREC_MASS("xtandem_min_prec_mass", "X!Tandem 'spectrum, minimum parent m+h' option. Default is '500'.", false),
    XTANDEM_QUICK_ACETYL("xtandem_quick_acetyl", "X!Tandem 'protein, quick acetyl' option. 1: true, 0: false, default is '1'.", false),
    XTANDEM_QUICK_PYRO("xtandem_quick_pyro", "X!Tandem 'protein, quick pyrolidone' option. 1: true, 0: false, default is '1'.", false),
    XTANDEM_STP_BIAS("xtandem_stp_bias", "X!Tandem 'protein, stP bias' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_REFINE("xtandem_refine", "X!Tandem 'refine' option. 1: true, 0: false, default is '1'.", false),
    XTANDEM_REFINE_EVALUE("xtandem_refine_evalue", "X!Tandem 'refine, maximum valid expectation value' option. Default is '0.01'.", false),
    XTANDEM_REFINE_UNANTICIPATED_CLEAVAGE("xtandem_refine_unanticipated_cleavage", "X!Tandem 'refine, unanticipated cleavage' option. 1: true, 0: false, default is '1'.", false),
    XTANDEM_REFINE_SEMI("xtandem_refine_semi", "X!Tandem 'refine, cleavage semi' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_REFINE_POTENTIAL_MOD_FULL_REFINEMENT("xtandem_refine_potential_mod_full", "X!Tandem 'refine, use potential modifications for full refinement' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_REFINE_POINT_MUTATIONS("xtandem_refine_point_mutations", "X!Tandem 'refine, point mutations' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_REFINE_SNAPS("xtandem_refine_snaps", "X!Tandem 'refine, saps' option. 1: true, 0: false, default is '1'.", false),
    XTANDEM_REFINE_SPECTRUM_SYNTHESIS("xtandem_refine_spectrum_synthesis", "X!Tandem 'refine, spectrum synthesis' option. 1: true, 0: false, default is '1'.", false),
    XTANDEM_EVALUE("xtandem_evalue", "X!Tandem 'output, maximum valid expectation value' option. Default is '100'.", false),
    XTANDEM_OUTPUT_PROTEINS("xtandem_output_proteins", "X!Tandem 'output, proteins' option. 1: true, 0: false, default is '0'.", true),
    XTANDEM_OUTPUT_SEQUENCES("xtandem_output_sequences", "X!Tandem 'output, sequences' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_OUTPUT_SPECTRA("xtandem_output_spectra", "X!Tandem 'output, spectra' option. 1: true, 0: false, default is '0'.", false),
    XTANDEM_SKYLINE("xtandem_skyline_path", "X!Tandem 'spectrum, skyline path' option.", false),
    //////////////////////////////////
    // PepNovo specific parameters
    //////////////////////////////////
    PEPNOVO_HITLIST_LENGTH("pepnovo_hitlist_length", "Pepnovo+ number of de novo solutions [0-2000]. Default is '10'.", false),
    PEPTNOVO_ESTIMATE_CHARGE("pepnovo_estimate_charge", "Pepnovo+ estimate precursor charge option. 1: true, 0: false, default is '1'.", false),
    PEPNOVO_CORRECT_PREC_MASS("pepnovo_correct_prec_mass", "Pepnovo+ correct precursor mass option. 1: true, 0: false, default is '1'.", false),
    PEPNOVO_DISCARD_SPECTRA("pepnovo_discard_spectra", "Pepnovo+ discard low quality spectra optoin. 1: true, 0: false, default is '1'.", false),
    PEPNOVO_FRAGMENTATION_MODEL("pepnovo_fragmentation_model", "PepNovo+ fragmentation model. Default is 'CID_IT_TRYP'.", false),
    PEPNOVO_GENERATE_BLAST("pepnovo_generate_blast", "PepNovo+ generate a BLAST query. 1: true, 0: false, default is '0'.", false);

    /**
     * Short Id for the CLI parameter.
     */
    public String id;
    /**
     * Explanation for the CLI parameter.
     */
    public String description;
    /**
     * Boolean indicating whether the parameter is mandatory.
     */
    public boolean mandatory;

    /**
     * Private constructor managing the various variables for the enum
     * instances.
     *
     * @param id the id
     * @param description the description
     * @param mandatory is the parameter mandatory
     */
    private IdentificationParametersCLIParams(String id, String description, boolean mandatory) {
        this.id = id;
        this.description = description;
        this.mandatory = mandatory;
    }
}