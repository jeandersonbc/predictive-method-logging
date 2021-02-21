package experiment.component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class ResultWriter implements AutoCloseable {

    private final PrintWriter out;
    private final ObjectMapper mapper;

    public ResultWriter(String output) throws IOException {
        out = new PrintWriter(new BufferedWriter(new FileWriter(output)));
        mapper = new ObjectMapper();
    }

    public void register(String sourceFilePath, Set<MethodExtractedData> result) {
        try {
            System.out.println(sourceFilePath);
            System.out.println(mapper.writeValueAsString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        out.flush();
        out.close();
    }
}
