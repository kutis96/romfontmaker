package in.spcct.romfontmaker;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface Exporter {

    void export(Font font, OutputStream outputStream, Map<String, String> config) throws IOException;

}
