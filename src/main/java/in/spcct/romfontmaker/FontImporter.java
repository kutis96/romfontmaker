package in.spcct.romfontmaker;

import java.util.Map;

public interface FontImporter {
    Font importFont(BitmapImage image, Map<String, String> config);
}
