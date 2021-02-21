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
    private boolean startedWriting;

    public ResultWriter(String output) throws IOException {
        out = new PrintWriter(new BufferedWriter(new FileWriter(output)));
        mapper = new ObjectMapper();
        startedWriting = false;
        out.println("[");
    }

    public void register(String sourceFilePath, Set<MethodExtractedData> result) {
        try {
            ClassExtractedData data = new ClassExtractedData();
            data.fileName = sourceFilePath;
            data.data = result;

            if (startedWriting) {
                out.printf(",%s%n", mapper.writeValueAsString(data));
            } else {
                out.printf("%s%n", mapper.writeValueAsString(data));
            }
            startedWriting = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        out.flush();
        out.println("]");
        out.close();
    }
}
