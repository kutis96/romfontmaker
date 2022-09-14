package in.spcct.romfontmaker.impl.freemarker;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;

public class RadixFormatter implements TemplateMethodModelEx {

    private String paddedNumber(
            int value,
            int radix,
            int width
    ) {
        return String.format("%" + width + "s", Integer.toString(value, radix)).replace(' ', '0');
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() != 3) {
            throw new TemplateModelException("Radix formatter should be passed these values: value, radix, width(digits).");
        }

        int value = ((SimpleNumber) args.get(0)).getAsNumber().intValue();
        int radix = ((SimpleNumber) args.get(1)).getAsNumber().intValue();
        int width = ((SimpleNumber) args.get(2)).getAsNumber().intValue();
        return paddedNumber(value, radix, width);

    }
}
