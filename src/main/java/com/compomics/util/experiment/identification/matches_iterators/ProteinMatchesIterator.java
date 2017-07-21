package com.compomics.util.experiment.identification.matches_iterators;

import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.waiting.WaitingHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * An iterator of protein matches.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 * @author Dominik Kopczynski
 */
public class ProteinMatchesIterator extends MatchesIterator {
    /**
     * Constructor.
     *
     * @param identification the identification where to get the matches from
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * 
     * @throws SQLException exception thrown whenever an error occurred while
     * interacting with the matches database
     * @throws IOException exception thrown whenever an error occurred while
     * interacting with the matches database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a match from the database
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while retrieving the match
     */
    public ProteinMatchesIterator(Identification identification, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        this(null, identification, waitingHandler, displayProgress);
    }
    
    /**
     * Constructor.
     *
     * @param keys the keys of the objects
     * @param identification the identification where to get the matchesloadPs from
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * 
     * @throws SQLException exception thrown whenever an error occurred while
     * interacting with the matches database
     * @throws IOException exception thrown whenever an error occurred while
     * interacting with the matches database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a match from the database
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while retrieving the match
     */
    public ProteinMatchesIterator(ArrayList<String> keys, Identification identification, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        super(keys, ProteinMatch.class, identification, waitingHandler, displayProgress, null);
    }
    


    /**
     * Returns the next match and updates the buffer. Null if the iterator is
     * done iterating.
     *
     * @return the next match
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * interacting with the matches database
     * @throws IOException exception thrown whenever an error occurred while
     * interacting with the matches database
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a match from the database
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while retrieving the match
     */
    public ProteinMatch next() throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        return (ProteinMatch)nextObject();
    }
}
