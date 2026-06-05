abstract class DataPayload {

    public abstract String getRawContent();
}

class JsonPayload extends DataPayload {

    private String rawContent;

    public JsonPayload(String rawContent) {
        this.rawContent = rawContent;
    }

    @Override
    public String getRawContent() {
        return rawContent;
    }
}

class XmlPayload extends DataPayload {

    private String rawContent;

    public XmlPayload(String rawContent) {
        this.rawContent = rawContent;
    }

    @Override
    public String getRawContent() {
        return rawContent;
    }
}

class PipelineProcessor<T extends DataPayload> {

    public void process(T payload) {

        System.out.println("Processing Payload:");
        System.out.println(payload.getRawContent());
    }
}

public class DataStreamETLPipeline {

    public static void main(String[] args) {

        JsonPayload jsonPayload =
                new JsonPayload("{\"name\":\"Prabhnoor\"}");

        XmlPayload xmlPayload =
                new XmlPayload("<name>Prabhnoor</name>");

        PipelineProcessor<JsonPayload> jsonProcessor =
                new PipelineProcessor<>();

        PipelineProcessor<XmlPayload> xmlProcessor =
                new PipelineProcessor<>();

        jsonProcessor.process(jsonPayload);
        System.out.println();

        xmlProcessor.process(xmlPayload);
    }
}