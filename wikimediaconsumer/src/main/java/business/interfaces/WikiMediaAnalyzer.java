package business.interfaces;

import dto.WikiMediaRecentChangesDTO;

import java.io.Writer;
import java.util.List;

public interface WikiMediaAnalyzer {
    void analyze(List<WikiMediaRecentChangesDTO> dtoList);
    void printInfo(Writer out);
}