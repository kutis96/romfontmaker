package in.spcct.romfontmaker;

import java.io.OutputStream;

public interface Exporter {

    void export(Font font, OutputStream outputStream);

}
