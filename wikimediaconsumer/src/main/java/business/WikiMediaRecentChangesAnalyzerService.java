package business;

import business.enums.WikiType;
import business.interfaces.WikiMediaAnalyzer;
import dto.WikiMediaRecentChangesDTO;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class WikiMediaRecentChangesAnalyzerService implements WikiMediaAnalyzer {
    //Constants
    private final WikiType wikiType;
    //Semi-Raw constants
    private long numberOfBotChanges;
    private long numberOfTotalChanges;
    private long numberOfTotalBatches;
    //CalculatedConstants
    private double botRatio;

    private long theLastTimeStamp ;

//    private Long getTheFirstTimeStamp() {
//        class TimeStampSupplier implements Supplier<Long>{
//            private long valueToGet;
//
//            @Override
//            public Long get() {
//                return valueToGet;
//            }
//            public void setValueToGet(long val){
//                this.valueToGet = val;
//            }
//        }
//        if(!TimeStampSupplier.class.isInstance(theLastTimeStamp)){
//            theLastTimeStamp
//        }
//    }

    private double averageTimeStamp;

    public WikiMediaRecentChangesAnalyzerService(WikiType wikiType) {
        this.wikiType = wikiType;
    }

    @Override
    public void analyze(List<WikiMediaRecentChangesDTO> recentChangesDTOList) {
        var filteredList = getFilteredList(recentChangesDTOList);
        if(filteredList.isEmpty()){
            return;
        }
        updateFields(filteredList);
        calculateFields(filteredList);
    }

    @Override
    public void printInfo(Writer out) {
        var message = String.format("The statistics for Wiki: %s:%n The average updating interval is: %f%n The ratio of changes made by bots to real users is: %f%n",
                wikiType.getWikiType(), averageTimeStamp, botRatio * 100);
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private List<WikiMediaRecentChangesDTO> getFilteredList(List<WikiMediaRecentChangesDTO> recentChangesDTOList) {
        return recentChangesDTOList.stream()
                .filter(wikiMediaRecentChangesDTO -> wikiMediaRecentChangesDTO.getWiki().equals(wikiType.getWikiType()))
                .collect(Collectors.toList());
    }

    private void updateFields(List<WikiMediaRecentChangesDTO> filteredList) {
        numberOfTotalBatches++;
        numberOfTotalChanges += filteredList.size();
        numberOfBotChanges += filteredList.stream()
                .filter(WikiMediaRecentChangesDTO::isBot)
                .count();
    }

    private void calculateFields(List<WikiMediaRecentChangesDTO> filteredList) {
        botRatio = ((double) numberOfBotChanges) / numberOfTotalChanges;
        var temp = averageTimeStamp * (numberOfTotalBatches - 1);
        temp += timeStampCalculator(filteredList);
        averageTimeStamp = temp / numberOfTotalBatches;
    }

    private double timeStampCalculator(List<WikiMediaRecentChangesDTO> dtoList) {
        var sortedList = dtoList.stream().
                sorted(Comparator.comparing(WikiMediaRecentChangesDTO::getTimestamp))
                .collect(Collectors.toList());

        var timeStampDiffInABatch= sortedList.get(dtoList.size() - 1).getTimestamp() - theLastTimeStamp;
        theLastTimeStamp = sortedList.get(dtoList.size() - 1).getTimestamp();
        return ((double) timeStampDiffInABatch) / dtoList.size();
    }

    public WikiType getWikiType() {
        return wikiType;
    }

    public long getNumberOfBotChanges() {
        return numberOfBotChanges;
    }

    public long getNumberOfTotalChanges() {
        return numberOfTotalChanges;
    }

    public long getNumberOfTotalBatches() {
        return numberOfTotalBatches;
    }

    public double getBotRatio() {
        return botRatio;
    }

    public double getAverageTimeStamp() {
        return averageTimeStamp;
    }
}
